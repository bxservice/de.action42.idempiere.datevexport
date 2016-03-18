package org.adempiere.datev.service.impl;

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

import org.adempiere.datev.DatevException;
import org.adempiere.datev.service.IFactAcctLoader;
import org.adempiere.datev.service.IMasterDataService;
import org.adempiere.datev.util.FactAcctTool;
import org.adempiere.model.MDatevExportLog;
import org.compiere.model.I_Fact_Acct;
import org.compiere.model.MElementValue;
import org.compiere.model.MFactAcct;
import org.compiere.model.X_C_ElementValue;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;

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
			final String trxName) {

		final HashSet<String> alreadyExported = loadAlreadyExported(dateFrom,
				dateTo, trxName);

		// get the data and put them into our hash tables
		loadFactActData(dateFrom, dateTo, trxName);

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
	 * 
	 * @param trxName
	 * @throws DatevException
	 *             if any exception is caught in this method.
	 */
	private void loadFactActData(final Timestamp dateFrom,
			final Timestamp dateTo, final String trxName) throws DatevException {

		final String factAcct_sql = "SELECT f.* "
				+ "FROM fact_acct f LEFT JOIN ad_table t on f.ad_table_id=t.ad_table_id "
				+ "WHERE t.tablename='C_Invoice' "
				+ " AND f.ad_client_id=? "
				+ " AND (?=0 OR f.ad_org_id=?) "
				+ " AND f.dateacct>=? AND f.dateacct<=?";

		PreparedStatement pstmt = null;
		try {
			pstmt = DB.prepareStatement(factAcct_sql, trxName);
			pstmt.setLong(1, clientId);
			pstmt.setLong(2, orgId);
			pstmt.setLong(3, orgId);
			pstmt.setTimestamp(4, dateFrom);
			pstmt.setTimestamp(5, dateTo);

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

				final MElementValue debitorAcctInfo = new MElementValue(Env
						.getCtx(), factAcct.getAccount_ID(), trxName);

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
					+ factAcct_sql, e);
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
