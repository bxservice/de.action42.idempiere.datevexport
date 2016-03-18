package org.adempiere.datev.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.adempiere.datev.DatevException;
import org.adempiere.datev.io.OBE_Verwaltungsdatei;
import org.adempiere.datev.io.CSV_Verwaltungsdatei;
import org.adempiere.datev.model.acct.OBE_Bewegungsdaten_Buchungssatz;
import org.adempiere.datev.model.acct.CSV_Bewegungsdaten_Buchungssatz;
import org.compiere.model.I_Fact_Acct;
import org.compiere.util.CLogger;

public final class FactAcctTool {

	private final static CLogger logger = CLogger
			.getCLogger(FactAcctTool.class);

	/**
	 * Regular expression used to find out from a fact_Acct record's description
	 * whether it is a cancellation and the original record's id.
	 */
	public static final String PATTERN_CANCELLED_RECORD = ".* \\(.*\\{->([0-9]+)\\)";

	public static final String PATTERN_RECORD = "([0-9]+).*";

	private FactAcctTool() {
	}

	public static boolean isCancellation(final I_Fact_Acct factAcct) {

		boolean negativeAmount = factAcct.getAmtAcctCr().doubleValue() < 0
				|| factAcct.getAmtAcctDr().doubleValue() < 0;
		String acctDescription = factAcct.getDescription();

		return negativeAmount && acctDescription != null
				&& acctDescription.matches(PATTERN_CANCELLED_RECORD);
	}

	public static String getCancelledDocNo(int recordId, String description)
			throws DatevException {

		Matcher matcher = Pattern.compile(PATTERN_CANCELLED_RECORD).matcher(
				description);

		if (!matcher.find()) {
			logger.log(Level.SEVERE,
					"Unable to find the document number which the cancellation (record_id "
							+ recordId + ") refers to");
			throw new DatevException(
					"Unable to find the document number which the cancellation (record_id "
							+ recordId
							+ ") refers to. See issue log for details.");
		}
		return matcher.group(1);
	}

	public static String getDocNr(final int recordId, final String description)
			throws DatevException {

		if (description == null) {
			throw new IllegalArgumentException("'description' may not be null");
		}

		final Matcher matcher = Pattern.compile(PATTERN_RECORD).matcher(
				description);

		if (!matcher.find()) {
			logger.log(Level.SEVERE,
					"Unable to find the document number of record_id "
							+ recordId);

			throw new DatevException(
					"Unable to find the document number of record_id "
							+ recordId + ". See issue log for details.");
		}
		return matcher.group(1);
	}

	public static int getDocNoInt(final String strDocNo) {
		int docNo = 0;
		if (strDocNo != null && !(strDocNo.length() == 0)) {

			Matcher matcher = Pattern.compile("[0-9]+").matcher(strDocNo);
			if (matcher.find()) {
				long ldocNo = Long.parseLong(matcher.group());
				docNo = (int) ldocNo;
				if (docNo < 0) {
					docNo *= -1;
				}
			}
		}
		return docNo;
	}

	public static List<OBE_Bewegungsdaten_Buchungssatz> compress(
			final List<OBE_Bewegungsdaten_Buchungssatz> input) {

		final List<OBE_Bewegungsdaten_Buchungssatz> resultList = new ArrayList<OBE_Bewegungsdaten_Buchungssatz>();

		final Map<String, OBE_Bewegungsdaten_Buchungssatz> existingRecords = new HashMap<String, OBE_Bewegungsdaten_Buchungssatz>();

		//
		// Step 1: compress the records by creating one record per key (compare
		// method 'mkNormalizedKey') and summing up the turnover values.
		for (final OBE_Bewegungsdaten_Buchungssatz currentRecord : input) {

			final String key = mkNormalizedKey(currentRecord);

			if (existingRecords.containsKey(key)) {

				final OBE_Bewegungsdaten_Buchungssatz existingRecord = existingRecords
						.get(key);

				final double newUmsatz;

				if (mkKey(existingRecord).equals(mkKey(currentRecord))) {
					newUmsatz = currentRecord.getUmsatz();
				} else {
					// currentRecord's "gegenKonto" is existingRecord's "konto"
					// and vice versa.
					newUmsatz = currentRecord.getUmsatz() * -1;
				}

				existingRecord
						.setUmsatz(existingRecord.getUmsatz() + newUmsatz);
				continue;
			}

			final OBE_Bewegungsdaten_Buchungssatz newRecord = new OBE_Bewegungsdaten_Buchungssatz();

			newRecord.setKonto(currentRecord.getKontoNr());
			newRecord.setGegenkonto(currentRecord.getBerichtigungsSchluessel(),
					currentRecord.getSteuerSchluessel(), currentRecord
							.getGegenkontoNr());
			newRecord.setBelegfeld1(currentRecord.getBelegfeld1());
			newRecord.setUmsatz(currentRecord.getUmsatz());

			newRecord.setBelegfeld2(currentRecord.getBelegfeld2());
			newRecord.setBuchungstextPlain(currentRecord.getBuchungstext());
			newRecord.setDatum(currentRecord.getDatum());
			newRecord.setEU_Id(currentRecord.getEuId());
			newRecord.setEuSteuersatz(currentRecord.getEuSteuersatz());
			newRecord.setKost1(currentRecord.getKost1());
			newRecord.setKost2(currentRecord.getKost2());
			newRecord.setSkonto(currentRecord.getSkonto());

			OBE_Verwaltungsdatei.validate(newRecord);

			existingRecords.put(key, newRecord);

			resultList.add(newRecord);
		}

		//
		// step 2: if a turnover value is now negative, switch konto and
		// gegenkonto to make the amount positive again

		for (final OBE_Bewegungsdaten_Buchungssatz currentRecord : resultList) {

			if (currentRecord.getUmsatz() < 0) {

				final String kontoOld = currentRecord.getKonto();

				currentRecord.setKonto(currentRecord.getGegenkonto().substring(
						1));
				currentRecord.setGegenkonto(currentRecord
						.getBerichtigungsSchluessel(), currentRecord
						.getSteuerSchluessel(), kontoOld.substring(1));

				currentRecord.setUmsatz(currentRecord.getUmsatz() * -1);
			}
		}
		return resultList;
	}

