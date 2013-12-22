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
	PRICE("price items", true), ID(null, true);

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
