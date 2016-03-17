package org.adempiere.datev.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.adempiere.bpartner.service.IBPartnerPA;
import org.adempiere.datev.IDatevSettings;
import org.adempiere.datev.model.OBE_Vorlaufinformationen;
import org.adempiere.datev.model.masterdata.OBE_Stammdaten_Buchungssatz;
import org.adempiere.datev.model.masterdata.OBE_Stammdaten_Kurzvorlauf;
import org.adempiere.datev.model.masterdata.StammdatensatzFileInfo;
import org.adempiere.datev.service.IMasterDataService;
import org.adempiere.misc.service.IBankingPA;
import org.adempiere.misc.service.IPOService;
import org.adempiere.util.Services;
import org.compiere.model.I_C_BP_BankAccount;
import org.compiere.model.I_C_BPartner;
import org.compiere.model.I_C_BPartner_Location;
import org.compiere.model.I_C_Bank;
import org.compiere.model.MCountry;
import org.compiere.model.MElementValue;
import org.compiere.model.MLocation;
import org.compiere.model.X_C_Greeting;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;

public final class MasterDataService implements IMasterDataService {

	private final static CLogger logger = CLogger
			.getCLogger(MasterDataService.class);

	private final static String LOOKUP_ACCOUNTELEMENT_SQL =
			  "SELECT v.account_id"
			+ " FROM c_bp_customer_acct c"
			+ " LEFT join c_validcombination v ON"
			+ "   c_receivable_acct=c_validcombination_id "
			+ "   AND v.ad_client_id=?" // Param 1 ad_client_id
//			+ "   AND (v.ad_org_id=? OR v.ad_org_id=0) " // Param 2 ad_org_id; Don't need this parameter at all!
			+ " WHERE " //
			+ "   c.c_bpartner_id=? " // Param 3 c_bpartner_id
			// + " AND c.ad_client_id=?" // Param 4 ad_client_id
			// + " AND (c.ad_org_id=? OR c.ad_org_id=0)"; // Param 5 ad_org_id
	;

	private final Set<String> acctNos = new HashSet<String>();

	private final Set<Integer> bPartnerIdsExporting = new HashSet<Integer>();

	private final Set<Integer> bPartnerIdsExportedEarlier = new HashSet<Integer>();

	private final StammdatensatzFileInfo stammdatensatzFileInfo;

	/**
	 * 
	 * @param targetDirectory
	 * @param settings
	 * @throws IllegalArgumentException
	 *             is <code>targetDirectory</code> or <code>settings</code> is
	 *             <code>null</code>.
	 */
	public MasterDataService(final File targetDirectory, IDatevSettings settings) {

		if (settings == null) {
			throw new IllegalArgumentException("'settings' may not be null");
		}
		if (targetDirectory == null) {
			throw new IllegalArgumentException(
					"'targetDirectory' may not be null");
		}

		stammdatensatzFileInfo = new StammdatensatzFileInfo(targetDirectory);

		final int currentYear = new GregorianCalendar()
				.get(GregorianCalendar.YEAR);

		OBE_Vorlaufinformationen commonHeaderInfo = new OBE_Vorlaufinformationen();

		commonHeaderInfo
				.setAnwendungsnummer(OBE_Vorlaufinformationen.Anwendungsnummer.FIBUOPOS_STAMMDATEN);
		commonHeaderInfo.namenskuerzel = settings.getNamenskuerzel();
		commonHeaderInfo.setBeraternummer(settings.getBeraternummer());
		commonHeaderInfo.setMandantennummer(settings.getMandantennummer());
		commonHeaderInfo.setAbrechnungsnummer(settings.getAbrechnungsnummer(),
				currentYear);

		commonHeaderInfo.setPrimanotaSeite(settings.getPrimanotaseite());
		commonHeaderInfo.passwort = settings.getPasswort();

		OBE_Stammdaten_Kurzvorlauf dataFileHeader = new OBE_Stammdaten_Kurzvorlauf();
		dataFileHeader.datentraegernummer = settings.getDatentraegernummer();
		dataFileHeader.setVorlaufinformationen(commonHeaderInfo);

		stammdatensatzFileInfo.setFileHeader(dataFileHeader);
	}

