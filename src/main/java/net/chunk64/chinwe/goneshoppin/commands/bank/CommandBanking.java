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

public class CommandBanking extends ShoppingCommand
{

	public CommandBanking(Permission perm, boolean playerOnly, String command)
	{
		super(perm, playerOnly, command);
	}

	// withdraw all
	// deposit max
	// steal all
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
		if ((ct == CommandType.STEAL && args.length != 2) || ct != CommandType.STEAL && args.length != 1)
			throw new IncorrectUsageException();

		// how much?
		Integer amount = Utils.getInt(args[0]);

		if (args[0].equalsIgnoreCase("all"))
			amount = ct == CommandType.DEPOSIT ? ShoppingUtils.valueInventory(player) : account.getBalance().intValue();

		if (amount == null)
			throw new IllegalArgumentException("Please enter an integer amount of GN, or 'all' to " + (ct == CommandType.DEPOSIT ? "deposit all your gold" : "empty the account") + ".");

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
				throw new IllegalArgumentException("Your account has a limit of " + limit + "GN!");

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
			}

			if (!account.hasBalance(amount))
				throw new IllegalArgumentException(String.format("%s bank is %dGN short of %dGN!", ct == CommandType.STEAL ? "That" : "Your", (amount - balance), amount));

			// give gold
			ShoppingUtils.giveGold(player, amount);

			//			HashMap<Integer, ItemStack> overflowGold = ShoppingUtils.addGoldToInventory(player, amount);
			//			if (!overflowGold.isEmpty())
			//			{
			//				int overflow = 0;
			//				for (ItemStack itemStack : overflowGold.values())
			//					overflow += ShoppingUtils.value(itemStack);
			//				amount -= overflow;
			//				if (amount <= 0)
			//					throw new IllegalArgumentException("There is no room in your inventory!");
			//				Utils.message(sender, "&7The amount was reduced to &8" + amount + "GN&7, as there isn't room in your inventory for more!");
			//			}

			// take from bank
			account.withdraw(amount);

			// message
			Utils.message(sender, String.format("You &b%s &6%dGN&f%s!", ct == CommandType.STEAL ? "stole" : "withdrew", amount, ct == CommandType.STEAL ? " from &b" + account.getName() + "&f" : ""));
		}

		// final balance
		Utils.message(sender, "&f" + (ct == CommandType.STEAL ? "Their" : "Your") + " &bnew &fbalance is &6" + account.getBalance().intValue() + "GN&f!");

	}
}
