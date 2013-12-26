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
			amount = max ? -1 : Utils.getInt(args[1]);
			if (amount == null)
				throw new IllegalArgumentException("Invalid amount given!");
			if (!max && amount < 1)
				throw new IllegalArgumentException("You cannot buy an amount less than 1!");
		} else
			amount = -2; // set to min amount
		itemStack.setAmount(amount);

		// validate
		if (itemStack.getType() == Material.AIR)
			throw new IllegalArgumentException("You cannot buy air!");
		if (ShoppingUtils.isGold(itemStack))
			throw new IllegalArgumentException("You cannot buy gold, ye dongle!");

		// get price
		GSItem gsItem = GSItem.loadItem(itemStack.getType(), itemStack.getData().getData());
		String name = ShoppingUtils.toString(itemStack, true);
		if (gsItem == null)
			throw new IllegalArgumentException(name + " could not be priced!");

		Double buyPrice = gsItem.getRawPrice(true, 1); // updating price later
		if (buyPrice == null)
			throw new IllegalArgumentException("You cannot buy that item!");

		// smart amount
		if (itemStack.getAmount() == -1)
			itemStack.setAmount(ShoppingUtils.getMaxPurchase(player, gsItem).intValue());
		else if (itemStack.getAmount() == -2)
			itemStack.setAmount(gsItem.getPerTransaction(true));

		// check amount
		checkMultiples(gsItem, itemStack, true);
		System.out.println("itemStack.getAmount() = " + itemStack.getAmount());

		// update price
		buyPrice *= itemStack.getAmount();

		int finalBuyPrice = buyPrice.intValue();

		// check money in inv
		int invValue = ShoppingUtils.valueInventory(player);
		if (invValue < buyPrice)
			throw new IllegalArgumentException("You need an extra " + (buyPrice - invValue) + "GN to buy that many!");

		final int finalAmount = itemStack.getAmount();

		// take money
		ShoppingUtils.takeGold(player, finalBuyPrice);

		// give
		ShoppingUtils.giveItems(player, itemStack);

		Utils.message(sender, String.format("You bought &6%d&fx %s &ffor &6%dGN&f!", finalAmount, name, finalBuyPrice));

	}
}
