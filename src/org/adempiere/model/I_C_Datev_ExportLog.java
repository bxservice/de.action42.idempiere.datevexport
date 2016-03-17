/**********************************************************************
 * This file is part of Adempiere ERP Bazaar                          *
 * http://www.adempiere.org                                           *
 *                                                                    *
 * Copyright (C) Trifon Trifonov.                                     *
 * Copyright (C) Contributors                                         *
 *                                                                    *
 * This program is free software;
 you can redistribute it and/or      *
 * modify it under the terms of the GNU General Public License        *
 * as published by the Free Software Foundation;
 either version 2     *
 * of the License, or (at your option) any later version.             *
 *                                                                    *
 * This program is distributed in the hope that it will be useful,    *
 * but WITHOUT ANY WARRANTY;
 without even the implied warranty of     *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the       *
 * GNU General Public License for more details.                       *
 *                                                                    *
 * You should have received a copy of the GNU General Public License  *
 * along with this program;
 if not, write to the Free Software        *
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,         *
 * MA 02110-1301, USA.                                                *
 *                                                                    *
 * Contributors:                                                      *
 * - Trifon Trifonov (trifonnt@users.sourceforge.net)                 *
 *                                                                    *
 * Sponsors:                                                          *
 * - Company (http://www.site.com)                                    *
 **********************************************************************/
package org.adempiere.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Interface for C_Datev_ExportLog
 *  @author Trifon Trifonov (generated) 
 *  @version Release 3.4.0s
 */
public interface I_C_Datev_ExportLog 
{

    /** TableName=C_Datev_ExportLog */
    public static final String Table_Name = "C_Datev_ExportLog";

    /** AD_Table_ID=1000002 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 3 - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(3);

    /** Load Meta Data */

    /** Column name C_BPartner_ID */
    public static final String COLUMNNAME_C_BPartner_ID = "C_BPartner_ID";

	/** Set Geschäftspartner.
	  * Bezeichnet einen Geschäftspartner
	  */
	public void setC_BPartner_ID (int C_BPartner_ID);

	/** Get Geschäftspartner.
	  * Bezeichnet einen Geschäftspartner
	  */
	public int getC_BPartner_ID();

	public I_C_BPartner getC_BPartner() throws Exception;

    /** Column name C_Datev_ExportLog_ID */
    public static final String COLUMNNAME_C_Datev_ExportLog_ID = "C_Datev_ExportLog_ID";

	/** Set C_Datev_ExportLog_ID	  */
	public void setC_Datev_ExportLog_ID (int C_Datev_ExportLog_ID);

	/** Get C_Datev_ExportLog_ID	  */
	public int getC_Datev_ExportLog_ID();

    /** Column name DateAcct */
    public static final String COLUMNNAME_DateAcct = "DateAcct";

	/** Set Buchungsdatum.
	  * Buchungsdatum
	  */
	public void setDateAcct (Timestamp DateAcct);

	/** Get Buchungsdatum.
	  * Buchungsdatum
	  */
	public Timestamp getDateAcct();

    /** Column name DateExp */
    public static final String COLUMNNAME_DateExp = "DateExp";

	/** Set DateExp	  */
	public void setDateExp (Timestamp DateExp);

	/** Get DateExp	  */
	public Timestamp getDateExp();

    /** Column name DocumentNo */
    public static final String COLUMNNAME_DocumentNo = "DocumentNo";

	/** Set Beleg Nr..
	  * Belegnummer für dieses Dokument
	  */
	public void setDocumentNo (String DocumentNo);

	/** Get Beleg Nr..
	  * Belegnummer für dieses Dokument
	  */
	public String getDocumentNo();
}
