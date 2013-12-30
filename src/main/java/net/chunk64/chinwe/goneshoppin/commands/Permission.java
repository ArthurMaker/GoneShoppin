package net.chunk64.chinwe.goneshoppin.commands;

import net.chunk64.chinwe.goneshoppin.util.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

public enum Permission
{

	BANK_DEPOSIT("deposit into the bank", true), BANK_WITHDRAW("withdraw from the bank", true),
	BANK_BALANCE("view your balance", true), BANK_BALANCE_OTHER("view others' balances", false),
	BANK_STEAL("steal from others", false),

	VALUE("value your inventory", true), VALUE_OTHER("value others' inventories", false),
	COUNT("count in your inventory", true), COUNT_OTHER("count in others' inventories", false),
	PRICE("price items", true), ID(null, true),
	SELL("sell items", true), BUY("buy items", true),

	SET_BALANCE("set players' balances", false), SET_BALANCE_OVERLIMIT("set players' balances over their limits", false),
	SET_LIMIT("set players' limits", false),
	CASH("cash your gold to nuggets", true), SIMPLIFY("simplify your gold", true), SET_PRICE("set prices", false), SET_NOTE("set notes", false),

	ALERT("receive transaction alerts", false), MONITOR("toggle alert receiving", false),

	// help menus
	HELP_GONESHOPPIN("view the help menu", true), HELP_BANK("view banking help", true),
	HELP_SHOP("view shopping help", true), HELP_ADMIN("view admin help", false), HELP_MISC("view miscellaneous help", true),
	SAVE("force a save", false), RELOAD("reload the config", false);

	private static final String PLUGIN_NAME = "goneshoppin";
	private String message;
	private boolean defaultPerm;

	Permission(String message, boolean defaultPerm)
	{
		this.message = message;
		this.defaultPerm = defaultPerm;
	}

	public String getMessage()
	{
		return message == null ? "do that!" : message;
	}

	@Override
	public String toString()
	{
		return name().toLowerCase().replace("_", ".");
	}

	public boolean isDefault()
	{
		return defaultPerm;
	}

	public org.bukkit.permissions.Permission getPermission()
	{
		return new org.bukkit.permissions.Permission(PLUGIN_NAME + "." + toString(), defaultPerm ? PermissionDefault.TRUE : PermissionDefault.OP);
	}

	public boolean senderHas(CommandSender sender)
	{
		boolean hasPerm = sender.hasPermission(getPermission());
		if (!hasPerm)
			Utils.message(sender, "&cYou can't " + getMessage() + ", you need &6" + getPermission().getName());
		return hasPerm;
	}
}
