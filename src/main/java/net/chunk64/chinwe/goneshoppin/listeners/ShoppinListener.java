package net.chunk64.chinwe.goneshoppin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class ShoppinListener implements Listener
{

	@EventHandler
	public void onPickup(PlayerPickupItemEvent event)
	{
		if (event.getItem().hasMetadata("GoneShoppin:DropProtection"))
		{
			String owner = event.getItem().getMetadata("GoneShoppin:DropProtection").get(0).asString();
			if (!event.getPlayer().getName().equals(owner))
				event.setCancelled(true);
		}
	}

}
