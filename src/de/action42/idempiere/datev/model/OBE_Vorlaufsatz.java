package de.action42.idempiere.datev.model;


public abstract class OBE_Vorlaufsatz extends OBE_Satz {

	//@NotNull
	private OBE_Vorlaufinformationen vorlaufinformationen;

	public final OBE_Vorlaufinformationen getVorlaufinformationen() {
		return vorlaufinformationen;
	}

	public final void setVorlaufinformationen(
			OBE_Vorlaufinformationen vorlaufinformationen) {
		this.vorlaufinformationen = vorlaufinformationen;
	}
	
}
