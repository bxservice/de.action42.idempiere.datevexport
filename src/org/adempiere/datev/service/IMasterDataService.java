package org.adempiere.datev.service;

import java.util.Collection;

import org.adempiere.datev.model.masterdata.StammdatensatzFileInfo;
import org.adempiere.datev.model.masterdata.StammdatensatzFileInfoCSV;

public interface IMasterDataService {

	/**
	 * Makes sure that all account info and business partners related to the
	 * given account number are looked up and added to their appropriate data
	 * structures.
	 * 
	 * @param accountNo
	 * @return <code>true</code> if new data has been looked up and added.
	 *         False if this account number has already been dealt with.
	 */
	boolean accountSeen(String accountNo);

	boolean bPartnerSeen(int bPartnerId, String trxName);
	boolean bPartnerSeenCSV(int bPartnerId, boolean isSOTrx, String trxName); 
	

	Collection<StammdatensatzFileInfo> getData();
	Collection<StammdatensatzFileInfoCSV> getDataCSV();

	/**
	 * Tells the master data exporter about an already exported bPartnerId
	 * 
	 * @param bPartnerId
	 */
	void addAlreadyExportedBPartnerId(int bPartnerId);
}
