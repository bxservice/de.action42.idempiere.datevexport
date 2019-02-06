package de.action42.idempiere.datev;

public interface IDatevSettings {

	String getBeratername();
	
	int getBeraternummer();
	
	int getMandantennummer();
	
	int getAbrechnungsnummer();
	
	String getDatentraegernummer();
	
	String getPasswort();
	
	String getNamenskuerzel();
	
	short getPrimanotaseite();
	
	// XXX a42 - AK
	void setAbrechnungsnummer(String abrechnungsnummer);
	//
}
