package de.action42.idempiere.datev.service;

import de.action42.idempiere.datev.model.acct.BewegungssatzFileInfo;
import de.action42.idempiere.datev.model.acct.BewegungssatzFileInfoCSV;

public interface IBewegungsSatzProcessor {

	BewegungssatzFileInfo process(BewegungssatzFileInfo records);
	BewegungssatzFileInfoCSV processCSV(BewegungssatzFileInfoCSV records);
	
}
