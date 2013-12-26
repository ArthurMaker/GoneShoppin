package net.chunk64.chinwe.goneshoppin.items;

import net.chunk64.chinwe.goneshoppin.GoneShoppin;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Alias
{
	private static List<Alias> instances = new ArrayList<Alias>();
	private Material material;
	private int id;
	private int damage;
	private Set<String> aliases;


	private Alias(int id, int damage)
	{
		this.id = id;
		this.damage = damage;
		this.material = Material.getMaterial(id);
		this.aliases = new HashSet<String>();
		instances.add(this);
	}

	public Material getMaterial()
	{
		return material;
	}

	public int getId()
	{
		return id;
	}

	public int getDamage()
	{
		return damage;
	}

	public Set<String> getAliases()
	{
		return aliases;
	}

	public void add(String alias)
	{
		if (!aliases.contains(alias))
			aliases.add(alias);
	}

	/**
	 * Loads all aliases from items.csv
	 */
	public static void loadFromFile()
	{
		try
		{
			InputStream input = GoneShoppin.getInstance().getResource("items.csv");

			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String line;

			while ((line = reader.readLine()) != null)
			{
				// comment
				if (line.charAt(0) == '#')
					continue;

				String[] split = line.split(",");
				String alias = split[0];
				int id = Integer.parseInt(split[1]);
				int damage = Integer.parseInt(split[2]);

				Alias a = getAlias(id, damage);
				a.add(alias);
			}

			reader.close();


		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	public static Alias getAlias(int id, int damage)
	{
		for (Alias alias : instances)
			if (id == alias.getId() && damage == alias.getDamage())
				return alias;
		return new Alias(id, damage);
	}

	public static Alias getAlias(Material material, int damage)
	{
		return getAlias(material.getId(), damage);
	}

	public static Alias getAlias(Material material)
	{
		return getAlias(material, 0);
	}

	public static Alias getAlias(int id)
	{
		Material m = Material.getMaterial(id);
		if (m == null)
			return null;
		return getAlias(m, 0);
	}

	public static Alias getAlias(String material)
	{
		for (Alias alias : instances)
			if (alias.aliases.contains(material))
				return alias;

		return null;

	}

	/**
	 * Is invoked implicitly
	 */
	protected static void unload()
	{
		for (Alias alias : instances)
			alias.aliases = null;
		instances = null;
	}

	public static Alias getAlias(ItemStack itemStack)
	{
		return getAlias(itemStack.getType(), itemStack.getData().getData());
	}
}
