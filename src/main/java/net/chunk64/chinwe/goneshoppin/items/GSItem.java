package net.chunk64.chinwe.goneshoppin.items;

import net.chunk64.chinwe.goneshoppin.GoneShoppin;
import net.chunk64.chinwe.goneshoppin.util.Utils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class GSItem
{
	private static FileConfiguration yml;

	private static Set<GSItem> instances = new HashSet<GSItem>();

	private Alias alias;
	private Material material;
	private int damage;

	private Integer buyingPrice, sellingPrice;
	private int perBuy, perSell;
	private String buyNote, sellNote;


	public GSItem(Alias alias, Material material, int damage, Integer buyingPrice, Integer sellingPrice, Integer perBuy, Integer perSell, String buyNote, String sellNote)
	{
		this.alias = alias;
		this.material = material;
		this.damage = material.getMaxDurability() > 0 ? 0 : damage;
		this.buyingPrice = buyingPrice;
		this.sellingPrice = sellingPrice;
		this.perBuy = perBuy;
		this.perSell = perSell;
		this.buyNote = buyNote;
		this.sellNote = sellNote;
		instances.add(this);
	}

	public static void loadFile(GoneShoppin plugin)
	{
		File pFile = new File(plugin.getDataFolder(), "prices.yml");
		if (!pFile.exists())
		{
			plugin.saveResource("prices.yml", true);
			plugin.getLogger().warning("Could not find prices.yml, creating...");
		} else
			plugin.getLogger().info("prices.yml found, loading...");

		yml = YamlConfiguration.loadConfiguration(pFile);
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


		String prefix = "blocks." + material.toString().replace(" ", "_").toUpperCase();

		// loop through subs
		for (String damage : yml.getConfigurationSection(prefix + ".subs").getKeys(false))
		{
			int data = Utils.getInt(damage);
			String subPrefix = prefix + ".subs." + damage;

//			String name = yml.getString(subPrefix + ".name");
//			String altName = yml.getString(subPrefix + ".alt-name");

			Integer buy = yml.getInt(subPrefix + ".buy.price"), sell = yml.getInt(subPrefix + ".sell.price");
			String buyNote = yml.getString(subPrefix + ".buy.note"), sellNote = yml.getString(subPrefix + ".sell.note");

			// get aliases
			Alias alias = Alias.getAlias(material, data);

			// finally create gsitem
			// TODO load perBuy/perSell
			new GSItem(alias, material, data, buy, sell, 1, 1, buyNote, sellNote);
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
		return String.format("GSItem{material=%s, damage=%d, buyPrice=%d, sellPrice=%d, buyNote=%s, sellNote=%s}", material.toString(), damage, buyingPrice, sellingPrice, buyNote, sellNote);
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

	/**
	 * Returns the price x amount given - will return null if price is 0
	 */
	public Integer getPrice(boolean buying, int amount)
	{
		Integer price = (buying ? buyingPrice : sellingPrice) * amount;
		if (price == 0)
			return null;
		return price;
	}
	/**
	 * Returns the price of 1 - will return null if price is 0
	 */
	public Integer getPrice(boolean buying)
	{
		return getPrice(buying, 1);
	}

	public String getFormattedPrice(boolean buying, int amount)
	{
		Integer price = getPrice(buying, amount);
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

	public String getNote(boolean buying)
	{
		String note = buying ? buyNote : sellNote;
		if (note == null)
			return "";
		return note;
	}
}


