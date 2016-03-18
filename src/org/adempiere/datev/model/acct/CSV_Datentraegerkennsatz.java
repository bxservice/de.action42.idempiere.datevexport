package org.adempiere.datev.model.acct;

import org.adempiere.datev.model.CSV_Satz;
import org.adempiere.datev.util.StringUtil;


public class CSV_Datentraegerkennsatz extends CSV_Satz {

	//@MatchPattern(pattern = "[0-9]{3}")
	//@NotNull
	public String datentraegernummer;

	public String getDatentraegerNummer(){
		
		return StringUtil.rPad(datentraegernummer, 6, ' ');
	}
	
	//@Max(9999999)
	//@Min(1)
	public int beraternummer;

	public final String getBeraternummer() {
		return StringUtil.lPad(Integer.toString(beraternummer), 7, '0');
	}
	
	//@MatchPattern(pattern = "[a-zA-Z0-9$%&*+-/\\.]{1,9}")
	//@NotNull
	public String beratername;

	public final String getBeratername() {
		return StringUtil.rPad(beratername, 9, ' ');
	}
	
	
	/**
	 * 0040=x20
	 */
	public final String restartKennzeichen = "\040";

	//@Min(1)
	//@Max(65536)
	public Short anzahlDatendateien;

	//@Min(1)
	//@Max(65536)
	public Short letzteDatendatei;
}
