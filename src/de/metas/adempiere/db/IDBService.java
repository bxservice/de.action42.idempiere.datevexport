package de.metas.adempiere.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public interface IDBService {

	/**
	 * 
	 * @param sql
	 * @param trxName
	 * @return
	 */
	PreparedStatement mkPstmt(String sql, String trxName);

	void close(ResultSet rs, PreparedStatement pstmt);

	void close(PreparedStatement pstmt);

	String createTrx(String prefix);

	boolean commitTrx(String trxName);

	boolean rollBackTrx(String trxName);

	boolean closeTrx(String trxName);

	int getSQLValueEx(String trxName, String sql, Object... params);

}
