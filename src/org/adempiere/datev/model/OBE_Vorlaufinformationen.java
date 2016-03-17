package org.adempiere.datev.model;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.adempiere.datev.util.StringUtil;

/**
 * @author ts
 * 
 */
public class OBE_Vorlaufinformationen extends OBE_Satz {

	public enum Anwendungsnummer {
		FIBUOPOS_VOLLVORL, FIBUOPOS_STAMMDATEN
	};

	private final static String ANWENDUGSNUMMER_FIBUOPOS_VOLLVORL = "11";

	private final static String ANWENDUGSNUMMER_FIBUOPOS_STAMMDATEN = "13";

	//@NotNull
	private String anwendungsnummer;

	//@NotNull
	public String namenskuerzel;

	//@Max(9999999)
	//@Min(1)
	private int beraternummer;

	//@Max(99999)
	//@Min(1)
	private int mandantennummer;

	private String abrechnungsnummer;

	private Date datumVon;

	private Date datumBis;

	//@Max(999)
	//@Min(1)
	private int primanotaSeite;

	public String getPrimanotaSeite() {
		return StringUtil.lPad(Integer.toString(primanotaSeite), 3, '0');
	}

	//@MatchPattern(pattern = "[a-zA-Z0-9$%&*+-/\\.]{4}")
	public String passwort;

	public final void setPrimanotaSeite(int primanotaSeite) {
		this.primanotaSeite = primanotaSeite;
	}

	public final String getMandantennummer() {

		DecimalFormat format = new DecimalFormat("00000");
		return format.format(mandantennummer);
	}

	public final void setMandantennummer(int mandantennummer) {
		this.mandantennummer = mandantennummer;
	}

	public final String getBeraternummer() {
		DecimalFormat format = new DecimalFormat("0000000");
		return format.format(beraternummer);
	}

	public final void setBeraternummer(int beraternummer) {
		this.beraternummer = beraternummer;
	}

	public final String getAbrechnungsnummer() {

		return abrechnungsnummer;
	}

	public final void setAbrechnungsnummer(int abrechnungsnummer,
			int currentYear) {

		DecimalFormat formatNb = new DecimalFormat("0000");
		DecimalFormat formatYear = new DecimalFormat("00");
		String strYear = formatYear.format(currentYear);

		this.abrechnungsnummer = formatNb.format(abrechnungsnummer)
				+ strYear.substring(strYear.length() - 2);
	}

	public String getDatumVon() {
		SimpleDateFormat f = new SimpleDateFormat("ddMMyy");
		return f.format(datumVon);
	}

	public String getDatumBis() {
		SimpleDateFormat f = new SimpleDateFormat("ddMMyy");
		return f.format(datumBis);
	}

	public final void setDatumVon(Date datumVon) {
		this.datumVon = datumVon;
	}

	public final void setDatumBis(Date datumBis) {
		this.datumBis = datumBis;
	}

	public final String getAnwendungsnummer() {
		return anwendungsnummer;
	}

	public final void setAnwendungsnummer(Anwendungsnummer anwendungsnummer) {
		if (anwendungsnummer == Anwendungsnummer.FIBUOPOS_VOLLVORL) {
			this.anwendungsnummer = ANWENDUGSNUMMER_FIBUOPOS_VOLLVORL;
		} else {
			this.anwendungsnummer = ANWENDUGSNUMMER_FIBUOPOS_STAMMDATEN;
		}

	}

}