package de.action42.idempiere.datev.util;

public final class StringUtil {

	private StringUtil() {
	}

	public static String rPad(final String inputString, final int length,
			final char character) {

		StringBuffer sb = new StringBuffer(inputString);

		for (int i = inputString.length(); i < length; i++) {
			sb.append(character);
		}

		return sb.toString();
	}

	public static String lPad(final String inputString, final int length,
			final char character) {

		StringBuffer sb = new StringBuffer();

		for (int i = inputString.length(); i < length; i++) {
			sb.append(character);
		}
		sb.append(inputString);
		return sb.toString();
	}
	
}
