package net.chunk64.chinwe.goneshoppin.util;

import net.chunk64.chinwe.goneshoppin.banking.BankLimit;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Utils
{
	public static final String PREFIX = "§8[§d|§8] §r";

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
	public static String formatList(List<BankLimit> list, boolean and)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++)
		{
			String st = list.get(i).toString().toLowerCase();
			if (i == list.size() - 1)
				sb.append(st);
			else if (i == list.size() - 2)
				sb.append(st).append(and ? " and " : " or ");
			else
				sb.append(st).append(", ");

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
	 * Attempts to convert a string to an double -> returns null otherwise
	 */
	public static Double getDouble(String s)
	{
		try
		{
			return Double.parseDouble(s);
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

	/**
	 * Strips a string of all & colour codes
	 */
	public static String stripColour(String s)
	{
		return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', s));
	}

	/**
	 * Returns true if the material is a damaged tool
	 */
	public static boolean isDamagedTool(ItemStack itemStack)
	{
		return isTool(itemStack) && itemStack.getDurability() > 0;
	}

	/**
	 * Returns true if the material is a tool
	 */
	public static boolean isTool(ItemStack itemStack)
	{
		return itemStack.getType().getMaxDurability() > 0;
	}

	/**
	 * Converts a List of ItemStacks to an array
	 */
	public static ItemStack[] listToArray(List<ItemStack> list)
	{
		return list.toArray(new ItemStack[list.size()]);
	}

	// Required CraftBukkit
	//	public static String getName(ItemStack itemStack)
	//	{
	//		return CraftItemStack.asNMSCopy(itemStack).getName();
	//	}

	/**
	 * @param str           The string to wrap
	 * @param colourCode    The colour code character to use, such as '&' or '§'
	 * @param lineLength    The line length to base around - not all lines will be this length exactly
	 * @param wrapLongWords Whether long words should be cut up to fit the lines
	 * @return The wrapped string, divided by new line characters
	 */
	public static String wrapString(String str, char colourCode, int lineLength, boolean wrapLongWords)
	{
		// split up into words
		String[] split = WordUtils.wrap(str, lineLength, null, wrapLongWords).split("\\r\\n");
		String[] fixed = new String[split.length];

		// set first element
		fixed[0] = split[0];

		for (int i = 1; i < split.length; i++)
		{
			String line = split[i];
			String previous = split[i - 1];

			// get last colour from last
			int code = previous.lastIndexOf(colourCode);

			// valid colour
			if (code != -1)
			{
				char cCode = previous.charAt(code == previous.length() - 1 ? code : code + 1);

				// colour has been split
				if (code == previous.length() - 1)
				{
					// validate code
					if (ChatColor.getByChar(line.charAt(0)) != null)
					{
						// remove off end of previous
						fixed[i - 1] = previous.substring(0, previous.length() - 1);

						// add & to start of line
						line = String.valueOf(colourCode) + line;
						split[i] = line; // update for next iteration
					}

				} else
				{
					// check next line doesn't already have a colour
					if (line.length() < 2 || line.charAt(0) != colourCode || ChatColor.getByChar(line.charAt(1)) == null)
						// prepend line with colour
						if (ChatColor.getByChar(cCode) != null)
							line = String.valueOf(colourCode) + cCode + line;
				}
			}

			// update the arrays
			fixed[i] = line;
			split[i] = line;

		}

		// join it all up to return a String
		return ChatColor.translateAlternateColorCodes(colourCode, StringUtils.join(fixed, '\n'));
	}

}
