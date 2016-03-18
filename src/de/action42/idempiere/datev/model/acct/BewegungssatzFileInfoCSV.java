package de.action42.idempiere.datev.model.acct;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.action42.idempiere.datev.DatevException;
import de.action42.idempiere.datev.io.CSV_Bewegungssatzdatei;
import de.action42.idempiere.datev.io.CSV_Datensatzdatei;
import de.action42.idempiere.datev.model.DatensatzFileInfoCSV;

/**
 * This class contains information that is gathered while the single accounting
 * records (Buchungssaetze) are set up for export.
 * 
 * @author ts
 * 
 */
public class BewegungssatzFileInfoCSV extends DatensatzFileInfoCSV {

	private CSV_Bewegungsdaten_Vollvorlauf fileHeaderCSV;

	private CSV_Datensatzdatei fileCSV;

	private HashMap<Integer, CSV_Bewegungsdaten_Buchungssatz> id2DataRecordCSV = new HashMap<Integer, CSV_Bewegungsdaten_Buchungssatz>();

	public BewegungssatzFileInfoCSV(final File myDirectory) {

		super(myDirectory);
	}

	public final CSV_Datensatzdatei getFileCSV(Timestamp dateFrom) {
		if (fileCSV == null) {

//			if (getFileNumber() == 0 || getDirectoryName() == null) {
//				throw new DatevException(
//						"Both members filenumber and directory name must be set. filenumer='"
//								+ getFileNumber() + "'; directoryName='"
//								+ getDirectoryName() + "'");
//			}
			setFileNumber((short) 1); // XXX Buchungssatz erzwingen
			fileCSV = new CSV_Bewegungssatzdatei(getFileNumber(),
					getDirectoryName(), dateFrom, this);
		}
		return fileCSV;
	}
	
	public final CSV_Bewegungsdaten_Vollvorlauf getFileHeaderCSV() {
		return fileHeaderCSV;
	}

	public final void setFileHeaderCSV(CSV_Bewegungsdaten_Vollvorlauf fileHeader) {
		this.fileHeaderCSV = fileHeader;
	}

	/**
	 * Adds the given data record to this instance, iff the record's
	 * {@link OBE_Bewegungsdaten_Buchungssatz#getUmsatz()} method returns not
	 * 0.0
	 * 
	 * @param dataRecord
	 *            the record to be added (if its
	 *            {@link OBE_Bewegungsdaten_Buchungssatz#getUmsatz()} returns
	 *            returns a value != 0.0)
	 * @return <code>true</code> if the record has been added,
	 *         <code>false</code> otherwise.
	 */
	public boolean addDataRecordCSV(CSV_Bewegungsdaten_Buchungssatz dataRecord) {

		if (dataRecord.getUmsatz() == 0.0) {
			return false;
		}
		id2DataRecordCSV.put(dataRecord.getId(), dataRecord);
		return true;
	}

	public List<CSV_Bewegungsdaten_Buchungssatz> getDataRecordsCSV() {
		
		return new ArrayList<CSV_Bewegungsdaten_Buchungssatz>(id2DataRecordCSV
				.values());
	}
}
