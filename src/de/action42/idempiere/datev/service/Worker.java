package de.action42.idempiere.datev.service;

import static de.action42.idempiere.util.DatevCustomColNames.C_BPartner_CREDITORID;
import static de.action42.idempiere.util.DatevCustomColNames.C_BPartner_DEBITORID;

import java.io.File;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.compiere.model.I_C_BPartner;
import org.compiere.model.I_C_ElementValue;
import org.compiere.model.I_C_Invoice;
import org.compiere.model.I_Fact_Acct;
import org.compiere.model.MInvoice;
import org.compiere.model.X_C_ElementValue;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;

import de.action42.idempiere.datev.DatevException;
import de.action42.idempiere.datev.IDatevSettings;
import de.action42.idempiere.datev.io.CSV_Verwaltungsdatei;
import de.action42.idempiere.datev.io.OBE_Verwaltungsdatei;
import de.action42.idempiere.datev.model.CSV_Buchungssatz;
import de.action42.idempiere.datev.model.CSV_Vorlaufinformationen;
import de.action42.idempiere.datev.model.DatensatzFileInfo;
import de.action42.idempiere.datev.model.DatensatzFileInfoCSV;
import de.action42.idempiere.datev.model.OBE_Buchungssatz;
import de.action42.idempiere.datev.model.OBE_Vorlaufinformationen;
import de.action42.idempiere.datev.model.acct.BewegungssatzFileInfo;
import de.action42.idempiere.datev.model.acct.BewegungssatzFileInfoCSV;
import de.action42.idempiere.datev.model.acct.CSV_Bewegungsdaten_Buchungssatz;
import de.action42.idempiere.datev.model.acct.CSV_Bewegungsdaten_Vollvorlauf;
import de.action42.idempiere.datev.model.acct.OBE_Bewegungsdaten_Buchungssatz;
import de.action42.idempiere.datev.model.acct.OBE_Bewegungsdaten_Vollvorlauf;
import de.action42.idempiere.datev.model.acct.OBE_Datentraegerkennsatz;
import de.action42.idempiere.datev.model.acct.OBE_Verwaltungssatz;
import de.action42.idempiere.datev.model.masterdata.CSV_Stammdaten_Buchungssatz;
import de.action42.idempiere.datev.model.masterdata.OBE_Stammdaten_Buchungssatz;
import de.action42.idempiere.datev.model.masterdata.StammdatensatzFileInfo;
import de.action42.idempiere.datev.util.FactAcctTool;
import de.action42.idempiere.model.I_C_Datev_ExportLog;
import de.metas.adempiere.bpartner.service.IBPartnerPA;
import de.metas.adempiere.invoice.service.IInvoicePA;
import de.metas.adempiere.misc.service.IPOService;
import de.metas.adempiere.util.Services;
import de.metas.adempiere.util.time.SystemTime;

public final class Worker {

	/**
	 * Comparator used to sort the the DATEV accounting records before they are
	 * written to the file system.
	 */
	private static Comparator<OBE_Buchungssatz> acctRecordComp = new Comparator<OBE_Buchungssatz>() {

		public int compare(OBE_Buchungssatz o1, OBE_Buchungssatz o2) {

			if (o1 instanceof OBE_Stammdaten_Buchungssatz) {
				return -1;
			}

			String docNo1 = ((OBE_Bewegungsdaten_Buchungssatz) o1)
					.getBelegfeld1();
			String docNo2 = ((OBE_Bewegungsdaten_Buchungssatz) o2)
					.getBelegfeld1();

			String docNoPrefix1 = docNo1.split("_")[0];
			String docNoPrefix2 = docNo2.split("_")[0];

			if (docNoPrefix1.equals(docNoPrefix2) && !docNo1.equals(docNo2)) {
				if (docNo1.endsWith("_WP") && docNoPrefix1.equals(docNoPrefix2)) {
					return 1;
				}
				if (docNo2.endsWith("_WP") && docNoPrefix1.equals(docNoPrefix2)) {
					return -1;
				}
			}
			return docNoPrefix1.compareTo(docNoPrefix2);
		}
	};

	private static Comparator<CSV_Buchungssatz> acctRecordCompCSV = new Comparator<CSV_Buchungssatz>() {

		public int compare(CSV_Buchungssatz o1, CSV_Buchungssatz o2) {

			if (o1 instanceof CSV_Stammdaten_Buchungssatz) {
				return -1;
			}

			String docNo1 = ((CSV_Bewegungsdaten_Buchungssatz) o1)
					.getBelegfeld1();
			String docNo2 = ((CSV_Bewegungsdaten_Buchungssatz) o2)
					.getBelegfeld1();

			String docNoPrefix1 = docNo1.split("_")[0];
			String docNoPrefix2 = docNo2.split("_")[0];

			if (docNoPrefix1.equals(docNoPrefix2) && !docNo1.equals(docNo2)) {
				if (docNo1.endsWith("_WP") && docNoPrefix1.equals(docNoPrefix2)) {
					return 1;
				}
				if (docNo2.endsWith("_WP") && docNoPrefix1.equals(docNoPrefix2)) {
					return -1;
				}
			}
			return docNoPrefix1.compareTo(docNoPrefix2);
		}
	};

	public static final String KEY = "key";
	/** Static Logger */
	private static final CLogger LOG = CLogger.getCLogger(Worker.class);
	private final static CLogger logger = CLogger.getCLogger(Worker.class);

	/**
	 * Contains the "headers" of different output files for accounting records.
	 */
	private Map<String, BewegungssatzFileInfo> bewegungsSatzFileInfos = new Hashtable<String, BewegungssatzFileInfo>();
	private Map<String, BewegungssatzFileInfoCSV> bewegungsSatzFileInfosCSV = new Hashtable<String, BewegungssatzFileInfoCSV>();

	private final Timestamp dateFrom, dateTo;
	
	/** what do we have to export **/
	private final Boolean exportAP, exportAR;

	/**
	 * After an export this is the number of fact_acct records that were
	 * actually exported.
	 */
	private int exportedRecords;

	private Timestamp exportStartTime;

	private Map<Integer, I_C_Datev_ExportLog> id2LogRecord = new HashMap<Integer, I_C_Datev_ExportLog>();

	private IFactAcctLoader loader;

	private IMasterDataService masterDataService;

	private int orgId;

	private File outputDir;

