package net.chunk64.chinwe.goneshoppin.util;

import net.chunk64.chinwe.goneshoppin.banking.BankLimit;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;

public class Config
{
	private Configuration config;
	private File configFile;
	private Plugin plugin;

	public static BankLimit DefaultLimit;
	public static boolean ConsoleLog, PriceProtection;
	public static List<String> PriceAdmins;


	public Config(Plugin plugin)
	{
		this.plugin = plugin;
		configFile = new File(plugin.getDataFolder(), "config.yml");
		config = plugin.getConfig().getRoot();
		if (!configFile.exists())
			plugin.saveDefaultConfig();

		load();

	}

	public void load()
	{
		DefaultLimit = BankLimit.valueOf(config.getString("bank.default-limit").toUpperCase());
		ConsoleLog = config.getBoolean("console-log");
		PriceProtection = config.getBoolean("price-protection.enabled");
		PriceAdmins = config.getStringList("price-protection.allowed");
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