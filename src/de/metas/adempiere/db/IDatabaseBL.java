package de.metas.adempiere.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.compiere.model.PO;

public interface IDatabaseBL {

	/**
	 * A generic way of retrieving a list of {@link PO} instances from database.
	 * 
	 * @param <T>
	 *            the type of the list items (e.g. I_C_Order).
	 * @param sql
	 *            the query to execute as {@link PreparedStatement}. Note: The
	 *            ResultSet is passed to the PO constructor, so the query needs
	 *            to match the PO type <T>.
	 * @param params
	 *            Array of prepared statement parameters. May be empty, but not
	 *            null.
	 * @param clazz
	 *            the real po type (e.g. MOrder oder X_C_Order). Needs to have a
	 *            constructor with three parameters: <li>{@link Properties}, <li>
	 *            {@link ResultSet}, <li>{@link String}
	 * @param trxName
	 * @return
	 */
	<T extends PO> List<T> retrieveList(String sql, Object[] params,
			Class<T> clazz, String trxName);

	/**
	 * Similar to {@link #retrieveList(String, Object[], Class, String)}, but
	 * returns a map of the {@link PO}'s with their ids as keys.
	 * 
	 * @param <T>
	 * @param sql
	 * @param params
	 * @param clazz
	 * @param trxName
	 * @return
	 */
	<T extends PO> Map<Integer, T> retrieveMap(String sql, Object[] params,
			Class<T> clazz, String trxName);

}