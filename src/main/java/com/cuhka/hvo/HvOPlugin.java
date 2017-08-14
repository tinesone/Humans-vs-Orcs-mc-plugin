package com.cuhka.hvo;

import org.bukkit.ChatColor;
import org.bukkit.Server;
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
	static final String TEAM_HUMANS = "Humans";
	static final String TEAM_ORCS = "Orcs";
	static final String KILLCOUNT = "killCount";
	private static final String AQUA = "\u00a7b";
	private static final String DARK_RED = "\u00a74";

	@Override
	public void onEnable() {
		Server server = getServer();
		server.getPluginManager().registerEvents(this, this);
		Scoreboard board = getServer().getScoreboardManager().getMainScoreboard();
		
		createTeam(board, TEAM_ORCS, ChatColor.DARK_RED, DARK_RED);
		createTeam(board, TEAM_HUMANS, ChatColor.AQUA, AQUA);
		createKillCountObjective(board);

		this.getCommand("start").setExecutor(new StartGame(server, board));
		super.onEnable();
	}

	private void createKillCountObjective(Scoreboard board) {
		Objective killCount = board.getObjective(KILLCOUNT);
		
		if (killCount == null){
			killCount = board.registerNewObjective(KILLCOUNT, "playerKillCount");
		}	
		
		killCount.setDisplaySlot(DisplaySlot.BELOW_NAME);
		killCount.setDisplayName("Kills");
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
