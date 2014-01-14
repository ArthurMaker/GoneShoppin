package net.chunk64.chinwe.goneshoppin;

import net.chunk64.chinwe.goneshoppin.banking.Account;
import net.chunk64.chinwe.goneshoppin.banking.Bank;
import net.chunk64.chinwe.goneshoppin.banking.BankLimit;
import net.chunk64.chinwe.goneshoppin.commands.Permission;
import net.chunk64.chinwe.goneshoppin.commands.ShoppingCommand;
import net.chunk64.chinwe.goneshoppin.commands.admin.CommandBankAdmin;
import net.chunk64.chinwe.goneshoppin.commands.admin.CommandMonitor;
import net.chunk64.chinwe.goneshoppin.commands.admin.CommandReload;
import net.chunk64.chinwe.goneshoppin.commands.admin.CommandSetPrice;
import net.chunk64.chinwe.goneshoppin.commands.bank.CommandBalance;
import net.chunk64.chinwe.goneshoppin.commands.bank.CommandBanking;
import net.chunk64.chinwe.goneshoppin.commands.help.*;
import net.chunk64.chinwe.goneshoppin.commands.misc.CommandChange;
import net.chunk64.chinwe.goneshoppin.commands.misc.CommandId;
import net.chunk64.chinwe.goneshoppin.commands.misc.CommandMisc;
import net.chunk64.chinwe.goneshoppin.commands.shop.CommandBuy;
import net.chunk64.chinwe.goneshoppin.commands.shop.CommandPrice;
import net.chunk64.chinwe.goneshoppin.commands.shop.CommandSell;
import net.chunk64.chinwe.goneshoppin.items.Alias;
import net.chunk64.chinwe.goneshoppin.items.GSItem;
import net.chunk64.chinwe.goneshoppin.listeners.ShoppinListener;
import net.chunk64.chinwe.goneshoppin.logging.GSLogger;
import net.chunk64.chinwe.goneshoppin.shop.SignManager;
import net.chunk64.chinwe.goneshoppin.util.Config;
import net.chunk64.chinwe.goneshoppin.util.FileManager;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;

public class GoneShoppin extends JavaPlugin
{

	private static GoneShoppin instance;
	private FileManager data;

	@Override
	public void onEnable()
	{
		instance = this;
		data = new FileManager("data", "This is where various data will be stored, such as player preferences", this);
		ConfigurationSerialization.registerClass(Account.class);
		init();
		registerCommands();
		registerListeners();
		registerHelp();
	}

	@Override
	public void onDisable()
	{
		GSSave.save();
		GSItem.unload();
		instance = null;
		GSLogger.unload();
		data.destroy(true);
	}

	private void init()
	{
		new Config(this);
		BankLimit.loadLimits();

		new Bank(this);
		Bank.loadAllAccounts();
		Alias.loadFromFile();
		GSItem.loadFile(this);

		new GSLogger(this);

		if (Config.SaveMinutes > 0)
			new GSSave(this);
	}

	private void registerCommands()
	{
		register("deposit", CommandBanking.class);
		register("withdraw", CommandBanking.class);
		register("steal", CommandBanking.class);
		register("balance", CommandBalance.class);
		register("value", CommandMisc.class);
		register("count", CommandMisc.class);
		register("price", CommandPrice.class);
		register("sell", CommandSell.class);
		register("buy", CommandBuy.class);
		register("id", CommandId.class);
		register("setbalance", CommandBankAdmin.class);
		register("setlimit", CommandBankAdmin.class);
		register("cash", CommandChange.class);
		register("simplify", CommandChange.class);
		register("setprice", CommandSetPrice.class);
		register("setnote", CommandSetPrice.class);
		register("monitor", CommandMonitor.class);
		register("gssave", CommandReload.class);
		register("gsreload", CommandReload.class);

	}

	private void register(String command, Class clazz)
	{
		try
		{
			Constructor constructor = clazz.getConstructor(String.class);
			ShoppingCommand sc = (ShoppingCommand) constructor.newInstance(command);
			getCommand(command).setExecutor(sc);
		} catch (Exception e)
		{
			getLogger().severe("Could not register command '" + command + "': " + e);
		}
	}

	private void registerListeners()
	{
		getServer().getPluginManager().registerEvents(new ShoppinListener(), this);
		getServer().getPluginManager().registerEvents(new SignManager(), this);
	}


	private void registerHelp()
	{
		HelpManager helpManager = new HelpManager(this, true, 10);
		HelpTopic goneshoppin = new HelpTopic("goneshoppin", "GoneShoppin Help", Permission.HELP_GONESHOPPIN.getPermission());
		helpManager.addHelpTopic(goneshoppin);

		// subs
		SubTopic bank = new SubTopic("bank", "View banking help", "Banking Help", Permission.HELP_BANK.getPermission());
		SubTopic shop = new SubTopic("shop", "View shopping help", "Shopping Help", Permission.HELP_SHOP.getPermission());
		SubTopic misc = new SubTopic("misc", "View miscellaneous command help", "Miscellaneous Help", Permission.HELP_MISC.getPermission());
		SubTopic admin = new SubTopic("admin", "View admin help", "Admin Help", Permission.HELP_ADMIN.getPermission());
		goneshoppin.addSubs(bank, shop, misc, admin);

		// topics
		GeneralTopic material = new GeneralTopic("&7Whenever you are prompted to enter a &3[material]&7, you can enter either a " +
				"material's &3id&7, &3name&7 or &3\"hand\"&7, optionally followed by " +
				"a &3damage value&7: i.e &81  hand  wood:3  birchwood  17:1  hand:2");

		bank.addTopics(new CommandTopic("withdraw"), new CommandTopic("deposit"), new CommandTopic("balance"), new CommandTopic("steal", Permission.BANK_STEAL.getPermission()), new CommandTopic("setbalance", Permission.SET_BALANCE.getPermission()), new CommandTopic("setlimit", Permission.SET_LIMIT.getPermission()));
		shop.addTopics(new CommandTopic("buy"), new CommandTopic("sell"), new CommandTopic("price"), new CommandTopic("value"), material);
		misc.addTopics(new CommandTopic("count"), new CommandTopic("id"), new CommandTopic("cash"), new CommandTopic("simplify"), material);
		admin.addTopics(new CommandTopic("setprice"), new CommandTopic("setnote"), new CommandTopic("monitor"));
	}

	public static GoneShoppin getInstance()
	{
		return instance;
	}


	public FileManager getData()
	{
		return data;
	}
}
