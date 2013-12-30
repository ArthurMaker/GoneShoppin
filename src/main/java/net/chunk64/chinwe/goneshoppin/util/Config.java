package net.chunk64.chinwe.goneshoppin.util;

import net.chunk64.chinwe.goneshoppin.banking.BankLimit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;

public class Config
{
	private static Config instance;

	private Configuration config;
	private File configFile;
	private Plugin plugin;

	public static BankLimit DefaultLimit;
	public static boolean ConsoleLog, PriceProtection;
	public static List<String> PriceAdmins;
	public static int SaveMinutes;


	public Config(Plugin plugin)
	{
		instance = this;
		this.plugin = plugin;
		create();
		load();
	}


	public void load()
	{
		DefaultLimit = BankLimit.valueOf(config.getString("bank.default-limit").toUpperCase());
		ConsoleLog = config.getBoolean("console-log");
		PriceProtection = config.getBoolean("price-protection.enabled");
		PriceAdmins = config.getStringList("price-protection.allowed");
		SaveMinutes = config.getInt("minute-interval-between-saves");
	}

	public void save()
	{
		plugin.saveConfig();
	}

	private void create()
	{
		configFile = new File(plugin.getDataFolder(), "config.yml");
		config = YamlConfiguration.loadConfiguration(configFile).getRoot();
		if (!configFile.exists())
			plugin.saveDefaultConfig();
	}

	public void reload()
	{
		create();
		load();
		BankLimit.loadLimits();
	}

	public Configuration getConfiguration()
	{
		return config;
	}

	public static Config getInstance()
	{
		return instance;
	}
}