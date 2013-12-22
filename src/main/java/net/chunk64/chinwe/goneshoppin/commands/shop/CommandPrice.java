package net.chunk64.chinwe.goneshoppin.commands.shop;

import net.chunk64.chinwe.goneshoppin.commands.IncorrectUsageException;
import net.chunk64.chinwe.goneshoppin.commands.Permission;
import net.chunk64.chinwe.goneshoppin.commands.ShoppingCommand;
import net.chunk64.chinwe.goneshoppin.util.ShoppingUtils;
import net.chunk64.chinwe.goneshoppin.util.Utils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandPrice extends ShoppingCommand
{

	public CommandPrice(Permission perm, boolean playerOnly, String command)
	{
		super(perm, playerOnly, command);
	}

	// /price material amount

	@Override
	public void run(CommandSender sender, Command cmd, String[] args) throws Exception
	{
		Player player = (Player) sender;

		// usage
		if (args.length > 2)
			throw new IncorrectUsageException("[material] [amount]");

		// get itemstack
		ItemStack itemStack = args.length == 0 ? player.getItemInHand() : ShoppingUtils.parseInput(args[0], sender);
		if (itemStack == null)
			return;

		// get amount
		if (args.length == 2)
		{
			Integer amount = Utils.getInt(args[1]);
			if (amount == null)
				throw new IllegalArgumentException("Invalid amount given!");
			itemStack.setAmount(amount);
		}

		// validate
		if (itemStack.getType() == Material.AIR)
			throw new IllegalArgumentException("You cannot price air!");
		if (ShoppingUtils.isGold(itemStack))
			throw new IllegalArgumentException("You cannot price gold, ye dongle!");

		// TODO get price
		Utils.message(sender, "pricing " + itemStack.getAmount() + "x " + ShoppingUtils.toString(itemStack, true));


	}
}
