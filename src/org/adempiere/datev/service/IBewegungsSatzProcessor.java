package org.adempiere.datev.service;

import org.adempiere.datev.model.acct.BewegungssatzFileInfo;

public interface IBewegungsSatzProcessor {

	BewegungssatzFileInfo process(BewegungssatzFileInfo records);
	
}
