package net.chunk64.chinwe.goneshoppin.commands;

import net.chunk64.chinwe.goneshoppin.items.GSItem;
import net.chunk64.chinwe.goneshoppin.logging.GSLogger;
import net.chunk64.chinwe.goneshoppin.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class ShoppingCommand implements CommandExecutor
{
	protected String command;
	protected boolean playerOnly;
	protected Permission permission;

	public ShoppingCommand(String command)
	{
		this.command = command;
	}

	protected ShoppingCommand()
	{
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		try
		{
			if (cmd.getName().equalsIgnoreCase(command))
			{
				// player only
				if (playerOnly && !isPlayer(sender))
					return true;

				// permission check
				if (!hasPermission(sender))
					return true;

				run(sender, cmd, args);

				return true;
			}


		} catch (NumberFormatException e)
		{
			Utils.message(sender, "&c" + (e.getMessage() == null ? "A number was expected!" : Utils.stripColour(e.getMessage())));
		} catch (IllegalArgumentException e)
		{
			Utils.message(sender, "&c" + Utils.stripColour(e.getMessage()));
		} catch (IncorrectUsageException e)
		{
			Utils.message(sender, "&cUsage: " + cmd.getUsage());
			// Utils.message(sender, "&cUsage: /" + cmd.getUsage() + (e.getMessage() != null ? " " + Utils.stripColour(e.getMessage()) : ""));
		} catch (Exception e)
		{
			String message = e.getMessage() == null ? e.toString() : Utils.stripColour(e.getMessage());
			Utils.message(sender, "&cError: " + message);
			GSLogger.getInstance().alert("Error when " + sender.getName() + " ran '/" + label + " " + StringUtils.join(args, ' ') + "': " + message, false, sender.getName());
			e.printStackTrace();
		}
		return true;
	}

	public abstract void run(CommandSender sender, Command cmd, String[] args) throws Exception;

	protected boolean isPlayer(CommandSender sender)
	{
		boolean player = sender instanceof Player;
		if (!player)
			Utils.message(sender, "&cOnly players can use that command!");
		return player;
	}

	public static void checkMultiples(GSItem gsItem, ItemStack itemStack, boolean buying)
	{
		int minAmount = gsItem.getPerTransaction(buying);
		if (itemStack.getAmount() % minAmount != 0)
		{
			// has more
			if (itemStack.getAmount() > minAmount)
				itemStack.setAmount((itemStack.getAmount() / minAmount) * minAmount);
			else
				throw new IllegalArgumentException("You can only " + (buying ? "buy" : "sell") + " that item in multiples of " + minAmount + "!");
		}
	}

	public static void validatePrice(double finalPrice, String itemName)
	{
		if (finalPrice == 0)
		{
			GSLogger.getInstance().alert("The price for " + itemName + " must be changed! Avoid multiples of 0.3.", false);
			throw new IllegalArgumentException("The price for this item is invalid! Online admins have been alerted.");
		}
	}

	public static Player getPlayer(CommandSender sender, String name)
	{
		Player player = Bukkit.getPlayer(name);
		if (player == null)
			Utils.message(sender, "&c'" + name + "' is not online!");
		return player;
	}

	public boolean hasPermission(CommandSender sender, Permission permission)
	{
		if (permission == null)
			return true;

		boolean hasPerm = sender.hasPermission(permission.getPermission());
		if (!hasPerm)
			Utils.message(sender, "&cYou can't " + permission.getMessage() + ", you need &6" + permission.getPermission().getName());
		return hasPerm;
	}

	public boolean hasPermission(CommandSender sender)
	{
		return hasPermission(sender, permission);
	}

	public String getCommand()
	{
		return command;
	}

	public void setCommand(String command)
	{
		this.command = command;
	}

	public boolean isPlayerOnly()
	{
		return playerOnly;
	}

	public void setPlayerOnly(boolean playerOnly)
	{
		this.playerOnly = playerOnly;
	}

	public Permission getPermission()
	{
		return permission;
	}

	public void setPermission(Permission permission)
	{
		this.permission = permission;
	}
}
