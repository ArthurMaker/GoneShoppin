package net.chunk64.chinwe.goneshoppin.banking;

import net.chunk64.chinwe.goneshoppin.util.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Bank
{

	private static Bank instance;

	private FileManager bankFile;
	private Map<String, Account> accounts;
	private Plugin plugin;

	public Bank(Plugin plugin)
	{
		instance = this;
		accounts = new HashMap<String, Account>();
		this.plugin = plugin;
		bankFile = new FileManager("bank", "This is where all bank accounts are stored.", plugin);
		if (bankFile.getYml().getConfigurationSection("accounts") == null)
			bankFile.getYml().createSection("accounts");
	}

	/**
	 * Loads all accounts from banks.yml
	 */
	public static void loadAllAccounts()
	{
		FileConfiguration yml = instance.bankFile.getYml();
		int count = 0;
		for (String s : yml.getConfigurationSection("accounts").getKeys(false))
		{
			Bank.getInstance().addAccount((Account) yml.get("accounts." + s));
			count++;
		}

		instance.plugin.getLogger().info("Loaded " + count + " accounts to " + instance.bankFile.getFile().getName());

	}

	/**
	 * Saves all accounts to banks.yml
	 */
	public static void saveAllAccounts()
	{
		FileConfiguration yml = instance.bankFile.getYml();
		int count = 0;
		for (Account account : instance.getAccounts())
		{
			yml.set("accounts." + account.getName(), account);
			count++;
		}
		instance.bankFile.save();
		instance.plugin.getLogger().info("Saved " + count + " accounts to " + instance.bankFile.getFile().getName());
	}

	public void addAccount(Account account)
	{
		accounts.put(account.getName(), account);
	}

	public void removeAccount (Account account)
	{
		accounts.remove(account.getName());
	}

	public static Bank getInstance()
	{
		return instance;
	}


	/**
	 * Returns the player's account, or makes a new one if it isn't found
	 */
	public Account getAccount(String player)
	{
		Account account = accounts.get(player);
		if (account == null)
			account = new Account(player);
		return account;
	}

	/** Returns the account for a player based on the given name: if not found, returns the account for the literal name. If still not found, returns null */
	public Account getAccountFuzzily(String player)
	{
		Player targetPlayer = Bukkit.getPlayer(player);
		String target = targetPlayer == null ? player : targetPlayer.getName();

		if (!Bank.getInstance().hasAccount(target))
			return null;
		return getAccount(target);
	}

	public boolean hasAccount(String player)
	{
		return accounts.containsKey(player);
	}

	public Collection<Account> getAccounts()
	{
		return accounts.values();
	}

}
