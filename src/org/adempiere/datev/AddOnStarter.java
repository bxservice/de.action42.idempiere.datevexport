package org.adempiere.datev;

import org.adempiere.addon.IAddOn;
import org.adempiere.bpartner.service.IBPartnerPA;
import org.adempiere.bpartner.service.impl.BPartnerPA;
import org.adempiere.datev.service.IAccountingPA;
import org.adempiere.datev.service.impl.AccountingPA;
import org.adempiere.db.IDBService;
import org.adempiere.db.IDatabaseBL;
import org.adempiere.db.impl.DBService;
import org.adempiere.db.impl.DatabaseBL;
import org.adempiere.invoice.service.IInvoicePA;
import org.adempiere.invoice.service.impl.InvoicePA;
import org.adempiere.misc.service.IBankingPA;
import org.adempiere.misc.service.IPOService;
import org.adempiere.misc.service.impl.BankingPA;
import org.adempiere.misc.service.impl.POService;
import org.adempiere.util.Services;

public class AddOnStarter implements IAddOn {

	public void initAddon() {

		Services.registerService(IAccountingPA.class, new AccountingPA());		
		Services.registerService(IBPartnerPA.class, new BPartnerPA());
		Services.registerService(IDatabaseBL.class, new DatabaseBL());
		Services.registerService(IDBService.class, new DBService());
		Services.registerService(IInvoicePA.class, new InvoicePA());
		Services.registerService(IBankingPA.class, new BankingPA());
		Services.registerService(IPOService.class, new POService());
	}
}
