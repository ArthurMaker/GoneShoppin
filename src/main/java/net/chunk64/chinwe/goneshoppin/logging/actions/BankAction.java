package net.chunk64.chinwe.goneshoppin.logging.actions;

import net.chunk64.chinwe.goneshoppin.banking.Account;
import net.chunk64.chinwe.goneshoppin.logging.Action;
import net.chunk64.chinwe.goneshoppin.logging.LoggerAction;

public class BankAction extends LoggerAction
{

	private Account account;
	private int amount;

	public BankAction(String playerName, Action action, Account account, int amount)
	{
		super(playerName, action);
		this.account = account;
		this.amount = amount;
	}

	@Override
	public String getMessage()
	{
		if (action == Action.STEAL)
			return String.format("%s stole %dGN from %s, who now has a balance of %sGN", playerName, amount, account.getName(), account.getBalance() + "GN");
		else
			return String.format("%s %s %dGN, and now has a balance of %sGN", playerName, action == Action.WITHDRAW ? "withdrew" : "deposited", amount, account.getBalance());

	}
}
