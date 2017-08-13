package com.cuhka.hvo;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class HvOPlugin extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		System.out.println("Enabled HvO2");
		getServer().getPluginManager().registerEvents(this, this);
		Scoreboard board = getServer().getScoreboardManager().getMainScoreboard();
		Objective dieCount = board.registerNewObjective("dieCount", "deathCount");
		dieCount.setDisplaySlot(DisplaySlot.BELOW_NAME);
		dieCount.setDisplayName("Deaths");
		super.onEnable();
	}
	
	@EventHandler
	public void defeated(PlayerDeathEvent e) {
		Player victim = e.getEntity();	
		Player killer = victim.getKiller();
		e.setDeathMessage(String.format("%s has been slayed by %s", victim.getName(), killer.getName()));
	}
	

	
}
