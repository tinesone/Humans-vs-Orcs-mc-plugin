package com.cuhka.hvo;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class StartGame implements CommandExecutor {
	private Scoreboard board;
	private Server server;
	private Configuration config;
	private Team orcs;
	private Team humans;
	private World world;

	public StartGame(Server server, World world, Scoreboard board, Configuration config){
		this.server = server;
		this.world = world;
		this.board = board;
		this.config = config;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player){
			orcs = Objects.requireNonNull(board.getTeam(HvOPlugin.TEAM_ORCS), "Orc team missing");
			humans = Objects.requireNonNull(board.getTeam(HvOPlugin.TEAM_HUMANS), "Humans team missing");
			resetScores();
			populateTeams();
			refillStarterItems();
			clearZombies();
			return true;
		}
		return false;
	}

private void clearZombies() {
		world.getEntitiesByClasses(Zombie.class).forEach(Entity -> Entity.remove());
	}

	private void refillStarterItems() {
		orcs.getEntries().stream()
			.map(name -> server.getPlayer(name))
			.forEach(player -> refillOrcItems(player));
		
		humans.getEntries().stream()
			.map(name -> server.getPlayer(name))
			.forEach(player -> refillHumanItems(player));
	}

	private void refillHumanItems(Player player) {
		PlayerInventory inventory = player.getInventory();
		
		player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 600, 200));
		
		inventory.clear();
		ItemStack sword = new ItemStack(Material.GOLD_SWORD);
		ItemMeta meta = sword.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + "Fine Crafted Sword");
		sword.setItemMeta(meta);
		inventory.addItem(
				sword, 
				new ItemStack(Material.BOAT, 1),
			    new ItemStack(Material.COOKED_CHICKEN, 3));
	}

	private void refillOrcItems(Player player) {
		PlayerInventory inventory = player.getInventory();
		
		player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 600, 200));
		
		inventory.clear();
		ItemStack sword = new ItemStack(Material.GOLD_SWORD, 1);
		ItemMeta meta = sword.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + "Rusty Cutlass");
		sword.setItemMeta(meta);
		inventory.addItem(
				sword, 
				new ItemStack(Material.BOAT, 1),
			    new ItemStack(Material.COOKED_CHICKEN, 3));
	}

	private void resetScores() {
		Objective killCount = board.getObjective(HvOPlugin.KILLCOUNT);

		board.getEntries()
		.stream()
		.map(e -> killCount.getScore(e))
		.forEach(s -> s.setScore(0));
	}
	private void populateTeams() {
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
		return new Location (world, x, y, z);
	}
	
	private void teleportTeam(Team team){
		String rawCoords = config.getString(team.getName().toLowerCase() + ".spawncords");
		Location coords = parseLocation(rawCoords);
		team.getEntries().stream()
		.map(name -> server.getPlayer(name))
		.forEach(player -> player.teleport(coords));
	}

}


