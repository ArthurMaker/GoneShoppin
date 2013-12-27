package net.chunk64.chinwe.goneshoppin.logging.actions;

import net.chunk64.chinwe.goneshoppin.banking.Account;
import net.chunk64.chinwe.goneshoppin.logging.Action;
import net.chunk64.chinwe.goneshoppin.logging.LoggerAction;

public class AdminBankAction extends LoggerAction
{

	private Account account;

	public AdminBankAction(String playerName, Action action, Account account)
	{
		super(playerName, action);
		this.account = account;
	}

	@Override
	public String getMessage()
	{
		return String.format("%s set the %s of %s to %s", playerName, action == Action.SET_BALANCE ? "balance" : "limit", account.getName(), action == Action.SET_BALANCE ? account.getBalance() + "GN" : account.getLimit().toAmount());
	}
}
