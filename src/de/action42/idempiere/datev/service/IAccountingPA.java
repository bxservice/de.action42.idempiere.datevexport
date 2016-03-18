package de.action42.idempiere.datev.service;

import org.compiere.model.I_C_ElementValue;

import de.action42.idempiere.model.I_C_Datev_ExportLog;

public interface IAccountingPA {

	I_C_ElementValue retrieveElementValue(int elementValueId, String trxName);

	I_C_Datev_ExportLog createDatevExportLog(int adOrgId, String trxName);
}
