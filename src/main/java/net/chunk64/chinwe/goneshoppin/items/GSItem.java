package net.chunk64.chinwe.goneshoppin.items;

import net.chunk64.chinwe.goneshoppin.GoneShoppin;
import net.chunk64.chinwe.goneshoppin.util.Utils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class GSItem
{
	private static FileConfiguration yml;
	private static File file;

	private static Set<GSItem> instances = new HashSet<GSItem>();

	private Alias alias;
	private Material material;
	private int damage;

	private double buyingPrice, sellingPrice;
	private int perBuy, perSell;
	private String note;


	public GSItem(Alias alias, Material material, int damage, double buyingPrice, double sellingPrice, Integer perBuy, Integer perSell, String note)
	{
		this.alias = alias;
		this.material = material;
		this.damage = material.getMaxDurability() > 0 ? 0 : damage;
		this.buyingPrice = buyingPrice;
		this.sellingPrice = sellingPrice;
		this.perBuy = perBuy;
		this.perSell = perSell;
		this.note = note;
		instances.add(this);
	}

	public static void loadFile(GoneShoppin plugin)
	{
		file = new File(plugin.getDataFolder(), "prices.yml");
		if (!file.exists())
		{
			plugin.saveResource("prices.yml", true);
			plugin.getLogger().warning("Could not find prices.yml, creating...");
		} else
			plugin.getLogger().info("prices.yml found, loading...");

		yml = YamlConfiguration.loadConfiguration(file);
	}

	/**
	 * Loads all prices for the given material
	 */
	public static void load(Material material, boolean check) throws NullPointerException
	{
		// check if already loaded
		if (check)
		{
			GSItem item = getItem(material, 0);
			if (item != null)
				return;
		}


		String prefix = "prices." + material.toString();

		if (!yml.contains(prefix))
			return;

		// loop through subs
		for (String damage : yml.getConfigurationSection(prefix + ".subs").getKeys(false))
		{
			int data = Utils.getInt(damage);
			String subPrefix = prefix + ".subs." + damage + ".";


			Double singleBuy = yml.getDouble(subPrefix + "buy.single");
			Double singleSell = yml.getDouble(subPrefix + "sell.single");
			Integer perBuy = yml.getInt(subPrefix + "buy.minimum");
			Integer perSell = yml.getInt(subPrefix + "sell.minimum");
			String note = yml.getString(subPrefix + ".note");


			// get aliases
			Alias alias = Alias.getAlias(material, data);

			// finally create gsitem
			new GSItem(alias, material, data, singleBuy, singleSell, perBuy, perSell, note);
		}
	}

	private static GSItem getItem(Material material, int damage)
	{
		// tool
		if (material.getMaxDurability() > 0)
			damage = 0;

		for (GSItem gsItem : instances)
			if (gsItem.material == material && gsItem.damage == damage)
				return gsItem;

		return null;
	}

	/**
	 * Retrieve a GSItem based on material and damage
	 */
	public static GSItem loadItem(Material material, int damage)
	{
		GSItem item = getItem(material, damage);
		if (item != null)
			return item;
		load(material, false);
		return getItem(material, damage);
	}

	public static GSItem loadItem(ItemStack itemStack)
	{
		return loadItem(itemStack.getType(), itemStack.getData().getData());
	}

	public static void unload()
	{
		for (GSItem item : instances)
			item.alias = null;
		instances = null;
		yml = null;

		Alias.unload();
	}

	public static Set<GSItem> getInstances()
	{
		return instances;
	}

	@Override
	public String toString()
	{
		return String.format("GSItem{material=%s, damage=%d, buyPrice=%f, sellPrice=%f, note=%s}", material.toString(), damage, buyingPrice, sellingPrice, note);
	}

	@Override
	public boolean equals(Object o)
	{
		return EqualsBuilder.reflectionEquals(this, o);
	}


	// getters


	public Alias getAlias()
	{
		return alias;
	}

	public Material getMaterial()
	{
		return material;
	}

	public int getDamage()
	{
		return damage;
	}

	//	/**
	//	 * Returns the price x amount given - will return null if price is 0
	//	 */
	//	public Integer getPrice(boolean buying, int amount)
	//	{
	//		Double price = (buying ? buyingPrice : sellingPrice) * amount;
	//		if (price == 0)
	//			return null;
	//		return price;
	//	}
	//
	//	/**
	//	 * Returns the price of 1 - will return null if price is 0
	//	 */
	//	public Integer getPrice(boolean buying)
	//	{
	//		return getPrice(buying, 1);
	//	}


	/**
	 * Gets the raw decimal price for the specified amount
	 */
	public Double getRawPrice(boolean buying, int amount)
	{
		double price = buying ? buyingPrice : sellingPrice;
		if (price == 0)
			return null;
		if (amount < 0)
			amount = 1;

		return price * amount;
	}

	public Double getRawPrice(boolean buying)
	{
		return getRawPrice(buying, 1);
	}

	public String getFormattedRawPrice(boolean buying, int amount)
	{
		Double price = getRawPrice(buying, amount);
		if (price == null)
			return "&cCannot be " + (buying ? "bought" : "sold") + ".";
		return "&6" + price + "GN";
	}

	public Integer getPerTransaction(boolean buying)
	{
		int amount = buying ? perBuy : perSell;
		if (amount < 1)
			return 1;
		return amount;
	}

	public String getNote()
	{
		if (note == null)
			return "";
		return note;
	}

	public static FileConfiguration getYml()
	{
		return yml;
	}

	public static void saveYml()
	{
		try
		{
			yml.save(file);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}


