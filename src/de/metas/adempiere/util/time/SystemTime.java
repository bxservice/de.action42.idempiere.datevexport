package de.metas.adempiere.util.time;

import java.sql.Timestamp;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Code taken from the book "Test Driven", Chapter 7 ("Test-driving the
 * unpredictable") by Lasse Koskela.
 * 
 * @author ts
 * 
 */
public final class SystemTime {

	private static final TimeSource defaultTimeSource = new TimeSource() {
		public long millis() {
			return System.currentTimeMillis();
		}

	};

	private static TimeSource timeSource;

	public static long millis() {
		return getTimeSource().millis();
	}

	public static GregorianCalendar asGregorianCalendar() {

		final GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(millis());

		return cal;
	}

	public static Date asDate() {

		return new Date(millis());
	}
	
	public static Timestamp asTimestamp() {

		return new Timestamp(millis());
	}

	private static TimeSource getTimeSource() {
		return timeSource == null ? defaultTimeSource : timeSource;
	}

	/**
	 * After invocation of this method, the time returned will be the system
	 * time again.
	 */
	public static void resetTimeSource() {
		timeSource = null;
	}

	/**
	 * 
	 * @param newTimeSource
	 *            the given TimeSource will be used for the time returned by the
	 *            methods of this class (unless it is null).
	 * 
	 */
	public static void setTimeSource(TimeSource newTimeSource) {
		timeSource = newTimeSource;
	}
}
