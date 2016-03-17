package org.adempiere.datev.model.acct;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.adempiere.datev.DatevException;
import org.adempiere.datev.model.OBE_Buchungssatz;

public class OBE_Bewegungsdaten_Buchungssatz extends OBE_Buchungssatz {

	public static final char TAXID_NOTAX = '1';

	public static final char TAXID_SALES_7 = '2';

	public static final char TAXID_SALES_19 = '3';

	public static final char TAXID_SALES_16 = '5';

	public static final char TAXID_PRE_16 = '7';

	public static final char TAXID_PRE_7 = '8';

	public static final char TAXID_PRE_19 = '9';

	public static final char FILLER = '\000';

	private static int nextId = 0;

	private int id;

	public OBE_Bewegungsdaten_Buchungssatz() {
		id = nextId++;
	}

	/**
	 * must be set, may not be 0
	 */
	// @NotNull
	private double umsatz;

	/**
	 * Consists of an 'a', the berichtigungsschluessel, the steuerschluessel and
	 * the kontonummer
	 */
	// @MatchPattern(pattern = "a[0-9]{1,7}")
	// @NotNull
	private String gegenkontoNr;

	public String getGegenkontoNr() {
		return gegenkontoNr;
	}

	// @MatchPattern(pattern = "b[0-9]{0,6}|")
	// @NotNull
	private String belegfeld1 = "";

	// @MatchPattern(pattern = "c[0-9]{0,6}|")
	// @NotNull
	private String belegfeld2 = "";

	// @MatchPattern(pattern = "d[1-3]?[0-9][0,1][0-9]")
	// @NotNull
	private String datum;

	// @MatchPattern(pattern = "e[0-9]{1,5}")
	// @NotNull
	private String konto;

	private String kontoNr;

	public String getKontoNr() {
		return kontoNr;
	}

	private char berichtigungsSchluessel;

	public char getBerichtigungsSchluessel() {
		return berichtigungsSchluessel;
	}

	private char steuerSchluessel;

	public char getSteuerSchluessel() {
		return steuerSchluessel;
	}

	// @MatchPattern(pattern = "(f[0-9]{1,4})|")
	// @NotNull
	private String kost1 = "";

	// @MatchPattern(pattern = "(g[0-9]{1,4})|")
	// @NotNull
	private String kost2 = "";

	/**
	 * The last two digits are supposed to be after the decimal point
	 */
	// @MatchPattern(pattern = "(h[0-9]{1,6})|")
	// @NotNull
	private String skonto = "";

	/**
	 * First character of the text may not be a comma. 0036=x1E; 0034=x1C
	 */
	// @MatchPattern(pattern = "(\\u001E.{0,30}\\u001C)|")
	// @NotNull
	private String buchungstext = "";

	/**
	 * The EU-Id consists of the EU-country-code and the EU-umsatzsteuer-id
	 * (together with start and end-marker). 0272=xBA, 0034=x1C
	 */
	// @MatchPattern(pattern = "(\\u00BA.{0,13}\\u001C)|")
	// @NotNull
	private String euId = "";

	// @MatchPattern(pattern = "(j[0-9]{1,4})|")
	// @NotNull
	private String euSteuersatz = "";

	// @MatchPattern(pattern = "o[0-9]{1,2}")
	// @NotNull
	public static final String WAEHRUNGSKENNUNG = "o1";

	// @MatchPattern(pattern = "y")
	// @NotNull
	public static final String SATZENDE = "y";

	public void setSkonto(final String newSkonto) {
		skonto = newSkonto;
	}

	public void setSkonto(final double newSkonto) {

		DecimalFormat skontoFormat = new DecimalFormat("#");
		skonto = 'h' + skontoFormat.format(newSkonto * 100);
	}

	public void setUmsatz(final double newUmsatz) {

		umsatz = newUmsatz;
	}

	public double getUmsatz() {

		return umsatz;
	}

	public final String getUmsatzStr() {

		DecimalFormat umsatzFormat = new DecimalFormat("+0;-0");
		return umsatzFormat.format(umsatz * 100);
	}

	public final int getId() {
		return id;
	}

	public void setDatum(final String newDatum) {
		datum = newDatum;
	}

