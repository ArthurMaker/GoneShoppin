package net.chunk64.chinwe.goneshoppin.util;

import net.chunk64.chinwe.goneshoppin.Gold;
import net.chunk64.chinwe.goneshoppin.GoneShoppin;
import net.chunk64.chinwe.goneshoppin.items.Alias;
import net.chunk64.chinwe.goneshoppin.items.GSItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;

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
	 * Gets the selling price of an entire inventory
	 */
	public static int priceInventory(Inventory inventory)
	{
		int value = 0;
		for (ItemStack itemStack : inventory.getContents())
			value += priceItemstack(itemStack);
		return value;
	}

	/**
	 * Gets the selling price of an itemstack
	 */
	public static double priceItemstack(ItemStack itemStack)
	{
		if (itemStack == null)
			return 0;
		if (isGold(itemStack))
			return 0;
		GSItem gsItem = GSItem.loadItem(itemStack);
		return gsItem.getRawPrice(false, itemStack.getAmount());
	}

	/**
	 * Counts the amount of this material and data in the player's inventory
	 */
	public static int countInInventory(Player player, ItemStack itemStack)
	{
		int count = 0;
		for (ItemStack is : player.getInventory().getContents())
			if (is != null && is.getType() == itemStack.getType())
				if (Utils.isTool(itemStack) || is.getData().getData() == itemStack.getData().getData())
					count += is.getAmount();
		return count;
	}


	/**
	 * Removes the given amount of the material and data from the player's inventory, ASSUMING THEY HAVE ENOUGH
	 */

	public static void removeFromInventory(Player player, ItemStack itemStack, int amount)
	{
		ItemStack[] contents = player.getInventory().getContents();
		for (int i = 0; i < contents.length; i++)
		{
			ItemStack is = contents[i];
			if (is == null)
				continue;

			if (is.getType() == itemStack.getType() && (Utils.isTool(itemStack) || is.getData().getData() == itemStack.getData().getData()))
			{
				final int stackCount = is.getAmount();

				// remove whole stack
				if (stackCount <= amount)
				{
					player.getInventory().setItem(i, null);
					amount -= stackCount;
				}

				// take part
				else
				{
					is.setAmount(stackCount - amount);
					amount = 0;
				}


			}

			// done
			if (amount == 0)
				return;
		}
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
	 * Gives the specified amount of gold to the player. Any overflow will be dropped on the floor, where only they can pick it up
	 *
	 * @return True if overflow was dropped, otherwise false
	 */
	public static boolean giveGold(Player player, Integer amount)
	{
		return giveItems(player, Utils.listToArray(simplify(amount)));
	}

	/**
	 * Gives the specified items to the player. Any overflow will be dropped on the floor, where only they can pick it up
	 *
	 * @return True if overflow was dropped, otherwise false
	 */
	public static boolean giveItems(Player player, ItemStack... itemStacks)
	{
		// give as much as possible
		HashMap<Integer, ItemStack> overflow = player.getInventory().addItem(itemStacks);

		if (overflow.isEmpty())
			return false;

		// drop the rest
		for (ItemStack itemStack : overflow.values())
		{
			Item drop = player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
			drop.setMetadata("GoneShoppin:DropProtection", new FixedMetadataValue(GoneShoppin.getInstance(), player.getName()));
		}
		return true;
	}


	/**
	 * Shortcut for inventory.add(simplify(goldAmount)), returns inventory.add() HashMap
	 */
	public static HashMap<Integer, ItemStack> addGoldToInventory(Player player, Integer amount)
	{
		List<ItemStack> gold = ShoppingUtils.simplify(amount);
		//		return player.getInventory().addItem(gold.toArray(new ItemStack[gold.size()]));
		return player.getInventory().addItem(Utils.listToArray(gold));
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
		if (data.getData() != 0 && !Utils.isDamagedTool(itemStack))
			sb.append("&f:&6").append(data.getData());

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
	public static ItemStack parseInput(Player player, String input)
	{
		String[] split = input.split(":");

		// invalid arguments
		if (split.length > 2)
			return null;

		boolean gaveDamage = split.length == 2;
		Material material;
		Integer damage;

		// id
		String stringId = gaveDamage ? split[0] : input;
		Integer numberId = Utils.getInt(stringId);

		Alias alias = Alias.getAlias(stringId);

		// material name
		if (numberId == null)
			material = alias == null ? stringId.equalsIgnoreCase("hand") ? player.getItemInHand().getType() : Material.getMaterial(stringId) : alias.getMaterial();
		else
			material = Material.getMaterial(numberId);

		// damage
		damage = alias == null ? 0 : alias.getDamage();

		if (gaveDamage)
		{
			Integer parsed = Utils.getInt(split[1]);
			if (parsed == null)
				return null;
			damage = parsed;
		}

		// invalid
		if (material == null)
			return null;

		return new ItemStack(material, 1, damage.shortValue());

	}

	/**
	 * Parses an input where a CommandSender enters it, and will notify the sender and return null if invalid.
	 */
	public static ItemStack parseInputAndMessage(Player player, String input)
	{
		ItemStack itemStack = parseInput(player, input);
		if (itemStack == null)
			Utils.message(player, "&c'" + input + "' is not a valid item!");
		return itemStack;
	}


	/**
	 * Gets the maximum amount of the item in hand that the player can buy.
	 */
	public static Double getMaxPurchase(Player player, GSItem gsItem)
	{
		int value = valueInventory(player);
		Double price = gsItem.getRawPrice(true);
		return Math.floor((double) value / price);
	}
}
