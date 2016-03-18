package de.action42.idempiere.datev;

import de.metas.adempiere.addon.IAddOn;
import de.metas.adempiere.bpartner.service.IBPartnerPA;
import de.metas.adempiere.bpartner.service.impl.BPartnerPA;
import de.metas.adempiere.db.IDBService;
import de.metas.adempiere.db.IDatabaseBL;
import de.metas.adempiere.db.impl.DBService;
import de.metas.adempiere.db.impl.DatabaseBL;
import de.metas.adempiere.invoice.service.IInvoicePA;
import de.metas.adempiere.invoice.service.impl.InvoicePA;
import de.metas.adempiere.misc.service.IBankingPA;
import de.metas.adempiere.misc.service.IPOService;
import de.metas.adempiere.misc.service.impl.BankingPA;
import de.metas.adempiere.misc.service.impl.POService;
import de.metas.adempiere.util.Services;

import de.action42.idempiere.datev.service.IAccountingPA;
import de.action42.idempiere.datev.service.impl.AccountingPA;

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