	public static List<CSV_Bewegungsdaten_Buchungssatz> compressCSV(
			final List<CSV_Bewegungsdaten_Buchungssatz> input) {

		final List<CSV_Bewegungsdaten_Buchungssatz> resultList = new ArrayList<CSV_Bewegungsdaten_Buchungssatz>();

		final Map<String, CSV_Bewegungsdaten_Buchungssatz> existingRecords = new HashMap<String, CSV_Bewegungsdaten_Buchungssatz>();

		//
		// Step 1: compress the records by creating one record per key (compare
		// method 'mkNormalizedKey') and summing up the turnover values.
		for (final CSV_Bewegungsdaten_Buchungssatz currentRecord : input) {

			final String key = mkNormalizedKeyCSV(currentRecord);

			if (existingRecords.containsKey(key)) {

				final CSV_Bewegungsdaten_Buchungssatz existingRecord = existingRecords
						.get(key);

				final double newUmsatz;

				if (mkKeyCSV(existingRecord).equals(mkKeyCSV(currentRecord))) {
					newUmsatz = currentRecord.getUmsatz();
				} else {
					// currentRecord's "gegenKonto" is existingRecord's "konto"
					// and vice versa.
					newUmsatz = currentRecord.getUmsatz() * -1;
				}

				existingRecord
						.setUmsatz(existingRecord.getUmsatz() + newUmsatz);
				continue;
			}

			final CSV_Bewegungsdaten_Buchungssatz newRecord = new CSV_Bewegungsdaten_Buchungssatz();

			newRecord.setKonto(currentRecord.getKontoNr());
			newRecord.setGegenkonto(currentRecord.getBerichtigungsSchluessel(),
					currentRecord.getSteuerSchluessel(), currentRecord
							.getGegenkontoNr());
			newRecord.setBelegfeld1(currentRecord.getBelegfeld1());
			newRecord.setUmsatz(currentRecord.getUmsatz());

			newRecord.setBelegfeld2(currentRecord.getBelegfeld2());
			newRecord.setBuchungstextPlain(currentRecord.getBuchungstext());
			newRecord.setDatum(currentRecord.getDatum());
			newRecord.setEU_Id(currentRecord.getEuId());
			newRecord.setEuSteuersatz(currentRecord.getEuSteuersatz());
			newRecord.setKost1(currentRecord.getKost1());
			newRecord.setKost2(currentRecord.getKost2());
			newRecord.setSkonto(currentRecord.getSkonto());

			CSV_Verwaltungsdatei.validate(newRecord);

			existingRecords.put(key, newRecord);

			resultList.add(newRecord);
		}

		//
		// step 2: if a turnover value is now negative, switch konto and
		// gegenkonto to make the amount positive again

		for (final CSV_Bewegungsdaten_Buchungssatz currentRecord : resultList) {

			if (currentRecord.getUmsatz() < 0) {

				final String kontoOld = currentRecord.getKonto();

				currentRecord.setKonto(currentRecord.getGegenkonto().substring(
						1));
				currentRecord.setGegenkonto(currentRecord
						.getBerichtigungsSchluessel(), currentRecord
						.getSteuerSchluessel(), kontoOld.substring(1));

				currentRecord.setUmsatz(currentRecord.getUmsatz() * -1);
			}
		}
		return resultList;
	}

	/**
	 * Returns a string to be used as key when distinguishing new records from
	 * those that have already been seen.
	 * 
	 * @param currentRecord
	 * @return
	 */
	static String mkNormalizedKey(
			final OBE_Bewegungsdaten_Buchungssatz currentRecord) {

		final String kto = currentRecord.getKonto();
		final String gegenkto = currentRecord.getGegenkonto();

		final String[] accounts = new String[] { kto.substring(1),
				gegenkto.substring(1) };

		Arrays.sort(accounts);

		final StringBuffer key = new StringBuffer();
		key.append(currentRecord.getBelegfeld1());
		key.append(accounts[0]);
		key.append(accounts[1]);

		return key.toString();
	}

	static String mkNormalizedKeyCSV(
			final CSV_Bewegungsdaten_Buchungssatz currentRecord) {

		final String kto = currentRecord.getKonto();
		final String gegenkto = currentRecord.getGegenkonto();

		final String[] accounts = new String[] { kto.substring(1),
				gegenkto.substring(1) };

		Arrays.sort(accounts);

		final StringBuffer key = new StringBuffer();
		key.append(currentRecord.getBelegfeld1());
		key.append(accounts[0]);
		key.append(accounts[1]);

		return key.toString();
	}

	private static String mkKey(
			final OBE_Bewegungsdaten_Buchungssatz currentRecord) {

		final StringBuffer key = new StringBuffer();
		key.append(currentRecord.getBelegfeld1());
		key.append(currentRecord.getKonto());
		key.append(currentRecord.getGegenkonto());

		return key.toString();
	}

	private static String mkKeyCSV(
			final CSV_Bewegungsdaten_Buchungssatz currentRecord) {

		final StringBuffer key = new StringBuffer();
		key.append(currentRecord.getBelegfeld1());
		key.append(currentRecord.getKonto());
		key.append(currentRecord.getGegenkonto());

		return key.toString();
	}

}
