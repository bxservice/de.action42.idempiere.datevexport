package de.action42.idempiere.datev.model.masterdata;

import de.action42.idempiere.datev.model.CSV_Buchungssatz;

public class CSV_Stammdaten_Buchungssatz extends CSV_Buchungssatz {

	public final static short B_ERSTEINGABE_AENDERUNG = 101;
	public final static short B_KONTONUMMER = 102;
	public final static short B_NAME1 = 103;
	public final static short B_KUNDENNUMMER = 104;
	public final static short B_POSTLEITZAHL = 106;
	public final static short B_ORT = 107;
	public final static short B_STRASSE = 108;
	public final static short B_ANREDE = 109;
	public final static short D_FAELLIGKEIT_TAGE = 110;
	public final static short D_MAHNSCHLUESSEL = 111;
	public final static short D_KONTOAUSZUG = 112;
	public final static short D_MAHNLIMIT_BETRAG = 113;
	public final static short D_MAHNLIMIT_PROZENT = 114;

	// 115 is not defined (i.e. no error here)

	public final static short D_SKONTO_PROZENT = 116;
	public final static short D_VORGABEWERT_DEBITORENANALYSE = 117;
	public final static short D_KREDITLIMIT = 118;
	public final static short D_DIVERSE_KONTO = 119;
	public final static short K_KREDITOREN_ZIEL1_TAGE = 120;
	public final static short K_KREDITOREN_ZIEL2_TAGE = 121;
	public final static short K_KREDITOREN_ZIEL_BUTTO_TAGE = 122;

	// TODO in der Doku checken!
	public final static short K_ZAHLUNGSKONDITION = 123;
	public final static short K_KREDITOREN_ZIEL4_TAGE = 125;
	public final static short K_KREDITOREN_ZIEL5_TAGE = 126;

	// TODO in der Doku checken!
	public final static short K_ZAHLUNGSTRAEGER = 127;

	public final static short B_BANKBEZEICHNUNG_ORT = 130;
	public final static short B_BANK_KONTONUMMER = 132;
	public final static short B_BANKLEITZAHL = 133;
	public final static short B_MANDANTENBANK = 134;

	public final static short B_LASTSCHRIFTVERFAHREN = 140;
	public final static short B_EINZEHLKONTENVERDICHTUNG = 150;
	public final static short B_WAEHRUNGSSTEUERUNG = 160;
	public final static short B_NAME2 = 203;

	// TODO doku checken
	public final static short D_MAHNTEXTE_MAHNSTUFE = 210;

