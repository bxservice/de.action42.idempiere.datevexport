package de.action42.idempiere.datev.io;

import java.io.File;
import java.text.DecimalFormat;

import de.action42.idempiere.datev.DatevException;
import de.action42.idempiere.datev.model.OBE_Buchungssatz;
import de.action42.idempiere.datev.model.OBE_Vorlaufsatz;
import de.action42.idempiere.datev.model.acct.BewegungssatzFileInfo;
import de.action42.idempiere.datev.model.acct.OBE_Bewegungsdaten_Buchungssatz;
import de.action42.idempiere.datev.model.acct.OBE_Bewegungsdaten_Vollvorlauf;

/**
 * Creates the data for a DATEV data file containing Buchungsdaten (booking
 * data).
 * 
 * @author ts
 * 
 */
public class OBE_Bewegunssatzdatei extends OBE_Datensatzdatei {

	private double sumUmsatz = 0;

	/**
	 * doesn't actually create the file. The file is created in
	 * {@link #writeVorlaufsatz(OBE_Bewegungsdaten_Vollvorlauf)}.
	 * 
	 * @param targetDirectory
	 * @throws DatevException
	 */
	public OBE_Bewegunssatzdatei(final short myFileNumer,
			final File targetDirectory, final BewegungssatzFileInfo myFileInfo)
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
		if (!(buchungssatz instanceof OBE_Bewegungsdaten_Buchungssatz)) {
			throw new IllegalArgumentException(
					"Parameter must be instanceof OBE_Bewegungsdaten_Buchungssatz");
		}
		final OBE_Bewegungsdaten_Buchungssatz bewegungsSatz = (OBE_Bewegungsdaten_Buchungssatz) buchungssatz;

		try {
			OBE_Verwaltungsdatei.validate(bewegungsSatz);
		} catch (DatevException e) {
			throw new DatevException("Unable to handle record '" + buchungssatz
					+ "'", e);
		}
		final StringBuffer sb = new StringBuffer();

		sb.append(bewegungsSatz.getUmsatzStr());
		sb.append(bewegungsSatz.getGegenkonto());
		sb.append(bewegungsSatz.getBelegfeld1());
		sb.append(bewegungsSatz.getBelegfeld2());
		sb.append(bewegungsSatz.getDatum());
		sb.append(bewegungsSatz.getKonto());
		sb.append(bewegungsSatz.getKost1());
		sb.append(bewegungsSatz.getKost2());
		sb.append(bewegungsSatz.getSkonto());
		sb.append(bewegungsSatz.getBuchungstext());
		sb.append(bewegungsSatz.getEuId());
		sb.append(bewegungsSatz.getEuSteuersatz());
		sb.append(OBE_Bewegungsdaten_Buchungssatz.WAEHRUNGSKENNUNG);
		sb.append(OBE_Bewegungsdaten_Buchungssatz.SATZENDE);

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
	public void writeVorlaufsatz(final OBE_Vorlaufsatz vorlaufssatz)
			throws DatevException {

		if (!initAsNecessary()) {
			return;
		}
		if (!(vorlaufssatz instanceof OBE_Bewegungsdaten_Vollvorlauf)) {
			throw new IllegalArgumentException(
					"Parameter must be instanceof OBE_Bewegungsdaten_Vollvorlauf");
		}
		final OBE_Bewegungsdaten_Vollvorlauf vollvorlauf = (OBE_Bewegungsdaten_Vollvorlauf) vorlaufssatz;

		OBE_Verwaltungsdatei.validate(vollvorlauf);
		// buchungssatzFileInfo.setFileHeader(vorlaufssatz);

		final StringBuffer sb = new StringBuffer();
		sb.append(OBE_Bewegungsdaten_Vollvorlauf.VORLAUFBEGINN);
		sb.append(OBE_Bewegungsdaten_Vollvorlauf.KENNUNG_NEUER_VORLAUF);
		sb.append(OBE_Bewegungsdaten_Vollvorlauf.VERSIONSNUMMER);
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
		sb.append(OBE_Bewegungsdaten_Vollvorlauf.ANWENDUGSINFO);
		sb.append(OBE_Bewegungsdaten_Vollvorlauf.INPUT_INFO);
		sb.append(OBE_Bewegungsdaten_Vollvorlauf.SATZENDE);

		addStringBuffer(sb);
		setHeaderWritten();
	}

	@Override
	void addFinalData() {
		StringBuffer sb = new StringBuffer();
		if (sumUmsatz >= 0) {
			sb.append('x');
		} else {
			sb.append('w');
		}
		sumUmsatz *= 100;
		DecimalFormat format = new DecimalFormat("000000000000");
		sb.append(format.format(sumUmsatz));
		sb.append("yz");

		addStringBuffer(sb);
	}

}
