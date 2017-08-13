package com.cuhka.hvo;

import org.bukkit.plugin.java.JavaPlugin;

public class HvOPlugin extends JavaPlugin {

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		super.onDisable();
	}

	@Override
	public void onLoad() {
		System.out.println("Hello World");
		super.onEnable();
	}

}
