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

	public CommandMisc(Permission perm, boolean playerOnly, String command)
	{
		super(perm, playerOnly, command);
	}

	@Override
	public void run(CommandSender sender, Command cmd, String[] args) throws Exception
	{
		Player player = (Player) sender;
		boolean value = cmd.getName().equalsIgnoreCase("value");

		// usage
		if (args.length > 1)
			throw new IncorrectUsageException("[player]");

		// get target
		Player target = args.length == 0 ? player : getPlayer(sender, args[0]);
		if (target == null)
			return;

		boolean self = target == player;

		// permission check
		if (!self)
		{
			if (value && !Permission.VALUE_OTHER.senderHas(sender))
				return;
			if (!value && !Permission.COUNT_OTHER.senderHas(sender))
				return;
		}

		String who = self ? "Your" : target.getName() + "'s";

		// value
		// TODO value selling prices
		if (value)
		{
			int goldValue = ShoppingUtils.valueInventory(target);

			if (goldValue == 0)
				Utils.message(sender, who + " &binventory&f is &6worthless&f!");
			else
				Utils.message(sender, who + " &binventory&f holds &6" + goldValue + "GN&f!");
			return;
		}

		// count
		ItemStack itemStack = player.getItemInHand();
		if (itemStack == null || itemStack.getType() == Material.AIR)
			throw new IllegalArgumentException("You can't count air!");
		int count = ShoppingUtils.countInInventory(player, itemStack.getData());

		String name = ShoppingUtils.toString(itemStack, true);

		if (count == 0)
			Utils.message(sender, who + " &binventory&f holds &bno &6" + name + "&f!");
		else
			Utils.message(sender, who + " &binventory&f holds &6" + count + " &b" + name + "&f!");

	}
}
