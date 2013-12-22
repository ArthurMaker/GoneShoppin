package net.chunk64.chinwe.goneshoppin.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileManager
{
	private static List<FileManager> instances = new ArrayList<FileManager>();
	private Plugin plugin;
	private File file;
	private FileConfiguration yml;

	public FileManager(String fileName, String fileHeader, Plugin plugin)
	{
		this.plugin = plugin;
		this.file = new File(plugin.getDataFolder(), fileName + ".yml");
		if (file == null)
			throw new IllegalArgumentException("File cannot be null");

		if (!file.exists())
		{
			try
			{
				file.getParentFile().mkdirs();
				file.createNewFile();
			} catch (IOException e)
			{
				plugin.getLogger().severe("Error whilst creating '" + file.getName() + "'");
			}
		}

		this.yml = YamlConfiguration.loadConfiguration(file);

		if (!fileHeader.isEmpty())
		{
			yml.options().header(fileHeader);
			save();
		}

		instances.add(this);
	}

	public FileManager(String fileName, Plugin plugin)
	{
		this(fileName, "", plugin);
	}

	public File getFile()
	{
		return file;
	}

	public FileConfiguration getYml()
	{
		return yml;
	}

	/**
	 * Raw save, using object.save() instead
	 */
	public void save()
	{
		try
		{
			yml.save(file);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void destroy(boolean removeFromList)
	{
		file = null;
		yml = null;
		if (removeFromList)
		{
			instances.remove(this);
			if (instances.size() == 0)
				instances = null;
		}
	}

	public static void saveAll()
	{
		for (FileManager f : instances)
			f.save();
		//		for (FileManager f : instances)
		//			f.destroy(false);
	}

}
