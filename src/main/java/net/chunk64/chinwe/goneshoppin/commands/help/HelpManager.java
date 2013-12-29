package net.chunk64.chinwe.goneshoppin.commands.help;


import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class HelpManager implements CommandExecutor
{
	protected static HelpManager instance;
	private static final String SPACE = "\n";

	protected JavaPlugin plugin;
	protected Set<Topic> allTopics;
	private CommandMap commandMap;
	private Map<String, HelpTopic> helpTopics;
	private boolean spaceOut;
	private String header;

	public HelpManager(JavaPlugin plugin, boolean spaceOut, int headerLength)
	{
		instance = this;
		this.plugin = plugin;
		this.spaceOut = spaceOut;
		this.helpTopics = new HashMap<String, HelpTopic>();
		this.allTopics = new HashSet<Topic>();

		// get command map
		if (plugin.getServer().getPluginManager() instanceof SimplePluginManager)
		{
			SimplePluginManager pluginManager = (SimplePluginManager) plugin.getServer().getPluginManager();
			try
			{
				Field map = pluginManager.getClass().getDeclaredField("commandMap");
				map.setAccessible(true);
				commandMap = (CommandMap) map.get(pluginManager);
			} catch (IllegalAccessException e)
			{
				e.printStackTrace();
			} catch (NoSuchFieldException e)
			{
				e.printStackTrace();
			}
		}

		// header
		if (headerLength < 1)
			headerLength = 1;

		StringBuilder header = new StringBuilder("&2");
		for (int i = 0; i < headerLength; i++)
			header.append("-");
		this.header = header.toString();
	}

	public HelpTopic addHelpTopic(HelpTopic helpTopic)
	{
		helpTopics.put(helpTopic.getCommand(), helpTopic);

		// register command
		PluginCommand cmd = getCommand(helpTopic.getCommand(), plugin);
		cmd.setDescription(helpTopic.getDescription());
		cmd.setUsage("/" + cmd.getName());
		cmd.setExecutor(this);
		commandMap.register(plugin.getName(), cmd);
		return helpTopic;
	}

	public HelpTopic getHelpTopic(String command)
	{
		return helpTopics.get(command);
	}


	// borrowed from https://forums.bukkit.org/threads/tutorial-registering-commands-at-runtime.158461/ :D
	private PluginCommand getCommand(String name, Plugin plugin)
	{
		PluginCommand command = null;
		try
		{
			Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
			c.setAccessible(true);
			command = c.newInstance(name, plugin);

		} catch (SecurityException e)
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		} catch (InstantiationException e)
		{
			e.printStackTrace();
		} catch (InvocationTargetException e)
		{
			e.printStackTrace();
		} catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		}

		return command;
	}

	/*
		helptopic
			holds subtopics -> links to other help topics

		helptopic goneshoppin help
			subtopic super bank - view bank help
			subtopic super shop - view shop help

		helptopic bank help
			buy - buy shit, use material and shit



		helptopic
			a header for several subtopics
		subtopic
			a topic that links to another header, is a subcommand of helptopic
			holds another helptopic that the command links to
		topic
			a simple command


	/HEADERCOMMAND:

		--- HEADER TITLE ---
		/HEADERCOMMAND <subcommand>
		.. subtopic - subtopic description

	/HEADERCOMMAND SUBTOPIC:

		--- SUBTOPIC TITLE ---
		/commandtopic - does shit
		normal topic, this will
		be a string that will
		wrap over lines



	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		HelpTopic topic = getHelpTopic(cmd.getName());

		// can't be too careful!
		if (topic == null)
			return true;

		// permission
		if (!hasPermission(topic, sender))
		{
			message(sender, "&cYou don't have permission to view this help topic!");
			return true;
		}

		// help topic header
		if (args.length == 0)
			topic.show(sender, spaceOut);

			// sub topic
		else if (args.length == 1)
		{
			SubTopic subTopic = topic.getSubTopic(args[0]);

			// check if subtopic exists, and permission
			if (subTopic == null || !hasPermission(subTopic, sender))
			{
				sender.sendMessage(ChatColor.RED + "Subtopic '" + args[0] + "' not found! Use /" + topic.getCommand() + " for help.");
				return true;
			}

			// subtopic header
			header(sender, subTopic.title);

			// show topics
			List<Topic> topics = subTopic.topics;
			for (int i = 0, topicsSize = topics.size(); i < topicsSize; i++)
			{
				Topic extra = topics.get(i);
				if (hasPermission(extra, sender))
					extra.show(sender);
				if (i != topics.size() - 1 && spaceOut)
					sender.sendMessage(SPACE);
			}

			header(sender, null);
		} else
			sender.sendMessage(ChatColor.RED + "Use /" + topic.getCommand() + " to view subtopics.");


		return true;
	}

	// utils

	protected static void message(CommandSender sender, String msg)
	{
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}

	protected static void header(CommandSender sender, String title)
	{
		if (instance.spaceOut && title != null)
			sender.sendMessage(SPACE);

		message(sender, instance.getHeader(title));

		//		if (title == null)
		//			sender.sendMessage(SPACE);
	}

	protected String getHeader(String title)
	{
		return instance.header + "&a " + (title == null ? "End of help" : title) + "&r " + instance.header;
	}

	public static boolean hasPermission(Topic topic, CommandSender sender)
	{
		Permission permission = topic instanceof CommandTopic ? ((CommandTopic) topic).permission : topic instanceof GeneralTopic ? ((GeneralTopic) topic).permission : null;
		return permission == null || sender.hasPermission(permission);
	}


	// getters/setters

	public static HelpManager getInstance()
	{
		return instance;
	}

	public Map<String, HelpTopic> getHelpTopics()
	{
		return helpTopics;
	}

}

