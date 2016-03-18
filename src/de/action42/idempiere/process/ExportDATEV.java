/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 * Contributor: Carlos Ruiz - globalqss                                       *
 *****************************************************************************/
package de.action42.idempiere.process;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.compiere.model.MSysConfig;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import de.action42.idempiere.datev.AddOnStarter;
import de.action42.idempiere.datev.DatevProperties;
import de.action42.idempiere.datev.IDatevSettings;
import de.action42.idempiere.datev.service.IMasterDataService;
import de.action42.idempiere.datev.service.Worker;
import de.action42.idempiere.datev.service.impl.BewegungsDatenCompressor;
import de.action42.idempiere.datev.service.impl.FactAcctLoader;
import de.action42.idempiere.datev.service.impl.MasterDataService;

/**
 *	Export DATEV Data for time frame 
 *
 * 	@author 	Jorg Janke
 * 	@version 	$Id: ImportBPartner.java,v 1.2 2006/07/30 00:51:02 jjanke Exp $
 * 
 * @author Teo Sarca, www.arhipac.ro
 * 			<li>FR [ 2788074 ] ImportBPartner: add IsValidateOnly option
 * 				https://sourceforge.net/tracker/?func=detail&aid=2788074&group_id=176962&atid=879335
 * 			<li>FR [ 2788278 ] Data Import Validator - migrate core processes
 * 				https://sourceforge.net/tracker/?func=detail&aid=2788278&group_id=176962&atid=879335
 */
public class ExportDATEV extends SvrProcess

{
	public static final String EXPORT = "datev.export";
	public static final String UNTIL = "datev.until";
	public static final String FROM = "datev.from";
	public static final String ZEITRAUM = "datev.timerange";
	public static final String SELECT_TARGET_DIR = "datev.select_target_dir";
	public static final String ERROR_DATES_ORDERING = "datev.err_dates_ordering";
	public static final String QUESTION_DELETE_EXITING = "datev_q_delete_existing";
	public static final String WARNING_NOTHING_EXPORTED = "datev.warn_nothing_exported";
	public static final String WARNING_FILE_DELETION_FAILED = "datev.warn_file_deletion_failed";
	public static final String ONE_RECORD_EXPORTED = "datev.one_record_exported";
	public static final String MULTIPLE_RECORD_EXPORTED = "datev.multiple_records_exported";
	public static final String ORG = "AD_Org_ID";

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 5984471103441104882L;

	/**	Client to be imported to		*/
	private int				m_AD_Org_ID = 0;

	/** Effective						*/
	private Timestamp		m_DateFrom = null;
	private Timestamp		m_DateTo = null;

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (name.equals("AD_Org_ID"))
				m_AD_Org_ID = ((BigDecimal)para[i].getParameter()).intValue();
			else if (name.equals("DateValue"))
				{
					m_DateFrom = (Timestamp)para[i].getParameter();
					m_DateTo = (Timestamp)para[i].getParameter_To();
				}
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
	}	//	prepare


	/**
	 *  Perform process.
	 *  @return Message
	 *  @throws Exception
	 */
	protected String doIt() throws java.lang.Exception
	{

		// FIXME check if directory exists and is writable
		final File exportDir = new File(MSysConfig.getValue("DATEV_EXPORT_DIR",null,Env.getAD_Client_ID(Env.getCtx()),Env.getAD_Org_ID(Env.getCtx())));
		final IDatevSettings settings = new DatevProperties();
		final int clientId = Env.getAD_Client_ID(getCtx());
		String msg = null;

		new AddOnStarter().initAddon();

		final Worker worker = new Worker( //
				exportDir, //
				m_AD_Org_ID, //
				m_DateFrom, //
				m_DateTo, settings);

		final IMasterDataService masterDataService = new MasterDataService(
				exportDir, settings);
		worker.setMasterDataService(masterDataService);
		worker.setProcessor(new BewegungsDatenCompressor());
		worker.setLoader(new FactAcctLoader(clientId, m_AD_Org_ID,
						masterDataService));


		try {
			//worker.exportData();
			worker.exportDataCSV();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error while Exporting!", e.getMessage());
			msg = e.getMessage();
			return "@Result@ = " + msg;
		}
		int exportedRecords = worker.getExportedRecords();
		if (exportedRecords == 0) {
			log.log(Level.INFO, WARNING_NOTHING_EXPORTED);
			msg = Msg.getMsg(Env.getCtx(), WARNING_NOTHING_EXPORTED);
		}
		else if (exportedRecords == 1) {
			msg = Msg.getMsg(Env.getCtx(), ONE_RECORD_EXPORTED);
		} else {
			msg = Msg.getMsg(Env.getCtx(), MULTIPLE_RECORD_EXPORTED,
					new Object[] { exportedRecords });
		}

        return "@Result@ = " + msg;
	}	//	doIt


	
}	//	ExportDATEV
