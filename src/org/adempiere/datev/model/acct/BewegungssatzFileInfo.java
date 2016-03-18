package org.adempiere.datev.model.acct;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.adempiere.datev.DatevException;
import org.adempiere.datev.io.OBE_Bewegunssatzdatei;
import org.adempiere.datev.io.OBE_Datensatzdatei;
import org.adempiere.datev.model.DatensatzFileInfo;

/**
 * This class contains information that is gathered while the single accounting
 * records (Buchungssaetze) are set up for export.
 * 
 * @author ts
 * 
 */
public class BewegungssatzFileInfo extends DatensatzFileInfo {

	private OBE_Bewegungsdaten_Vollvorlauf fileHeader;

	private OBE_Datensatzdatei file;

	private HashMap<Integer, OBE_Bewegungsdaten_Buchungssatz> id2DataRecord = new HashMap<Integer, OBE_Bewegungsdaten_Buchungssatz>();

	public BewegungssatzFileInfo(final File myDirectory) {

		super(myDirectory);
	}

	public final OBE_Datensatzdatei getFile() {
		if (file == null) {

			if (getFileNumber() == 0 || getDirectoryName() == null) {
				throw new DatevException(
						"Both members filenumber and directory name must be set. filenumer='"
								+ getFileNumber() + "'; directoryName='"
								+ getDirectoryName() + "'");
			}
			file = new OBE_Bewegunssatzdatei(getFileNumber(),
					getDirectoryName(), this);
		}
		return file;
	}

	
	public final OBE_Bewegungsdaten_Vollvorlauf getFileHeader() {
		return fileHeader;
	}


	public final void setFileHeader(OBE_Bewegungsdaten_Vollvorlauf fileHeader) {
		this.fileHeader = fileHeader;
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
	public boolean addDataRecord(OBE_Bewegungsdaten_Buchungssatz dataRecord) {

		if (dataRecord.getUmsatz() == 0.0) {
			return false;
		}
		id2DataRecord.put(dataRecord.getId(), dataRecord);
		return true;
	}

	public List<OBE_Bewegungsdaten_Buchungssatz> getDataRecords() {
		
		return new ArrayList<OBE_Bewegungsdaten_Buchungssatz>(id2DataRecord
				.values());
	}

}
