package net.chunk64.chinwe.goneshoppin;

import net.chunk64.chinwe.goneshoppin.banking.Account;
import net.chunk64.chinwe.goneshoppin.banking.Bank;
import net.chunk64.chinwe.goneshoppin.banking.BankLimit;
import net.chunk64.chinwe.goneshoppin.commands.Permission;
import net.chunk64.chinwe.goneshoppin.commands.ShoppingCommand;
import net.chunk64.chinwe.goneshoppin.commands.bank.CommandBalance;
import net.chunk64.chinwe.goneshoppin.commands.bank.CommandBanking;
import net.chunk64.chinwe.goneshoppin.commands.misc.CommandId;
import net.chunk64.chinwe.goneshoppin.commands.misc.CommandMisc;
import net.chunk64.chinwe.goneshoppin.commands.shop.CommandPrice;
import net.chunk64.chinwe.goneshoppin.util.Config;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;

public class GoneShoppin extends JavaPlugin
{

	private static GoneShoppin instance;

	private Config config;


	@Override
	public void onEnable()
	{
		instance = this;
		ConfigurationSerialization.registerClass(Account.class);
		init();
		registerCommands();
//		MySQLUtils.init(this);

	}

	@Override
	public void onDisable()
	{
		Bank.saveAllAccounts();

		instance = null;
		config = null;
		MaterialAlias.unload();
	}

	private void init()
	{
		this.config = new Config(this);
		BankLimit.loadLimits();

		new Bank(this);
		Bank.loadAllAccounts();

		MaterialAlias.loadFromFile();
	}

	private void registerCommands()
	{
		register("deposit", CommandBanking.class, true, Permission.BANK_DEPOSIT);
		register("withdraw", CommandBanking.class, true, Permission.BANK_WITHDRAW);
		register("steal", CommandBanking.class, true, Permission.BANK_STEAL);
		register("balance", CommandBalance.class, true, Permission.BANK_BALANCE);
		register("value", CommandMisc.class, true, Permission.VALUE);
		register("count", CommandMisc.class, true, Permission.COUNT);
		register("price", CommandPrice.class, true, Permission.PRICE);
		register("id", CommandId.class, true, Permission.ID);
	}

	private void register(String command, Class clazz, boolean playerOnly, Permission perm)
	{
		try
		{
			Constructor constructor = clazz.getConstructor(Permission.class, Boolean.TYPE, String.class);
			ShoppingCommand sc = (ShoppingCommand) constructor.newInstance(perm, playerOnly, command);
			getCommand(command).setExecutor(sc);
		} catch (Exception e)
		{
			getLogger().severe("Could not register command '" + command + "': " + e);
		}
	}

	public static GoneShoppin getInstance()
	{
		return instance;
	}


	public Config getConfigFile()
	{
		return config;
	}
}
