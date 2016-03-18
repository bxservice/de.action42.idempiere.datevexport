package org.adempiere.datev.model.acct;

import org.adempiere.datev.model.CSV_Satz;
import org.adempiere.datev.model.CSV_Vorlaufinformationen;

public class CSV_Verwaltungssatz extends CSV_Satz {

	public enum Type {
		STAMMDATEN, BEWEGUNGSDATEN
	};
	
	//@NotNull
	public final Type type;
	
	public CSV_Verwaltungssatz(Type type){
		this.type=type;
	}
	
	//	@MatchPattern(pattern = "V|\\*|L")
	//@NotNull
	public String verarbeitungsKennzeichen;

	//@NotNegative
	//@Max(65536)
	public Short dateiNummer;

	//@NotNull
	public CSV_Vorlaufinformationen vorlaufinformationen;

	/**
	 * Number of 256 byte blocks in the data file
	 */
	//@Min(1)
	//@Max(65536)
	//@NotNull
	public Short letzteBlockNummer;

	/**
	 * Postition of the {@link CSV_Bewegungsdaten_Buchungssatz#SATZENDE} in the last block
	 */
	//@Min(0)
	//@Max(65536)
	//@NotNull
	public Short letzteBlockPos;

	//@Min(0)
	//@Max(999)
	//@NotNull
	public Short letztePrimanotaSeite;

	/**
	 * 040=x20
	 */
	public static final byte LAUFENDE_ABSTIMMSUMME_RESERVIERT = 0x20;

	//@MatchPattern(pattern = " |\\*")
	//@NotNull
	public String korrekturKennzeichen;

	//@NotNull
	public final String sonderverarbeitung = "1";

	public final Type getType() {
		return type;
	}
}
