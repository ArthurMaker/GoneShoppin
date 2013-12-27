package net.chunk64.chinwe.goneshoppin.logging;

import net.chunk64.chinwe.goneshoppin.GoneShoppin;
import net.chunk64.chinwe.goneshoppin.commands.Permission;
import net.chunk64.chinwe.goneshoppin.util.Config;
import net.chunk64.chinwe.goneshoppin.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class GSLogger
{

	private static GSLogger instance;
	private static File file;
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat();

	private BufferedWriter writer;
	private Set<String> grumpyAdmins = new HashSet<String>();

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
			}
			else
				log = "Log file found, loading...";

			GoneShoppin.getInstance().getLogger().info(log);
			writer = new BufferedWriter(new FileWriter(file, true));
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	public static GSLogger getInstance()
	{
		return instance;
	}

	public void log(String string)
	{
		// save to log file
		try
		{
			writer.write(getTime() + " : " + string);
			writer.newLine();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		// message admins
		for (Player player : Bukkit.getOnlinePlayers())
			if (Permission.ALERT.senderHas(player) && !grumpyAdmins.contains(player.getName()))
				Utils.message(player, "&7" + string);

		// log to console
		if (Config.ConsoleLog)
			Utils.message(Bukkit.getConsoleSender(), string);
	}

	public static void unload()
	{
		file = null;
		try
		{
			getInstance().writer.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		getInstance().writer = null;
		instance = null;
	}


	public static void log(LoggerAction action)
	{
		getInstance().log(action.getMessage());
	}

	private static String getTime()
	{
		return simpleDateFormat.format(new Date());
	}
}
