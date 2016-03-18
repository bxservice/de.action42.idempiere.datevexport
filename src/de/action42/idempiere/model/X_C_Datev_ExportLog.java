/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2007 ComPiere, Inc. All Rights Reserved.                *
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
 *****************************************************************************/
/** Generated Model - DO NOT CHANGE */
package de.action42.idempiere.model;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;
import org.compiere.model.*;

/** Generated Model for C_Datev_ExportLog
 *  @author Adempiere (generated) 
 *  @version Release 3.4.0s - $Id$ */
public class X_C_Datev_ExportLog extends PO implements I_C_Datev_ExportLog, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

    /** Standard Constructor */
    public X_C_Datev_ExportLog (Properties ctx, int C_Datev_ExportLog_ID, String trxName)
    {
      super (ctx, C_Datev_ExportLog_ID, trxName);
      /** if (C_Datev_ExportLog_ID == 0)
        {
			setC_Datev_ExportLog_ID (0);
			setDateExp (new Timestamp(System.currentTimeMillis()));
			setDocumentNo (null);
        } */
    }

    /** Load Constructor */
    public X_C_Datev_ExportLog (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuffer sb = new StringBuffer ("X_C_Datev_ExportLog[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public I_C_BPartner getC_BPartner() throws Exception 
    {
        Class<?> clazz = MTable.getClass(I_C_BPartner.Table_Name);
        I_C_BPartner result = null;
        try	{
	        Constructor<?> constructor = null;
	    	constructor = clazz.getDeclaredConstructor(new Class[]{Properties.class, int.class, String.class});
    	    result = (I_C_BPartner)constructor.newInstance(new Object[] {getCtx(), new Integer(getC_BPartner_ID()), get_TrxName()});
        } catch (Exception e) {
	        log.log(Level.SEVERE, "(id) - Table=" + Table_Name + ",Class=" + clazz, e);
	        log.saveError("Error", "Table=" + Table_Name + ",Class=" + clazz);
           throw e;
        }
        return result;
    }

	/** Set Geschäftspartner.
		@param C_BPartner_ID 
		Bezeichnet einen Geschäftspartner
	  */
	public void setC_BPartner_ID (int C_BPartner_ID)
	{
		if (C_BPartner_ID < 1) 
			set_Value (COLUMNNAME_C_BPartner_ID, null);
		else 
			set_Value (COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
	}

	/** Get Geschäftspartner.
		@return Bezeichnet einen Geschäftspartner
	  */
	public int getC_BPartner_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set C_Datev_ExportLog_ID.
		@param C_Datev_ExportLog_ID C_Datev_ExportLog_ID	  */
	public void setC_Datev_ExportLog_ID (int C_Datev_ExportLog_ID)
	{
		if (C_Datev_ExportLog_ID < 1)
			 throw new IllegalArgumentException ("C_Datev_ExportLog_ID is mandatory.");
		set_ValueNoCheck (COLUMNNAME_C_Datev_ExportLog_ID, Integer.valueOf(C_Datev_ExportLog_ID));
	}

	/** Get C_Datev_ExportLog_ID.
		@return C_Datev_ExportLog_ID	  */
	public int getC_Datev_ExportLog_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Datev_ExportLog_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Buchungsdatum.
		@param DateAcct 
		Buchungsdatum
	  */
	public void setDateAcct (Timestamp DateAcct)
	{
		set_Value (COLUMNNAME_DateAcct, DateAcct);
	}

	/** Get Buchungsdatum.
		@return Buchungsdatum
	  */
	public Timestamp getDateAcct () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateAcct);
	}

	/** Set DateExp.
		@param DateExp DateExp	  */
	public void setDateExp (Timestamp DateExp)
	{
		if (DateExp == null)
			throw new IllegalArgumentException ("DateExp is mandatory.");
		set_ValueNoCheck (COLUMNNAME_DateExp, DateExp);
	}

	/** Get DateExp.
		@return DateExp	  */
	public Timestamp getDateExp () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateExp);
	}

	/** Set Beleg Nr..
		@param DocumentNo 
		Belegnummer für dieses Dokument
	  */
	public void setDocumentNo (String DocumentNo)
	{
		if (DocumentNo == null)
			throw new IllegalArgumentException ("DocumentNo is mandatory.");

		if (DocumentNo.length() > 255)
		{
			log.warning("Length > 255 - truncated");
			DocumentNo = DocumentNo.substring(0, 255);
		}
		set_ValueNoCheck (COLUMNNAME_DocumentNo, DocumentNo);
	}

	/** Get Beleg Nr..
		@return Belegnummer für dieses Dokument
	  */
	public String getDocumentNo () 
	{
		return (String)get_Value(COLUMNNAME_DocumentNo);
	}
}