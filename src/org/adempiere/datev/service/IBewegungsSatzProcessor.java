package org.adempiere.datev.service;

import org.adempiere.datev.model.acct.BewegungssatzFileInfo;
import org.adempiere.datev.model.acct.BewegungssatzFileInfoCSV;

public interface IBewegungsSatzProcessor {

	BewegungssatzFileInfo process(BewegungssatzFileInfo records);
	BewegungssatzFileInfoCSV processCSV(BewegungssatzFileInfoCSV records);
	
}
