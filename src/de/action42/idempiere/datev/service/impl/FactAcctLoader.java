package de.action42.idempiere.datev.service.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.compiere.model.I_Fact_Acct;
import org.compiere.model.MElementValue;
import org.compiere.model.MFactAcct;
import org.compiere.model.X_C_ValidCombination;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;

import de.action42.idempiere.datev.DatevException;
import de.action42.idempiere.datev.service.IFactAcctLoader;
import de.action42.idempiere.datev.service.IMasterDataService;
import de.action42.idempiere.datev.util.FactAcctTool;
import de.action42.idempiere.model.MDatevExportLog;

public class FactAcctLoader implements IFactAcctLoader {

	private static final CLogger LOG = CLogger.getCLogger(FactAcctLoader.class);

	private final int clientId, orgId;

	private Map<String, Set<I_Fact_Acct>> docNr2FactAccts = new HashMap<String, Set<I_Fact_Acct>>();

	private final IMasterDataService masterDataService;

	private Map<Integer, Map<String, Set<I_Fact_Acct>>> recordId2FactAccts = new HashMap<Integer, Map<String, Set<I_Fact_Acct>>>();

	public FactAcctLoader(final int client, final int orgId,
			final IMasterDataService masterDataService) {

		this.clientId = client;
		this.orgId = orgId;
		this.masterDataService = masterDataService;
	}

	public Map<String, Set<I_Fact_Acct>> getDocNr2FactAccts() {
		return Collections.unmodifiableMap(docNr2FactAccts);
	}

	public Map<Integer, Map<String, Set<I_Fact_Acct>>> getResult() {
		return recordId2FactAccts;
	}

	public void load(final Timestamp dateFrom, final Timestamp dateTo, 
			final boolean exportAP, final boolean exportAR, 
			final String trxName) {

		final HashSet<String> alreadyExported = loadAlreadyExported(dateFrom,
				dateTo, trxName);

		// get the data and put them into our hash tables
		loadFactActData(dateFrom, dateTo, exportAP, exportAR, trxName);

		// remove records that belong to cancellations
		removeNonExportableFactAccts(alreadyExported, trxName);
	}

	/**
	 * 
	 * @param trxName
	 * @param tableName
	 * @return a hash set with the document numbers of all records that have
	 *         already been exported.
	 */
	private HashSet<String> loadAlreadyExported(final Timestamp dateFrom,
			final Timestamp dateTo, final String trxName) {

		final HashSet<String> alreadyExportedDocNrs = new HashSet<String>();

		//
		// Remove the records that already have been exported
		final String exportLog_sql = "SELECT l.* "
				+ "FROM c_datev_exportlog l ";

		final PreparedStatement pstmt = DB.prepareStatement(exportLog_sql,
				trxName);
		ResultSet rs = null;
		try {

			rs = pstmt.executeQuery();

			while (rs.next()) {

				final MDatevExportLog datevLog = new MDatevExportLog(Env
						.getCtx(), rs, trxName);

				// Tell the master data exporter about an already exported
				// bPartnerId
				masterDataService.addAlreadyExportedBPartnerId(datevLog
						.getC_BPartner_ID());

				if (datevLog.getDateAcct().getTime() >= dateFrom.getTime()
						&& datevLog.getDateAcct().getTime() <= dateTo.getTime()) {

					alreadyExportedDocNrs.add(datevLog.getDocumentNo());
				}
			}

		} catch (SQLException e) {
			LOG
					.log(
							Level.SEVERE,
							"Exception while trying to load log of already exported records.",
							e);
			throw new DatevException(
					"Exception while trying to load log of already exported records. See issue log for details.",
					e);
		} finally {
			DB.close(rs, pstmt);
		}
		LOG.info("Loaded document numbers of " + alreadyExportedDocNrs.size()
				+ " records that have already been exported earlier");
		return alreadyExportedDocNrs;
	}

