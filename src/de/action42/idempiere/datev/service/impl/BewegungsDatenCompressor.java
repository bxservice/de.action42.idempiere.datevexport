package de.action42.idempiere.datev.service.impl;

import java.util.List;

import org.compiere.util.CLogger;

import de.action42.idempiere.datev.model.acct.BewegungssatzFileInfo;
import de.action42.idempiere.datev.model.acct.BewegungssatzFileInfoCSV;
import de.action42.idempiere.datev.model.acct.CSV_Bewegungsdaten_Buchungssatz;
import de.action42.idempiere.datev.model.acct.OBE_Bewegungsdaten_Buchungssatz;
import de.action42.idempiere.datev.service.IBewegungsSatzProcessor;
import de.action42.idempiere.datev.util.FactAcctTool;

public final class BewegungsDatenCompressor implements IBewegungsSatzProcessor {

	private static final CLogger logger = CLogger
			.getCLogger(BewegungsDatenCompressor.class);

	/**
	 * 
	 */
	public BewegungssatzFileInfo process(
			final BewegungssatzFileInfo recordsToProcess) {

		if (recordsToProcess == null) {
			throw new IllegalArgumentException("Parameter may not be null");
		}

		logger.info("Beeing called with "
				+ recordsToProcess.getDataRecords().size() + " records");

		final BewegungssatzFileInfo result = new BewegungssatzFileInfo(
				recordsToProcess.getDirectoryName());
		result.setFileHeader(recordsToProcess.getFileHeader());

		final List<OBE_Bewegungsdaten_Buchungssatz> resultList = FactAcctTool
				.compress(recordsToProcess.getDataRecords());

		for (final OBE_Bewegungsdaten_Buchungssatz dataRecord : resultList) {
			result.addDataRecord(dataRecord);
		}

		logger.info("Returning " + resultList.size() + " records");

		return result;
	}

	public BewegungssatzFileInfoCSV processCSV(
			final BewegungssatzFileInfoCSV recordsToProcess) {

		if (recordsToProcess == null) {
			throw new IllegalArgumentException("Parameter may not be null");
		}

		logger.info("Beeing called with "
				+ recordsToProcess.getDataRecordsCSV().size() + " records");

		final BewegungssatzFileInfoCSV result = new BewegungssatzFileInfoCSV(
				recordsToProcess.getDirectoryName());
		result.setFileHeaderCSV(recordsToProcess.getFileHeaderCSV());

		final List<CSV_Bewegungsdaten_Buchungssatz> resultList = FactAcctTool
				.compressCSV(recordsToProcess.getDataRecordsCSV());

		for (final CSV_Bewegungsdaten_Buchungssatz dataRecord : resultList) {
			result.addDataRecordCSV(dataRecord);
		}

		logger.info("Returning " + resultList.size() + " records");

		return result;
	}
}
