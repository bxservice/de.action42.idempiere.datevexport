package de.metas.adempiere.misc.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import de.metas.adempiere.db.IDatabaseBL;
import de.metas.adempiere.misc.service.IBankingPA;
import de.metas.adempiere.util.Services;
import org.compiere.model.I_C_BP_BankAccount;
import org.compiere.model.I_C_Bank;
import org.compiere.model.I_C_BankAccount;
import org.compiere.model.I_C_BankStatement;
import org.compiere.model.I_C_BankStatementLine;
import org.compiere.model.MBPBankAccount;
import org.compiere.model.MBank;
import org.compiere.model.MBankAccount;
import org.compiere.model.MBankStatement;
import org.compiere.model.MBankStatementLine;
import org.compiere.util.CLogger;
import org.compiere.util.CPreparedStatement;
import org.compiere.util.DB;
import org.compiere.util.Env;

public final class BankingPA implements IBankingPA {

	public static final String SQL_BANKACCOUNT_ROUTING_ACCT = "SELECT * "
			+ "FROM C_BankAccount a LEFT JOIN C_Bank b ON a.C_Bank_ID=b.C_Bank_ID "
			+ "WHERE b.RoutingNo=? AND a.AccountNo=?";

	public static final String SQL_SELECT_BANK = "SELECT * FROM "
			+ I_C_Bank.Table_Name + " WHERE " + I_C_Bank.COLUMNNAME_RoutingNo
			+ "=?";

	public static final String SQL_BANKACCOUNT_ID = //
	"SELECT * FROM C_BP_BankAccount WHERE C_BPartner_ID=?";

	public static final String SQL_BANKSTATEMENT_NAME = //
	"SELECT * FROM C_BankStatement WHERE Name=?";

	private static final CLogger logger = CLogger.getCLogger(BankingPA.class);

	public MBank retrieveBank(final String routingNumber, final String trxName) {

		if (routingNumber == null) {
			throw new IllegalArgumentException(
					"Param 'routingNumber' may not be null");
		}
		CPreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = DB.prepareStatement(SQL_SELECT_BANK, trxName);
			pstmt.setString(1, routingNumber);

			rs = pstmt.executeQuery();
			if (rs.next()) {

				logger.fine("Returning bank account with routing number '"
						+ routingNumber + "'");
				return new MBank(Env.getCtx(), rs, trxName);
			}

			logger.fine("Didn't find bank with routing '" + routingNumber
					+ "'. Returning null.");
			return null;

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			DB.close(rs, pstmt);
		}
	}

	public MBPBankAccount createNewBankAccount(final String trxName) {

		return new MBPBankAccount(Env.getCtx(), 0, trxName);
	}

	public MBank createNewBank(String trxName) {
		return new MBank(Env.getCtx(), 0, trxName);
	}

	public List<? extends I_C_BP_BankAccount> retrieveBankAccountsOfBPartner(
			final int partnerId, final String trxName) {

		final IDatabaseBL databaseBL = Services.get(IDatabaseBL.class);

		final List<MBPBankAccount> result = databaseBL.retrieveList(
				SQL_BANKACCOUNT_ID, new Object[] { partnerId },
				MBPBankAccount.class, trxName);

		return result;
	}

	public I_C_Bank retrieveBank(int bankId, String trxName) {

		return new MBank(Env.getCtx(), bankId, trxName);
	}

	/**
	 * Invokes
	 * {@link MBankStatement#MBankStatement(java.util.Properties, int, String)}.
	 */
	public I_C_BankStatement createNewBankStatement(final String trxName) {

		return new MBankStatement(Env.getCtx(), 0, trxName);
	}

	/**
	 * Invokes
	 * {@link MBankAccount#MBankAccount(java.util.Properties, int, String)}.
	 */
	public I_C_BankAccount retrieveBankAccount(final int bankAccountId,
			final String trxName) {

		return new MBankAccount(Env.getCtx(), bankAccountId, trxName);
	}

	/**
	 * Invokes
	 * {@link MBankStatementLine#MBankStatementLine(java.util.Properties, int, String)}.
	 */
	public I_C_BankStatementLine createNewBankStatementLine(final String trxName) {

		return new MBankStatementLine(Env.getCtx(), 0, trxName);
	}

	public I_C_BankAccount retrieveBankAccount(String routingNo,
			String accountNo, String trxName) {

		final IDatabaseBL databaseBL = Services.get(IDatabaseBL.class);

		final List<? extends I_C_BankAccount> accounts = databaseBL
				.retrieveList(SQL_BANKACCOUNT_ROUTING_ACCT, new Object[] {
						routingNo, accountNo }, MBankAccount.class, trxName);

		if (accounts.isEmpty()) {
			return null;
		}

		return accounts.get(0);
	}

	public I_C_BankStatement retrieveBankStatement(final String name,
			final String trxName) {

		final IDatabaseBL dataBaseBL = Services.get(IDatabaseBL.class);

		final List<? extends I_C_BankStatement> accounts = dataBaseBL
				.retrieveList(SQL_BANKSTATEMENT_NAME, new Object[] { name },
						MBankStatement.class, trxName);

		if (accounts.isEmpty()) {
			return null;
		}

		return accounts.get(0);
	}
}