	private IBewegungsSatzProcessor processor;

	private IDatevSettings settings;

	/**
	 * This constructor is intended for unit testing of single methods only.
	 * @param settings2 
	 * @param m_exportAR 
	 * @param m_exportAP 
	 * @param m_DateTo 
	 * @param m_DateFrom 
	 * @param m_AD_Org_ID 
	 * @param exportDir 
	 */
	Worker(File exportDir, int m_AD_Org_ID, Timestamp m_DateFrom, Timestamp m_DateTo, boolean m_exportAP, boolean m_exportAR, IDatevSettings settings2) {
		dateFrom = SystemTime.asTimestamp();
		dateTo = SystemTime.asTimestamp();
		exportAP = true;
		exportAR = true;
	}

	public Worker(final File myOutputdir, final int myOrgId,
			final Date myDateFrom, final Date myDateTo,
			final Boolean myExportAP, final Boolean myExportAR,
			final IDatevSettings mySettings) {

		outputDir = myOutputdir;
		orgId = myOrgId;

		dateFrom = new Timestamp(myDateFrom.getTime());
		dateTo = new Timestamp(myDateTo.getTime());
		exportAP = myExportAP;
		exportAR = myExportAR;
		settings = mySettings;

		init();
	}

	/**
	 * Processes the records from the database table fact_acct. Also invokes
	 * {@link IMasterDataService#accountSeen(String)} to make sure that master
	 * data for each exported record is also exported.
	 * 
	 * @param trxName
	 *            the id of the ADempiere db-transaction
	 * @param debitorAcct
	 *            the ADempiere fact_acct entry of the debitor account
	 * @param revenueAccts
	 *            the contra accounts (one or more) that belong to the debitor
	 *            account.
	 * @throws DatevException
	 */
	public List<OBE_Bewegungsdaten_Buchungssatz> createFactAcctRecords(
			final String trxName, final I_Fact_Acct debitorAcct,
			final Set<I_Fact_Acct> revenueAccts, final Set<I_Fact_Acct> taxAccts)
			throws DatevException {

		if (masterDataService == null) {
			throw new IllegalStateException("masterDataService may not be null");
		}

		final IAccountingPA accountingPA = Services.get(IAccountingPA.class);

		final I_C_ElementValue debitorAcctInfo = accountingPA
				.retrieveElementValue(debitorAcct.getAccount_ID(), trxName);

		final Map<Integer, Set<I_Fact_Acct>> tax2ToAcct = sortByTaxId(revenueAccts);

		final List<OBE_Bewegungsdaten_Buchungssatz> result = new ArrayList<OBE_Bewegungsdaten_Buchungssatz>();

		for (final int taxId : tax2ToAcct.keySet()) {

			final List<OBE_Bewegungsdaten_Buchungssatz> resultPerTaxId = new ArrayList<OBE_Bewegungsdaten_Buchungssatz>();

			for (final I_Fact_Acct revenueAcct : tax2ToAcct.get(taxId)) {

				// register the bPartner with our master data service
				masterDataService.bPartnerSeen(revenueAcct.getC_BPartner_ID(),
						trxName);

				final I_C_ElementValue revenueAcctInfo = accountingPA
						.retrieveElementValue(revenueAcct.getAccount_ID(),
								trxName);

				// Generell immer Soll an Haben (Konto an Gegenkonto)
				// "Haben" (=Credit!) im Erloeskonto <=> "Soll" im
				// Debitorenkonto <=> Erloeskonto ist Gegenkonto
				final BigDecimal revCredit = revenueAcct.getAmtAcctCr();
				final BigDecimal revDebit = revenueAcct.getAmtAcctDr();

				final Object[] turnOverAmtAndDevToRev = getTurnOverAmtAndDebToRev(
						revCredit, revDebit);

				if (turnOverAmtAndDevToRev.length == 0) {
					continue;
				}

				final BigDecimal turnOverAmount = (BigDecimal) turnOverAmtAndDevToRev[0];
				final boolean deb2rev = (Boolean) turnOverAmtAndDevToRev[1];
				//
				// use the debitor or creditor id instead of the generic account
				// id,
				// if appropriate.

				final String debitorAcctType = debitorAcctInfo.getAccountType();
				final I_C_BPartner debitor = Services.get(IBPartnerPA.class)
						.retrieveBPartner(debitorAcct.getC_BPartner_ID(),
								trxName);

				final IPOService poService = Services.get(IPOService.class);

				String debitorAcctTmp = debitorAcctInfo.getValue();

				if (X_C_ElementValue.ACCOUNTTYPE_Asset.equals(debitorAcctType)) {
					final Integer debitorId = (Integer) poService.getValue(
							debitor, C_BPartner_DEBITORID);
					if (debitorId != null) {
						debitorAcctTmp = debitorId.toString();
					}
				} else if (X_C_ElementValue.ACCOUNTTYPE_Liability
						.equals(debitorAcctType)) {
					final Integer creditorId = (Integer) poService.getValue(
							debitor, C_BPartner_CREDITORID);
					if (creditorId != null) {
						debitorAcctTmp = creditorId.toString();
					}
				}
				final String debitorAccount = debitorAcctTmp;

				final String revenueAccount = revenueAcctInfo.getValue();

				masterDataService.accountSeen(debitorAccount);
				masterDataService.accountSeen(revenueAccount);

				final OBE_Bewegungsdaten_Buchungssatz dataRecord = new OBE_Bewegungsdaten_Buchungssatz();

				dataRecord.setUmsatz(turnOverAmount.doubleValue());

				if (deb2rev) {
					dataRecord.setKonto(debitorAccount);
					// dataRecord.setGegenkonto('1', taxKey, revenueAccount);
					dataRecord.setGegenkonto('0', '0', revenueAccount);
				} else {
					dataRecord.setKonto(revenueAccount);
					// dataRecord.setGegenkonto('1', taxKey, debitorAccount);
					dataRecord.setGegenkonto('0', '0', debitorAccount);
				}

				dataRecord.setDatum(debitorAcct.getDateAcct());

				if (FactAcctTool.isCancellation(debitorAcct)) {

					final String canceledDocNo = FactAcctTool
							.getCancelledDocNo(debitorAcct.getRecord_ID(),
									debitorAcct.getDescription());
					dataRecord.setBelegfeld1(Integer.parseInt(canceledDocNo));

				} else {

					final IInvoicePA invoicePA = Services.get(IInvoicePA.class);
					final I_C_Invoice invoice = invoicePA.retrieveInvoice(
							debitorAcct.getRecord_ID(), trxName);

					final int docNo1 = FactAcctTool.getDocNoInt(invoice
							.getDocumentNo());
					dataRecord.setBelegfeld1(docNo1);
				}
				dataRecord.setBuchungstext(debitor.getName());

				OBE_Verwaltungsdatei.validate(dataRecord);

				resultPerTaxId.add(dataRecord);

				final I_C_Datev_ExportLog exportLog = createLogRecord(trxName,
						"fact_acct", FactAcctTool.getDocNr(debitorAcct
								.getRecord_ID(), debitorAcct.getDescription()),
						revenueAcct.getRecord_ID(), revenueAcct.getDateAcct());

				id2LogRecord.put(dataRecord.getId(), exportLog);
			}

			final List<OBE_Bewegungsdaten_Buchungssatz> compressedResult = FactAcctTool
					.compress(resultPerTaxId);
			if (!resultPerTaxId.isEmpty() && compressedResult.size() != 1) {
				throw new IllegalStateException(
						"After compression there are still "
								+ compressedResult.size()
								+ " records for taxId " + taxId);
			}

			//
			// Get the tax info
			final I_Fact_Acct taxAcct = findTaxAcct(taxAccts, taxId);
			if (taxAcct != null) {

				final Object[] turnOverAmtAndDevToRev = getTurnOverAmtAndDebToRev(
						taxAcct.getAmtAcctCr(), taxAcct.getAmtAcctDr());

				if (turnOverAmtAndDevToRev.length == 0) {
					continue;
				}
				final BigDecimal turnOverAmount = (BigDecimal) turnOverAmtAndDevToRev[0];

				final OBE_Bewegungsdaten_Buchungssatz comprRecord = compressedResult
						.get(0);
				comprRecord.setUmsatz(comprRecord.getUmsatz()
						+ turnOverAmount.doubleValue());
			}
			result.addAll(compressedResult);
		}
		return result;
	}

