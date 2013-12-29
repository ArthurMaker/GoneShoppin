package net.chunk64.chinwe.goneshoppin.commands.help;


import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.List;

public class SubTopic extends CommandTopic
{
	protected List<Topic> topics = new ArrayList<Topic>();
	protected String title;

	public SubTopic(String command, String description, String title, Permission permission)
	{
		super(command, description, permission);
		this.title = title;
	}

	public SubTopic(String command, String description, String title)
	{
		super(command, description);
		this.title = title;
	}

	public void addTopics(Topic... topics)
	{
		for (Topic topic : topics)
			this.topics.add(topic);
	}

	public void show(CommandSender sender)
	{
		HelpManager.message(sender, "&7.. &b" + command + "&3 - &7" + description);
	}

	public static void addTopicsToAll(Topic... topics)
	{
		for (Topic subTopic : HelpManager.getInstance().allTopics)
			if (subTopic instanceof SubTopic)
				((SubTopic) subTopic).addTopics(topics);
	}


}
