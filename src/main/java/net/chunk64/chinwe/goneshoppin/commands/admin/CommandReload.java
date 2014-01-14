package net.chunk64.chinwe.goneshoppin.commands.admin;

import net.chunk64.chinwe.goneshoppin.GSSave;
import net.chunk64.chinwe.goneshoppin.commands.IncorrectUsageException;
import net.chunk64.chinwe.goneshoppin.commands.Permission;
import net.chunk64.chinwe.goneshoppin.commands.ShoppingCommand;
import net.chunk64.chinwe.goneshoppin.logging.GSLogger;
import net.chunk64.chinwe.goneshoppin.util.Config;
import net.chunk64.chinwe.goneshoppin.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandReload extends ShoppingCommand
{

	public CommandReload()
	{
		setPermission(Permission.RELOAD);
		setPlayerOnly(false);
	}

	// /gsreload
	// /gssave

	@Override
	public void run(CommandSender sender, Command cmd, String[] args) throws Exception
	{
		// usage
		if (args.length != 0)
			throw new IncorrectUsageException();

		boolean config = cmd.getName().equalsIgnoreCase("gsreload");

		if (config)
			Config.getInstance().reload();
		else
		{
			GSSave.save();
			GSSave.getInstance().updateSaveTime();
		}

		String message = config ? "reloaded the config" : "saved all data";
		Utils.message(sender, "You &b" + message + "&f!");
		GSLogger.getInstance().alert(sender.getName() + " " + message, false, sender.getName());
	}

}