	public List<CSV_Bewegungsdaten_Buchungssatz> createFactAcctRecordsCSV(
			final String trxName, final I_Fact_Acct debitorAcct,
			final Set<I_Fact_Acct> revenueAccts, final Set<I_Fact_Acct> taxAccts)
			throws DatevException {

		if (masterDataService == null) {
			throw new IllegalStateException("masterDataService may not be null");
		}

		final IAccountingPA accountingPA = Services.get(IAccountingPA.class);

		final I_C_ElementValue debitorAcctInfo = accountingPA
				.retrieveElementValue(debitorAcct.getAccount_ID(), trxName);

		final Map<Integer, Set<I_Fact_Acct>> tax2ToAcct = sortByTaxId(revenueAccts);

		final List<CSV_Bewegungsdaten_Buchungssatz> result = new ArrayList<CSV_Bewegungsdaten_Buchungssatz>();

		for (final int taxId : tax2ToAcct.keySet()) {

			final List<CSV_Bewegungsdaten_Buchungssatz> resultPerTaxId = new ArrayList<CSV_Bewegungsdaten_Buchungssatz>();

			for (final I_Fact_Acct revenueAcct : tax2ToAcct.get(taxId)) {

				// register the bPartner with our master data service
				MInvoice inv = new MInvoice(Env.getCtx(),debitorAcct.getRecord_ID(),trxName);
				masterDataService.bPartnerSeenCSV(revenueAcct.getC_BPartner_ID(), inv.isSOTrx(), trxName);

				final I_C_ElementValue revenueAcctInfo = accountingPA
						.retrieveElementValue(revenueAcct.getAccount_ID(),
								trxName);

				// Generell immer Soll an Haben (Konto an Gegenkonto)
				// "Haben" (=Credit!) im Erloeskonto <=> "Soll" im
				// Debitorenkonto <=> Erloeskonto ist Gegenkonto
				final BigDecimal revCredit = revenueAcct.getAmtAcctCr();
				final BigDecimal revDebit = revenueAcct.getAmtAcctDr();

				final Object[] turnOverAmtAndDebToRev = getTurnOverAmtAndDebToRev(
						revCredit, revDebit);

				if (turnOverAmtAndDebToRev.length == 0) {
					continue;
				}

				final BigDecimal turnOverAmount = (BigDecimal) turnOverAmtAndDebToRev[0];
				final boolean deb2rev = (Boolean) turnOverAmtAndDebToRev[1];
				//
				// use the debitor or creditor id instead of the generic account
				// id,
				// if appropriate.

				final String debitorAcctType = debitorAcctInfo.getAccountType();
				final I_C_BPartner debitor = Services.get(IBPartnerPA.class)
						.retrieveBPartner(debitorAcct.getC_BPartner_ID(),
								trxName);

				final IPOService poService = Services.get(IPOService.class);

				String debitorAcctTmp = debitorAcctInfo.getValue();

				if (X_C_ElementValue.ACCOUNTTYPE_Asset.equals(debitorAcctType)) {
					final Integer debitorId = (Integer) poService.getValue(
							debitor, C_BPartner_DEBITORID);
					if (debitorId != null) {
						debitorAcctTmp = debitorId.toString();
					}
				} else if (X_C_ElementValue.ACCOUNTTYPE_Liability
						.equals(debitorAcctType)) {
					final Integer creditorId = (Integer) poService.getValue(
							debitor, C_BPartner_CREDITORID);
					if (creditorId != null) {
						debitorAcctTmp = creditorId.toString();
					}
				}
				final String debitorAccount = debitorAcctTmp;

				final String revenueAccount = revenueAcctInfo.getValue();

				masterDataService.accountSeen(debitorAccount);
				masterDataService.accountSeen(revenueAccount);

				final CSV_Bewegungsdaten_Buchungssatz dataRecord = new CSV_Bewegungsdaten_Buchungssatz();

				dataRecord.setUmsatz(turnOverAmount.doubleValue());

				if (deb2rev) {
					dataRecord.setKonto(debitorAccount);
					// dataRecord.setGegenkonto('1', taxKey, revenueAccount);
					dataRecord.setGegenkonto('0', '0', revenueAccount);
				} else {
					dataRecord.setKonto(revenueAccount);
					// dataRecord.setGegenkonto('1', taxKey, debitorAccount);
					dataRecord.setGegenkonto('0', '0', debitorAccount);
				}

				dataRecord.setDatum(debitorAcct.getDateAcct());

				if (FactAcctTool.isCancellation(debitorAcct)) {

					final String canceledDocNo = FactAcctTool
							.getCancelledDocNo(debitorAcct.getRecord_ID(),
									debitorAcct.getDescription());
					dataRecord.setBelegfeld1(Integer.parseInt(canceledDocNo));

				} else {

					final IInvoicePA invoicePA = Services.get(IInvoicePA.class);
					final I_C_Invoice invoice = invoicePA.retrieveInvoice(
							debitorAcct.getRecord_ID(), trxName);

					final int docNo1 = FactAcctTool.getDocNoInt(invoice
							.getDocumentNo());
					dataRecord.setBelegfeld1(docNo1);
			}
				dataRecord.setBuchungstext(debitor.getName());

				// XXX a42 - AK - VAT-ID
				String taxID = debitor.getTaxID();
				if (taxID != null) {
					String taxCountry = taxID.substring(0, 2);
					String taxNo = taxID.substring(2, taxID.length());
					taxNo = taxNo.replaceAll(" ", "");
					if (taxNo.length() > 13) {
						taxNo = taxNo.substring(0,13);
					}
					if (taxNo.length() < 13) {
						for (int i = taxNo.length(); i < 13; i++) {
							taxNo = taxNo + ' ';
						}
					}
					dataRecord.setEU_Id(taxCountry, taxNo);
				}
				// end a42

				CSV_Verwaltungsdatei.validate(dataRecord);

				resultPerTaxId.add(dataRecord);

				final I_C_Datev_ExportLog exportLog = createLogRecord(trxName,
						"fact_acct", FactAcctTool.getDocNr(debitorAcct
								.getRecord_ID(), debitorAcct.getDescription()),
						revenueAcct.getRecord_ID(), revenueAcct.getDateAcct());

				if (!id2LogRecord.containsKey(debitorAcct.getRecord_ID())) {
					id2LogRecord.put(debitorAcct.getRecord_ID(), exportLog);
				}
			}

			final List<CSV_Bewegungsdaten_Buchungssatz> compressedResult = FactAcctTool
					.compressCSV(resultPerTaxId);
            // a42 - AK - FIXME - Ticket #15 compare must be per user1_id + per tax_id
			if (!resultPerTaxId.isEmpty() && compressedResult.size() != 1) {
				throw new IllegalStateException(
						"After compression there are still "
								+ compressedResult.size()
								+ " records for taxId " + taxId);
			}

			//
			// Get the tax info
			final I_Fact_Acct taxAcct = findTaxAcct(taxAccts, taxId);
			if (taxAcct != null) {

				final Object[] turnOverAmtAndDevToRev = getTurnOverAmtAndDebToRev(
						taxAcct.getAmtAcctCr(), taxAcct.getAmtAcctDr());

				if (turnOverAmtAndDevToRev.length == 0) {
					continue;
				}
				final BigDecimal turnOverAmount = (BigDecimal) turnOverAmtAndDevToRev[0];

				final CSV_Bewegungsdaten_Buchungssatz comprRecord = compressedResult
						.get(0);
				comprRecord.setUmsatz(comprRecord.getUmsatz()
						+ turnOverAmount.doubleValue());
			}
			result.addAll(compressedResult);
		}
		return result;
	}

