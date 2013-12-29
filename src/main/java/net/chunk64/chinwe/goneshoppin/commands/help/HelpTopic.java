package net.chunk64.chinwe.goneshoppin.commands.help;


import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import java.util.HashMap;
import java.util.Map;

public class HelpTopic extends CommandTopic
{
	private Map<String, SubTopic> subTopics = new HashMap<String, SubTopic>();

	public HelpTopic(String superCommand, String title)
	{
		super(superCommand, title);
	}

	public HelpTopic(String supercommand, String title, Permission permission)
	{
		super(supercommand, title, permission);
	}

	public void addSubs(SubTopic... subTopics)
	{
		for (SubTopic subTopic : subTopics)
			this.subTopics.put(subTopic.getCommand(), subTopic);
	}

	public SubTopic getSubTopic(String subcommand)
	{
		return subTopics.get(subcommand);
	}


	@Override
	public void show(CommandSender sender)
	{
		HelpManager.message(sender, "&8/&7" + command + " &3<&bsubcommand&3> &7...");
		for (SubTopic subTopic : subTopics.values())
			if (HelpManager.hasPermission(subTopic, sender))
				subTopic.show(sender);

	}

	public void show(CommandSender sender, boolean headers)
	{
		if (headers)
			HelpManager.header(sender, description);

		show(sender);

		if (headers)
			HelpManager.header(sender, null);
	}


}

