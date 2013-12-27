package net.chunk64.chinwe.goneshoppin.logging.actions;

import net.chunk64.chinwe.goneshoppin.logging.Action;
import net.chunk64.chinwe.goneshoppin.logging.LoggerAction;
import org.bukkit.inventory.ItemStack;

public class TransactionAction extends LoggerAction
{

	private ItemStack itemStack;
	private int amount;
	private int cost;

	public TransactionAction(String playerName, Action action, ItemStack itemStack, int amount, int cost)
	{
		super(playerName, action);
		this.itemStack = itemStack;
		this.amount = amount;
		this.cost = cost;
	}


	@Override
	public String getMessage()
	{
		return String.format("%s %s %dx %s for %dGN", playerName, action == Action.BUY ? "bought" : "sold", amount, toString(itemStack), cost);
	}
}
