package com.cuhka.hvo;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class StartGame implements CommandExecutor {
	private Scoreboard board;
	private Server server;
	private Configuration config;

	public StartGame(Server server, Scoreboard board, Configuration config){
		this.server = server;
		this.board = board;
		this.config = config;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player){
			resetScores();
			populateTeams();
			return true;
		}
		return false;
	}

	private void resetScores() {
		Objective killCount = board.getObjective(HvOPlugin.KILLCOUNT);

		board.getEntries()
		.stream()
		.map(e -> killCount.getScore(e))
		.forEach(s -> s.setScore(0));
	}
	private void populateTeams() {
		Team orcs = Objects.requireNonNull(board.getTeam(HvOPlugin.TEAM_ORCS), "Orc team missing");
		Team humans = Objects.requireNonNull(board.getTeam(HvOPlugin.TEAM_HUMANS), "Humans team missing");
		Random random = new Random();

		Team team1;
		Team team2;

		if (random.nextBoolean()) {
			team1 = orcs;
			team2 = humans;
		} else {
			team1 = humans;
			team2 = orcs;
		}

		List<String> entries  = server.getOnlinePlayers().stream()
				.map(Player::getName)
				.collect(Collectors.toCollection(ArrayList::new));

		int half = entries.size() / 2;

		for (int c = 0; c < half; c++) {
			int num = random.nextInt(entries.size());
			String entry = entries.remove(num);
			team1.addEntry(entry);
		}

		entries.forEach(team2::addEntry);
		broadcastTeam(humans);
		broadcastTeam(orcs);
		teleportTeam(orcs);
		teleportTeam(humans);

	}


	private void broadcastTeam(Team team) {
		String message = String.format("%sTeam %s: %s", team.getColor(), team.getName(),
				team.getEntries().stream()
				.collect(Collectors.joining(", ")));
		server.broadcastMessage(message);
	}

	private Location parseLocation(String value) {
		String[] parts = value.split("\\s*,\\s*");
		double x = Double.parseDouble(parts[0]);
		double y = Double.parseDouble(parts[1]);
		double z = Double.parseDouble(parts[2]);
		return new Location (server.getWorld("level"), x, y, z);
	}
	
	private void teleportTeam(Team team){
		String rawCoords = config.getString(team.getName().toLowerCase() + ".spawncords");
		Location coords = parseLocation(rawCoords);
		System.out.println(coords);
		team.getEntries().stream()
		.map(name -> server.getPlayer(name))
		.forEach(player -> player.teleport(coords));
	}

}