	private static Object[] getTurnOverAmtAndDebToRev(
			final BigDecimal revCredit, final BigDecimal revDebit) {

		BigDecimal turnOverAmount = null;
		boolean deb2rev = false;
		if (revCredit.signum() != 0) {

			turnOverAmount = revCredit;
			if (turnOverAmount.doubleValue() > 0) {
				deb2rev = true;
			} else {
				turnOverAmount = turnOverAmount.negate();
				deb2rev = false;
			}
		} else if (revDebit.signum() != 0) {

			turnOverAmount = revDebit;
			if (turnOverAmount.signum() > 0) {
				deb2rev = false;
			} else {
				turnOverAmount = turnOverAmount.negate();
				deb2rev = true;
			}
		} else {
			return new Object[0];
		}

		return new Object[] { turnOverAmount, deb2rev };
	}

	private static Map<Integer, Set<I_Fact_Acct>> sortByTaxId(
			final Set<I_Fact_Acct> accts) {

		final Map<Integer, Set<I_Fact_Acct>> result = new HashMap<Integer, Set<I_Fact_Acct>>();

		if (accts != null)
		for (final I_Fact_Acct acct : accts) {

			Set<I_Fact_Acct> set = result.get(acct.getC_Tax_ID());
			if (set == null) {
				set = new HashSet<I_Fact_Acct>();
				result.put(acct.getC_Tax_ID(), set);
			}
			set.add(acct);
		}

		return result;
	}

	private static I_Fact_Acct findTaxAcct(final Set<I_Fact_Acct> accts,
			final int taxId) {

		if (taxId == 0 || accts == null) {
			return null;
		}

		for (final I_Fact_Acct factAcct : accts) {

			if (factAcct.getC_Tax_ID() == taxId) {
				return factAcct;
			}
		}
		return null;
	}

	private I_C_Datev_ExportLog createLogRecord(final String trxName,
			String tableName, String documentNo, int recordId,
			final Timestamp dateAcct) {

		final IAccountingPA accountingPA = Services.get(IAccountingPA.class);

		final I_C_Datev_ExportLog exportLogEntry = accountingPA
				.createDatevExportLog(orgId, trxName);

		if (exportStartTime == null) {
			// setting the log entry here is just a workaround
			exportLogEntry.setDateExp(SystemTime.asTimestamp());
		} else {
			exportLogEntry.setDateExp(exportStartTime);
		}
		exportLogEntry.setDocumentNo(documentNo);
		exportLogEntry.setDateAcct(dateAcct);

		return exportLogEntry;
	}

