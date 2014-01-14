package net.chunk64.chinwe.goneshoppin.querys;

import net.chunk64.chinwe.goneshoppin.banking.Account;
import net.chunk64.chinwe.goneshoppin.commands.ShoppingCommand;
import net.chunk64.chinwe.goneshoppin.items.GSItem;
import net.chunk64.chinwe.goneshoppin.logging.Action;
import net.chunk64.chinwe.goneshoppin.logging.GSLogger;
import net.chunk64.chinwe.goneshoppin.logging.actions.TransactionAction;
import net.chunk64.chinwe.goneshoppin.util.ShoppingUtils;
import net.chunk64.chinwe.goneshoppin.util.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SellQuery extends BaseQuery
{
	private ItemStack itemStack;
	private Account sellerAccount;
	private Inventory sourceInventory;


	public SellQuery(CommandSender sender, Action action, ItemStack itemStack, Account sourceAccount, Inventory targetInventory)
	{
		super(sender, action);
		this.itemStack = itemStack;
		this.sellerAccount = sourceAccount;
		this.sourceInventory = targetInventory;
	}

	@Override
	public QueryResult execute()
	{
		if (!(sender instanceof Player))
			return error("Only players can do that!");

		Player player = (Player) sender;
		String name = ShoppingUtils.toString(itemStack, true);

		GSItem gsItem = new PriceQuery(sender, Action.SELL, itemStack).getPrice();
		if (gsItem == null)
			return error(name + " could not be priced!");

		// sell it
		Double sellPrice = gsItem.getRawPrice(false, itemStack.getAmount());
		if (sellPrice == null)
			return error("You cannot sell that item!");

		// check amount
		ShoppingCommand.checkMultiples(gsItem, itemStack, false);

		int finalSellPrice = sellPrice.intValue();

		// another validation
		ShoppingCommand.validatePrice(finalSellPrice, name);

		// damaged tool
		if (Utils.isDamagedTool(itemStack))
			return error("You must repair that " + name + " before selling it!");

		// check inventory has it
		int invAmount = ShoppingUtils.countInInventory(sourceInventory, itemStack);
		if (invAmount < itemStack.getAmount())
			return error("You only have " + invAmount + " in your inventory!");

		final int amount = itemStack.getAmount();

		// remove from inv
		ShoppingUtils.removeFromInventory(sourceInventory, itemStack, itemStack.getAmount());

		// pay
		if (sender == player)
		{
			if (ShoppingUtils.giveGold(player, finalSellPrice))
				Utils.message(sender, "&7Dropping excess money onto the floor!");
			else
				sellerAccount.deposit(finalSellPrice);
		}

		GSLogger.log(new TransactionAction(player.getName(), Action.SELL, itemStack, amount, finalSellPrice));

		String toWhom = sender == player ? "&f to &b" + player.getName() : "";
		return new QueryResult(String.format("You sold &6%d&fx %s%s &ffor &6%dGN&f!", amount, name, toWhom, finalSellPrice), sender, this);

	}
}
