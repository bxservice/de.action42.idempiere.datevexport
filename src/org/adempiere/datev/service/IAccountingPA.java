package org.adempiere.datev.service;

import org.adempiere.model.I_C_Datev_ExportLog;
import org.compiere.model.I_C_ElementValue;

public interface IAccountingPA {

	I_C_ElementValue retrieveElementValue(int elementValueId, String trxName);

	I_C_Datev_ExportLog createDatevExportLog(int adOrgId, String trxName);
}