	public synchronized void exportData() {

		if (processor == null) {
			throw new IllegalStateException("processor may not be null");
		}
		if (loader == null) {
			throw new IllegalStateException("loader may not be null");
		}
		exportStartTime = SystemTime.asTimestamp();

		final String trxName = "dateExport_" + Long.toString(exportStartTime.getTime());

		// Part2: Get the data, and insert them into our datev api's objects
		//
		loader.load(dateFrom, dateTo, exportAP, exportAR, trxName);

		final Map<Integer, Map<String, Set<I_Fact_Acct>>> recordId2FactAccts = loader
				.getResult();

		for (final int recordId : recordId2FactAccts.keySet()) {

			final Set<I_Fact_Acct> assetAccts = recordId2FactAccts.get(
					recordId).get(X_C_ElementValue.ACCOUNTTYPE_Asset);

			final Set<I_Fact_Acct> revenueAccts = recordId2FactAccts.get(
					recordId).get(X_C_ElementValue.ACCOUNTTYPE_Revenue);

			final Set<I_Fact_Acct> liabilityAccts = recordId2FactAccts.get(
					recordId).get(X_C_ElementValue.ACCOUNTTYPE_Liability);

			final Set<I_Fact_Acct> expenseAccts = recordId2FactAccts.get(
					recordId).get(X_C_ElementValue.ACCOUNTTYPE_Expense);

			// ARL - Invoice Customer
			if (assetAccts != null && assetAccts.size() == 1 && liabilityAccts != null && liabilityAccts.size() == 1 && revenueAccts != null && revenueAccts.size() >= 1) {

				// there is one debitor record. There might be one tax
				// record (unless we ship to switzerland etc) and at least one
				// revenue record. We only need the revenue record(s)
				final I_Fact_Acct debitorAcct = assetAccts.iterator().next();

				if (!loader.getDocNr2FactAccts().containsKey(
						FactAcctTool.getDocNr(debitorAcct.getRecord_ID(),
								debitorAcct.getDescription()))) {

					// this record has been removed
					continue;
				}

				for (final OBE_Bewegungsdaten_Buchungssatz dataRecord : createFactAcctRecords(
						trxName, debitorAcct, revenueAccts, liabilityAccts)) {

					bewegungsSatzFileInfos.get(KEY).addDataRecord(dataRecord);
				}
			}
			 
			// Invoice Vendor  - EAL / AAL or combination
						if (assetAccts != null && liabilityAccts != null && revenueAccts == null && assetAccts.size() >= 1 && liabilityAccts.size() == 1) {

							// there is one debitor (creditor) record. There might be one tax
							// record (unless we ship to switzerland etc) and at least one
							// liability record. We only need the liability record(s)
							final I_Fact_Acct creditorAcct = liabilityAccts.iterator().next();

							if (!loader.getDocNr2FactAccts().containsKey(
									FactAcctTool.getDocNr(creditorAcct.getRecord_ID(),
											creditorAcct.getDescription()))) {

								// this record has been removed
								continue;
							}
							// debitorAcct (creditorAcct), revenueAccts(non-tax assetAccts + expenseAccts), taxAccts(tax assetAccts)
							// create set and remove non-tax factAcct entries - having UoM and qty
							Set<I_Fact_Acct> taxAccts = assetAccts;
							Iterator<I_Fact_Acct> itrTax = taxAccts.iterator();
							while (itrTax.hasNext()) {
								I_Fact_Acct entry = itrTax.next();
								if (entry.getC_UOM_ID() > 0 && entry.getQty().compareTo(Env.ZERO) != 0) {
									itrTax.remove();
								}
							}
							// create set and remove tax related entries - having no UoM and no qty 
							Set<I_Fact_Acct> revAccts = assetAccts;
							Iterator<I_Fact_Acct> itrRev = revAccts.iterator();
							while (itrRev.hasNext()) {
								I_Fact_Acct entry = itrRev.next();
								if (entry.getC_UOM_ID() == 0 && entry.getQty().compareTo(Env.ZERO) == 0) {
									itrRev.remove();
								}
							}
							// add expense entries
							if (expenseAccts != null) {
								Iterator<I_Fact_Acct> itrExp = expenseAccts.iterator();
								while (itrExp.hasNext()) {
									I_Fact_Acct entry = itrExp.next();
									revAccts.add(entry);
								}
							}

							for (final OBE_Bewegungsdaten_Buchungssatz dataRecord : createFactAcctRecords(
									trxName, creditorAcct, revAccts, taxAccts)) {

								bewegungsSatzFileInfos.get(KEY).addDataRecord(dataRecord);
							}
						}
			
						// Invoice Vendor  - EAL / AAL or combination - Erstattung?!
						if (expenseAccts != null && liabilityAccts != null && revenueAccts == null && liabilityAccts.size() == 1) {

							// there is one debitor (creditor) record. There might be one tax
							// record (unless we ship to switzerland etc) and at least one
							// liability record. We only need the liability record(s)
							final I_Fact_Acct creditorAcct = liabilityAccts.iterator().next();

							if (!loader.getDocNr2FactAccts().containsKey(
									FactAcctTool.getDocNr(creditorAcct.getRecord_ID(),
											creditorAcct.getDescription()))) {

								// this record has been removed
								continue;
							}
							// debitorAcct (creditorAcct), revenueAccts(non-tax assetAccts + expenseAccts), taxAccts(tax assetAccts)
							// create set and remove non-tax factAcct entries - having UoM and qty
							Set<I_Fact_Acct> revAccts = expenseAccts;
							// add expense entries
							/*
								Iterator<I_Fact_Acct> itrExp = expenseAccts.iterator();
								while (itrExp.hasNext()) {
									I_Fact_Acct entry = itrExp.next();
									revAccts.add(entry);
								}
							*/
							
							for (final OBE_Bewegungsdaten_Buchungssatz dataRecord : createFactAcctRecords(
									trxName, creditorAcct, revAccts, null)) {

								bewegungsSatzFileInfos.get(KEY).addDataRecord(dataRecord);
							}
						}

		}

		logger.info("Created " + id2LogRecord.size() + " records");

		//
		// insert the exported records into our log table
		for (final I_C_Datev_ExportLog datevExportLog : id2LogRecord.values()) {
			Services.get(IPOService.class).save(datevExportLog, trxName);
		}

		bewegungsSatzFileInfos.put(KEY, processor
				.process(getBewegungsSatzFileInfo(KEY)));

		//
		// End of Part2
		// Part3: iterate through the data and write it to the data
		// file(s)
		//
		short currentFileNumber = 0;
		exportedRecords = 0;

		Collection<DatensatzFileInfo> allFileInfos = new ArrayList<DatensatzFileInfo>(
				bewegungsSatzFileInfos.values());
		allFileInfos.addAll(masterDataService.getData());

		for (DatensatzFileInfo currentFileInfo : allFileInfos) {

			final List<OBE_Buchungssatz> records = new ArrayList<OBE_Buchungssatz>(
					currentFileInfo.getDataRecords());
			if (records.isEmpty()) {
				continue;
			}

			Collections.sort(records, acctRecordComp);

			exportedRecords += records.size();
			currentFileNumber++;
			currentFileInfo.setFileNumber(currentFileNumber);

			currentFileInfo.getFile().writeVorlaufsatz(
					currentFileInfo.getFileHeader());

			for (OBE_Buchungssatz dataRecord : records) {
				currentFileInfo.getFile().appendBuchungssatz(dataRecord);
			}
			currentFileInfo.getFile().finish();
		}

		if (exportedRecords == 0) {
			return;
		}

		// Set up and write the index file's header
		final OBE_Datentraegerkennsatz indexHeader = new OBE_Datentraegerkennsatz();

		indexHeader.anzahlDatendateien = currentFileNumber;
		indexHeader.letzteDatendatei = currentFileNumber;
		indexHeader.beraternummer = settings.getBeraternummer();
		indexHeader.datentraegernummer = settings.getDatentraegernummer();
		indexHeader.beratername = settings.getBeratername();

		final OBE_Verwaltungsdatei indexFile = new OBE_Verwaltungsdatei(
				outputDir);
		indexFile.writeKennsatz(indexHeader);

		for (DatensatzFileInfo currentDataFileInfo : allFileInfos) {

			if (currentDataFileInfo.getDataRecords().isEmpty()) {
				continue;
			}
			OBE_Verwaltungssatz indexRecord;
			if (currentDataFileInfo instanceof StammdatensatzFileInfo) {
				indexRecord = new OBE_Verwaltungssatz(
						OBE_Verwaltungssatz.Type.STAMMDATEN);
			} else {
				indexRecord = new OBE_Verwaltungssatz(
						OBE_Verwaltungssatz.Type.BEWEGUNGSDATEN);
			}
			indexRecord.verarbeitungsKennzeichen = "V";
			indexRecord.dateiNummer = currentDataFileInfo.getFileNumber();
			indexRecord.vorlaufinformationen = currentDataFileInfo
					.getFileHeader().getVorlaufinformationen();
			indexRecord.letzteBlockNummer = currentDataFileInfo.getBlockCount();
			indexRecord.letzteBlockPos = currentDataFileInfo.getLastBlockPos();
			indexRecord.letztePrimanotaSeite = 1;
			indexRecord.korrekturKennzeichen = " ";

			indexFile.writeVerwaltunssatz(indexRecord);
		}

		indexFile.finish();

		try {
			DB.commit(true, trxName);
		} catch (SQLException e) {
			LOG.log(Level.SEVERE,
					"Storing of the exported records in log table failed.", e);
			try {
				DB.rollback(false, trxName);
			} catch (SQLException e1) {
			}
			throw new DatevException(e);
		}
	}

