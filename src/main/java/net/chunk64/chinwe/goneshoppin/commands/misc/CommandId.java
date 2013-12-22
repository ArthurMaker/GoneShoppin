package net.chunk64.chinwe.goneshoppin.commands.misc;

import net.chunk64.chinwe.goneshoppin.commands.IncorrectUsageException;
import net.chunk64.chinwe.goneshoppin.commands.Permission;
import net.chunk64.chinwe.goneshoppin.commands.ShoppingCommand;
import net.chunk64.chinwe.goneshoppin.util.ShoppingUtils;
import net.chunk64.chinwe.goneshoppin.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandId extends ShoppingCommand
{

	public CommandId(Permission perm, boolean playerOnly, String command)
	{
		super(perm, playerOnly, command);
	}

	// id material

	@Override
	public void run(CommandSender sender, Command cmd, String[] args) throws Exception
	{
		Player player = (Player) sender;

		// usage
		if (args.length > 1)
			throw new IncorrectUsageException("[material]");

		// get itemstack
		ItemStack itemStack = args.length == 0 ? player.getItemInHand() : ShoppingUtils.parseInput(args[0]);
		if (itemStack == null)
			return;

		// id
		String id = ShoppingUtils.toString(itemStack, false);
		Utils.message(sender, "The ID of" + (args.length == 0 ? " that" : "") + " &6" + Utils.friendlyName(itemStack.getType()) + " &fis " + id);

	}
}
