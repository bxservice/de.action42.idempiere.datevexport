package de.action42.idempiere.datev.io;

import java.io.File;
import java.sql.Timestamp;
import java.text.DecimalFormat;

import de.action42.idempiere.datev.DatevException;
import de.action42.idempiere.datev.model.CSV_Buchungssatz;
import de.action42.idempiere.datev.model.CSV_Vorlaufsatz;
import de.action42.idempiere.datev.model.acct.BewegungssatzFileInfoCSV;
import de.action42.idempiere.datev.model.acct.CSV_Bewegungsdaten_Buchungssatz;
import de.action42.idempiere.datev.model.acct.CSV_Bewegungsdaten_Vollvorlauf;

/**
 * Creates the data for a DATEV data file containing Buchungsdaten (booking
 * data).
 * 
 * @author ts
 * 
 */
public class CSV_Bewegungssatzdatei extends CSV_Datensatzdatei {

	private double sumUmsatz = 0;

	/**
	 * doesn't actually create the file. The file is created in
	 * {@link #writeVorlaufsatz(CSV_Bewegungsdaten_Vollvorlauf)}.
	 * 
	 * @param targetDirectory
	 * @param dateFrom 
	 * @throws DatevException
	 */
	public CSV_Bewegungssatzdatei(final short myFileNumber,
			final File targetDirectory, Timestamp dateFrom, final BewegungssatzFileInfoCSV myFileInfo)
			throws DatevException {

		
		super(myFileNumber, targetDirectory, dateFrom, myFileInfo);
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
		if (!(buchungssatz instanceof CSV_Bewegungsdaten_Buchungssatz)) {
			throw new IllegalArgumentException(
					"Parameter must be instanceof CSV_Bewegungsdaten_Buchungssatz");
		}
		final CSV_Bewegungsdaten_Buchungssatz bewegungsSatz = (CSV_Bewegungsdaten_Buchungssatz) buchungssatz;

		try {
			CSV_Verwaltungsdatei.validate(bewegungsSatz);
		} catch (DatevException e) {
			throw new DatevException("Unable to handle record '" + buchungssatz
					+ "'", e);
		}
		final StringBuffer sb = new StringBuffer();

		sb.append(bewegungsSatz.getUmsatzStr() + ";"); // 1 
		sb.append("\"" + "S" + "\";"); // 2
		// XXX Waehrung ermitteln!
		sb.append("\"" + "EUR" + "\";"); // 3
		sb.append(";"); // 4 Kurs
		sb.append(";"); // 5 Basis-Umsatz
		sb.append(";"); // 6 Waehrung Basis-Umsatz
		sb.append("\"" + bewegungsSatz.getKonto() + "\";"); // 7
		sb.append("\"" + bewegungsSatz.getGegenkonto() + "\";"); // 8
		sb.append(";"); // 9 BU-Schluessel
		sb.append("\"" + bewegungsSatz.getDatum() + "\";"); // 10
		sb.append("\"" + bewegungsSatz.getBelegfeld1() + "\";"); // 11
		sb.append(";"); // 12
		sb.append(";"); // 13
		sb.append(CSV_Bewegungsdaten_Buchungssatz.SATZENDE);

		addStringBuffer(sb);

		sumUmsatz += bewegungsSatz.getUmsatz();
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
		if (!(vorlaufssatz instanceof CSV_Bewegungsdaten_Vollvorlauf)) {
			throw new IllegalArgumentException(
					"Parameter must be instanceof CSV_Bewegungsdaten_Vollvorlauf");
		}
		final CSV_Bewegungsdaten_Vollvorlauf vollvorlauf = (CSV_Bewegungsdaten_Vollvorlauf) vorlaufssatz;

		CSV_Verwaltungsdatei.validate(vollvorlauf);
		// buchungssatzFileInfo.setFileHeader(vorlaufssatz);

		final StringBuffer sb = new StringBuffer();
		sb.append(CSV_Bewegungsdaten_Vollvorlauf.VORLAUFBEGINN);
		sb.append(CSV_Bewegungsdaten_Vollvorlauf.KENNUNG_NEUER_VORLAUF);
		sb.append(CSV_Bewegungsdaten_Vollvorlauf.VERSIONSNUMMER);
		sb.append(vollvorlauf.datentraegernummer);
		sb.append(vollvorlauf.getVorlaufinformationen().getAnwendungsnummer());

		sb.append(vollvorlauf.getVorlaufinformationen().namenskuerzel);
		sb.append(vollvorlauf.getVorlaufinformationen().getBeraternummer());
		sb.append(vollvorlauf.getVorlaufinformationen().getMandantennummer());
		sb.append(vollvorlauf.getVorlaufinformationen().getAbrechnungsnummer());
		sb.append(vollvorlauf.getVorlaufinformationen().getDatumVon());
		sb.append(vollvorlauf.getVorlaufinformationen().getDatumBis());
		sb.append(vollvorlauf.getVorlaufinformationen().getPrimanotaSeite());
		sb.append(vollvorlauf.getVorlaufinformationen().passwort);
		sb.append(CSV_Bewegungsdaten_Vollvorlauf.ANWENDUGSINFO);
		sb.append(CSV_Bewegungsdaten_Vollvorlauf.INPUT_INFO);
		sb.append(CSV_Bewegungsdaten_Vollvorlauf.SATZENDE);

		//addStringBuffer(sb);
		setHeaderWritten();
	}

	@Override
	void addFinalData() {
//		StringBuffer sb = new StringBuffer();
//		if (sumUmsatz >= 0) {
//			sb.append('x');
//		} else {
//			sb.append('w');
//		}
//		sumUmsatz *= 100;
//		DecimalFormat format = new DecimalFormat("000000000000");
//		sb.append(format.format(sumUmsatz));
//		sb.append("yz");
//
//		addStringBuffer(sb);
	}

}
