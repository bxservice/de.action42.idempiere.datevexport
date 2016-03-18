package de.action42.idempiere.datev.model.masterdata;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

import de.action42.idempiere.datev.DatevException;
import de.action42.idempiere.datev.io.CSV_Stammdatensatzdatei;
import de.action42.idempiere.datev.model.DatensatzFileInfoCSV;

/**
 * This class contains information that is gathered while the single accounting
 * records (Buchungssaetze) are set up for export.
 * 
 * @author ts
 * 
 */
public class StammdatensatzFileInfoCSV extends DatensatzFileInfoCSV {

	private CSV_Stammdaten_Kurzvorlauf fileHeaderCSV;

	private SortedMap<Integer, CSV_Stammdaten_Buchungssatz> id2DataRecordCSV = new TreeMap<Integer, CSV_Stammdaten_Buchungssatz>();

	private CSV_Stammdatensatzdatei fileCSV;

	public StammdatensatzFileInfoCSV(File myDirectory) {
		super(myDirectory);
	}

	public final CSV_Stammdaten_Kurzvorlauf getFileHeaderCSV() {
		return fileHeaderCSV;
	}

	public final void setFileHeaderCSV(CSV_Stammdaten_Kurzvorlauf fileHeader) {
		this.fileHeaderCSV = fileHeader;
	}

	/**
	 * Adds the given data record to this instance.
	 * 
	 * @param dataRecord
	 *            the record to be added
	 * @return <code>true</code> if the record has been added,
	 *         <code>false</code> otherwise.
	 */

	public boolean addDataRecordCSV(CSV_Stammdaten_Buchungssatz dataRecord) {

		id2DataRecordCSV.put(dataRecord.getId(), dataRecord);
		return true;
	}

	public ArrayList<CSV_Stammdaten_Buchungssatz> getDataRecordsCSV() {

		return new ArrayList<CSV_Stammdaten_Buchungssatz>(id2DataRecordCSV
				.values());
	}

	public final CSV_Stammdatensatzdatei getFileCSV(Timestamp dateFrom) {
		if (fileCSV == null) {

//			if (getFileNumber() == 0 || getDirectoryName() == null) {
//				throw new DatevException(
//						"Both members filenumber and directory name must be set. filenumer='"
//								+ getFileNumber() + "'; directoryName='"
//								+ getDirectoryName() + "'");
//			}
			setFileNumber((short) 2); // XXX Stammdatensatz erzwingen
			fileCSV = new CSV_Stammdatensatzdatei(getFileNumber(),
					getDirectoryName(), dateFrom, this);
		}
		return fileCSV;
	}
}
