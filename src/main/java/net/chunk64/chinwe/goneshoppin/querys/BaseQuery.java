package net.chunk64.chinwe.goneshoppin.querys;

import net.chunk64.chinwe.goneshoppin.logging.Action;
import org.bukkit.command.CommandSender;

public abstract class BaseQuery
{
	protected CommandSender sender;
	protected Action action;

	public BaseQuery(CommandSender sender, Action action)
	{
		this.sender = sender;
		this.action = action;
	}

	public CommandSender getSender()
	{
		return sender;
	}

	public Action getAction()
	{
		return action;
	}

	public abstract QueryResult execute();

	protected QueryResult error(String message)
	{
		QueryResult result = new QueryResult(message, sender, this);
		result.setError(true);
		return result;
	}
}