	public synchronized void exportDataCSV() {

		if (processor == null) {
			throw new IllegalStateException("processor may not be null");
		}
		if (loader == null) {
			throw new IllegalStateException("loader may not be null");
		}
		exportStartTime = SystemTime.asTimestamp();

		// a42 / AK - #120 trxName too short
		final String trxName = "dateExport_" + Long.toString(exportStartTime.getTime()) + "_" + UUID.randomUUID().toString();

		// Part2: Get the data, and insert them into our datev api's objects
		//
		loader.load(dateFrom, dateTo, exportAP, exportAR, trxName);

		final Map<Integer, Map<String, Set<I_Fact_Acct>>> recordId2FactAccts = loader
				.getResult();

		for (final int recordId : recordId2FactAccts.keySet()) {

			final Set<I_Fact_Acct> assetAccts = recordId2FactAccts.get(
					recordId).get(X_C_ElementValue.ACCOUNTTYPE_Asset);

			final Set<I_Fact_Acct> revenueAccts = recordId2FactAccts.get(
					recordId).get(X_C_ElementValue.ACCOUNTTYPE_Revenue);

			final Set<I_Fact_Acct> liabilityAccts = recordId2FactAccts.get(
					recordId).get(X_C_ElementValue.ACCOUNTTYPE_Liability);

			final Set<I_Fact_Acct> expenseAccts = recordId2FactAccts.get(
					recordId).get(X_C_ElementValue.ACCOUNTTYPE_Expense);

			/**
			 * From Doc_Invoice.java
			 *
			 *  Create Facts (the accounting logic) for
			 *  ARI, ARC, ARF, API, APC.
			 *  <pre>
			 *  ARI Invoice (Customer), ARF ProForma 
			 *      Receivables     DR
			 *      Charge                  CR
			 *      TaxDue                  CR
			 *      Revenue                 CR
			 *
			 *  ARC Credit Note (Customer)
			 *      Receivables             CR
			 *      Charge          DR
			 *      TaxDue          DR
			 *      Revenue         RR
			 *
			 *  API Invoice (Vendor)
			 *      Payables                CR
			 *      Charge          DR
			 *      TaxCredit       DR
			 *      Expense         DR
			 *
			 *  APC CreditNote (Vendor)
			 *      Payables        DR
			 *      Charge                  CR
			 *      TaxCredit               CR
			 *      Expense                 CR
			 *  </pre>
			 */

			MInvoice invoice = new MInvoice(Env.getCtx(), recordId, trxName);
			// ARL - Invoice Customer
			if (invoice.isSOTrx()) {
			//if (assetAccts != null && assetAccts.size() == 1 && liabilityAccts != null && liabilityAccts.size() == 1 && revenueAccts != null && revenueAccts.size() >= 1) {

				// there is one debitor record. There might be one tax
				// record (unless we ship to switzerland etc) and at least one
				// revenue record. We only need the revenue record(s)
				if (assetAccts == null) {
					// ignore - something is wrong in this invoice
					continue;
				}

				final I_Fact_Acct debitorAcct = assetAccts.iterator().next();

				if (!loader.getDocNr2FactAccts().containsKey(
						FactAcctTool.getDocNr(debitorAcct.getRecord_ID(),
								debitorAcct.getDescription()))) {

					// this record has been removed
					continue;
				}

				for (final CSV_Bewegungsdaten_Buchungssatz dataRecord : createFactAcctRecordsCSV(
						trxName, debitorAcct, revenueAccts, liabilityAccts)) {

					bewegungsSatzFileInfosCSV.get(KEY).addDataRecordCSV(dataRecord);
				}
			}

			// Invoice Vendor  - EAL / AAL or combination
			else if (!invoice.isSOTrx()) {
			//else if (assetAccts != null && liabilityAccts != null && revenueAccts == null && assetAccts.size() >= 1 && liabilityAccts.size() == 1) {

				// there is one debitor (creditor) record. There might be one tax
				// record (unless we ship to switzerland etc) and at least one
				// liability record. We only need the liability record(s)
				if (liabilityAccts == null) {
					// ignore - something is wrong in this invoice
					continue;
				}
				
				final I_Fact_Acct creditorAcct = liabilityAccts.iterator().next();

				if (!loader.getDocNr2FactAccts().containsKey(
						FactAcctTool.getDocNr(creditorAcct.getRecord_ID(),
								creditorAcct.getDescription()))) {

					// this record has been removed
					continue;
				}
				// debitorAcct (creditorAcct), revenueAccts(non-tax assetAccts + expenseAccts), taxAccts(tax assetAccts)
				// create set and remove non-tax factAcct entries - having UoM and qty
				Set<I_Fact_Acct> taxAccts = new HashSet<I_Fact_Acct>();
				Set<I_Fact_Acct> revAccts = new HashSet<I_Fact_Acct>();
				if (assetAccts != null) {
					taxAccts = assetAccts;
					Iterator<I_Fact_Acct> itrTax = taxAccts.iterator();
					while (itrTax.hasNext()) {
						I_Fact_Acct entry = itrTax.next();
						if (entry.getC_UOM_ID() > 0 && entry.getQty().compareTo(Env.ZERO) != 0) {
							itrTax.remove();
						}
					}
					// create set and remove tax related entries - having no UoM and no qty 
					revAccts = assetAccts;
					Iterator<I_Fact_Acct> itrRev = revAccts.iterator();
					while (itrRev.hasNext()) {
						I_Fact_Acct entry = itrRev.next();
						if (entry.getC_UOM_ID() == 0 && entry.getQty().compareTo(Env.ZERO) == 0) {
							itrRev.remove();
						}
					}
				}
				// add expense entries
				if (expenseAccts != null) {
					Iterator<I_Fact_Acct> itrExp = expenseAccts.iterator();
					while (itrExp.hasNext()) {
						I_Fact_Acct entry = itrExp.next();
						revAccts.add(entry);
					}
				}

				for (final CSV_Bewegungsdaten_Buchungssatz dataRecord : createFactAcctRecordsCSV(
						trxName, creditorAcct, revAccts, taxAccts)) {

					bewegungsSatzFileInfosCSV.get(KEY).addDataRecordCSV(dataRecord);
				}
			}

//			// Invoice Vendor  - EAL / AAL or combination - Erstattung?!
//			else if (expenseAccts != null && liabilityAccts != null && revenueAccts == null && liabilityAccts.size() == 1) {
//
//				// there is one debitor (creditor) record. There might be one tax
//				// record (unless we ship to switzerland etc) and at least one
//				// liability record. We only need the liability record(s)
//				final I_Fact_Acct creditorAcct = liabilityAccts.iterator().next();
//
//				if (!loader.getDocNr2FactAccts().containsKey(
//						FactAcctTool.getDocNr(creditorAcct.getRecord_ID(),
//								creditorAcct.getDescription()))) {
//
//					// this record has been removed
//					continue;
//				}
//				// debitorAcct (creditorAcct), revenueAccts(non-tax assetAccts + expenseAccts), taxAccts(tax assetAccts)
//				// create set and remove non-tax factAcct entries - having UoM and qty
//				Set<I_Fact_Acct> revAccts = expenseAccts;
//				// add expense entries
//				/*
//								Iterator<I_Fact_Acct> itrExp = expenseAccts.iterator();
//								while (itrExp.hasNext()) {
//									I_Fact_Acct entry = itrExp.next();
//									revAccts.add(entry);
//								}
//				 */
//
//				for (final CSV_Bewegungsdaten_Buchungssatz dataRecord : createFactAcctRecordsCSV(
//						trxName, creditorAcct, revAccts, null)) {
//
//					bewegungsSatzFileInfosCSV.get(KEY).addDataRecordCSV(dataRecord);
//				}
//			}
			else {
				throw new IllegalStateException("Record is not an Invoice");
			}

		}

		logger.info("Created " + id2LogRecord.size() + " records");

		//
		// insert the exported records into our log table
		for (final I_C_Datev_ExportLog datevExportLog : id2LogRecord.values()) {
			Services.get(IPOService.class).save(datevExportLog, trxName);
		}

		bewegungsSatzFileInfosCSV.put(KEY, processor
				.processCSV(getBewegungsSatzFileInfoCSV(KEY)));

		//
		// End of Part2
		// Part3: iterate through the data and write it to the data
		// file(s)
		//
		//short currentFileNumber = 1;
		exportedRecords = 0;

		Collection<DatensatzFileInfoCSV> allFileInfos = new ArrayList<DatensatzFileInfoCSV>(
				bewegungsSatzFileInfosCSV.values());
		allFileInfos.addAll(masterDataService.getDataCSV());

		for (DatensatzFileInfoCSV currentFileInfo : allFileInfos) {

			final List<CSV_Buchungssatz> records = new ArrayList<CSV_Buchungssatz>(
					currentFileInfo.getDataRecordsCSV());
			if (records.isEmpty()) {
				continue;
			}

			Collections.sort(records, acctRecordCompCSV);

			exportedRecords += records.size();
//			currentFileNumber++;
//			currentFileInfo.setFileNumber(currentFileNumber);
//
			currentFileInfo.getFileCSV(dateFrom).writeVorlaufsatz(
					currentFileInfo.getFileHeaderCSV());

			for (CSV_Buchungssatz dataRecord : records) {
				currentFileInfo.getFileCSV(dateFrom).appendBuchungssatz(dataRecord);
			}
			currentFileInfo.getFileCSV(dateFrom).finish();
		}

		if (exportedRecords == 0) {
			return;
		}

//		// Set up and write the index file's header
//		final CSV_Datentraegerkennsatz indexHeader = new CSV_Datentraegerkennsatz();
//
//		indexHeader.anzahlDatendateien = currentFileNumber;
//		indexHeader.letzteDatendatei = currentFileNumber;
//		indexHeader.beraternummer = settings.getBeraternummer();
//		indexHeader.datentraegernummer = settings.getDatentraegernummer();
//		indexHeader.beratername = settings.getBeratername();
//
//		final CSV_Verwaltungsdatei indexFile = new CSV_Verwaltungsdatei(
//				outputDir);
//		indexFile.writeKennsatz(indexHeader);
//
//		for (DatensatzFileInfoCSV currentDataFileInfo : allFileInfos) {
//
//			if (currentDataFileInfo.getDataRecordsCSV().isEmpty()) {
//				continue;
//			}
//			CSV_Verwaltungssatz indexRecord;
//			if (currentDataFileInfo instanceof StammdatensatzFileInfoCSV) {
//				indexRecord = new CSV_Verwaltungssatz(
//						CSV_Verwaltungssatz.Type.STAMMDATEN);
//			} else {
//				indexRecord = new CSV_Verwaltungssatz(
//						CSV_Verwaltungssatz.Type.BEWEGUNGSDATEN);
//			}
//			indexRecord.verarbeitungsKennzeichen = "V";
//			indexRecord.dateiNummer = currentDataFileInfo.getFileNumber();
//			indexRecord.vorlaufinformationen = currentDataFileInfo
//					.getFileHeaderCSV().getVorlaufinformationen();
//			indexRecord.letzteBlockNummer = currentDataFileInfo.getBlockCount();
//			indexRecord.letzteBlockPos = currentDataFileInfo.getLastBlockPos();
//			indexRecord.letztePrimanotaSeite = 1;
//			indexRecord.korrekturKennzeichen = " ";
//
//			indexFile.writeVerwaltunssatz(indexRecord);
//		}
//
//		indexFile.finish();

		try {
			DB.commit(true, trxName);
		} catch (SQLException e) {
			LOG.log(Level.SEVERE,
					"Storing of the exported records in log table failed.", e);
			try {
				DB.rollback(false, trxName);
			} catch (SQLException e1) {
			}
			throw new DatevException(e);
		}
	}

