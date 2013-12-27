package net.chunk64.chinwe.goneshoppin.logging;

import net.chunk64.chinwe.goneshoppin.util.ShoppingUtils;
import net.chunk64.chinwe.goneshoppin.util.Utils;
import org.bukkit.inventory.ItemStack;

public abstract class LoggerAction
{

	// player is given
	//	BUY, amountx item; money
	//	SELL, amountx item; money

	//	SET_PRICE, item; new prices
	//	SET_NOTE, item; new note

	//	SET_LIMIT, account; new limit
	//	SET_BALANCE, account; new balance

	//	STEAL, account; amount stolen; new balance
	//	WITHDRAW, amount; new balance
	//	DEPOSIT; amount; new balance


	protected String playerName;
	protected Action action;

	public LoggerAction(String playerName, Action action)
	{
		this.playerName = playerName;
		this.action = action;
	}

	public abstract String getMessage();

	protected String toString(ItemStack itemStack)
	{
		return Utils.stripColour(ShoppingUtils.toString(itemStack, true));
	}







}
