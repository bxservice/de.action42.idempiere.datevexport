package org.adempiere.datev.io;

import java.io.File;
import java.text.DecimalFormat;

import org.adempiere.datev.DatevException;
import org.adempiere.datev.model.CSV_Buchungssatz;
import org.adempiere.datev.model.CSV_Vorlaufsatz;
import org.adempiere.datev.model.acct.BewegungssatzFileInfoCSV;
import org.adempiere.datev.model.acct.CSV_Bewegungsdaten_Buchungssatz;
import org.adempiere.datev.model.acct.CSV_Bewegungsdaten_Vollvorlauf;

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
	 * @throws DatevException
	 */
	public CSV_Bewegungssatzdatei(final short myFileNumber,
			final File targetDirectory, final BewegungssatzFileInfoCSV myFileInfo)
			throws DatevException {

		
		super(myFileNumber, targetDirectory, myFileInfo);
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
		// XXX Belegfeld1 ist fuer OPOS !? => erst mal leer lassen
		//sb.append("\"" + bewegungsSatz.getBelegfeld1() + "\";"); // 11
		sb.append(";"); // 11
		// XXX Belegfeld2 soll Rg-Nummer sein, steht jedoch nicht drin!? => erst mal Belegfeld1 verwenden
		//sb.append("\"" + bewegungsSatz.getBelegfeld2() + "\";"); // 12
		sb.append("\"" + bewegungsSatz.getBelegfeld1() + "\";"); // 12
		sb.append(bewegungsSatz.getSkonto()); // 13
		sb.append("\"" + bewegungsSatz.getBuchungstext() + "\";"); // 14
		sb.append(";"); // 15
		sb.append(";"); // 16
		sb.append(";"); // 17
		sb.append(";"); // 18
		sb.append(";"); // 19
		sb.append(";"); // 20
		sb.append(";"); // 21
		sb.append(";"); // 22
		sb.append(";"); // 23
		sb.append(";"); // 24
		sb.append(";"); // 25
		sb.append(";"); // 26
		sb.append(";"); // 27
		sb.append(";"); // 28
		sb.append(";"); // 29
		sb.append(";"); // 30
		sb.append(";"); // 31
		sb.append(";"); // 32
		sb.append(";"); // 33
		sb.append(";"); // 34
		sb.append(";"); // 35
		sb.append(";"); // 36
		sb.append(bewegungsSatz.getKost1());
		sb.append(bewegungsSatz.getKost2());
		sb.append(";"); // 39
		sb.append(bewegungsSatz.getEuId()); // 40
		sb.append(bewegungsSatz.getEuSteuersatz()); // 41
		sb.append(";"); // 42
		sb.append(";"); // 43
		sb.append(";"); // 44
		sb.append(";"); // 45
		sb.append(";"); // 46
		sb.append(";"); // 47
		sb.append(";"); // 48
		sb.append(";"); // 49
		sb.append(";"); // 50
		sb.append(";"); // 51
		sb.append(";"); // 52
		sb.append(";"); // 53
		sb.append(";"); // 54
		sb.append(";"); // 55
		sb.append(";"); // 56
		sb.append(";"); // 57
		sb.append(";"); // 58
		sb.append(";"); // 59
		sb.append(";"); // 60
		sb.append(";"); // 61
		sb.append(";"); // 62
		sb.append(";"); // 63
		sb.append(";"); // 64
		sb.append(";"); // 65
		sb.append(";"); // 66
		sb.append(";"); // 67
		sb.append(";"); // 68
		sb.append(";"); // 69
		sb.append(";"); // 70
		sb.append(";"); // 71
		sb.append(";"); // 72
		sb.append(";"); // 73
		sb.append(";"); // 74
		sb.append(";"); // 75
		sb.append(";"); // 76
		sb.append(";"); // 77
		sb.append(";"); // 78
		sb.append(";"); // 79
		sb.append(";"); // 80
		sb.append(";"); // 81
		sb.append(";"); // 82
		sb.append(";"); // 83
		sb.append(";"); // 84
		sb.append(";"); // 85
		sb.append(";"); // 86
		sb.append(";"); // 87
		sb.append(";"); // 88
		sb.append(";"); // 89
		//sb.append(CSV_Bewegungsdaten_Buchungssatz.WAEHRUNGSKENNUNG);
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
