package net.chunk64.chinwe.goneshoppin.commands.admin;

import net.chunk64.chinwe.goneshoppin.GoneShoppin;
import net.chunk64.chinwe.goneshoppin.commands.IncorrectUsageException;
import net.chunk64.chinwe.goneshoppin.commands.Permission;
import net.chunk64.chinwe.goneshoppin.commands.ShoppingCommand;
import net.chunk64.chinwe.goneshoppin.logging.GSLogger;
import net.chunk64.chinwe.goneshoppin.util.FileManager;
import net.chunk64.chinwe.goneshoppin.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CommandMonitor extends ShoppingCommand
{

	public CommandMonitor()
	{
		setPermission(Permission.MONITOR);
		setPlayerOnly(true);
	}

	@Override
	public void run(CommandSender sender, Command cmd, String[] args) throws Exception
	{
		// usage
		if (args.length != 0)
			throw new IncorrectUsageException();

		List<String> admins = GSLogger.getInstance().grumpyAdmins;

		// update list
		boolean isRemoving = admins.contains(sender.getName());
		if (isRemoving)
			admins.remove(sender.getName());
		else
			admins.add(sender.getName());

		FileManager data = GoneShoppin.getInstance().getData();
		data.getYml().set("grumpy-transactions-monitors", admins);
		data.save();

		// message
		Utils.message(sender, "You will &6" + (!isRemoving ? "no longer" : "now") + "&f receive notifications of &ball transactions&f!");

	}

}