	public Collection<StammdatensatzFileInfo> getData() {

		ArrayList<StammdatensatzFileInfo> result = new ArrayList<StammdatensatzFileInfo>();

		result.add(stammdatensatzFileInfo);
		return result;
	}

	public boolean accountSeen(final String accountNo) {

		if (acctNos.contains(accountNo)) {
			// this number has already been dealt with
			return false;
		}

		// not implemented

		// OBE_Stammdaten_Buchungssatz accountElement =
		// lookupAccountElement(accountNo);
		// stammdatensatzFileInfo.addDataRecord(accountElement);

		acctNos.add(accountNo);
		return true;
	}

	public void addAlreadyExportedBPartnerId(final int bPartnerId) {
		bPartnerIdsExportedEarlier.add(bPartnerId);
	}

	public boolean bPartnerSeen(final int bPartnerId, final String trxName) {

		if (bPartnerId < 1 || bPartnerIdsExporting.contains(bPartnerId)) {
			return false;
		}

		final IBPartnerPA bPArtnerPa = Services.get(IBPartnerPA.class);
		final I_C_BPartner bPartner = bPArtnerPa.retrieveBPartner(bPartnerId,
				trxName);
		if (!bPartner.isCustomer()) {
			throw new IllegalArgumentException("BPartner with id " + bPartnerId
					+ " is not a customer");
		}
		if (bPartnerIdsExportedEarlier.contains(bPartnerId)) {
			addDataRecord(OBE_Stammdaten_Buchungssatz.B_ERSTEINGABE_AENDERUNG,
					"2");
		} else {
			addDataRecord(OBE_Stammdaten_Buchungssatz.B_ERSTEINGABE_AENDERUNG,
					"1");
		}

		final MElementValue customerAcct = lookupAccountElement(bPartner, trxName);
		if (customerAcct == null) {
			logger.warning("Found no cusomer accounting number for business partner "+ bPartner);
			return false;
		}
		final int kontonummer = Integer.parseInt(customerAcct.getValue());

		addDataRecord(OBE_Stammdaten_Buchungssatz.B_KONTONUMMER, Integer
				.toString(kontonummer));
		addDataRecord(kontonummer, customerAcct.getName());

		addDataRecord(OBE_Stammdaten_Buchungssatz.B_NAME1, bPartner.getName());
		addDataRecord(OBE_Stammdaten_Buchungssatz.B_KUNDENNUMMER, bPartner
				.getValue());

		final List<I_C_BPartner_Location> bPartNerLocations = bPArtnerPa
				.retrieveBPartnerLocations(bPartnerId, false, trxName);
		;
		if (bPartNerLocations.size() > 0) {

			final MLocation location = MLocation.getBPLocation(Env.getCtx(),
					bPartNerLocations.get(0).getC_BPartner_Location_ID(), null);

			addDataRecord(OBE_Stammdaten_Buchungssatz.B_POSTLEITZAHL, location
					.getPostal());
			addDataRecord(OBE_Stammdaten_Buchungssatz.B_ORT, location.getCity());
			addDataRecord(OBE_Stammdaten_Buchungssatz.B_STRASSE, location
					.getAddress1());

		}

		final X_C_Greeting greeting = new X_C_Greeting(Env.getCtx(), bPartner
				.getC_Greeting_ID(), null);
		if ("Firma".equals(greeting.getName())) {
			addDataRecord(OBE_Stammdaten_Buchungssatz.B_ANREDE, "5");
		} else if ("Frau".equals(greeting.getName())) {
			addDataRecord(OBE_Stammdaten_Buchungssatz.B_ANREDE, "3");
		} else if ("Herr".equals(greeting.getName())) {
			addDataRecord(OBE_Stammdaten_Buchungssatz.B_ANREDE, "2");
		}

		// 110 - 117
		// TODO: Formatierung der Zahl checken
		addDataRecord(OBE_Stammdaten_Buchungssatz.D_KREDITLIMIT, bPartner
				.getSO_CreditLimit().toPlainString());

		// 119

		// 120 - 122 weglassen
		// 123 sp�ter neues Feld "DATEV-ID" in Fenster Zahlungsbedingung

		// 125 - 127 weglassen

		final IBankingPA bankingPA = Services.get(IBankingPA.class);

		final List<? extends I_C_BP_BankAccount> bankAccounts = bankingPA
				.retrieveBankAccountsOfBPartner(bPartnerId, trxName);
		if (bankAccounts.size() > 0) {

			final int bankId = bankAccounts.get(0).getC_Bank_ID();
			final I_C_Bank bank1 = bankingPA.retrieveBank(bankId, trxName);
			if (bank1 != null) {
				addDataRecord(
						OBE_Stammdaten_Buchungssatz.B_BANKBEZEICHNUNG_ORT,
						bank1.getName());
			}
			addDataRecord(OBE_Stammdaten_Buchungssatz.B_BANK_KONTONUMMER,
					bankAccounts.get(0).getAccountNo());
			addDataRecord(OBE_Stammdaten_Buchungssatz.B_BANKLEITZAHL,
					bankAccounts.get(0).getRoutingNo());
		}

		// 134 weglassen

		addDataRecord(OBE_Stammdaten_Buchungssatz.B_NAME2, bPartner.getName2());

		addDataRecord(OBE_Stammdaten_Buchungssatz.B_UST_ID, bPartner.getTaxID());

		if (bPartNerLocations.size() > 0) {
			MLocation location = MLocation.getBPLocation(Env.getCtx(),
					bPartNerLocations.get(0).getC_BPartner_Location_ID(), null);

			MCountry country = location.getCountry();
			if (country != null) {
				addDataRecord(
						OBE_Stammdaten_Buchungssatz.B_NATIONALITAETSKENNZEICHEN,
						country.getCountryCode());
			}
			addDataRecord(OBE_Stammdaten_Buchungssatz.B_TELEFONNUMMER,
					bPartNerLocations.get(0).getPhone());
			addDataRecord(OBE_Stammdaten_Buchungssatz.B_TELEFAX,
					bPartNerLocations.get(0).getFax());
		}

		bPartnerIdsExporting.add(bPartnerId);
		return true;
	}

