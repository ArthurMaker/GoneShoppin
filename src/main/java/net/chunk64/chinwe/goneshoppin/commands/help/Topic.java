package net.chunk64.chinwe.goneshoppin.commands.help;

import org.bukkit.command.CommandSender;

public interface Topic
{

	/**
	 * Sends the topic contents to the provided CommandSender
	 *
	 * @param sender Who to show to
	 */
	public void show(CommandSender sender);


}
