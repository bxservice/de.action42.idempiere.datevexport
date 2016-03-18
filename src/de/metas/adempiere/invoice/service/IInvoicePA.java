package de.metas.adempiere.invoice.service;

import java.util.List;

import org.compiere.model.I_C_Invoice;
import org.compiere.model.I_C_InvoiceLine;
import org.compiere.model.I_C_LandedCost;
import org.compiere.model.MInvoice;

public interface IInvoicePA {

	I_C_Invoice createInvoice(String trxName);

	I_C_Invoice retrieveInvoice(int invoiceId, String trxName);
	
	/**
	 * 
	 * @param invoice
	 * @return
	 * @throws IllegalArgumentException
	 *             if invoice is not an {@link MInvoice}
	 */
	I_C_InvoiceLine createInvoiceLine(I_C_Invoice invoice);

	List<I_C_InvoiceLine> retrieveLines(I_C_Invoice invoice, String trxName);

	List<I_C_LandedCost> retrieveLandedCosts(I_C_InvoiceLine invoiceLine,
			String whereClause, String trxName);

	I_C_LandedCost createLandedCost(String trxName);

	I_C_InvoiceLine createInvoiceLine(String trxName);

}
