package net.chunk64.chinwe.goneshoppin.commands.help;


import net.chunk64.chinwe.goneshoppin.util.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public class GeneralTopic implements Topic
{

	//	private static final int LINE_LENGTH = 30;
	protected String description;
	protected Permission permission;

	public GeneralTopic(String description)
	{
		this.description = Utils.wrapString(description, '&', 60, false);
		HelpManager.getInstance().allTopics.add(this);
	}

	public GeneralTopic(String description, Permission permission)
	{
		this(description);
		this.permission = permission;
	}

	public void show(CommandSender sender)
	{
		for (String line : description.split("\\n"))
			HelpManager.message(sender, "&8: &r" + line);
	}


}
