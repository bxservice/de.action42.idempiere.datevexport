package de.action42.idempiere.datev.model;

import java.io.File;
import java.util.List;

import de.action42.idempiere.datev.io.OBE_Datensatzdatei;

public abstract class DatensatzFileInfo {

	private final File directoryName;

	private short fileNumber;

	private short blockCount = 0;

	private short lastBlockPos = 0;

	public DatensatzFileInfo(final File myDirectory) {

		this.directoryName = myDirectory;
	}

	public final short getFileNumber() {
		return fileNumber;
	}

	public final short getBlockCount() {
		return blockCount;
	}

	public final void incBlockCount() {
		blockCount++;
	}

	public final void setFileNumber(short fileNumber) {
		this.fileNumber = fileNumber;
	}

	public final short getLastBlockPos() {
		return lastBlockPos;
	}

	public final void setLastBlockPos(short lastBlockPos) {
		this.lastBlockPos = lastBlockPos;
	}

	public final File getDirectoryName() {
		return directoryName;
	}

	public abstract List<? extends OBE_Buchungssatz> getDataRecords();

	public abstract OBE_Datensatzdatei getFile();
	
	public abstract OBE_Vorlaufsatz getFileHeader();

}