	/**
	 * Gets records from the fact_acct database table out of the db and stores
	 * then in docNr2FactAccts for later processing.
	 * @param exportAP 
	 * @param exportAR 
	 * 
	 * @param trxName
	 * @throws DatevException
	 *             if any exception is caught in this method.
	 */
	private void loadFactActData(final Timestamp dateFrom, final Timestamp dateTo, 
			boolean exportAP, boolean exportAR, final String trxName) throws DatevException {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT f.* ")
				.append("FROM fact_acct f LEFT JOIN ad_table t on f.ad_table_id=t.ad_table_id ")
				.append("LEFT JOIN C_Invoice i ON (f.RECORD_ID=i.C_INVOICE_ID) ") 
				.append("WHERE t.tablename='C_Invoice' ")
				.append(" AND f.ad_client_id=").append(clientId)
				.append(" AND (0=").append(orgId)
				.append(" OR f.ad_org_id=").append(orgId)
				.append(") AND f.dateacct BETWEEN ? AND ?")
				.append(" AND i.ISSOTRX='Y'") 
				.append(" AND 'Y'='").append(exportAR ? "Y" : "N").append("' ") 
				.append("UNION ")
				.append("SELECT f.* ") 
				.append("FROM fact_acct f LEFT JOIN ad_table t on f.ad_table_id=t.ad_table_id ")
				.append("LEFT JOIN C_Invoice i ON (f.RECORD_ID=i.C_INVOICE_ID) ") 
				.append("WHERE t.tablename='C_Invoice' ")
				.append(" AND f.ad_client_id=").append(clientId)
				.append(" AND (0=").append(orgId)
				.append(" OR f.ad_org_id=").append(orgId)
				.append(") AND f.dateacct BETWEEN ? AND ?")
				.append(" AND i.ISSOTRX='N' ")
				.append(" AND 'Y'='").append(exportAP ? "Y" : "N").append("' "); 
		String factAcctSql = sql.toString();
		PreparedStatement pstmt = null;
		try {
			pstmt = DB.prepareStatement(factAcctSql, trxName);
			pstmt.setTimestamp(1, dateFrom);
			pstmt.setTimestamp(2, dateTo);
			pstmt.setTimestamp(3, dateFrom);
			pstmt.setTimestamp(4, dateTo);

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				final MFactAcct factAcct = new MFactAcct(Env.getCtx(), rs,
						trxName);

				//
				// Add the factacct to docNr2FactAccts.
				// (will be used in the method removeCancellations)
				final String documentNr = FactAcctTool.getDocNr(factAcct
						.getRecord_ID(), factAcct.getDescription());
				Set<I_Fact_Acct> factAccts = docNr2FactAccts.get(documentNr);
				if (factAccts == null) {
					factAccts = new HashSet<I_Fact_Acct>();
					docNr2FactAccts.put(documentNr, factAccts);
				}
				factAccts.add(factAcct);

				//
				// Add the factacct to recordId2FactAccts.
				// (will be used in the method createFactAcctAccounts)
				Map<String, Set<I_Fact_Acct>> accountType2FactAccts = recordId2FactAccts
						.get(factAcct.getRecord_ID());

				if (accountType2FactAccts == null) {
					accountType2FactAccts = new HashMap<String, Set<I_Fact_Acct>>();

					recordId2FactAccts.put(factAcct.getRecord_ID(),
							accountType2FactAccts);
				}

				
				// XXX AK change to use RevenueAcct from Tax if applicable
// 				final MTax debitorTaxInfo = new MTax(Env
//						.getCtx(), factAcct.getC_Tax_ID(), trxName);
				MElementValue debitorAcctInfo = null;
				if (factAcct.getC_Tax_ID() > 0) {
					final String taxAcct_sql = "SELECT XX_Revenue_Acct "
						+ "FROM C_Tax_Acct " // 1
						+ "WHERE C_Tax_ID=? "  // 2
						+ " AND ad_client_id=? ";

					int validCombinationID = 0;

					PreparedStatement pstmt1 = null;
					try {
						pstmt1 = DB.prepareStatement(taxAcct_sql, trxName);
						pstmt1.setLong(1, factAcct.getC_Tax_ID());
						pstmt1.setLong(2, clientId);

						ResultSet rs1 = pstmt1.executeQuery();

						while (rs1.next()) {
							validCombinationID = rs1.getInt(1);

						}
						rs1.close();
						pstmt1.close();
						pstmt1 = null;
					} catch (Exception e) {
						LOG.log(Level.SEVERE, "Retrieval of booking data failed. SQL: "
								+ taxAcct_sql, e);
						throw new DatevException(
						"Retrieval of booking data failed. See issue log for details.");
					}

					final X_C_ValidCombination validCombination = new X_C_ValidCombination(Env.getCtx(), validCombinationID, trxName);

					debitorAcctInfo = new MElementValue(Env
							.getCtx(), validCombination.getAccount_ID(), trxName);
					factAcct.setAccount_ID(validCombination.getAccount_ID());
					// End AK
				}
				else {
					debitorAcctInfo = new MElementValue(Env
						.getCtx(), factAcct.getAccount_ID(), trxName);
				}

				factAccts = accountType2FactAccts.get(debitorAcctInfo
						.getAccountType());
				if (factAccts == null) {
					factAccts = new HashSet<I_Fact_Acct>();
					accountType2FactAccts.put(debitorAcctInfo.getAccountType(),
							factAccts);
				}
				factAccts.add(factAcct);
			}
			rs.close();
			pstmt.close();
			pstmt = null;
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Retrieval of booking data failed. SQL: "
					+ factAcctSql, e);
			throw new DatevException(
					"Retrieval of booking data failed. See issue log for details.");
		}
	}

	/**
	 * Removes records that shall not be exported for one of these reasons:
	 * <ul>
	 * <li>The record has already been exported and is thus logged in the table
	 * C_DATEV_EXPORTLOG</li>
	 * <li>The record and its cancellation would both be exported</li>
	 * </ul>
	 * 
	 * @throws DatevException
	 */
	private void removeNonExportableFactAccts(
			Set<String> alreadyExportedDocNrs, final String trxName) {

		final HashSet<String> docNrsToRemove = new HashSet<String>(
				alreadyExportedDocNrs);

		// XXX - AK / a42 - FR2011042920000032 - export canceled also
		/*		
		//
		// Now remove from the remaining records those that are canceled (of
		// course the cancellation is also removed.)
		for (final int recordId : recordId2FactAccts.keySet()) {

			final Set<I_Fact_Acct> debitorAccts = recordId2FactAccts.get(
					recordId).get(X_C_ElementValue.ACCOUNTTYPE_Asset);

			// Why do we only look at assets here?
			if (debitorAccts == null) {
				continue;
			}
			for (final I_Fact_Acct debitorAcct : debitorAccts) {

				if (FactAcctTool.isCancellation(debitorAcct)) {

					// The current record is a cancellation

					// Look for the original record (i.e.
					// the one which the cancellation refers to)
					String cancelledDocNr = FactAcctTool.getCancelledDocNo(
							recordId, debitorAcct.getDescription());

					if (docNr2FactAccts.containsKey(cancelledDocNr)
							&& !docNrsToRemove.contains(cancelledDocNr)) {

						// we found the original record the cancellation belongs
						// to. Therefore we can remove both entries.
						docNrsToRemove.add(cancelledDocNr);
						docNrsToRemove.add(FactAcctTool.getDocNr(debitorAcct
								.getRecord_ID(), debitorAcct.getDescription()));
					}
				}
			}
		}
		*/
		// end AK / a42
		for (final String docNrToRemove : docNrsToRemove) {

			docNr2FactAccts.remove(docNrToRemove);
		}
		LOG
				.info("Removed "
						+ docNrsToRemove.size()
						+ " already exported Fact_Accts");
	}

}
