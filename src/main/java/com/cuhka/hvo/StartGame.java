package com.cuhka.hvo;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class StartGame implements CommandExecutor {
	private Scoreboard board;
	public StartGame(Scoreboard board){
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
				
				return true;
			}
		return false;
	}

}