	public final static short B_UST_ID = 500;
	public final static short B_POSTFACH = 700;
	public final static short B_NATIONALITAETSKENNZEICHEN = 702;
	public final static short B_SPRACHE = 703;
	public final static short B_TELEFONNUMMER = 710;
	public final static short B_TELEFAX = 711;
	public final static short B_EMAIL1 = 712;
	public final static short B_EMAIL2 = 713;
	public final static short B_BRIEFANREDE1 = 720;
	public final static short B_BRIEFANREDE2 = 721;
	public final static short B_BRIEFANREDE3 = 722;
	public final static short B_GRUSSFORMEL1 = 725;
	public final static short B_GRUSSFORMEL2 = 726;
	public final static short B_ANSPRECHPARTNER = 730;
	public final static short B_VERTRETER = 731;
	public final static short B_KURZBEZEICHNUNG = 740;
	public final static short K_DIVERSE_KONTO = 750;
	public final static short D_ABBUNGUNGSVERFAHREN = 755;
	public final static short B_TITEL_AKADGRAD = 801;
	public final static short B_MOBILTELEFON = 802;
	public final static short B_INTERNET_URL1 = 803;
	public final static short B_INTERNET_URL2 = 804;
	public final static short B_ANREDE_INDIVIDUELL = 809;
	public final static short B_BANKLEITZAHL_BANK2 = 822;
	public final static short B_BANKLEITZAHL_BANK3 = 832;
	public final static short B_BANKLEITZAHL_BANK4 = 842;
	public final static short B_BANKLEITZAHL_BANK5 = 852;
	public final static short B_BANKBEZEICHNUNG_ORT2 = 823;
	public final static short B_BANKBEZEICHNUNG_ORT3 = 833;
	public final static short B_BANKBEZEICHNUNG_ORT4 = 843;
	public final static short B_BANKBEZEICHNUNG_ORT5 = 853;
	public final static short B_BANK_KONTONUMMER2 = 824;
	public final static short B_BANK_KONTONUMMER3 = 834;
	public final static short B_BANK_KONTONUMMER4 = 844;
	public final static short B_BANK_KONTONUMMER5 = 854;
	public final static short B_LAENDERKENNZEICHEN_BANK1 = 811;
	public final static short B_LAENDERKENNZEICHEN_BANK2 = 821;
	public final static short B_LAENDERKENNZEICHEN_BANK3 = 831;
	public final static short B_LAENDERKENNZEICHEN_BANK4 = 841;
	public final static short B_LAENDERKENNZEICHEN_BANK5 = 851;
	public final static short B_IBAN_NR_BANK1 = 815;
	public final static short B_IBAN_NR_BANK2 = 825;
	public final static short B_IBAN_NR_BANK3 = 845;
	public final static short B_IBAN_NR_BANK4 = 855;
	public final static short B_IBAN_NR_BANK5 = 815;
	public final static short B_SWIFT_CODE_BANK1 = 816;
	public final static short B_SWIFT_CODE_BANK2 = 826;
	public final static short B_SWIFT_CODE_BANK3 = 836;
	public final static short B_SWIFT_CODE_BANK4 = 846;
	public final static short B_SWIFT_CODE_BANK5 = 856;
	public final static short B_ABWEICHENDER_KONTOINHABER_TEIL1_BANK1 = 817;
	public final static short B_ABWEICHENDER_KONTOINHABER_TEIL1_BANK2 = 827;
	public final static short B_ABWEICHENDER_KONTOINHABER_TEIL1_BANK3 = 837;
	public final static short B_ABWEICHENDER_KONTOINHABER_TEIL1_BANK4 = 847;
	public final static short B_ABWEICHENDER_KONTOINHABER_TEIL1_BANK5 = 857;
	public final static short B_ABWEICHENDER_KONTOINHABER_TEIL2_BANK1 = 818;
	public final static short B_ABWEICHENDER_KONTOINHABER_TEIL2_BANK2 = 828;
	public final static short B_ABWEICHENDER_KONTOINHABER_TEIL2_BANK3 = 838;
	public final static short B_ABWEICHENDER_KONTOINHABER_TEIL2_BANK4 = 848;
	public final static short B_ABWEICHENDER_KONTOINHABER_TEIL2_BANK5 = 858;

	public final static short B_GESCHAEFTSPARTNERBANK = 860;
	public final static short B_AUSGABEZIEL = 862;
	public final static short D_ZINSBERECHNUG = 865;
	public final static short D_ZINSSATZ_MAHNUNG1 = 866;
	public final static short D_ZINSSATZ_MAHNUNG2 = 867;
	public final static short D_ZINSSATZ_MAHNUNG3 = 868;

	// TODO Konstanten fuer Abweichenden Addressaten und Adresse

	public static final char FILLER = '\n';

	private static int nextId = 0;

	private int id;

	public CSV_Stammdaten_Buchungssatz() {
		id = nextId++;
		kennziffer = ""; // immer leer für CSV
	}

	/**
	 * Kennziffer/Kontonummer
	 */
	//@MatchPattern(pattern = "t[0-9]{1,5}")
	//@NotNull
	private String kennziffer;

	/**
	 * 036=x1E
	 */
	public static final String TEXTSTART = "\036";

	//@MinLength(0)
	//@MaxLength(40)
	//@NotNull
	private String text;

	/**
	 * 034=x1C
	 */
	public static final String TEXTENDE = "\034";

	public static final String SATZENDE = "\n";

	public final int getId() {
		return id;
	}

	/**
	 * Sets {@link #kontonummer} to <code>null</code> and {@link #kennziffer}
	 * to the given value.
	 * 
	 * @param kennziffer
	 */
	public final void setKennziffer(int kennziffer) {
		this.kennziffer = "t" + kennziffer;
	}

	public final String getKennziffer() {
		return kennziffer;
	}

	public final String getText() {
		return text;
	}

	/**
	 * @param text
	 *            is automatically truncated to 40 characters
	 */
	public final void setText(final String text) {
//		if (text.length() > 40) {
//			this.text = text.substring(0, 40);
//		} else {
			this.text = text;
//		}
	}

}
