package de.metas.adempiere.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import de.metas.adempiere.util.time.SystemTime;
import org.compiere.model.GridTab;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.ValueNamePair;

public final class MiscUtils {

	private MiscUtils() {
	}

	public static String loggerMsgs() {

		final ValueNamePair lastError = CLogger.retrieveError();

		final StringBuffer msg = new StringBuffer(" Infos from CLogger: ");
		if (lastError != null) {
			msg.append("; Last error: [value='").append(lastError.getValue())
					.append("', name='").append(lastError.getName() + "']");
		}

		final ValueNamePair lastWarning = CLogger.retrieveWarning();
		if (lastWarning != null) {
			msg.append(" Last warning: [value='")
					.append(lastWarning.getValue()).append("', name='").append(
							lastWarning.getName()).append("']");
		}
		return msg.toString();
	}

	public static void throwIllegalArgumentEx(final Object value,
			final String paramName) throws IllegalArgumentException {

		final StringBuffer sb = new StringBuffer();
		sb.append("Illegal value '");
		sb.append(value);
		sb.append("' for param ");
		sb.append(paramName);

		throw new IllegalArgumentException(sb.toString());
	}

	/**
	 * Checks if the given object is a {@link PO} instance and returns it cast
	 * to PO.
	 * 
	 * @param po
	 *            the object that should be <code>instanceof</code> po
	 * @return
	 * @throws IllegalArgumentException
	 *             if the given 'po' is <code>null</code> or not instance of
	 *             PO.
	 */
	public static PO asPO(final Object po) {

		if (po == null) {
			throw new IllegalArgumentException("Param 'po' may not be null");
		}

		if (!(po instanceof PO)) {
			throw new IllegalArgumentException("Param 'po' must be a PO. Is: "
					+ po.getClass().getName());
		}
		return (PO) po;
	}

	public static <T extends PO> T asPO(final Object po, Class<T> clazz) {

		if (po == null) {
			throw new IllegalArgumentException("Param 'po' may not be null");
		}

		if (!clazz.isAssignableFrom(po.getClass())) {

			throw new IllegalArgumentException("Param 'po' must be a "
					+ clazz.getName() + ". Is: " + po.getClass().getName());

		}
		return clazz.cast(po);
	}

	public static boolean isToday(final Timestamp timestamp) {

		if (timestamp == null) {
			throw new IllegalArgumentException(
					"Param 'timestamp' may not be null");
		}

		final Calendar calNow = SystemTime.asGregorianCalendar();

		final Calendar calTs = new GregorianCalendar();
		calTs.setTime(timestamp);

		return calNow.get(Calendar.DAY_OF_MONTH) == calTs
				.get(Calendar.DAY_OF_MONTH)
				&& calNow.get(Calendar.MONTH) == calTs.get(Calendar.MONTH)
				&& calNow.get(Calendar.YEAR) == calTs.get(Calendar.YEAR);
	}

	public static Timestamp toTimeStamp(final String string) {
		try {
			return new Timestamp(new SimpleDateFormat("yyyy-MM-dd").parse(
					string).getTime());
		} catch (ParseException e) {
			throwIllegalArgumentEx(string, "string");
			return null;
		}
	}

	public static int getCalloutId(final GridTab mTab, final String colName) {

		final Integer id = (Integer) mTab.getValue(colName);

		if (id == null || id <= 0) {
			return 0;
		}
		return id;
	}

}
