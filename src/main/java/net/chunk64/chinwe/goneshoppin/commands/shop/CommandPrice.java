package net.chunk64.chinwe.goneshoppin.commands.shop;

import net.chunk64.chinwe.goneshoppin.commands.IncorrectUsageException;
import net.chunk64.chinwe.goneshoppin.commands.Permission;
import net.chunk64.chinwe.goneshoppin.commands.ShoppingCommand;
import net.chunk64.chinwe.goneshoppin.items.GSItem;
import net.chunk64.chinwe.goneshoppin.logging.Action;
import net.chunk64.chinwe.goneshoppin.querys.PriceQuery;
import net.chunk64.chinwe.goneshoppin.querys.QueryResult;
import net.chunk64.chinwe.goneshoppin.util.ShoppingUtils;
import net.chunk64.chinwe.goneshoppin.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandPrice extends ShoppingCommand
{

	public CommandPrice()
	{
		setCommand("price");
		setPlayerOnly(true);
		setPermission(Permission.PRICE);
	}

	@Override
	public void run(CommandSender sender, Command cmd, String[] args) throws Exception
	{
		Player player = (Player) sender;
		// usage
		if (args.length > 2)
			throw new IncorrectUsageException();

		// get itemstack
		ItemStack itemStack = (args.length == 0 ? player.getItemInHand() : ShoppingUtils.parseInputAndMessage(player, args[0]));
		if (itemStack == null)
			return;

		itemStack = itemStack.clone(); // to be safe

		// get amount
		if (args.length == 2)
		{
			String input = args[1];
			Integer amount;
			amount = input.equalsIgnoreCase("all") ? Integer.valueOf(ShoppingUtils.countInInventory(player.getInventory(), itemStack)) : Utils.getInt(input);
			if (amount == null)
				throw new IncorrectUsageException();
			itemStack.setAmount(amount);
		}

		PriceQuery priceQuery = new PriceQuery(sender, Action.PRICE, itemStack);
		QueryResult result = priceQuery.execute();

		if (result.didError())
			throw new IllegalArgumentException(result.getMessage());

		GSItem gsItem = (GSItem) result.getObject();
		String name = ShoppingUtils.toString(itemStack, true);

		// price it
		name = "&6" + itemStack.getAmount() + "&fx " + name;
		String note = gsItem.getNote();

		Utils.message(sender, (args.length == 0 ? "That " : "") + name + " costs:");
		ShoppingUtils.sendPrices(player, gsItem, itemStack);
		if (!note.isEmpty())
			Utils.message(sender, "  &f- &b" + note);
		sender.sendMessage("§8----------------");
	}

}
