package net.chunk64.chinwe.goneshoppin.banking;

import net.chunk64.chinwe.goneshoppin.util.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.math.BigDecimal;
import java.util.*;

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

		// lowercase
		instance.plugin.getLogger().info("Loaded " + count + " accounts to " + instance.bankFile.getFile().getName());

	}

	/**
	 * Saves all accounts to banks.yml
	 */
	public static void saveAllAccounts()
	{
		FileConfiguration yml = instance.bankFile.getYml();
		List<Account> sortedAccounts = new ArrayList<Account>(instance.getAccounts());
		Collections.sort(sortedAccounts);
		int count = 0;
		for (Account account : sortedAccounts)
		{
			// checks
			if (!account.isTemporary() || !account.getBalance().equals(BigDecimal.ZERO))
			{
				yml.set("accounts." + account.getName(), account);
				count++;
			}

		}
		instance.bankFile.save();
		instance.plugin.getLogger().info("Saved " + count + " accounts to " + instance.bankFile.getFile().getName());
	}

	public void addAccount(Account account)
	{
		accounts.put(account.getName(), account);
	}

	public void removeAccount(Account account)
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
		player = player.toLowerCase();
		Account account = findAccount(player);
		if (account == null)
			account = new Account(player, true);
		return account;
	}

	private Account findAccount(String name)
	{
		for (Account account : accounts.values())
			if (account.getName().equalsIgnoreCase(name) || account.getName().startsWith(name))
				return account;

		return accounts.get(name);

	}

	/**
	 * Returns the account for a player based on the given name: if not found, returns the account for the literal name. If still not found, returns null
	 */
	public Account getAccountFuzzily(String player)
	{
		Player targetPlayer = Bukkit.getPlayer(player);
		String target = targetPlayer == null ? player : targetPlayer.getName();

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
