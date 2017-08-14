package com.cuhka.hvo;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class HvOPlugin extends JavaPlugin implements Listener {
	private static final String AQUA = "\u00a74";
	private static final String DARK_RED = "\u00a7b";

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		Scoreboard board = getServer().getScoreboardManager().getMainScoreboard();
		createTeam(board, "Orcs", ChatColor.DARK_RED, DARK_RED);
		createTeam(board, "Humans", ChatColor.AQUA, AQUA);
	
		Objective killCount = board.registerNewObjective("killCount", "playerKillCount");
		killCount.setDisplaySlot(DisplaySlot.BELOW_NAME);
		killCount.setDisplayName("Kills");
		super.onEnable();
	}

	private Team createTeam(Scoreboard board, String name, ChatColor color, String prefix) {
		Team team = board.getTeam(name);
		if (team == null){
			team = board.registerNewTeam(name);
		}
		team.setAllowFriendlyFire(false);
		team.setColor(color);
		team.setPrefix(prefix);
		team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
		team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OWN_TEAM);
		return team;
	}
	
	@EventHandler
	public void defeated(PlayerDeathEvent e) {
		Player victim = e.getEntity();
		Player killer = victim.getKiller();		
	}
	

	
}
