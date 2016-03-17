package org.adempiere.datev.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.DecimalFormat;

import org.adempiere.datev.DatevException;
import org.adempiere.datev.model.DatensatzFileInfo;
import org.adempiere.datev.model.OBE_Buchungssatz;
import org.adempiere.datev.model.OBE_Vorlaufsatz;
import org.adempiere.datev.model.acct.OBE_Bewegungsdaten_Buchungssatz;

public abstract class OBE_Datensatzdatei {

	private boolean headerAlreadyWritten;
	private boolean fileCreated;

	// @NotNegative
	// @Min(999)
	private final short fileNumber;

	private Writer writer;
	private StringBuffer currentBlock = new StringBuffer();

	private final File outputfile;
	private final DatensatzFileInfo datensatzFileInfo;

	public OBE_Datensatzdatei(final short myFileNumer,
			final File targetDirectory, final DatensatzFileInfo myFileInfo)
			throws DatevException {

		fileNumber = myFileNumer;

		DecimalFormat decimalFormat = new DecimalFormat("000");

		String fileName = "DE" + decimalFormat.format(fileNumber);

		outputfile = new File(targetDirectory, fileName);
		datensatzFileInfo = myFileInfo;
	}

	public final boolean isFileCreated() {
		return fileCreated;
	}

	final boolean isHeaderAlreadyWritten() {
		return headerAlreadyWritten;
	}

	final short getFileNumber() {
		return fileNumber;
	}

	final Writer getWriter() {
		return writer;
	}

	final StringBuffer getCurrentBlock() {
		return currentBlock;
	}

	final File getOutputfile() {
		return outputfile;
	}

	final boolean initAsNecessary() {

		if (headerAlreadyWritten) {
			return false;
		}

		if (!fileCreated) {
			try {
				writer = new OutputStreamWriter(
						new FileOutputStream(outputfile), Charset
								.forName("cp1252"));

			} catch (FileNotFoundException e) {
				throw new DatevException(e);
			}
		}
		return true;
	}

	final void setHeaderWritten() {
		headerAlreadyWritten = true;
	}

	public final void finish() throws DatevException {

		addFinalData();

		datensatzFileInfo
				.setLastBlockPos((short) (getCurrentBlock().length() - 1));
		fillCurrentBlock();

		datensatzFileInfo.incBlockCount();

		try {
			writer.write(getCurrentBlock().toString());
			writer.close();
		} catch (IOException e) {
			throw new DatevException(e);
		}
	}

	final void addStringBuffer(StringBuffer sb) throws DatevException {

		if (getCurrentBlock().length() + sb.length() > 250) {

			fillCurrentBlock();
			try {
				getWriter().write(currentBlock.toString());
				datensatzFileInfo.incBlockCount();
			} catch (IOException e) {
				throw new DatevException(e);
			}
			getCurrentBlock().setLength(0);
		}
		getCurrentBlock().append(sb);
	}

	private void fillCurrentBlock() {
		for (int i = getCurrentBlock().length(); i < 256; i++) {
			getCurrentBlock().append(OBE_Bewegungsdaten_Buchungssatz.FILLER);
		}
	}

	abstract void addFinalData();

	public abstract void writeVorlaufsatz(OBE_Vorlaufsatz header);

	public abstract void appendBuchungssatz(final OBE_Buchungssatz buchungssatz);
}
