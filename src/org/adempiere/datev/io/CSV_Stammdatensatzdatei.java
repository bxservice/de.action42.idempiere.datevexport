package org.adempiere.datev.io;

import java.io.File;
import java.sql.Timestamp;

import org.adempiere.datev.DatevException;
import org.adempiere.datev.model.CSV_Buchungssatz;
import org.adempiere.datev.model.CSV_Vorlaufsatz;
import org.adempiere.datev.model.acct.CSV_Bewegungsdaten_Vollvorlauf;
import org.adempiere.datev.model.masterdata.CSV_Stammdaten_Buchungssatz;
import org.adempiere.datev.model.masterdata.CSV_Stammdaten_Kurzvorlauf;
import org.adempiere.datev.model.masterdata.StammdatensatzFileInfoCSV;

/**
 * Creates the data for a DATEV data file containing Stammdaten (master data).
 * 
 * @author ts
 * 
 */
public class CSV_Stammdatensatzdatei extends CSV_Datensatzdatei {

	/**
	 * doesn't actually create the file. The file is created in
	 * {@link #writeVorlaufsatz(CSV_Bewegungsdaten_Vollvorlauf)}.
	 * 
	 * @param targetDirectory
	 * @param dateFrom 
	 * @throws DatevException
	 */
	public CSV_Stammdatensatzdatei(final short myFileNumer,
			final File targetDirectory, Timestamp dateFrom, final StammdatensatzFileInfoCSV myFileInfo)
			throws DatevException {

		super(myFileNumer, targetDirectory, dateFrom, myFileInfo); 
	}

	/**
	 * 
	 * @param buchungssatz
	 *            the data record to be written
	 * @throws DatevException
	 *             if <code>buchungssatz</code> is not valid or if a header
	 *             has not yet been written to output.
	 */
	public void appendBuchungssatz(final CSV_Buchungssatz buchungssatz)
			throws DatevException {

//		if (!isHeaderAlreadyWritten()) {
//			throw new IllegalStateException(
//					"Header must be written first, using 'writeVorlaufsatz'");
//		}
		if (!(buchungssatz instanceof CSV_Stammdaten_Buchungssatz)) {
			throw new IllegalArgumentException(
					"Parameter must be instanceof CSV_Bewegungsdaten_Buchungssatz");
		}
		CSV_Stammdaten_Buchungssatz stammdatenSatz = (CSV_Stammdaten_Buchungssatz) buchungssatz;

		CSV_Verwaltungsdatei.validate(buchungssatz);

		StringBuffer sb = new StringBuffer();

		sb.append(stammdatenSatz.getKennziffer());
//		sb.append(CSV_Stammdaten_Buchungssatz.TEXTSTART);
		sb.append(stammdatenSatz.getText());
//		sb.append(CSV_Stammdaten_Buchungssatz.TEXTENDE);
//		sb.append(CSV_Stammdaten_Buchungssatz.SATZENDE);
		
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
	public void writeVorlaufsatz(final CSV_Vorlaufsatz vorlaufssatz)
			throws DatevException {

		if (!initAsNecessary()) {
			return;
		}
		if (!(vorlaufssatz instanceof CSV_Stammdaten_Kurzvorlauf)) {
			throw new IllegalArgumentException(
					"Parameter must be instanceof CSV_Stammdaten_Kurzvorlauf. Is: "
							+ vorlaufssatz.getClass().getName());
		}
		CSV_Stammdaten_Kurzvorlauf kurzvorlauf = (CSV_Stammdaten_Kurzvorlauf) vorlaufssatz;
		CSV_Verwaltungsdatei.validate(vorlaufssatz);

		StringBuffer sb = new StringBuffer();
		sb.append(CSV_Bewegungsdaten_Vollvorlauf.VORLAUFBEGINN);
		sb.append(CSV_Bewegungsdaten_Vollvorlauf.KENNUNG_NEUER_VORLAUF);
		sb.append(CSV_Bewegungsdaten_Vollvorlauf.VERSIONSNUMMER);
		sb.append(kurzvorlauf.datentraegernummer);
		sb.append(kurzvorlauf.getVorlaufinformationen().getAnwendungsnummer());
		sb.append(kurzvorlauf.getVorlaufinformationen().namenskuerzel);
		sb.append(kurzvorlauf.getVorlaufinformationen().getBeraternummer());
		sb.append(kurzvorlauf.getVorlaufinformationen().getMandantennummer());
		sb.append(kurzvorlauf.getVorlaufinformationen().getAbrechnungsnummer());

		sb.append(kurzvorlauf.getVorlaufinformationen().passwort);
		sb.append(CSV_Bewegungsdaten_Vollvorlauf.ANWENDUGSINFO);
		sb.append(CSV_Bewegungsdaten_Vollvorlauf.INPUT_INFO);
		sb.append(CSV_Bewegungsdaten_Vollvorlauf.SATZENDE);

//		addStringBuffer(sb);
		setHeaderWritten();
	}

	@Override
	void addFinalData() {
		
		StringBuffer sb = new StringBuffer();
		//sb.append('\n');
		addStringBuffer(sb);
	}

}