	public void setDatum(final Date newDatum) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dMM");
		datum = "d" + dateFormat.format(newDatum);
	}

	public String getDatum() {
		return datum;
	}

	public void setEU_Id(final String countryCode, final String taxId) {

		StringBuffer sbEuId = new StringBuffer();
		sbEuId.append('\272');
		sbEuId.append(countryCode);
		sbEuId.append(taxId);
		sbEuId.append('\034');

		euId = sbEuId.toString();
	}

	public void setEU_Id(final String newEuId) {

		euId = newEuId;
	}

	public final String getEuId() {
		return euId;
	}

	public final String getBuchungstext() {
		return buchungstext;
	}

	public final void setBuchungstextPlain(final String newBuchungsText) {
		buchungstext = newBuchungsText;
	}

	public final void setBuchungstext(final String newBuchungstext)
			throws DatevException {

		String text = newBuchungstext.replace('\n', ' ').replace('\t', ' ');

		if (text.startsWith(",")) {
			// in DATEV, this text may not start with a comma
			text.replaceFirst(",", " ");
		}

		StringBuffer sbBuchungstext = new StringBuffer();

		sbBuchungstext.append('\036');

		if (newBuchungstext.length() > 30) {
			sbBuchungstext.append(text.substring(0, 30));
		} else {
			sbBuchungstext.append(text);
		}
		sbBuchungstext.append('\034');
		this.buchungstext = sbBuchungstext.toString();
	}

	public final String getGegenkonto() {

		final StringBuffer sbGegenkonto = new StringBuffer();
		sbGegenkonto.append('a');
		if (berichtigungsSchluessel != '0') {
			sbGegenkonto.append(berichtigungsSchluessel);
		}
		if (steuerSchluessel != '0') {
			sbGegenkonto.append(steuerSchluessel);
		}

		sbGegenkonto.append(gegenkontoNr);

		return sbGegenkonto.toString();
	}

	public final void setGegenkonto(final char berichtigungsSchluessel,
			final char steuerSchluessel, final String kontoNr)
			throws DatevException {

		if (kontoNr.length() > 5) {
			throw new DatevException("kontoNr '" + kontoNr
					+ "' mustn't have more than five digits");
		}
		if (kontoNr.length() < 1) {
			throw new DatevException("kontoNr '" + kontoNr
					+ "' must have at least one digit");
		}

		this.berichtigungsSchluessel = berichtigungsSchluessel;
		this.steuerSchluessel = steuerSchluessel;
		this.gegenkontoNr = kontoNr;
	}

	public final String getKonto() {

		final StringBuffer sbKonto = new StringBuffer();
		sbKonto.append('e');
		sbKonto.append(kontoNr);

		return sbKonto.toString();
	}

	public final void setKonto(final String newKonto) throws DatevException {

		if (newKonto.length() > 5 || newKonto.length() < 1) {
			throw new DatevException("konto '" + newKonto
					+ "' must have between 2 and 5 digits");
		}
		this.kontoNr = newKonto;
	}

	public final String getEuSteuersatz() {
		return euSteuersatz;
	}

	public final void setEuSteuersatz(String newEuSteuersatz) {

		euSteuersatz = newEuSteuersatz;
	}

	public final void setEuSteuersatz(double newEuSteuersatz) {

		DecimalFormat euSteuersatzFormat = new DecimalFormat("#");
		euSteuersatz = 'j' + euSteuersatzFormat.format(newEuSteuersatz);
	}

	public final String getBelegfeld1() {
		return belegfeld1;
	}

	public final void setBelegfeld1(String newBelegfeld1) throws DatevException {

		this.belegfeld1 = newBelegfeld1;
	}

	public final void setBelegfeld1(int newBelegfeld1) throws DatevException {

		final DecimalFormat fmt = new DecimalFormat("#");
		String strBelegFeld1 = fmt.format(newBelegfeld1);

		if (strBelegFeld1.length() > 6) {
			// throw new DatevException("belegfeld1 '" + newBelegfeld1
			// + "' mustn't have more than six digits");
			strBelegFeld1 = strBelegFeld1.substring(strBelegFeld1.length() - 6);
		}
		StringBuffer sbBelegfeld1 = new StringBuffer();
		sbBelegfeld1.append('b');
		sbBelegfeld1.append(strBelegFeld1);

		this.belegfeld1 = sbBelegfeld1.toString();
	}

	public final String getBelegfeld2() {
		return belegfeld2;
	}

	public final void setBelegfeld2(String newBelegfeld2) {

		this.belegfeld2 = newBelegfeld2;
	}

	public final void setBelegfeld2(short newBelegfeld2) throws DatevException {

		if (newBelegfeld2 > 999999) {
			throw new DatevException("belegfeld2 '" + newBelegfeld2
					+ "' mustn't have more than six digits");
		}
		StringBuffer sbBelegfeld2 = new StringBuffer();
		sbBelegfeld2.append('c');

		// DecimalFormat fmt = new DecimalFormat("000000");
		DecimalFormat fmt = new DecimalFormat("#");
		sbBelegfeld2.append(fmt.format(newBelegfeld2));

		this.belegfeld2 = sbBelegfeld2.toString();
	}

	public final String getSkonto() {
		return skonto;
	}

	public final String getKost1() {
		return kost1;
	}

	public final void setKost1(String newKost1) {
		kost1 = newKost1;
	}

	public final void setKost1(short newKost1) throws DatevException {

		if (newKost1 > 9999) {
			throw new DatevException("kost1 '" + newKost1
					+ "' mustn't have more than four digits");
		}
		StringBuffer sbKost1 = new StringBuffer();
		sbKost1.append('f');
		sbKost1.append(newKost1);

		this.kost1 = sbKost1.toString();
	}

	public final String getKost2() {
		return kost2;
	}

	public final void setKost2(String newKost2) {
		kost2 = newKost2;
	}

	public final void setKost2(short newKost2) throws DatevException {
		if (newKost2 > 9999) {
			throw new DatevException("kost2 '" + newKost2
					+ "' mustn't have more than four digits");
		}
		StringBuffer sbKost2 = new StringBuffer();
		sbKost2.append('g');
		sbKost2.append(newKost2);

		this.kost2 = sbKost2.toString();
	}

	@Override
	public String toString() {

		final StringBuffer sb = new StringBuffer();

		sb.append("[");
		sb.append("Umsatz=");
		sb.append(getUmsatzStr());
		sb.append(";Gegenkonto=");
		sb.append(getGegenkonto());
		sb.append(";Belegfeld1=");
		sb.append(getBelegfeld1());
		sb.append(";Belegfeld2=");
		sb.append(getBelegfeld2());
		sb.append(";Datum=");
		sb.append(getDatum());
		sb.append(";Konto=");
		sb.append(getKonto());
		sb.append(";Kost1=");
		sb.append(getKost1());
		sb.append(";Kost2=");
		sb.append(getKost2());
		sb.append(";Skonto=");
		sb.append(getSkonto());
		sb.append(";Buchungstext=");
		sb.append(getBuchungstext());
		sb.append(";EuID=");
		sb.append(getEuId());
		sb.append(";EuSteuersatz=");
		sb.append(getEuSteuersatz());
		sb.append("]");

		return sb.toString();
	}
}