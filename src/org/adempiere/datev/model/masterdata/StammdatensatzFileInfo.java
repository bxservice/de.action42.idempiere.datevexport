package org.adempiere.datev.model.masterdata;

import java.io.File;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

import org.adempiere.datev.DatevException;
import org.adempiere.datev.io.OBE_Stammdatensatzdatei;
import org.adempiere.datev.model.DatensatzFileInfo;

/**
 * This class contains information that is gathered while the single accounting
 * records (Buchungssaetze) are set up for export.
 * 
 * @author ts
 * 
 */
public class StammdatensatzFileInfo extends DatensatzFileInfo {

	private OBE_Stammdaten_Kurzvorlauf fileHeader;

	private SortedMap<Integer, OBE_Stammdaten_Buchungssatz> id2DataRecord = new TreeMap<Integer, OBE_Stammdaten_Buchungssatz>();

	private OBE_Stammdatensatzdatei file;

	public StammdatensatzFileInfo(File myDirectory) {
		super(myDirectory);
	}

	public final OBE_Stammdaten_Kurzvorlauf getFileHeader() {
		return fileHeader;
	}

	public final void setFileHeader(OBE_Stammdaten_Kurzvorlauf fileHeader) {
		this.fileHeader = fileHeader;
	}

	/**
	 * Adds the given data record to this instance.
	 * 
	 * @param dataRecord
	 *            the record to be added
	 * @return <code>true</code> if the record has been added,
	 *         <code>false</code> otherwise.
	 */
	public boolean addDataRecord(OBE_Stammdaten_Buchungssatz dataRecord) {

		id2DataRecord.put(dataRecord.getId(), dataRecord);
		return true;
	}

	public ArrayList<OBE_Stammdaten_Buchungssatz> getDataRecords() {

		return new ArrayList<OBE_Stammdaten_Buchungssatz>(id2DataRecord
				.values());
	}

	public final OBE_Stammdatensatzdatei getFile() {
		if (file == null) {

			if (getFileNumber() == 0 || getDirectoryName() == null) {
				throw new DatevException(
						"Both members filenumber and directory name must be set. filenumer='"
								+ getFileNumber() + "'; directoryName='"
								+ getDirectoryName() + "'");
			}
			file = new OBE_Stammdatensatzdatei(getFileNumber(),
					getDirectoryName(), this);
		}
		return file;
	}

}
