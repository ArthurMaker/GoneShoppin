package net.chunk64.chinwe.goneshoppin.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum Gold
{
	NUGGET(1), INGOT(9), BLOCK(81);

	private int value;

	Gold(int value)
	{
		this.value = value;
	}

	public int getValue()
	{
		return value;
	}

	public static Gold parse(Material material)
	{
		if (material == Material.GOLD_NUGGET)
			return NUGGET;
		if (material == Material.GOLD_INGOT)
			return INGOT;
		if (material == Material.GOLD_BLOCK)
			return BLOCK;
		return null;
	}


	public static Gold parse(ItemStack itemStack)
	{
		return parse(itemStack.getType());
	}

}
