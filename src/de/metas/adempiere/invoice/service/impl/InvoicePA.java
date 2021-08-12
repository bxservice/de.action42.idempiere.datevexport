package de.metas.adempiere.invoice.service.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import de.metas.adempiere.invoice.service.IInvoicePA;
import org.compiere.model.I_C_Invoice;
import org.compiere.model.I_C_InvoiceLine;
import org.compiere.model.I_C_LandedCost;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MLandedCost;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;

public class InvoicePA implements IInvoicePA {

	public static final CLogger logger = CLogger.getCLogger(InvoicePA.class);

	public I_C_Invoice createInvoice(String trxName) {
		return new MInvoice(Env.getCtx(), 0, trxName);
	}

	public I_C_InvoiceLine createInvoiceLine(final I_C_Invoice invoice) {

		return new MInvoiceLine(getMInvoice(invoice));
	}

	public I_C_InvoiceLine createInvoiceLine(final String trxName) {

		return new MInvoiceLine(Env.getCtx(), 0, trxName);
	}

	public List<I_C_InvoiceLine> retrieveLines(final I_C_Invoice invoice,
			final String trxName) {

		final List<I_C_InvoiceLine> result = new ArrayList<I_C_InvoiceLine>();

		for (final MInvoiceLine il : getMInvoice(invoice).getLines()) {
			result.add(il);
		}
		return result;

	}

	public List<I_C_LandedCost> retrieveLandedCosts(
			final I_C_InvoiceLine invoiceLine, final String whereClause,
			final String trxName) {

		final List<I_C_LandedCost> list = new ArrayList<I_C_LandedCost>();

		String sql = "SELECT * FROM C_LandedCost WHERE C_InvoiceLine_ID=? ";
		if (whereClause != null)
			sql += whereClause;
		final PreparedStatement pstmt = DB.prepareStatement(sql, trxName);
		ResultSet rs = null;

		try {

			pstmt.setInt(1, invoiceLine.getC_InvoiceLine_ID());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				MLandedCost lc = new MLandedCost(Env.getCtx(), rs, trxName);
				list.add(lc);
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, "getLandedCost", e);
		} finally {
			DB.close(rs, pstmt);
		}

		return list;
	} // getLandedCost

	private static MInvoice getMInvoice(final I_C_Invoice invoice) {

		if (invoice instanceof MInvoice) {

			return (MInvoice) invoice;
		}
		throw new IllegalArgumentException("Invoice must be an MInvoice. Is a "
				+ invoice.getClass());
	}

	public I_C_LandedCost createLandedCost(String trxName) {
		return new MLandedCost(Env.getCtx(), 0, trxName);
	}

	public I_C_Invoice retrieveInvoice(int invoiceId, String trxName) {
		return new MInvoice(Env.getCtx(),invoiceId,trxName);
	}

}
