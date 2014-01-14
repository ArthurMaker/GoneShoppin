package net.chunk64.chinwe.goneshoppin.commands.shop;

import net.chunk64.chinwe.goneshoppin.banking.Bank;
import net.chunk64.chinwe.goneshoppin.commands.IncorrectUsageException;
import net.chunk64.chinwe.goneshoppin.commands.Permission;
import net.chunk64.chinwe.goneshoppin.commands.ShoppingCommand;
import net.chunk64.chinwe.goneshoppin.logging.Action;
import net.chunk64.chinwe.goneshoppin.querys.QueryResult;
import net.chunk64.chinwe.goneshoppin.querys.SellQuery;
import net.chunk64.chinwe.goneshoppin.util.ShoppingUtils;
import net.chunk64.chinwe.goneshoppin.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandSell extends ShoppingCommand
{

	public CommandSell()
	{
		setPlayerOnly(true);
		setPermission(Permission.SELL);
	}

	@Override
	public void run(CommandSender sender, Command cmd, String[] args) throws Exception
	{
		Player player = (Player) sender;

		// usage
		if (args.length > 1)
			throw new IncorrectUsageException();

		// get itemstack
		ItemStack itemStack = player.getItemInHand().clone(); // to be safe

		// get amount
		if (args.length == 1)
		{
			Integer amount;
			amount = args[0].equalsIgnoreCase("all") ? Integer.valueOf(ShoppingUtils.countInInventory(player.getInventory(), itemStack)) : Utils.getInt(args[0]);
			if (amount == null)
				throw new IncorrectUsageException();
			itemStack.setAmount(amount);
		}

		SellQuery sellQuery = new SellQuery(sender, Action.SELL, itemStack, Bank.getInstance().getAccount(player.getName()), player.getInventory());
		QueryResult result = sellQuery.execute();

		if (result.didError())
			throw new IllegalArgumentException(result.getMessage());
		else
			Utils.message(sender, result.getMessage());

	}

}
