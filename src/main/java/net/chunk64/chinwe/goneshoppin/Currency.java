package net.chunk64.chinwe.goneshoppin;

import org.bukkit.inventory.ItemStack;

public class Currency
{

	private ItemStack itemStack;
	private int value;
	private Gold gold;

	public Currency(ItemStack itemStack)
	{
		this.gold = Gold.parse(itemStack);
		if (gold == null)
			throw new IllegalArgumentException("Given ItemStack is not gold!");
		this.itemStack = itemStack;
		this.value = gold.getValue() * itemStack.getAmount();
	}

	public ItemStack getItemStack()
	{
		return itemStack;
	}

	public int getValue()
	{
		return value;
	}

	public Gold getGold()
	{
		return gold;
	}

	@Override
	public String toString()
	{
		return String.format("Currency{type=%s, value=%d)}", getGold(), getValue());
	}
}
