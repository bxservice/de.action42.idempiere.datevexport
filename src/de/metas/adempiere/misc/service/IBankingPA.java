package de.metas.adempiere.misc.service;

import java.util.List;

import org.compiere.model.I_C_BP_BankAccount;
import org.compiere.model.I_C_Bank;
import org.compiere.model.I_C_BankAccount;
import org.compiere.model.I_C_BankStatement;
import org.compiere.model.I_C_BankStatementLine;

public interface IBankingPA {

	I_C_BankAccount retrieveBankAccount(int bankAccountId, String trxName);

	I_C_BankAccount retrieveBankAccount(String routingNo, String accountNo,
			String trxName);

	I_C_Bank retrieveBank(String routingNumber, String trxName);

	I_C_BankStatement createNewBankStatement(String trxName);

	I_C_BankStatement retrieveBankStatement(String name, String trxName);

	I_C_BankStatementLine createNewBankStatementLine(String trxName);

	I_C_BP_BankAccount createNewBankAccount(String trxName);

	I_C_Bank createNewBank(String trxName);

	List<? extends I_C_BP_BankAccount> retrieveBankAccountsOfBPartner(
			int bPartnerId, String trxName);

	I_C_Bank retrieveBank(int bankId, String trxName);
}
