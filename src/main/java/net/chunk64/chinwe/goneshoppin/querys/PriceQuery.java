package net.chunk64.chinwe.goneshoppin.querys;

import net.chunk64.chinwe.goneshoppin.items.GSItem;
import net.chunk64.chinwe.goneshoppin.logging.Action;
import net.chunk64.chinwe.goneshoppin.util.ShoppingUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class PriceQuery extends BaseQuery
{

	private ItemStack itemStack;

	public PriceQuery(CommandSender sender, Action action, ItemStack itemStack)
	{
		super(sender, action);
		this.itemStack = itemStack;
	}

	@Override
	public QueryResult execute()
	{
		String commandName = action.toString().toLowerCase();

		// validate
		if (itemStack.getType() == Material.AIR)
			return error("You cannot " + commandName + " air!");
		if (ShoppingUtils.isGold(itemStack))
			return error("You cannot " + commandName + " gold, ye dongle!");

		// get price
		String name = ShoppingUtils.toString(itemStack, true);
		GSItem gsItem = GSItem.loadItem(itemStack.getType(), itemStack.getData().getData());

		if (gsItem == null)
			return error(name + " could not be priced!");

		QueryResult result = new QueryResult("NaN", sender, this);
		result.setObject(gsItem);
		return result;

	}

	/**
	 * Can return null if the price query fails
	 */
	public GSItem getPrice()
	{
		QueryResult result = execute();
		return result.didError() ? null : (GSItem) result.getObject();
	}
}
