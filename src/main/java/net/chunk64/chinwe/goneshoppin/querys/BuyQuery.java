package net.chunk64.chinwe.goneshoppin.querys;

import net.chunk64.chinwe.goneshoppin.banking.Account;
import net.chunk64.chinwe.goneshoppin.commands.ShoppingCommand;
import net.chunk64.chinwe.goneshoppin.items.GSItem;
import net.chunk64.chinwe.goneshoppin.logging.Action;
import net.chunk64.chinwe.goneshoppin.logging.GSLogger;
import net.chunk64.chinwe.goneshoppin.logging.actions.TransactionAction;
import net.chunk64.chinwe.goneshoppin.util.ShoppingUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BuyQuery extends BaseQuery
{
	private ItemStack itemStack;
	private Account targetAccount;

	/**
	 * @param targetAccount Leave null to only take money, without giving it to someone else
	 */
	public BuyQuery(CommandSender sender, Action action, ItemStack itemStack, Account targetAccount)
	{
		super(sender, action);
		this.itemStack = itemStack;
		this.targetAccount = targetAccount;
	}

	@Override
	public QueryResult execute()
	{
		if (!(sender instanceof Player))
			return error("Only players can do that!");

		Player player = (Player) sender;
		String name = ShoppingUtils.toString(itemStack, true);

		GSItem gsItem = new PriceQuery(sender, Action.BUY, itemStack).getPrice();
		if (gsItem == null)
			return error(name + " could not be priced!");

		Double buyPrice = gsItem.getRawPrice(true, 1); // updating price later
		if (buyPrice == null)
			return error("You cannot buy that item!");

		// smart amount
		if (itemStack.getAmount() == -1)
			itemStack.setAmount(ShoppingUtils.getMaxPurchase(player, gsItem).intValue());
		else if (itemStack.getAmount() == -2)
			itemStack.setAmount(gsItem.getPerTransaction(true));

		// check amount
		if (itemStack.getAmount() == 0)
			return error("You can't buy any of that!");

		ShoppingCommand.checkMultiples(gsItem, itemStack, true);

		// update price
		buyPrice *= itemStack.getAmount();

		int finalBuyPrice = buyPrice.intValue();


		// yet another validate
		ShoppingCommand.validatePrice(finalBuyPrice, name);

		// check money in inv
		int invValue = ShoppingUtils.valueInventory(player);
		if (invValue < buyPrice)
			return error("You need an extra " + (buyPrice - invValue) + "GN to buy that many!");

		final int finalAmount = itemStack.getAmount();

		// take money
		ShoppingUtils.takeGold(player, finalBuyPrice);

		// add money to optional bank
		if (targetAccount != null)
			targetAccount.deposit(finalBuyPrice);

		// give
		ShoppingUtils.giveItems(player, itemStack);

		// success!
		GSLogger.log(new TransactionAction(player.getName(), Action.BUY, itemStack, finalAmount, finalBuyPrice));

		String fromWhom = targetAccount != null ? "&f from &b" + targetAccount.getName() : "";
		return new QueryResult(String.format("You bought &6%d&fx %s%s &ffor &6%dGN&f!", finalAmount, name, fromWhom, finalBuyPrice), sender, this);

	}
}
