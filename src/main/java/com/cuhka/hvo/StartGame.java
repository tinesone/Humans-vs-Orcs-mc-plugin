package com.cuhka.hvo;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
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
	private Plugin plugin;

	public StartGame(Plugin plugin, Server server, World world, Scoreboard board, Configuration config){
		this.server = server;
		this.world = world;
		this.board = board;
		this.config = Objects.requireNonNull(config, "config");
		this.plugin = Objects.requireNonNull(plugin, "plugin");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player){
			startGame();
			return true;
		}

		return false;
	}

	private void startGame() {
		orcs = Objects.requireNonNull(board.getTeam(HvOPlugin.TEAM_ORCS), "Orc team missing");
		humans = Objects.requireNonNull(board.getTeam(HvOPlugin.TEAM_HUMANS), "Humans team missing");
		resetScores();
		populateTeams();
		refillStarterItems();
		clearActors();
		startGrinder(humans);
		startGrinder(orcs);
		spawnVillager(orcs);
		spawnVillager(humans);
	}

	private void spawnVillager(Team team) {
		Location coords = getLocation(team, "villager");
		Villager merchant = (Villager) world.spawnEntity(coords, EntityType.VILLAGER);
		MerchantRecipe recipe = new MerchantRecipe(new ItemStack(Material.IRON_SWORD, 1), Integer.MAX_VALUE);
		recipe.addIngredient(new ItemStack(Material.GOLD_NUGGET, 3));
		recipe.addIngredient(new ItemStack(Material.EMERALD, 1));
		merchant.setRecipes(Collections.singletonList(recipe));

	}

	private void startGrinder(Team team) {
		Location coords = getLocation(team, "zombiecords");
		ZombieGrinder grinder = new ZombieGrinder(world, coords, team, plugin);
		
		int delay = config.getInt("zombiedelay", 5) * 20;
		grinder.runTaskTimer(plugin, 100, delay);
	}

	private void clearActors() {
		world.getEntitiesByClasses(Zombie.class, Villager.class).forEach(Entity::remove);
	}

	private void refillStarterItems() {
		orcs.getEntries().stream()
			.map(server::getPlayer)
			.forEach(player -> refillOrcItems(player));

		humans.getEntries().stream()
			.map(server::getPlayer)
			.forEach(player -> refillHumanItems(player));
	}

	private void refillHumanItems(Player player) {
		PlayerInventory inventory = player.getInventory();

		player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 600, 200));

		inventory.clear();
		ItemStack sword = new ItemStack(Material.GOLD_SWORD);
		ItemMeta meta = sword.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + "Fine Sword");
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

		board.getEntries().stream()
			.map(e -> killCount.getScore(e))
			.forEach(s -> s.setScore(0));
	}
	private void populateTeams() {
		orcs.getEntries().forEach(orcs::removeEntry);
		humans.getEntries().forEach(humans::removeEntry);
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

	private Location getLocation(Team team, String subkey) {
		String path = team.getName().toLowerCase() + "." + subkey;
		String value = Objects.requireNonNull(config.getString(path), () -> "No location value at " + path);

		String[] parts = value.split("\\s*,\\s*");
		double x = Double.parseDouble(parts[0]);
		double y = Double.parseDouble(parts[1]);
		double z = Double.parseDouble(parts[2]);
		return new Location (world, x, y, z);
	}

	private void teleportTeam(Team team){
		Location coords = getLocation(team, "spawncords");

		team.getEntries().stream()
			.map(server::getPlayer)
			.forEach(player -> player.teleport(coords));
	}

}


