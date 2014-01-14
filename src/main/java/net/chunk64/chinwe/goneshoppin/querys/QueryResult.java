package net.chunk64.chinwe.goneshoppin.querys;

import org.bukkit.command.CommandSender;

public class QueryResult
{
	private String message;
	private CommandSender sender;
	private BaseQuery query;
	private Object object;
	private boolean error = false;

	public QueryResult(String message, CommandSender sender, BaseQuery query)
	{
		this.sender = sender;
		this.query = query;
		setMessage(message);
	}

	public QueryResult(Exception e, CommandSender sender, BaseQuery query)
	{
		this.sender = sender;
		this.query = query;
		setMessage(e);
		error = true;
	}

	public String getMessage()
	{
		return message;
	}

	public CommandSender getSender()
	{
		return sender;
	}

	public BaseQuery getQuery()
	{
		return query;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public void setMessage(Exception e)
	{
		this.message = e.getMessage() == null ? e.toString() : e.getMessage();
	}

	public void setSender(CommandSender sender)
	{
		this.sender = sender;
	}

	public void setQuery(BaseQuery query)
	{
		this.query = query;
	}

	public Object getObject()
	{
		return object;
	}

	public void setObject(Object object)
	{
		this.object = object;
	}

	public void setError(boolean error)
	{
		this.error = error;
	}

	public boolean didError()
	{
		return error;
	}
}
