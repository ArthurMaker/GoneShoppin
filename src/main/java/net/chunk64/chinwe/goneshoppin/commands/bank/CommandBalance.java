package net.chunk64.chinwe.goneshoppin.commands.bank;

import net.chunk64.chinwe.goneshoppin.banking.Account;
import net.chunk64.chinwe.goneshoppin.banking.Bank;
import net.chunk64.chinwe.goneshoppin.banking.BankLimit;
import net.chunk64.chinwe.goneshoppin.commands.IncorrectUsageException;
import net.chunk64.chinwe.goneshoppin.commands.Permission;
import net.chunk64.chinwe.goneshoppin.commands.ShoppingCommand;
import net.chunk64.chinwe.goneshoppin.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandBalance extends ShoppingCommand
{

	public CommandBalance(Permission perm, boolean playerOnly, String command)
	{
		super(perm, playerOnly, command);
	}

	@Override
	public void run(CommandSender sender, Command cmd, String[] args) throws Exception
	{
		Player player = (Player) sender;

		// usage
		if (args.length > 1)
			throw new IncorrectUsageException();

		// get target
		Account account = args.length == 0 ? Bank.getInstance().getAccount(player.getName()) : Bank.getInstance().getAccountFuzzily(args[0]);
		if (account == null)
			throw new IllegalArgumentException("Bank account for " + args[0] + " was not found!");

		boolean self = account.getName().equals(player.getName());

		// permission check
		if (!self && !Permission.BANK_BALANCE_OTHER.senderHas(sender))
			return;

		String limit = account.getLimit() == BankLimit.UNLIMITED ? "It has &bno limit" : "It is &blimited &rto &6" + account.getLimit().toAmount();
		Utils.message(player, String.format("%s account holds &6%dGN&f! %s&r.", self ? "Your" : "&b" + account.getName() + "'s&r", account.getBalance().intValue(), limit));
//		if (!self && account.isTemporary())
//			Utils.message(player, "&7This account is newly created.");
	}
}
