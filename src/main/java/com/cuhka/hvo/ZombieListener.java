package com.cuhka.hvo;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ZombieListener implements Listener {
	@EventHandler
	public void onZombieDied(EntityDeathEvent event){
		if (event.getEntityType() == EntityType.ZOMBIE){
			List<ItemStack> drops = event.getDrops();
			drops.clear();
			ItemStack coin = new ItemStack(Material.GOLD_NUGGET);
			ItemMeta meta = coin.getItemMeta();
			meta.setDisplayName(ChatColor.YELLOW +"" + ChatColor.BOLD + "Coin");
			coin.setItemMeta(meta);
			drops.add(coin);
		}
	}
}