	/**
	 * 
	 * @param id
	 * @param text may be <code>null</code>. In this case nothing is done.
	 */
	private void addDataRecord(final int id, final String text) {

		if (text == null) {
			return;
		}
		OBE_Stammdaten_Buchungssatz bs = new OBE_Stammdaten_Buchungssatz();
		bs.setKennziffer(id);
		bs.setText(text.trim());
		stammdatensatzFileInfo.addDataRecord(bs);
	}

	private MElementValue lookupAccountElement(final I_C_BPartner bPartner,
			final String trxName) {

		final PreparedStatement pstmt = DB.prepareStatement(LOOKUP_ACCOUNTELEMENT_SQL, null);
		ResultSet rs = null;
		final IPOService poService = Services.get(IPOService.class);
		
		final int clientId = (Integer) poService.getValue(bPartner, "AD_Client_ID");
		final int orgId = (Integer) poService.getValue(bPartner, "AD_Org_ID");

		try {
			int indx = 0;
			pstmt.setInt(++indx, clientId);
			//pstmt.setInt(++indx, orgId);

			pstmt.setInt(++indx, bPartner.getC_BPartner_ID());
			// pstmt.setInt(++indx, bPartner.getAD_Client_ID());
			// pstmt.setInt(++indx, bPartner.getAD_Org_ID());
			rs = pstmt.executeQuery();

			if (rs.next()) {
				BigDecimal acctId = rs.getBigDecimal(1);
				MElementValue acctInfo = new MElementValue(Env.getCtx(), acctId.intValue(), null);
				return acctInfo;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DB.close(rs, pstmt);
		}
		return null;
	}
}
