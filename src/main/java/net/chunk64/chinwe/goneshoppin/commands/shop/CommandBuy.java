package net.chunk64.chinwe.goneshoppin.commands.shop;

import net.chunk64.chinwe.goneshoppin.commands.IncorrectUsageException;
import net.chunk64.chinwe.goneshoppin.commands.Permission;
import net.chunk64.chinwe.goneshoppin.commands.ShoppingCommand;
import net.chunk64.chinwe.goneshoppin.items.GSItem;
import net.chunk64.chinwe.goneshoppin.util.ShoppingUtils;
import net.chunk64.chinwe.goneshoppin.util.Utils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandBuy extends ShoppingCommand
{

	public CommandBuy(Permission perm, boolean playerOnly, String command)
	{
		super(perm, playerOnly, command);
	}

	// /buy material amount

	@Override
	public void run(CommandSender sender, Command cmd, String[] args) throws Exception
	{
		Player player = (Player) sender;

		// usage
		if (args.length != 2 && args.length != 1)
			throw new IncorrectUsageException("<material> [amount]");

		// get itemstack
		ItemStack itemStack = ShoppingUtils.parseInput(args[0], sender);
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
			throw new IllegalArgumentException("You cannot buy air!");
		if (ShoppingUtils.isGold(itemStack))
			throw new IllegalArgumentException("You cannot buy gold, ye dongle!");

		// get price
		String name = ShoppingUtils.toString(itemStack, true);
		GSItem gsItem = GSItem.loadItem(itemStack.getType(), itemStack.getData().getData());
		Integer buyPrice = gsItem.getPrice(true, itemStack.getAmount());

		if (gsItem == null)
			throw new IllegalArgumentException(name + " could not be priced!");

		// check money in inv
		int invValue = ShoppingUtils.valueInventory(player);
		if (invValue < buyPrice)
			throw new IllegalArgumentException("You need an extra " + (buyPrice - invValue) + "GN to buy that many!");

		// take money
		ShoppingUtils.takeGold(player, buyPrice);

		// give
		ShoppingUtils.giveItems(player, itemStack);

		Utils.message(sender, String.format("You bought &6%d&fx %s &ffor &6%dGN&f!", itemStack.getAmount(), name, buyPrice));

	}
}
