package net.chunk64.chinwe.goneshoppin.commands.shop;

import net.chunk64.chinwe.goneshoppin.commands.IncorrectUsageException;
import net.chunk64.chinwe.goneshoppin.commands.Permission;
import net.chunk64.chinwe.goneshoppin.commands.ShoppingCommand;
import net.chunk64.chinwe.goneshoppin.logging.Action;
import net.chunk64.chinwe.goneshoppin.querys.BuyQuery;
import net.chunk64.chinwe.goneshoppin.querys.QueryResult;
import net.chunk64.chinwe.goneshoppin.util.ShoppingUtils;
import net.chunk64.chinwe.goneshoppin.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandBuy extends ShoppingCommand
{

	public CommandBuy()
	{
		setCommand("buy");
		setPlayerOnly(true);
		setPermission(Permission.BUY);
	}

	@Override
	public void run(CommandSender sender, Command cmd, String[] args) throws Exception
	{
		Player player = (Player) sender;

		// usage
		if (args.length != 2 && args.length != 1)
			throw new IncorrectUsageException();

		// get itemstack
		ItemStack itemStack = ShoppingUtils.parseInput(player, args[0]);
		if (itemStack == null)
			return;

		// get amount
		Integer amount;
		if (args.length == 2)
		{
			boolean max = args[1].equalsIgnoreCase("max");
			if (max)
				amount = -1;
			else
				amount = Utils.getInt(args[1]);

			if (amount == null)
				throw new IllegalArgumentException("Invalid amount given!");
			if (!max && amount < 1)
				throw new IllegalArgumentException("You cannot buy an amount less than 1!");
		} else
			amount = -2; // set to min amount
		itemStack.setAmount(amount);

		BuyQuery buyQuery = new BuyQuery(sender, Action.BUY, itemStack, null);
		QueryResult result = buyQuery.execute();

		if (result.didError())
			throw new IllegalArgumentException(result.getMessage());
		else
			Utils.message(sender, result.getMessage());
	}
}
