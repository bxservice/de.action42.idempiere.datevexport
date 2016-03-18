package de.action42.idempiere.datev.service.impl;

import org.compiere.model.I_C_ElementValue;
import org.compiere.util.Env;
import org.compiere.model.MElementValue;

import de.action42.idempiere.datev.service.IAccountingPA;
import de.action42.idempiere.model.I_C_Datev_ExportLog;
import de.action42.idempiere.model.MDatevExportLog;

public class AccountingPA implements IAccountingPA {

	public I_C_Datev_ExportLog createDatevExportLog(int adOrgId, String trxName) {

		final MDatevExportLog result = new MDatevExportLog(Env.getCtx(), 0,
				trxName);
		result.setAD_Org_ID(adOrgId);
		return result;
	}

	public I_C_ElementValue retrieveElementValue(final int elementValueId,
			final String trxName) {

		return new MElementValue(Env.getCtx(), elementValueId, trxName);
	}

}
