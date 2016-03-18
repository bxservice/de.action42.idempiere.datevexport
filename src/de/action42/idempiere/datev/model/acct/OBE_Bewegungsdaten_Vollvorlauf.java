package de.action42.idempiere.datev.model.acct;

import de.action42.idempiere.datev.model.OBE_Vorlaufsatz;

public class OBE_Bewegungsdaten_Vollvorlauf extends OBE_Vorlaufsatz {

	/**
	 * 035=x1D
	 */
	public static final String VORLAUFBEGINN = "\035";

	/**
	 * 030=x18
	 */
	public static final String KENNUNG_NEUER_VORLAUF = "\030";

	/**
	 * 061=x31
	 */
	public static final String VERSIONSNUMMER = "\061";

	/**
	 * Has to be equal to {@link OBE_Datentraegerkennsatz#datentraegernummer}
	 */
	//@NotNull
	//@NotBlank
	public String datentraegernummer;

	/**
	 * 040=x20
	 */
	public final static String ANWENDUGSINFO = "\040\040\040\040"
			+ "\040\040\040\040" + "\040\040\040\040" + "\040\040\040\040";

	/**
	 * 040=x20
	 */
	public final static String INPUT_INFO = "\040\040\040\040"
			+ "\040\040\040\040" + "\040\040\040\040" + "\040\040\040\040";

	public final static String SATZENDE = "y";

}
