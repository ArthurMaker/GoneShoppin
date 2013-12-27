package net.chunk64.chinwe.goneshoppin.commands.admin;

import net.chunk64.chinwe.goneshoppin.banking.Account;
import net.chunk64.chinwe.goneshoppin.banking.Bank;
import net.chunk64.chinwe.goneshoppin.banking.BankLimit;
import net.chunk64.chinwe.goneshoppin.commands.IncorrectUsageException;
import net.chunk64.chinwe.goneshoppin.commands.Permission;
import net.chunk64.chinwe.goneshoppin.commands.ShoppingCommand;
import net.chunk64.chinwe.goneshoppin.logging.Action;
import net.chunk64.chinwe.goneshoppin.logging.GSLogger;
import net.chunk64.chinwe.goneshoppin.logging.actions.AdminBankAction;
import net.chunk64.chinwe.goneshoppin.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class CommandAdmin extends ShoppingCommand
{

	public CommandAdmin(Permission perm, boolean playerOnly, String command)
	{
		super(perm, playerOnly, command);
	}

	@Override
	public void run(CommandSender sender, Command cmd, String[] args) throws Exception
	{
		boolean balance = cmd.getName().equals("setbalance");

		// usage
		if (args.length != 2)
			throw new IncorrectUsageException();

		// get account
		Account account = Bank.getInstance().getAccountFuzzily(args[0]);
		String value;
		// get new
		if (balance)
		{
			Integer newBalance = Utils.getInt(args[1]);
			if (newBalance == null)
				throw new NumberFormatException();

			if (newBalance < 0)
				throw new IllegalArgumentException("Balances cannot be negative!");

			// over limit?
			if (!Permission.SET_BALANCE_OVERLIMIT.senderHas(sender) && newBalance > account.getLimit().getAmount().intValue())
				return;

			account.setBalance(newBalance);
			value = String.valueOf(newBalance) + "GN";
		} else
		{
			BankLimit limit = BankLimit.match(args[1]);
			if (limit == null)
				throw new IllegalArgumentException("Invalid limit!\n    Options: " + Utils.formatList(Arrays.asList(BankLimit.values()), false));
			account.setLimit(limit);
			value = limit.toAmount();
		}
		Utils.message(sender, String.format("You set &b%s&f's %s to &6%s&f!", account.getName(), balance ? "balance" : "bank limit", value));

		GSLogger.log(new AdminBankAction(sender.getName(), balance ? Action.SET_BALANCE : Action.SET_LIMIT, account));
	}

}
