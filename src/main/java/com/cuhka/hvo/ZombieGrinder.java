package com.cuhka.hvo;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

public class ZombieGrinder extends BukkitRunnable {
	private World world;
	private Location location;
	private Team team;
	private Plugin plugin;

	public ZombieGrinder(World world, Location location, Team team, Plugin plugin) {
		this.world = world;
		this.location = location;
		this.team = team;
		this.plugin = plugin;

	}

	@Override
	public void run() {
		long count = world.getEntitiesByClasses(Zombie.class).stream()
				.filter(entity -> entity.hasMetadata("team"))
				.flatMap(entity -> entity.getMetadata("team").stream())
				.filter(metadata -> metadata.value() == team)
				.count();

		
		if (count < 5){
			Entity zombie = world.spawnEntity(location, EntityType.ZOMBIE);
			zombie.setMetadata("team", new FixedMetadataValue(plugin, team));
		}
	}

}
