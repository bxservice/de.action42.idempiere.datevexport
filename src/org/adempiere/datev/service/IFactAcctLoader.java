package org.adempiere.datev.service;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;

import org.compiere.model.I_Fact_Acct;

public interface IFactAcctLoader {

	void load( Timestamp dateFrom,  Timestamp dateTo,String trxName);

	Map<Integer, Map<String, Set<I_Fact_Acct>>> getResult();

	Map<String, Set<I_Fact_Acct>> getDocNr2FactAccts();
}
