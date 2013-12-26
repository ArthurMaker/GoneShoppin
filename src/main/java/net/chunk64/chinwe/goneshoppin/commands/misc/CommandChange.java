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

public class CommandChange extends ShoppingCommand
{

	public CommandChange(Permission perm, boolean playerOnly, String command)
	{
		super(perm, playerOnly, command);
	}

	@Override
	public void run(CommandSender sender, Command cmd, String[] args) throws Exception
	{
		Player player = (Player) sender;
		boolean simplify = cmd.getName().equalsIgnoreCase("simplify");

		// usage
		if (args.length != 0)
			throw new IncorrectUsageException();

		int value = ShoppingUtils.valueInventory(player);
		if (value == 0)
			throw new IllegalArgumentException("You have no money to " + cmd.getName().toLowerCase() + "!");

		// take money
		ShoppingUtils.takeGold(player, value);


		if (simplify)
			ShoppingUtils.giveGold(player, value);// give it back, simplified
		else
			ShoppingUtils.giveItems(player, new ItemStack(Material.GOLD_NUGGET, value)); // give it back in only nuggets


		Utils.message(sender, "You " + (simplify ? "simplified" : "cashed") + " &6" + value + "GN &fto&b " + (!simplify ? "" : "gold blocks&f, &bingots&f and&b ") + "nuggets&f!");

	}
}
