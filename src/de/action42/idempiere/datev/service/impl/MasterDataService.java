package de.action42.idempiere.datev.service.impl;

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

import de.metas.adempiere.bpartner.service.IBPartnerPA;
import de.metas.adempiere.misc.service.IBankingPA;
import de.metas.adempiere.misc.service.IPOService;
import de.metas.adempiere.util.Services;
import org.compiere.model.I_C_BP_BankAccount;
import org.compiere.model.I_C_BPartner;
import org.compiere.model.I_C_BPartner_Location;
import org.compiere.model.I_C_Bank;
import org.compiere.model.MBPartner;
import org.compiere.model.MCountry;
import org.compiere.model.MElementValue;
import org.compiere.model.MLocation;
import org.compiere.model.X_C_Greeting;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;

import de.action42.idempiere.datev.IDatevSettings;
import de.action42.idempiere.datev.model.CSV_Vorlaufinformationen;
import de.action42.idempiere.datev.model.OBE_Vorlaufinformationen;
import de.action42.idempiere.datev.model.masterdata.CSV_Stammdaten_Buchungssatz;
import de.action42.idempiere.datev.model.masterdata.CSV_Stammdaten_Kurzvorlauf;
import de.action42.idempiere.datev.model.masterdata.OBE_Stammdaten_Buchungssatz;
import de.action42.idempiere.datev.model.masterdata.OBE_Stammdaten_Kurzvorlauf;
import de.action42.idempiere.datev.model.masterdata.StammdatensatzFileInfo;
import de.action42.idempiere.datev.model.masterdata.StammdatensatzFileInfoCSV;
import de.action42.idempiere.datev.service.IMasterDataService;

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
	private final StammdatensatzFileInfoCSV stammdatensatzFileInfoCSV;

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
		stammdatensatzFileInfoCSV = new StammdatensatzFileInfoCSV(targetDirectory);

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

		// CSV
		CSV_Vorlaufinformationen commonHeaderInfoCSV = new CSV_Vorlaufinformationen();
		commonHeaderInfoCSV
		.setAnwendungsnummer(CSV_Vorlaufinformationen.Anwendungsnummer.FIBUOPOS_STAMMDATEN);
		commonHeaderInfoCSV.namenskuerzel = settings.getNamenskuerzel();
		commonHeaderInfoCSV.setBeraternummer(settings.getBeraternummer());
		commonHeaderInfoCSV.setMandantennummer(settings.getMandantennummer());
		commonHeaderInfoCSV.setAbrechnungsnummer(settings.getAbrechnungsnummer(),
				currentYear);

		commonHeaderInfoCSV.setPrimanotaSeite(settings.getPrimanotaseite());
		commonHeaderInfoCSV.passwort = settings.getPasswort();

		CSV_Stammdaten_Kurzvorlauf dataFileHeaderCSV = new CSV_Stammdaten_Kurzvorlauf();

		dataFileHeaderCSV.datentraegernummer = settings.getDatentraegernummer();
		dataFileHeaderCSV.setVorlaufinformationen(commonHeaderInfoCSV);

		stammdatensatzFileInfoCSV.setFileHeaderCSV(dataFileHeaderCSV);
	}

	public Collection<StammdatensatzFileInfo> getData() {

		ArrayList<StammdatensatzFileInfo> result = new ArrayList<StammdatensatzFileInfo>();

		result.add(stammdatensatzFileInfo);
		return result;
	}

	public Collection<StammdatensatzFileInfoCSV> getDataCSV() {

		ArrayList<StammdatensatzFileInfoCSV> result = new ArrayList<StammdatensatzFileInfoCSV>();

		result.add(stammdatensatzFileInfoCSV);
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

		/* erst mal raus
		if (!(bPartner.isCustomer() || bPartner.isVendor()) || bPartner.isEmployee() || bPartner.isSalesRep()) {

			throw new IllegalArgumentException("BPartner with id " + bPartnerId
					+ " is not a customer or vendor (or employee or SalesRep");
		}
		*/
		
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

	public boolean bPartnerSeenCSV(final int bPartnerId, final boolean isSOTrx, final String trxName) {

		if (bPartnerId < 1 || bPartnerIdsExporting.contains(bPartnerId)) {
			return false;
		}

		final IBPartnerPA bPArtnerPa = Services.get(IBPartnerPA.class);
		final I_C_BPartner bPartner = bPArtnerPa.retrieveBPartner(bPartnerId,
				trxName);

		/* erst mal raus
		if (!(bPartner.isCustomer() || bPartner.isVendor()) || bPartner.isEmployee() || bPartner.isSalesRep()) {

			throw new IllegalArgumentException("BPartner with id " + bPartnerId
					+ " is not a customer or vendor (or employee or SalesRep");
		}
		*/
		
//		if (bPartnerIdsExportedEarlier.contains(bPartnerId)) {
//			addDataRecordCSV(CSV_Stammdaten_Buchungssatz.B_ERSTEINGABE_AENDERUNG,
//					"2");
//		} else {
//			addDataRecordCSV(CSV_Stammdaten_Buchungssatz.B_ERSTEINGABE_AENDERUNG,
//					"1");
//		}

		final MElementValue customerAcct = lookupAccountElement(bPartner, trxName);
		if (customerAcct == null) {
			logger.warning("Found no customer accounting number for business partner "+ bPartner);
			return false;
		}
		final int kontonummer = Integer.parseInt(customerAcct.getValue());
		MBPartner bp = new MBPartner(Env.getCtx(), bPartnerId, trxName); 
		String personenkonto = "";
		if (isSOTrx) {
			personenkonto = bp.get_ValueAsString("DebtorID");
		}
		else {
			personenkonto = bp.get_ValueAsString("CreditorID");
		}
		
		addDataRecordCSV("\"" + personenkonto + "\""); // 1
		addDataRecordCSV("\"" + bPartner.getName() + "\""); // 2
//		addDataRecordCSV("\"" + Integer.toString(kontonummer) + "\""); // 1 kontonummer
//		addDataRecordCSV("\"" + customerAcct.getName() + "\""); // 2 name
		addDataRecordCSV(""); // 3 
		addDataRecordCSV(""); // 4 
		addDataRecordCSV(""); // 5 
		addDataRecordCSV(""); // 6 
		addDataRecordCSV("\"" + "2" + "\""); // 7 Adressattyp Unternehmen 
		addDataRecordCSV(""); // 8

		if (bPartner.getTaxID() != null) {
		addDataRecordCSV("\"" + bPartner.getTaxID().substring(0, 2) + "\";"); // 9 EU-Land
		addDataRecordCSV("\"" + bPartner.getTaxID().substring(2, bPartner.getTaxID().length()) + "\";"); // 10 EU-UstID
		}
		else {
			addDataRecordCSV(""); // 9 
			addDataRecordCSV(""); // 10 			
		}
		addDataRecordCSV(""); // 11 Anrede
		addDataRecordCSV(""); // 12 
		addDataRecordCSV(""); // 13 
		addDataRecordCSV(""); // 14 
		addDataRecordCSV(""); // 15 

		final List<I_C_BPartner_Location> bPartnerLocations = bPArtnerPa
				.retrieveBPartnerLocations(bPartnerId, false, trxName);
		;
		if (bPartnerLocations.size() > 0) {

			final MLocation location = MLocation.getBPLocation(Env.getCtx(),
					bPartnerLocations.get(0).getC_BPartner_Location_ID(), null);

			addDataRecordCSV("\"" + location.getAddress1() + "\""); // 16
			addDataRecordCSV(""); // 17

			addDataRecordCSV("\"" + location.getPostal() + "\""); // 18
			addDataRecordCSV("\"" + location.getCity() + "\""); // 19
			addDataRecordCSV("\"" + location.getCountry().getCountryCode() + "\""); // 20 Land

		}

		addDataRecordCSV(""); // 21
		addDataRecordCSV(""); // 22
		addDataRecordCSV(""); // 23
		addDataRecordCSV(""); // 24
		addDataRecordCSV(""); // 25
		addDataRecordCSV(""); // 26
		addDataRecordCSV(""); // 27
		addDataRecordCSV(""); // 28
		addDataRecordCSV(""); // 29
		addDataRecordCSV(""); // 30

		addDataRecordCSV(""); // 31
		addDataRecordCSV(""); // 32
		addDataRecordCSV(""); // 33
		addDataRecordCSV(""); // 34
		addDataRecordCSV(""); // 35
		addDataRecordCSV(""); // 36
		addDataRecordCSV(""); // 37
		addDataRecordCSV(""); // 38
		addDataRecordCSV(""); // 39
		addDataRecordCSV(""); // 40

		addDataRecordCSV(""); // 41
		addDataRecordCSV(""); // 22
		addDataRecordCSV(""); // 23
		addDataRecordCSV(""); // 24
		addDataRecordCSV(""); // 25
		addDataRecordCSV(""); // 26
		addDataRecordCSV(""); // 27
		addDataRecordCSV(""); // 28
		addDataRecordCSV(""); // 29
		addDataRecordCSV(""); // 50

		addDataRecordCSV(""); // 51
		addDataRecordCSV(""); // 32
		addDataRecordCSV(""); // 33
		addDataRecordCSV(""); // 34
		addDataRecordCSV(""); // 35
		addDataRecordCSV(""); // 36
		addDataRecordCSV(""); // 37
		addDataRecordCSV(""); // 38
		addDataRecordCSV(""); // 39
		addDataRecordCSV(""); // 60

		addDataRecordCSV(""); // 61
		addDataRecordCSV(""); // 22
		addDataRecordCSV(""); // 23
		addDataRecordCSV(""); // 24
		addDataRecordCSV(""); // 25
		addDataRecordCSV(""); // 26
		addDataRecordCSV(""); // 27
		addDataRecordCSV(""); // 28
		addDataRecordCSV(""); // 29
		addDataRecordCSV(""); // 70

		addDataRecordCSV(""); // 71
		addDataRecordCSV(""); // 32
		addDataRecordCSV(""); // 33
		addDataRecordCSV(""); // 34
		addDataRecordCSV(""); // 35
		addDataRecordCSV(""); // 36
		addDataRecordCSV(""); // 37
		addDataRecordCSV(""); // 38
		addDataRecordCSV(""); // 39
		addDataRecordCSV(""); // 80

		addDataRecordCSV(""); // 81
		addDataRecordCSV(""); // 22
		addDataRecordCSV(""); // 23
		addDataRecordCSV(""); // 24
		addDataRecordCSV(""); // 25
		addDataRecordCSV(""); // 26
		addDataRecordCSV(""); // 27
		addDataRecordCSV(""); // 28
		addDataRecordCSV(""); // 29
		addDataRecordCSV(""); // 90

		addDataRecordCSV(""); // 91
		addDataRecordCSV(""); // 32
		addDataRecordCSV(""); // 33
		addDataRecordCSV(""); // 34
		addDataRecordCSV(""); // 35
		addDataRecordCSV(""); // 36
		addDataRecordCSV(""); // 37
		addDataRecordCSV(""); // 38
		addDataRecordCSV(""); // 39
		addDataRecordCSV(""); // 100

		addDataRecordCSV(""); // 101
		addDataRecordCSV(""); // 22
		addDataRecordCSV(""); // 23
		addDataRecordCSV(""); // 24
		addDataRecordCSV(""); // 25
		addDataRecordCSV(""); // 26
		addDataRecordCSV(""); // 27
		addDataRecordCSV(""); // 28
		addDataRecordCSV(""); // 29
		addDataRecordCSV(""); // 110

		addDataRecordCSV(""); // 111
		addDataRecordCSV(""); // 32
		addDataRecordCSV(""); // 33
		addDataRecordCSV(""); // 34
		addDataRecordCSV(""); // 35
		addDataRecordCSV(""); // 36
		addDataRecordCSV(""); // 37
		addDataRecordCSV(""); // 38
		addDataRecordCSV(""); // 39
		addDataRecordCSV(""); // 120

		addDataRecordCSV(""); // 121
		addDataRecordCSV(""); // 22
		addDataRecordCSV(""); // 23
		addDataRecordCSV(""); // 24
		addDataRecordCSV(""); // 25
		addDataRecordCSV(""); // 26
		addDataRecordCSV(""); // 27
		addDataRecordCSV(""); // 28
		addDataRecordCSV(""); // 29
		addDataRecordCSV(""); // 130

		addDataRecordCSV(""); // 131
		addDataRecordCSV(""); // 32
		addDataRecordCSV(""); // 33
		addDataRecordCSV(""); // 34
		addDataRecordCSV(""); // 35
		addDataRecordCSV(""); // 36
		addDataRecordCSV(""); // 37
		addDataRecordCSV(""); // 38
		addDataRecordCSV(""); // 39
		addDataRecordCSV(""); // 140

		addDataRecordCSV(""); // 141
		addDataRecordCSV(""); // 22
		addDataRecordCSV(""); // 23
		addDataRecordCSV(""); // 24
		addDataRecordCSV(""); // 25
		addDataRecordCSV(""); // 26
		addDataRecordCSV(""); // 27
		addDataRecordCSV(""); // 28
		addDataRecordCSV(""); // 29
		addDataRecordCSV(""); // 150

		addDataRecordCSV(""); // 151
		addDataRecordCSV(""); // 32
		addDataRecordCSV(""); // 33
		addDataRecordCSV(""); // 34
		addDataRecordCSV(""); // 35
		addDataRecordCSV(""); // 36
		addDataRecordCSV(""); // 37
		addDataRecordCSV(""); // 38
		addDataRecordCSV(""); // 39
		addDataRecordCSV(""); // 160

		addDataRecordCSV(""); // 161
		addDataRecordCSV(""); // 32
		addDataRecordCSV(""); // 33
		addDataRecordCSV(""); // 164

//		final X_C_Greeting greeting = new X_C_Greeting(Env.getCtx(), bPartner
//				.getC_Greeting_ID(), null);
//		addDataRecordCSV(greeting.getName());

		// 110 - 117
		// TODO: Formatierung der Zahl checken
//		addDataRecordCSV(bPartner
//				.getSO_CreditLimit().toPlainString());

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
			addDataRecordCSV(
					bankAccounts.get(0).getRoutingNo()); // 42
			if (bank1 != null) {
				addDataRecordCSV(
						bank1.getName()); // 43
			}
			addDataRecordCSV(
					bankAccounts.get(0).getAccountNo()); // 44
			addDataRecordCSV(
					bank1.getC_Location().getC_Country().getCountryCode()); // 45
			
			addDataRecordCSV(""); // 46
			addDataRecordCSV(""); // 47
			addDataRecordCSV(bank1.getSwiftCode()); // 48
			addDataRecordCSV(""); // 49
			addDataRecordCSV("1"); // 50 Hauptbankverbindung
		}

		// 134 weglassen

//		addDataRecordCSV(CSV_Stammdaten_Buchungssatz.B_NAME2, bPartner.getName2());
//
//		addDataRecordCSV(CSV_Stammdaten_Buchungssatz.B_UST_ID, bPartner.getTaxID());
//
//		if (bPartNerLocations.size() > 0) {
//			MLocation location = MLocation.getBPLocation(Env.getCtx(),
//					bPartNerLocations.get(0).getC_BPartner_Location_ID(), null);
//
//			MCountry country = location.getCountry();
//			if (country != null) {
//				addDataRecordCSV(
//						CSV_Stammdaten_Buchungssatz.B_NATIONALITAETSKENNZEICHEN,
//						country.getCountryCode());
//			}
//			addDataRecordCSV(CSV_Stammdaten_Buchungssatz.B_TELEFONNUMMER,
//					bPartNerLocations.get(0).getPhone());
//			addDataRecordCSV(CSV_Stammdaten_Buchungssatz.B_TELEFAX,
//					bPartNerLocations.get(0).getFax());
//		}

		addDataRecordCSV("\n"); // XXX Kludge! sollte eigentlich woanders passieren pro geschriebener Zeile

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

	private void addDataRecordCSV(final String text) {

		if (text == null) {
			return;
		}
		CSV_Stammdaten_Buchungssatz bs = new CSV_Stammdaten_Buchungssatz();
		if (text.equalsIgnoreCase("\n")) {
			bs.setText(text);
		}
		else {
//			bs.setText(text.trim() + ";");
			bs.setText(text + ";");
		}
		stammdatensatzFileInfoCSV.addDataRecordCSV(bs);
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
