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

public class CommandPrice extends ShoppingCommand
{

	public CommandPrice(Permission perm, boolean playerOnly, String command)
	{
		super(perm, playerOnly, command);
	}

	// /price material amount
	// /sell  material amount


	@Override
	public void run(CommandSender sender, Command cmd, String[] args) throws Exception
	{
		Player player = (Player) sender;
		String commandName = cmd.getName().toLowerCase();
		boolean price = commandName.equals("price");

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
			throw new IllegalArgumentException("You cannot " + commandName + " air!");
		if (ShoppingUtils.isGold(itemStack))
			throw new IllegalArgumentException("You cannot " + commandName + " gold, ye dongle!");

		// get price
		String name = ShoppingUtils.toString(itemStack, true);
		GSItem gsItem = GSItem.loadItem(itemStack.getType(), itemStack.getData().getData());

		if (gsItem == null)
			throw new IllegalArgumentException(name + " could not be priced!");

		Integer buyPrice = gsItem.getPrice(true, itemStack.getAmount());
		Integer sellPrice = gsItem.getPrice(true, itemStack.getAmount());

		// price it
		if (price)
		{
			name = "&6" + itemStack.getAmount() + "&fx " + name;
			String[] notes = {gsItem.getNote(true), gsItem.getNote(false)};

			Utils.message(sender, (args.length == 0 ? "That " : "") + name + " costs:");
			Utils.message(sender, "To buy: &b" + gsItem.getFormattedPrice(true, itemStack.getAmount()) + (!notes[0].isEmpty() ? "\n    &f- &3" + notes[0] : ""));
			Utils.message(sender, "To sell: &b" + gsItem.getFormattedPrice(false, itemStack.getAmount()) + (!notes[1].isEmpty() ? "\n    &f- &3" + notes[1] : ""));
			sender.sendMessage("§8----------------");
			return;
		}

		// check if sellprice is null - cant sell

		// check if tool - needs reparing

		// check inventory has it

		// remove all metadata from it

		// remove from inv


	}
}
