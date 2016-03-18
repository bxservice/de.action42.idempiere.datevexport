package de.action42.idempiere.datev.io;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.action42.idempiere.datev.DatevException;
import de.action42.idempiere.datev.model.acct.CSV_Bewegungsdaten_Buchungssatz;
import de.action42.idempiere.datev.model.acct.CSV_Datentraegerkennsatz;
import de.action42.idempiere.datev.model.acct.CSV_Verwaltungssatz;
import de.action42.idempiere.datev.util.StringUtil;

public class CSV_Verwaltungsdatei {

	private boolean headerAlreadyWritten;

	private DataOutputStream writer;

	//private static final Validator validator = new Validator();

	public CSV_Verwaltungsdatei(final File targetDirectory)
			throws DatevException {

		File outputfile = new File(targetDirectory, "DV01");
		try {
			writer = new DataOutputStream(new FileOutputStream(outputfile));

		} catch (FileNotFoundException e) {
			throw new DatevException(e);
		}
	}

	/**
	 * Serializes the given object to the output file
	 * 
	 * @param kennsatz
	 *            the Datentraegerkennsatz (header) to be written
	 * @throws DatevException
	 *             if <code>kennsatz</code> is not valid or if a header has
	 *             already been written to output.
	 */
	public void writeKennsatz(final CSV_Datentraegerkennsatz kennsatz)
			throws DatevException {

		if (headerAlreadyWritten) {
			throw new DatevException(
					"The Datentraegerkennsatz (header) has already been written to the output");
		}
		validate(kennsatz);

		StringBuffer sb = new StringBuffer();
		sb.append(kennsatz.getDatentraegerNummer());
		sb.append(kennsatz.getBeraternummer());
		sb.append(kennsatz.getBeratername());
		sb.append(kennsatz.restartKennzeichen);

		try {
			writer.write(sb.toString().getBytes());
			writer.writeShort(Short.reverseBytes(kennsatz.anzahlDatendateien));
			writer.writeShort(Short.reverseBytes(kennsatz.letzteDatendatei));

			for (int i = writer.size(); i < 64; i++) {
				writer.write(CSV_Datentraegerkennsatz.FILLER);
			}

		} catch (IOException e) {
			throw new DatevException(e);
		}
		headerAlreadyWritten = true;
	}

	/**
	 * 
	 * @param verwaltungssatz
	 *            the data record to be written
	 * @throws DatevException
	 *             if <code>verwaltungssatz</code> is not valid or if a header
	 *             has not yet been written to output.
	 */
	public void writeVerwaltunssatz(final CSV_Verwaltungssatz verwaltungssatz)
			throws DatevException {

		if (!headerAlreadyWritten) {
			throw new DatevException(
					"The Datentraegerkennsatz (header) has not yet been written to the output");
		}
		validate(verwaltungssatz);

		try {
			writer.write(verwaltungssatz.verarbeitungsKennzeichen.getBytes());
			writer.writeShort(Short.reverseBytes(verwaltungssatz.dateiNummer));

			// Vorlaufinformationen

			StringBuffer sbVorlauf = new StringBuffer();
			sbVorlauf.append(verwaltungssatz.vorlaufinformationen
					.getAnwendungsnummer());
			sbVorlauf
					.append(verwaltungssatz.vorlaufinformationen.namenskuerzel);
			sbVorlauf.append(verwaltungssatz.vorlaufinformationen
					.getBeraternummer());
			sbVorlauf.append(verwaltungssatz.vorlaufinformationen
					.getMandantennummer());
			sbVorlauf.append(verwaltungssatz.vorlaufinformationen
					.getAbrechnungsnummer());
			if (verwaltungssatz.getType() == CSV_Verwaltungssatz.Type.STAMMDATEN) {
				for (int i = 0; i < 16; i++) {
					sbVorlauf.append('\040');
				}
			} else {
				sbVorlauf.append(StringUtil.lPad(
						verwaltungssatz.vorlaufinformationen.getDatumVon(), 10,
						'0'));
				sbVorlauf.append(verwaltungssatz.vorlaufinformationen
						.getDatumBis());
			}
			sbVorlauf.append(verwaltungssatz.vorlaufinformationen
					.getPrimanotaSeite());
			sbVorlauf.append(verwaltungssatz.vorlaufinformationen.passwort);

			writer.write(sbVorlauf.toString().getBytes());

			writer.writeShort(Short
					.reverseBytes(verwaltungssatz.letzteBlockNummer));
			writer.writeShort(Short
					.reverseBytes(verwaltungssatz.letzteBlockPos));
			writer.writeShort(Short
					.reverseBytes(verwaltungssatz.letztePrimanotaSeite));

			for (int i = 0; i < 8; i++) {
				writer
						.write(CSV_Verwaltungssatz.LAUFENDE_ABSTIMMSUMME_RESERVIERT);
			}
			writer.write(verwaltungssatz.korrekturKennzeichen.getBytes());
			writer.write(verwaltungssatz.sonderverarbeitung.getBytes());

		} catch (IOException e) {
			throw new DatevException(e);
		}
	}

	public static void validate(final Object value) throws DatevException {

//		final List<ConstraintViolation> contraintViolations = validator
//				.validate(value);
//		if (contraintViolations.isEmpty()) {
//			return;
//		}
//		final StringBuffer msg = new StringBuffer("Invalid record of type ");
//		msg.append(value.getClass());
//		msg.append("; value='");
//		msg.append(value.toString());
//		msg.append("' :\n");
//		for (ConstraintViolation constraintViolation : contraintViolations) {
//			msg.append('\t');
//			msg.append("Error msg: ");
//			msg.append(constraintViolation.getMessage());
//			msg.append(";\t Invalid value: ");
//			msg.append(constraintViolation.getInvalidValue());
//			msg.append('\n');
//		}
//		logger.saveError("", msg.toString());
//		throw new DatevException(msg.toString());
	}

	boolean checkDirectory(final File file) {
		return file.isDirectory();
	}

	public void finish() throws DatevException {
		try {
			writer.close();
		} catch (IOException e) {
			throw new DatevException(e);
		}
	}

}
