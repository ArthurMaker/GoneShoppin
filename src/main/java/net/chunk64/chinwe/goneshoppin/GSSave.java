package net.chunk64.chinwe.goneshoppin;

import net.chunk64.chinwe.goneshoppin.banking.Bank;
import net.chunk64.chinwe.goneshoppin.logging.GSLogger;
import net.chunk64.chinwe.goneshoppin.util.Config;
import org.apache.commons.lang.time.DateUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GSSave extends BukkitRunnable
{
	private static GSSave instance;
	private static FileConfiguration data;

	private GoneShoppin plugin;
	private long nextSaveTime;

	public GSSave(GoneShoppin plugin)
	{
		instance = this;
		data = plugin.getData().getYml();
		this.plugin = plugin;

		// load time
		this.nextSaveTime = data.getLong("next-save-time");
		if (this.nextSaveTime == 0)
			updateSaveTime();

		// run
		runTaskTimer(plugin, 100L, 600); // every 30 seconds
	}


	@Override
	public void run()
	{
		// check time
		if (System.currentTimeMillis() < nextSaveTime)
			return;

		plugin.getLogger().info("Attempting to save...");
		save();
		plugin.getLogger().info("Save complete!");

		// noone is going to give a shit when it saves :<
		// GSLogger.getInstance().alert("All data saved!", false);

		updateSaveTime();
	}

	public void updateSaveTime()
	{
		// load from file
		this.nextSaveTime = System.currentTimeMillis() + (DateUtils.MILLIS_PER_MINUTE * Config.SaveMinutes);
		data.set("next-save-time", nextSaveTime);
		GoneShoppin.getInstance().getData().save();

	}

	public static void save()
	{
		// bank
		Bank.saveAllAccounts();

		// log
		try
		{
			// empty queue
			if (GSLogger.getInstance().getQueue().isEmpty())
				return;

			BufferedWriter writer = new BufferedWriter(new FileWriter(GSLogger.getInstance().getFile(), true));
			for (String log : GSLogger.getInstance().getQueue())
			{
				writer.write(log);
				writer.newLine();
			}
			writer.close();

			// clear queue
			GSLogger.getInstance().getQueue().clear();
		} catch (IOException e)
		{
			instance.plugin.getLogger().severe("Error whilst saving");
			e.printStackTrace();
		}
	}

	public static GSSave getInstance()
	{
		return instance;
	}
}
