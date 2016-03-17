package org.adempiere.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MDatevExportLog extends X_C_Datev_ExportLog {

	/**
	 */
	private static final long serialVersionUID = 1L;

	public MDatevExportLog(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	public MDatevExportLog(Properties ctx, int C_DATEV_EXPORTLOG_ID,
			String trxName) {
		
		super(ctx, C_DATEV_EXPORTLOG_ID, trxName);
	}
}
