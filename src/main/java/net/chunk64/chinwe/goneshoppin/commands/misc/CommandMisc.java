package net.chunk64.chinwe.goneshoppin.commands.misc;

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

public class CommandMisc extends ShoppingCommand
{

	public CommandMisc()
	{
		setPermission(null);
		setPlayerOnly(true);
	}

	@Override
	public void run(CommandSender sender, Command cmd, String[] args) throws Exception
	{
		Player player = (Player) sender;
		boolean value = cmd.getName().equalsIgnoreCase("value");

		// usage
		if (args.length > 1)
			throw new IncorrectUsageException();

		// individual command permissions
		if (value && !hasPermission(sender, Permission.VALUE) || !value && !hasPermission(sender, Permission.COUNT))
			return;

		// get target
		Player target = args.length == 0 ? player : getPlayer(sender, args[0]);
		if (target == null)
			return;

		boolean self = target == player;

		// self permission check
		if (!self)
		{
			if (value && !hasPermission(sender, Permission.VALUE_OTHER) || !value && !hasPermission(sender, Permission.COUNT_OTHER))
				return;
		}

		String who = self ? "Your" : target.getName() + "'s";

		// value
		if (value)
		{
			int goldValue = ShoppingUtils.valueInventory(target);
			int sellValue = ShoppingUtils.priceInventory(target.getInventory());

			if (goldValue == 0)
				Utils.message(sender, who + " &binventory&f doesn't hold &6any GN&f!");
			else
				Utils.message(sender, who + " &binventory&f holds &6" + goldValue + "GN&f!");
			if (sellValue != 0)
				Utils.message(sender, who + " &binventory&f can sell for &6" + sellValue + "GN&f!");
			return;
		}

		// count
		ItemStack itemStack = player.getItemInHand();
		if (itemStack == null || itemStack.getType() == Material.AIR)
			throw new IllegalArgumentException("You can't count air!");
		int count = ShoppingUtils.countInInventory(player.getInventory(), itemStack);

		String name = ShoppingUtils.toString(itemStack, true);

		if (count == 0)
			Utils.message(sender, who + " &binventory&f holds &bno &6" + name + "&f!");
		else
			Utils.message(sender, who + " &binventory&f holds &6" + count + " &b" + name + "&f!");

	}
}
