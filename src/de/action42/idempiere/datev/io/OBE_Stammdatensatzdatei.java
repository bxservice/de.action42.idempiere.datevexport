package de.action42.idempiere.datev.io;

import java.io.File;

import de.action42.idempiere.datev.DatevException;
import de.action42.idempiere.datev.model.OBE_Buchungssatz;
import de.action42.idempiere.datev.model.OBE_Vorlaufsatz;
import de.action42.idempiere.datev.model.acct.OBE_Bewegungsdaten_Vollvorlauf;
import de.action42.idempiere.datev.model.masterdata.OBE_Stammdaten_Buchungssatz;
import de.action42.idempiere.datev.model.masterdata.OBE_Stammdaten_Kurzvorlauf;
import de.action42.idempiere.datev.model.masterdata.StammdatensatzFileInfo;

/**
 * Creates the data for a DATEV data file containing Stammdaten (master data).
 * 
 * @author ts
 * 
 */
public class OBE_Stammdatensatzdatei extends OBE_Datensatzdatei {

	/**
	 * doesn't actually create the file. The file is created in
	 * {@link #writeVorlaufsatz(OBE_Bewegungsdaten_Vollvorlauf)}.
	 * 
	 * @param targetDirectory
	 * @throws DatevException
	 */
	public OBE_Stammdatensatzdatei(final short myFileNumer,
			final File targetDirectory, final StammdatensatzFileInfo myFileInfo)
			throws DatevException {

		super(myFileNumer, targetDirectory, myFileInfo);
	}

	/**
	 * 
	 * @param buchungssatz
	 *            the data record to be written
	 * @throws DatevException
	 *             if <code>buchungssatz</code> is not valid or if a header
	 *             has not yet been written to output.
	 */
	public void appendBuchungssatz(final OBE_Buchungssatz buchungssatz)
			throws DatevException {

		if (!isHeaderAlreadyWritten()) {
			throw new IllegalStateException(
					"Header must be written first, using 'writeVorlaufsatz'");
		}
		if (!(buchungssatz instanceof OBE_Stammdaten_Buchungssatz)) {
			throw new IllegalArgumentException(
					"Parameter must be instanceof OBE_Bewegungsdaten_Buchungssatz");
		}
		OBE_Stammdaten_Buchungssatz stammdatenSatz = (OBE_Stammdaten_Buchungssatz) buchungssatz;

		OBE_Verwaltungsdatei.validate(buchungssatz);

		StringBuffer sb = new StringBuffer();

		sb.append(stammdatenSatz.getKennziffer());
		sb.append(OBE_Stammdaten_Buchungssatz.TEXTSTART);
		sb.append(stammdatenSatz.getText());
		sb.append(OBE_Stammdaten_Buchungssatz.TEXTENDE);
		sb.append(OBE_Stammdaten_Buchungssatz.SATZENDE);

		addStringBuffer(sb);
	}

	/**
	 * 
	 * Writes the file header ("Vorlaufsatz") to the file.
	 * 
	 * @param vorlaufssatz
	 *            the data record to be written
	 * @throws DatevException
	 *             if <code>vorlaufssatz</code> is not valid or if a header
	 *             has not yet been written to output.
	 */
	public void writeVorlaufsatz(final OBE_Vorlaufsatz vorlaufssatz)
			throws DatevException {

		if (!initAsNecessary()) {
			return;
		}
		if (!(vorlaufssatz instanceof OBE_Stammdaten_Kurzvorlauf)) {
			throw new IllegalArgumentException(
					"Parameter must be instanceof OBE_Stammdaten_Kurzvorlauf. Is: "
							+ vorlaufssatz.getClass().getName());
		}
		OBE_Stammdaten_Kurzvorlauf kurzvorlauf = (OBE_Stammdaten_Kurzvorlauf) vorlaufssatz;
		OBE_Verwaltungsdatei.validate(vorlaufssatz);

		StringBuffer sb = new StringBuffer();
		sb.append(OBE_Bewegungsdaten_Vollvorlauf.VORLAUFBEGINN);
		sb.append(OBE_Bewegungsdaten_Vollvorlauf.KENNUNG_NEUER_VORLAUF);
		sb.append(OBE_Bewegungsdaten_Vollvorlauf.VERSIONSNUMMER);
		sb.append(kurzvorlauf.datentraegernummer);
		sb.append(kurzvorlauf.getVorlaufinformationen().getAnwendungsnummer());
		sb.append(kurzvorlauf.getVorlaufinformationen().namenskuerzel);
		sb.append(kurzvorlauf.getVorlaufinformationen().getBeraternummer());
		sb.append(kurzvorlauf.getVorlaufinformationen().getMandantennummer());
		sb.append(kurzvorlauf.getVorlaufinformationen().getAbrechnungsnummer());

		sb.append(kurzvorlauf.getVorlaufinformationen().passwort);
		sb.append(OBE_Bewegungsdaten_Vollvorlauf.ANWENDUGSINFO);
		sb.append(OBE_Bewegungsdaten_Vollvorlauf.INPUT_INFO);
		sb.append(OBE_Bewegungsdaten_Vollvorlauf.SATZENDE);

		addStringBuffer(sb);
		setHeaderWritten();
	}

	@Override
	void addFinalData() {
		
		StringBuffer sb = new StringBuffer();
		sb.append('z');
		addStringBuffer(sb);
	}

}
