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
		if (price && args.length > 2 || !price && args.length > 1)
			throw new IncorrectUsageException();

		// get itemstack
		ItemStack itemStack = (!price ? player.getItemInHand() : args.length == 0 ? player.getItemInHand() : ShoppingUtils.parseInputAndMessage(player, args[0]));
		if (itemStack == null)
			return;

		itemStack = itemStack.clone(); // to be safe

		// get amount
		if ((price && args.length == 2) || !price && args.length == 1)
		{
			String input = args[price ? 1 : 0];
			Integer amount;
			amount = input.equalsIgnoreCase("all") ? Integer.valueOf(ShoppingUtils.countInInventory(player, itemStack)) : Utils.getInt(input);
			if (amount == null)
				throw new IncorrectUsageException();
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
			String note = gsItem.getNote();

			// TODO reformat
			Utils.message(sender, (args.length == 0 ? "That " : "") + name + " costs:");
			Utils.message(sender, formatPrice(gsItem, itemStack, true));
			Utils.message(sender, formatPrice(gsItem, itemStack, false));
			if (!note.isEmpty())
				Utils.message(sender, "  &f- &b" + note);
			sender.sendMessage("§8----------------");
			return;
		}

		// sell it
		Double sellPrice = gsItem.getRawPrice(false, itemStack.getAmount());
		if (sellPrice == null)
			throw new IllegalArgumentException("You cannot sell that item!");

		// check amount
		checkMultiples(gsItem, itemStack, false);

		int finalSellPrice = sellPrice.intValue();

		// damaged tool
		if (Utils.isDamagedTool(itemStack))
			throw new IllegalArgumentException("You must repair that " + name + " before selling it!");

		// check inventory has it
		int invAmount = ShoppingUtils.countInInventory(player, itemStack);
		if (invAmount < itemStack.getAmount())
			throw new IllegalArgumentException("You only have " + invAmount + " in your inventory!");

		final int amount = itemStack.getAmount();

		// remove from inv
		ShoppingUtils.removeFromInventory(player, itemStack, itemStack.getAmount());

		// give money
		if (ShoppingUtils.giveGold(player, finalSellPrice))
			Utils.message(sender, "&7Dropping excess money onto the floor!");


		Utils.message(sender, String.format("You sold &6%d&fx %s &ffor &6%dGN&f!", amount, name, finalSellPrice));

	}

	private String formatPrice(GSItem gsItem, ItemStack itemStack, boolean buying)
	{
		int per = gsItem.getPerTransaction(buying);
		return "To " + (buying ? "buy" : "sell") + ": &b" + gsItem.getFormattedRawPrice(buying, itemStack.getAmount()) + (per == 1 ? "" : " &a| &7in multiples of &8" + per);
	}

}
