package net.chunk64.chinwe.goneshoppin.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Utils
{
	public static final String PREFIX = "§8[§6|§8] §r";

	/**
	 * Messages a command sender with the given message with translated chat colours
	 */
	public static void message(CommandSender sender, String message)
	{
		sender.sendMessage(PREFIX + ChatColor.translateAlternateColorCodes('&', message));
	}

	/**
	 * Formats a list with "and" or "or" between the last two elements
	 */
	public static String formatList(List<String> list, boolean and)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++)
		{
			String st = list.get(i);
			if (i == list.size() - 1)
				sb.append(st);
			else if (i == list.size() - 2)
				sb.append(st + (and ? " and " : " or "));
			else
				sb.append(st + ", ");

		}
		return sb.toString();
	}

	/**
	 * Attempts to convert a string to an integer -> returns null otherwise
	 */
	public static Integer getInt(String s)
	{
		try
		{
			return Integer.parseInt(s);
		} catch (NumberFormatException e)
		{
			return null;
		}

	}

	/**
	 * Returns the Material name, converted to a friendly form
	 */
	public static String friendlyName(Material material)
	{
		return material.toString().toLowerCase().replace("_", " ");
	}


}
