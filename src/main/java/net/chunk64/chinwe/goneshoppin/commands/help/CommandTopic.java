package net.chunk64.chinwe.goneshoppin.commands.help;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.permissions.Permission;

public class CommandTopic implements Topic
{

	protected String command;
	protected String description;
	protected Permission permission;

	public CommandTopic(String command, String description)
	{
		this.command = command;
		this.description = description;
		HelpManager.getInstance().allTopics.add(this);
	}
	public CommandTopic(String pluginCommand)
	{
		PluginCommand cmd = HelpManager.instance.plugin.getCommand(pluginCommand);
		if (cmd == null)
			throw new IllegalArgumentException("Command '" + pluginCommand + "' was not found! Is it registered in your plugin.yml?");
		this.command = cmd.getUsage();
		this.description = cmd.getDescription();
		HelpManager.getInstance().allTopics.add(this);
	}

	public CommandTopic(String command, String description, Permission permission)
	{
		this(command, description);
		this.permission = permission;
	}

	public String getCommand()
	{
		return command;
	}

	public String getDescription()
	{
		return description;
	}

	public Permission getPermission()
	{
		return permission;
	}


	public void show(CommandSender sender)
	{
		String[] splitCommand = command.split(" ");
		HelpManager.message(sender, "&b" + splitCommand[0] + "&3&o " + StringUtils.join(splitCommand, ' ', 1, splitCommand.length) + " - &7" + description);
	}


}
