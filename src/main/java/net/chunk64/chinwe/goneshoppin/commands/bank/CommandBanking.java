package net.chunk64.chinwe.goneshoppin.commands.bank;

import net.chunk64.chinwe.goneshoppin.banking.Account;
import net.chunk64.chinwe.goneshoppin.banking.Bank;
import net.chunk64.chinwe.goneshoppin.commands.IncorrectUsageException;
import net.chunk64.chinwe.goneshoppin.commands.Permission;
import net.chunk64.chinwe.goneshoppin.commands.ShoppingCommand;
import net.chunk64.chinwe.goneshoppin.util.ShoppingUtils;
import net.chunk64.chinwe.goneshoppin.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class CommandBanking extends ShoppingCommand
{

	public CommandBanking(Permission perm, boolean playerOnly, String command)
	{
		super(perm, playerOnly, command);
	}

	private enum CommandType
	{
		WITHDRAW, DEPOSIT, STEAL
	}

	@Override
	public void run(CommandSender sender, Command cmd, String[] args) throws Exception
	{
		Player player = (Player) sender;
		Account account = Bank.getInstance().getAccount(player.getName());

		CommandType ct = CommandType.valueOf(cmd.getName().toUpperCase());

		// usage
		if (ct == CommandType.STEAL)
		{
			if (args.length != 2)
				throw new IncorrectUsageException("<amount> <player name>");
		} else if (args.length != 1)
			throw new IncorrectUsageException("<amount>");

		// how much?
		Integer amount = Utils.getInt(args[0]);
		if (amount == null)
			throw new NumberFormatException("You can only " + cmd.getName() + " GN in integer amounts!");

		// check amounts
		if (amount < 1)
			throw new IllegalArgumentException("You can't " + cmd.getName() + " amounts under 1GN!");

		int limit = account.getLimit().getAmount().intValue();
		int balance = account.getBalance().intValue();

		// depositing
		if (ct == CommandType.DEPOSIT)
		{
			// check inv
			int value = ShoppingUtils.valueInventory(player);
			if (value < amount)
				throw new IllegalArgumentException("You need an extra " + (amount - value) + " GN to deposit that much!");

			// check limit
			if (limit < balance + amount)
				throw new IllegalArgumentException("Depositing that much would take you over your limit of " + limit + "GN!");

			// remove from inv
			ShoppingUtils.takeGold(player, amount);

			// add to bank
			account.deposit(amount);

			// message
			Utils.message(sender, "You &bdeposited &6" + amount + "GN&f!");
		}

		// withdrawing and stealing
		else
		{

			// steal
			if (ct == CommandType.STEAL)
			{
				account = Bank.getInstance().getAccountFuzzily(args[1]);
				if (account == null)
					throw new IllegalArgumentException("Bank account for " + args[0] + " was not found!");
				//				if (account.getName().equals(player.getName()))
				//					throw new IllegalArgumentException("You can't steal from yourself");
			}

			if (!account.hasBalance(amount))
				throw new IllegalArgumentException(String.format("%s bank is %dGN short of %dGN!", ct == CommandType.STEAL ? "That" : "Your", (amount - balance), amount));

			// give as much as possible
			List<ItemStack> gold = ShoppingUtils.simplify(amount);
			HashMap<Integer, ItemStack> overflowGold = player.getInventory().addItem(gold.toArray(new ItemStack[gold.size()]));

			if (!overflowGold.isEmpty())
			{
				int overflow = 0;
				for (ItemStack itemStack : overflowGold.values())
					overflow += ShoppingUtils.value(itemStack);
				amount -= overflow;
				if (amount <= 0)
					throw new IllegalArgumentException("There is no room in your inventory!");
				Utils.message(sender, "&7The amount was reduced to &8" + amount + "GN&7, as there isn't room in your inventory for more!");
			}

			// take from bank
			account.withdraw(amount);

			// message
			Utils.message(sender, String.format("You &b%s &6%dGN&f%s!", ct == CommandType.STEAL ? "stole" : "withdrew", amount, ct == CommandType.STEAL ? " from &b" + account.getName() + "&f" : ""));
		}

			// final balance
			Utils.message(sender, "&f" + (ct == CommandType.STEAL ? "Their" : "Your") + " &bnew &fbalance is &6" + account.getBalance().intValue() + "GN&f!");

	}
}
