package de.metas.adempiere.db.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import de.metas.adempiere.db.IDBService;
import de.metas.adempiere.db.IDatabaseBL;
import de.metas.adempiere.util.Services;
import org.compiere.model.PO;
import org.compiere.util.DB;
import org.compiere.util.Env;

public final class DatabaseBL implements IDatabaseBL {

	/**
	 * Executes the given sql (prepared) statement and returns the result as a
	 * list of {@link PO}s.
	 * 
	 * @param sql
	 *            the sql statement to execute
	 * @param params
	 *            prepared statement parameters (its length must correspond to
	 *            the number of '?'s in the sql)
	 * @param clazz
	 *            the class of the returned list elements needs to have a
	 *            constructor with three parameters:
	 *            <li>{@link Properties},
	 *            <li>{@link ResultSet},
	 *            <li>{@link String}
	 * @param trxName
	 */
	public final <T extends PO> List<T> retrieveList(final String sql,
			final Object[] params, final Class<T> clazz, final String trxName) {

		final IDBService db = Services.get(IDBService.class);
		final PreparedStatement pstmt = db.mkPstmt(sql, trxName);

		ResultSet rs = null;

		try {

			final ArrayList<T> result = new ArrayList<T>();

			for (int i = 0; i < params.length; i++) {
				pstmt.setObject(i + 1, params[i]);
			}
			rs = pstmt.executeQuery();

			while (rs.next()) {

				final T newPO = createInstance(clazz, rs, trxName);
				result.add(newPO);
			}
			return result;

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			DB.close(rs, pstmt);
		}
	}

	private <T> T createInstance(final Class<T> clazz, final ResultSet rs,
			final String trxName) {

		try {

			final Constructor<T> constructor = clazz.getConstructor(
					Properties.class, ResultSet.class, String.class);

			return constructor.newInstance(Env.getCtx(), rs, trxName);

		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public <T extends PO> Map<Integer, T> retrieveMap(final String sql,
			final Object[] params, final Class<T> clazz, final String trxName) {

		final Map<Integer, T> result = new HashMap<Integer, T>();

		for (T currentPO : retrieveList(sql, params, clazz, trxName)) {

			result.put(currentPO.get_ID(), currentPO);
		}
		return result;
	}
}