	public BewegungssatzFileInfoCSV getBewegungsSatzFileInfoCSV(final String key) {
		return bewegungsSatzFileInfosCSV.get(key);
	}

	public BewegungssatzFileInfo getBewegungsSatzFileInfo(final String key) {
		return bewegungsSatzFileInfos.get(key);
	}

	public int getExportedRecords() {
		return exportedRecords;
	}

	/**
	 * Prepares the DATEV output files and objects (doesn't write anything to
	 * the file system yet!)
	 */
	private void init() {

		final int currentYear = new GregorianCalendar().get(GregorianCalendar.YEAR);

		OBE_Vorlaufinformationen commonHeaderInfo = new OBE_Vorlaufinformationen();
		commonHeaderInfo.setAnwendungsnummer(OBE_Vorlaufinformationen.Anwendungsnummer.FIBUOPOS_VOLLVORL);
		commonHeaderInfo.namenskuerzel = settings.getNamenskuerzel();
		commonHeaderInfo.setBeraternummer(settings.getBeraternummer());
		commonHeaderInfo.setMandantennummer(settings.getMandantennummer());
		commonHeaderInfo.setAbrechnungsnummer(settings.getAbrechnungsnummer(),
				currentYear);

		commonHeaderInfo.setDatumVon(dateFrom);
		commonHeaderInfo.setDatumBis(dateTo);
		commonHeaderInfo.setPrimanotaSeite(settings.getPrimanotaseite());
		commonHeaderInfo.passwort = settings.getPasswort();

		OBE_Bewegungsdaten_Vollvorlauf dataFileHeader = new OBE_Bewegungsdaten_Vollvorlauf();
		dataFileHeader.datentraegernummer = settings.getDatentraegernummer();
		dataFileHeader.setVorlaufinformationen(commonHeaderInfo);

		BewegungssatzFileInfo accountingDataFileInfo = new BewegungssatzFileInfo(
				outputDir);
		accountingDataFileInfo.setFileHeader(dataFileHeader);

		bewegungsSatzFileInfos.put(KEY, accountingDataFileInfo);

		//CSV
		CSV_Vorlaufinformationen commonHeaderInfoCSV = new CSV_Vorlaufinformationen();
		commonHeaderInfoCSV.setAnwendungsnummer(CSV_Vorlaufinformationen.Anwendungsnummer.FIBUOPOS_VOLLVORL);
		commonHeaderInfoCSV.namenskuerzel = settings.getNamenskuerzel();
		commonHeaderInfoCSV.setBeraternummer(settings.getBeraternummer());
		commonHeaderInfoCSV.setMandantennummer(settings.getMandantennummer());
		commonHeaderInfoCSV.setAbrechnungsnummer(settings.getAbrechnungsnummer(),
				currentYear);

		commonHeaderInfoCSV.setDatumVon(dateFrom);
		commonHeaderInfoCSV.setDatumBis(dateTo);
		commonHeaderInfoCSV.setPrimanotaSeite(settings.getPrimanotaseite());
		commonHeaderInfoCSV.passwort = settings.getPasswort();

		CSV_Bewegungsdaten_Vollvorlauf dataFileHeaderCSV = new CSV_Bewegungsdaten_Vollvorlauf();
		dataFileHeaderCSV.datentraegernummer = settings.getDatentraegernummer();
		dataFileHeaderCSV.setVorlaufinformationen(commonHeaderInfoCSV);

		BewegungssatzFileInfoCSV accountingDataFileInfoCSV = new BewegungssatzFileInfoCSV(
				outputDir);
		accountingDataFileInfoCSV.setFileHeaderCSV(dataFileHeaderCSV);

		bewegungsSatzFileInfosCSV.put(KEY, accountingDataFileInfoCSV);


		// no files created yet...
	}

	public void setLoader(IFactAcctLoader loader) {
		this.loader = loader;
	}

	public void setMasterDataService(IMasterDataService masterDataService) {
		this.masterDataService = masterDataService;
	}

	public void setProcessor(IBewegungsSatzProcessor processor) {
		this.processor = processor;
	}

};
