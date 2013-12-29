package net.chunk64.chinwe.goneshoppin.logging;

import net.chunk64.chinwe.goneshoppin.GoneShoppin;
import net.chunk64.chinwe.goneshoppin.commands.Permission;
import net.chunk64.chinwe.goneshoppin.util.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GSLogger
{

	private static GSLogger instance;
	private static File file;
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat();

	public List<String> grumpyAdmins;

	public GSLogger(Plugin plugin)
	{
		instance = this;
		try
		{
			file = new File(plugin.getDataFolder(), "log.txt");

			String log;
			if (!file.exists())
			{
				log = "Could not find transaction log file! Creating...";
				file.createNewFile();
			} else
				log = "Log file found, loading...";

			GoneShoppin.getInstance().getLogger().info(log);
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		loadData();

	}

	public static GSLogger getInstance()
	{
		return instance;
	}

	private void loadData()
	{
		grumpyAdmins = GoneShoppin.getInstance().getData().getYml().getStringList("grumpy-transactions-monitors");
	}

	public void log(String string)
	{

		// save to log file
		try
		{
			// decided to save file after each log
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));

			writer.write(getTime() + " : " + string);
			writer.newLine();
			writer.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		// message admins
		for (Player player : Bukkit.getOnlinePlayers())
			if (player.hasPermission(Permission.ALERT.getPermission()) && !grumpyAdmins.contains(player.getName()))
				message(player, string);

		// log to console
		if (Config.ConsoleLog)
			message(Bukkit.getConsoleSender(), string);
	}

	public static void unload()
	{
		file = null;
		instance = null;
		simpleDateFormat = null;
	}


	public static void log(LoggerAction action)
	{
		getInstance().log(action.getMessage());
	}

	private static String getTime()
	{
		return simpleDateFormat.format(new Date());
	}

	private static void message(CommandSender sender, String message)
	{
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8: &7&o" + message));
	}
}
