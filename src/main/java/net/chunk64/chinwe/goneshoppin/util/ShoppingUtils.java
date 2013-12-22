package net.chunk64.chinwe.goneshoppin.util;

import net.chunk64.chinwe.goneshoppin.Gold;
import net.chunk64.chinwe.goneshoppin.MaterialAlias;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingUtils
{
	/**
	 * Returns true if they have the given amount of GN in their inventory
	 */
	public static boolean hasEnoughGN(Player player, int amount)
	{
		return valueInventory(player) >= amount;
	}

	/**
	 * Returns the value of GN in a player's inventory
	 */
	public static int valueInventory(Player player)
	{
		int count = 0;
		for (ItemStack itemStack : getGold(player).values())
			count += value(itemStack);
		return count;
	}

	/**
	 * Counts the amount of this material and data in the player's inventory
	 */
	public static int countInInventory(Player player, MaterialData data)
	{
		int count = 0;
		for (ItemStack itemStack : player.getInventory().getContents())
			if (itemStack != null && itemStack.getData().equals(data))
				count += itemStack.getAmount();

		return count;
	}

	/**
	 * Returns the currency value of a single itemstack
	 */
	public static int value(ItemStack itemStack)
	{
		Gold gold = Gold.parse(itemStack);
		if (gold == null)
			return 0;

		return gold.getValue() * itemStack.getAmount();
	}

	/**
	 * Returns true if the itemstack is currency
	 */
	public static boolean isGold(ItemStack itemStack)
	{
		return Gold.parse(itemStack) != null;
	}


	/**
	 * Removes the specified amount of gold from the player's inventory, assuming they have enough
	 */
	public static void takeGold(Player player, Integer amount)
	{
		Map<Integer, ItemStack> gold = getGold(player);
		for (Integer i : gold.keySet())
		{
			ItemStack itemStack = gold.get(i);
			int stackValue = value(itemStack);
			//			Currency currency = new Currency(itemStack);

			// take full stacks
			if (stackValue <= amount)
			{
				player.getInventory().setItem(i, null);
				amount -= stackValue;
			}
			// take part of a stack
			else
			{


				// take more than needed
				while (amount > 0)
				{
					// decrement
					itemStack.setAmount(itemStack.getAmount() - 1);
					final int tempStackValue = stackValue;

					stackValue = value(itemStack);
					amount -= (tempStackValue - stackValue);

					if (itemStack.getAmount() == 0)
					{
						player.getInventory().setItem(i, null);
						break;
					}
				}

				// give remainder
				if (amount < 0)
				{
					for (ItemStack remainder : simplify(Math.abs(amount)))
						player.getInventory().addItem(remainder);
					return;
				}

				// finished
				if (amount == 0)
					return;

			}
		}
	}

	/**
	 * Returns all gold from a player's inventory
	 *
	 * @return Keys = slot, values = itemstack
	 */
	public static Map<Integer, ItemStack> getGold(Player player)
	{
		// TODO add item in hand to front of list
		Map<Integer, ItemStack> map = new HashMap<Integer, ItemStack>();

		ItemStack[] contents = player.getInventory().getContents();
		for (int i = 0; i < contents.length; i++)
		{
			ItemStack itemStack = contents[i];
			if (itemStack != null && Gold.parse(itemStack) != null)
				map.put(i, itemStack);
		}

		return map;
	}

	/**
	 * Returns a List of gold that values to the given amount
	 */
	public static List<ItemStack> simplify(int amount)
	{
		List<ItemStack> list = new ArrayList<ItemStack>();

		int blocks = amount / 81;
		amount -= blocks * 81;

		int ingots = amount / 9;
		amount -= ingots * 9;

		if (blocks > 0)
			list.add(new ItemStack(Material.GOLD_BLOCK, blocks));
		if (ingots > 0)
			list.add(new ItemStack(Material.GOLD_INGOT, ingots));
		if (amount > 0)
			list.add(new ItemStack(Material.GOLD_NUGGET, amount));

		return list;
	}

	/**
	 * Returns a friendly coloured name for the itemstack, eg Wood:2 or Wood
	 */
	public static String toString(ItemStack itemStack, boolean name)
	{
		MaterialData data = itemStack.getData();
		StringBuilder sb = new StringBuilder("&b" + (name ? Utils.friendlyName(data.getItemType()) : data.getItemTypeId()));
		if (data.getData() != 0)
			sb.append("&f:&6" + data.getData());

		return sb.toString() + ChatColor.RESET;
	}

	/**
	 * Parses an input to an itemstack of amount 1, and returns null if invalid
	 * Example inputs:
	 * wood
	 * wood:2
	 * 95
	 * 95:3
	 */
	public static ItemStack parseInput(String input)
	{
		String[] split = input.split(":");

		// invalid arguments
		if (split.length > 2)
			return null;

		boolean gaveDamage = split.length == 2;
		Material material;
		Integer damage = 0;

		// id
		String stringId = gaveDamage ? split[0] : input;
		Integer numberId = Utils.getInt(stringId);

		MaterialAlias alias = MaterialAlias.getAlias(stringId);
		System.out.println("got alias");

		// material name
		material = numberId == null ? alias == null ? Material.getMaterial(stringId) : alias.getMaterial() : Material.getMaterial(numberId);
		System.out.println("material = " + material);

		// damage
		damage = alias == null ? 0 : alias.getDamage();
		System.out.println("aliasDamage = " + damage);

		if (gaveDamage)
		{
			Integer parsed = Utils.getInt(split[1]);
			if (parsed == null)
				return null;
			damage = parsed;
		}
		System.out.println("damage = " + damage);

		// invalid
		if (material == null)
			return null;

		return new ItemStack(material, 1, damage.shortValue());
	}

	/**
	 * Parses an input where a CommandSender enters it, and will notify the sender and return null if invalid.
	 */
	public static ItemStack parseInput(String input, CommandSender sender)
	{
		ItemStack itemStack = parseInput(input);
		if (itemStack == null)
			Utils.message(sender, "&c'" + input + "' is not a valid item!");
		return itemStack;
	}


}