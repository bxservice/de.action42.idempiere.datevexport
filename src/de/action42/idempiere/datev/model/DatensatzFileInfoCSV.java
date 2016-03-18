package de.action42.idempiere.datev.model;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;

import de.action42.idempiere.datev.io.CSV_Datensatzdatei;

public abstract class DatensatzFileInfoCSV {

	private final File directoryName;

	private short fileNumber;

	private short blockCount = 0;

	private short lastBlockPos = 0;

	public DatensatzFileInfoCSV(final File myDirectory) {

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

	public abstract List<? extends CSV_Buchungssatz> getDataRecordsCSV();

	public abstract CSV_Datensatzdatei getFileCSV(Timestamp dateFrom);
	
	public abstract CSV_Vorlaufsatz getFileHeaderCSV();


}