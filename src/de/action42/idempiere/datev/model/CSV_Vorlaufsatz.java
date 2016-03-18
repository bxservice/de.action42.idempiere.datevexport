package de.action42.idempiere.datev.model;


public abstract class CSV_Vorlaufsatz extends CSV_Satz {

	//@NotNull
	private CSV_Vorlaufinformationen vorlaufinformationen;

	public final CSV_Vorlaufinformationen getVorlaufinformationen() {
		return vorlaufinformationen;
	}

	public final void setVorlaufinformationen(
			CSV_Vorlaufinformationen vorlaufinformationen) {
		this.vorlaufinformationen = vorlaufinformationen;
	}
	
}
