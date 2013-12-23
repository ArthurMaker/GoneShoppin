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
	// /sell amount

	@Override
	public void run(CommandSender sender, Command cmd, String[] args) throws Exception
	{
		Player player = (Player) sender;
		String commandName = cmd.getName().toLowerCase();
		boolean price = commandName.equals("price");

		// usage
		if (price && args.length > 2)
			throw new IncorrectUsageException("[material] [amount]");
		if (!price && args.length > 1)
			throw new IncorrectUsageException("[amount]");

		// get itemstack
		ItemStack itemStack = (!price ? player.getItemInHand() : args.length == 0 ? player.getItemInHand() : ShoppingUtils.parseInput(args[0], sender));

		if (itemStack == null)
			return;

		itemStack = itemStack.clone(); // to be safe

		// get amount
		if ((price && args.length == 2) || !price && args.length == 1)
		{
			Integer amount = Utils.getInt(args[price ? 1 : 0]);
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

		Integer sellPrice = gsItem.getPrice(false, itemStack.getAmount());

		// validation
		if (sellPrice == null)
			throw new IllegalArgumentException("You cannot sell that!");

		// damaged tool
		if (Utils.isDamagedTool(itemStack))
			throw new IllegalArgumentException("You must repair that " + name + " before selling it!");

		// check inventory has it
		int invAmount = ShoppingUtils.countInInventory(player, itemStack);
		if (invAmount < itemStack.getAmount())
			throw new IllegalArgumentException("You only have " + invAmount + " in your inventory!");

		// remove from inv
		ShoppingUtils.removeFromInventory(player, itemStack, itemStack.getAmount());

		// give money
		if (ShoppingUtils.giveGold(player, sellPrice))
			Utils.message(sender, "&7Dropping excess money onto the floor!");

		Utils.message(sender, String.format("You sold &6%d&fx %s &ffor &6%dGN&f!", itemStack.getAmount(), name, sellPrice));

	}
}
