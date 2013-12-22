package net.chunk64.chinwe.goneshoppin;

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

public class MaterialAlias
{
	private static List<MaterialAlias> instances = new ArrayList<MaterialAlias>();
	private Material material;
	private int id;
	private int damage;
	private Set<String> aliases;

	public static MaterialAlias getMaterialAlias(int id, int damage)
	{
		for (MaterialAlias alias : instances)
			if (id == alias.getId() && damage == alias.getDamage())
				return alias;
		return new MaterialAlias(id, damage);
	}

	private MaterialAlias(int id, int damage)
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

				MaterialAlias a = getMaterialAlias(id, damage);
				a.add(alias);
			}

			reader.close();


		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	public static MaterialAlias getAlias(Material material, int damage)
	{
		for (MaterialAlias alias : instances)
			if (alias.getMaterial() == material && alias.getDamage() == damage)
				return alias;
		return null;
	}

	public static MaterialAlias getAlias(Material material)
	{
		return getAlias(material, 0);
	}

	public static MaterialAlias getAlias(int id)
	{
		Material m = Material.getMaterial(id);
		if (m == null)
			return null;
		return getAlias(m, 0);
	}

	public static MaterialAlias getAlias(String material)
	{
		for (MaterialAlias alias : instances)
			if (alias.aliases.contains(material))
				return alias;

		return null;

	}

	public static void unload()
	{
		for (MaterialAlias materialAlias : instances)
			materialAlias.aliases = null;
		instances = null;
	}

	public static MaterialAlias getAlias(ItemStack itemStack)
	{
		return getAlias(itemStack.getType(), itemStack.getData().getData());
	}
}
