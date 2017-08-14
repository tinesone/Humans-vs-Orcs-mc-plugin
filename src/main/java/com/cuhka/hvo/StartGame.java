package com.cuhka.hvo;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.google.common.base.Predicates;

public class StartGame implements CommandExecutor {
	private Scoreboard board;
	private Server server;
	
	public StartGame(Server server, Scoreboard board){
		this.server = server;
		this.board = board;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player){
			Objective killCount = board.getObjective(HvOPlugin.KILLCOUNT);

			board.getEntries()
			.stream()
			.map(e -> killCount.getScore(e))
			.forEach(s -> s.setScore(0));
			populateTeams();
			return true;
		}
		return false;
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
		
		// Collect all player entities from the board
		List<String> entries = board.getEntries().stream()
			.map(server::getPlayer)
			.filter(Predicates.notNull())
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
	}

	private void broadcastTeam(Team team) {
		String message = String.format("%sTeam %s: %s", team.getColor(), team.getName(),
					team.getEntries().stream()
							 .collect(Collectors.joining(", ")));
		server.broadcastMessage(message);
	}
}


