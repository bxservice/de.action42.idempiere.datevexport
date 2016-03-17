package org.adempiere.datev.service.impl;

import org.adempiere.datev.service.IAccountingPA;
import org.adempiere.model.I_C_Datev_ExportLog;
import org.adempiere.model.MDatevExportLog;
import org.compiere.model.I_C_ElementValue;
import org.compiere.util.Env;
import org.compiere.model.MElementValue;

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
