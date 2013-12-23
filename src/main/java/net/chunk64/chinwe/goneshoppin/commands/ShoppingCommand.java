package net.chunk64.chinwe.goneshoppin.commands;

import net.chunk64.chinwe.goneshoppin.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class ShoppingCommand implements CommandExecutor
{
	protected String command;
	protected boolean playerOnly;
	protected Permission perm;

	public ShoppingCommand(Permission perm, boolean playerOnly, String command)
	{
		this.playerOnly = playerOnly;
		this.perm = perm;
		this.command = command;
		//		this.commands = new ArrayList<String>();
		//		for (String s : commands)
		//			this.commands.add(s);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		try
		{
			if (cmd.getName().equalsIgnoreCase(command))
			{
				// player only
				if (playerOnly && !isPlayer(sender))
					return true;

				// permission check
				if (perm != null && !perm.senderHas(sender))
					return true;

				run(sender, cmd, args);

				return true;
			}


		} catch (NumberFormatException e)
		{
			Utils.message(sender, "&c" + (e.getMessage() == null ? "A number was expected!" : Utils.stripColour(e.getMessage())));
		} catch (IllegalArgumentException e)
		{
			Utils.message(sender, "&c" + Utils.stripColour(e.getMessage()));
		} catch (IncorrectUsageException e)
		{
			Utils.message(sender, "&cUsage: /" + cmd.getName() + (e.getMessage() != null ? " " + Utils.stripColour(e.getMessage()) : ""));
		} catch (Exception e)
		{
			Utils.message(sender, "&cError: " + (e.getMessage() == null ? e : Utils.stripColour(e.getMessage())));
			e.printStackTrace();
		}
		return true;
	}

	public abstract void run(CommandSender sender, Command cmd, String[] args) throws Exception;

	protected boolean isPlayer(CommandSender sender)
	{
		boolean player = sender instanceof Player;
		if (!player)
			Utils.message(sender, "&cOnly players can use that command!");
		return player;
	}

	public static Player getPlayer(CommandSender sender, String name)
	{
		Player player = Bukkit.getPlayer(name);
		if (player == null)
			Utils.message(sender, "&c'" + name + "' is not online!");
		return player;
	}

}
