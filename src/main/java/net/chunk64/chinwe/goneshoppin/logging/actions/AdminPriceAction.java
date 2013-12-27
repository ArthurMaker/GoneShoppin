package net.chunk64.chinwe.goneshoppin.logging.actions;

import net.chunk64.chinwe.goneshoppin.logging.Action;
import net.chunk64.chinwe.goneshoppin.logging.LoggerAction;
import org.bukkit.inventory.ItemStack;

public class AdminPriceAction extends LoggerAction
{

	private ItemStack itemStack;
	private String newValue;

	public AdminPriceAction(String playerName, Action action, ItemStack itemStack, String newValue)
	{
		super(playerName, action);
		this.itemStack = itemStack;
		this.newValue = newValue;
	}

	@Override
	public String getMessage()
	{
		return String.format("%s set the %s of %s to %s", playerName, action == Action.SET_NOTE ? "note" : "prices", toString(itemStack), newValue);
	}
}
