package net.chunk64.chinwe.goneshoppin.commands.admin;

import net.chunk64.chinwe.goneshoppin.commands.IncorrectUsageException;
import net.chunk64.chinwe.goneshoppin.commands.Permission;
import net.chunk64.chinwe.goneshoppin.commands.ShoppingCommand;
import net.chunk64.chinwe.goneshoppin.items.Alias;
import net.chunk64.chinwe.goneshoppin.items.GSItem;
import net.chunk64.chinwe.goneshoppin.logging.Action;
import net.chunk64.chinwe.goneshoppin.logging.GSLogger;
import net.chunk64.chinwe.goneshoppin.logging.actions.AdminPriceAction;
import net.chunk64.chinwe.goneshoppin.util.Config;
import net.chunk64.chinwe.goneshoppin.util.ShoppingUtils;
import net.chunk64.chinwe.goneshoppin.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;

public class CommandSetPrice extends ShoppingCommand
{

	public CommandSetPrice(Permission perm, boolean playerOnly, String command)
	{
		super(perm, playerOnly, command);
	}

	// /setprice material buy buymin sell sellmin

	@Override
	public void run(CommandSender sender, Command cmd, String[] args) throws Exception
	{
		boolean note = cmd.getName().equalsIgnoreCase("setnote");

		// usage
		if ((!note && args.length != 5) || (note && args.length < 1))
			throw new IncorrectUsageException();

		// permission check
		if (!note && Config.PriceProtection && sender instanceof Player)
			if (!Config.PriceAdmins.contains(sender.getName()))
				throw new IllegalArgumentException("There are only " + Config.PriceAdmins.size() + " player" + (Config.PriceAdmins.size() == 1 ? "" : "s") + " who can change prices - and you ain't one of them!");

		// get material
		ItemStack itemStack = ShoppingUtils.parseInputAndMessage(sender, args[0]);
		if (itemStack == null)
			return;

		Alias alias = Alias.getAlias(itemStack);
		if (alias == null)
			throw new IllegalArgumentException("Could not set the " + (note ? "note" : "price") + " of '" + args[0] + "'!");

		Double buyPrice = null, sellPrice = null;
		Integer buyMin = null, sellMin = null;
		String itemNote = "";
		if (!note)
		{

			// get prices
			buyPrice = Utils.getDouble(args[1]);
			sellPrice = Utils.getDouble(args[3]);

			if (buyPrice == null || sellPrice == null)
				throw new NumberFormatException((buyPrice == null ? "Buying" : "Selling") + " price must be a number!");

			// get mins
			buyMin = Utils.getInt(args[2]);
			sellMin = Utils.getInt(args[4]);

			if (buyMin == null || sellMin == null)
				throw new NumberFormatException("Minimum transaction amount must be a number!");
		} else
			itemNote = args.length == 1 ? "" : StringUtils.join(args, ' ', 1, args.length);

		// change file
		FileConfiguration prices = GSItem.getYml();
		String prefix = "prices." + itemStack.getType().toString() + ".subs." + itemStack.getData().getData();

		if (note)
			prices.set(prefix + ".note", itemNote);
		else
		{
			prices.set(prefix + ".buy.single", buyPrice);
			prices.set(prefix + ".buy.minimum", buyMin);
			prices.set(prefix + ".sell.single", sellPrice);
			prices.set(prefix + ".sell.minimum", sellMin);
		}

		GSItem.saveYml();
		GSItem previous = GSItem.loadItem(itemStack);

		// unload previous
		for (Iterator<GSItem> iterator = GSItem.getInstances().iterator(); iterator.hasNext(); )
		{
			GSItem item = iterator.next();
			if (item.getAlias() == alias)
			{
				iterator.remove();
				break;
			}
		}

		String msg = "The &6" + (note ? "note" : "price") + " &ffor " + ShoppingUtils.toString(itemStack, true);
		if (!note)
		{
			// make new
			GSItem newPrice = new GSItem(alias, itemStack.getType(), itemStack.getData().getData(), buyPrice, sellPrice, buyMin, sellMin, previous == null ? "" : previous.getNote());

			// message
			Utils.message(sender, msg + " was set to:");
			ShoppingUtils.sendPrices(sender, newPrice, itemStack);
		} else
			Utils.message(sender, msg + (itemNote.isEmpty() ? " was &bremoved&r!" : " was set to &7'&b" + itemNote + "&7'"));

		String newValue = note ? "'" + itemNote + "'" : "buy " + buyMin + " at " + buyPrice + " each, and sell " + sellMin + " at " + sellPrice + " each";
		GSLogger.log(new AdminPriceAction(sender.getName(), note ? Action.SET_NOTE : Action.SET_PRICE, itemStack, newValue));


	}
}
