package net.chunk64.chinwe.goneshoppin.util;

import net.chunk64.chinwe.goneshoppin.banking.BankLimit;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class Config
{
	private Configuration config;
	private File configFile;
	private Plugin plugin;

	public static BankLimit DefaultLimit;
	public static boolean ConsoleLog;


	public Config(Plugin plugin)
	{
		this.plugin = plugin;
		configFile = new File(plugin.getDataFolder(), "config.yml");
		config = plugin.getConfig().getRoot();
		if (!configFile.exists())
			plugin.saveDefaultConfig();

		// load values
		DefaultLimit = BankLimit.valueOf(config.getString("bank.default-limit").toUpperCase());
		ConsoleLog = config.getBoolean("console-log");

	}

	public void save()
	{
		plugin.saveConfig();
	}

	public Configuration getConfiguration()
	{
		return config;
	}
}