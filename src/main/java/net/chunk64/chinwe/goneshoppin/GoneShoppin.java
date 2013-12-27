package net.chunk64.chinwe.goneshoppin;

import net.chunk64.chinwe.goneshoppin.banking.Account;
import net.chunk64.chinwe.goneshoppin.banking.Bank;
import net.chunk64.chinwe.goneshoppin.banking.BankLimit;
import net.chunk64.chinwe.goneshoppin.commands.Permission;
import net.chunk64.chinwe.goneshoppin.commands.ShoppingCommand;
import net.chunk64.chinwe.goneshoppin.commands.admin.CommandAdmin;
import net.chunk64.chinwe.goneshoppin.commands.admin.CommandSetPrice;
import net.chunk64.chinwe.goneshoppin.commands.bank.CommandBalance;
import net.chunk64.chinwe.goneshoppin.commands.bank.CommandBanking;
import net.chunk64.chinwe.goneshoppin.commands.misc.CommandChange;
import net.chunk64.chinwe.goneshoppin.commands.misc.CommandId;
import net.chunk64.chinwe.goneshoppin.commands.misc.CommandMisc;
import net.chunk64.chinwe.goneshoppin.commands.shop.CommandBuy;
import net.chunk64.chinwe.goneshoppin.commands.shop.CommandPrice;
import net.chunk64.chinwe.goneshoppin.items.Alias;
import net.chunk64.chinwe.goneshoppin.items.GSItem;
import net.chunk64.chinwe.goneshoppin.listeners.ShoppinListener;
import net.chunk64.chinwe.goneshoppin.logging.GSLogger;
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
		registerListeners();
		//		MySQLUtils.init(this);

	}

	@Override
	public void onDisable()
	{
		Bank.saveAllAccounts();
		GSItem.unload();
		instance = null;
		config = null;
		GSLogger.unload();
	}

	private void init()
	{
		this.config = new Config(this);
		BankLimit.loadLimits();

		new Bank(this);
		Bank.loadAllAccounts();
		Alias.loadFromFile();
		GSItem.loadFile(this);

		new GSLogger(this);
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
		register("sell", CommandPrice.class, true, Permission.SELL);
		register("buy", CommandBuy.class, true, Permission.BUY);
		register("id", CommandId.class, true, Permission.ID);
		register("setbalance", CommandAdmin.class, false, Permission.SET_BALANCE);
		register("setlimit", CommandAdmin.class, false, Permission.SET_LIMIT);
		register("cash", CommandChange.class, true, Permission.CASH);
		register("simplify", CommandChange.class, true, Permission.SIMPLIFY);
		register("setprice", CommandSetPrice.class, false, Permission.SET_PRICE);
		register("setnote", CommandSetPrice.class, false, Permission.SET_NOTE);
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

	private void registerListeners()
	{
		getServer().getPluginManager().registerEvents(new ShoppinListener(), this);
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
