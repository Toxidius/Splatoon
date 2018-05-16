package Splatoon.GameManagement;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.entity.Player;

import Splatoon.Main.Core;

public class WorldManager {
	
	public WorldTools worldTools;
	//private ConfigManager configManager;
	private File worldsDir;
	private ArrayList<File> worlds;
	
	public WorldManager(){
		worldTools = new WorldTools();
		
		worldsDir = new File("SplatoonGameWorlds");
		if (worldsDir.exists() == false){
			worldsDir.mkdir();
		}
		
		worlds = new ArrayList<>();
		for (File file : worldsDir.listFiles()){
			if (file.isDirectory()){
				// is a world directory
				worlds.add(file);
			}
		}
	}
	
	public void createGameWorld(){
		// delete game world if already exists
		if (worldTools.checkWorldExists("world_game") == true){
			for (Player player : Bukkit.getOnlinePlayers()){
				player.teleport(Core.lobbySpawn);
			}
			deleteGameWorld();
		}
		
		// choose random game world
		File choosenGameWorld = worlds.get(Core.r.nextInt(worlds.size()));
		
		// copy game world
		try{
			worldTools.copyWorld(choosenGameWorld.getPath(), "world_game");
		}
		catch (Exception e){
			System.out.println("----- Error while copying the new game world! -----");
			System.out.println("Error: " + e.getMessage());
			System.out.println("-------------------------------------");
		}
		
		// load game world
		worldTools.loadWorld("world_game");
		Core.gameWorld = Bukkit.getWorld("world_game");
		
		// set some world values
		Core.gameWorld.setAutoSave(false);
		Core.gameWorld.setDifficulty(Difficulty.HARD);
	}
	
	public void deleteGameWorld(){
		// unload and delete old game world
		try{
			worldTools.unloadWorld("world_game");
			worldTools.deleteWorld("world_game");
		}
		catch (Exception e){
			System.out.println("----- Error while unloading and deleting the game world! -----");
			System.out.println("Error: " + e.getMessage());
			System.out.println("-------------------------------------");
		}
	}
	
	/*
	public void readConfig(){
		// loads in all values from the config, and sets up the necessary values in the main class
		double x, y, z, yaw;
		World world = CACore.lobbyWorld;
		
		configManager = new ConfigManager(choosenWorldConfig);
		CACore.numTeams = configManager.getInt("num-teams");
		CACore.hasMiddleResource = configManager.getBoolean("has-middle-resource");
		
		CACore.team1Name = configManager.getString("team1.name");
		CACore.team1Color = ChatColor.valueOf(CACore.team1Name.toUpperCase()) + "";
		x = configManager.getDouble("team1.spawn-x");
		y = configManager.getDouble("team1.spawn-y");
		z = configManager.getDouble("team1.spawn-z");
		yaw = configManager.getInt("team1.spawn-yaw");
		CACore.team1Spawn = new Location(world, x, y, z, (float)yaw, 0F);
		x = configManager.getDouble("team1.beacon-x");
		y = configManager.getDouble("team1.beacon-y");
		z = configManager.getDouble("team1.beacon-z");
		CACore.team1Beacon = new Location(world, x, y, z);
		x = configManager.getDouble("team1.resource-x");
		y = configManager.getDouble("team1.resource-y");
		z = configManager.getDouble("team1.resource-z");
		CACore.team1Resource = new Location(world, x, y, z);
		
		CACore.team2Name = configManager.getString("team2.name");
		CACore.team2Color = ChatColor.valueOf(CACore.team2Name.toUpperCase()) + "";
		x = configManager.getDouble("team2.spawn-x");
		y = configManager.getDouble("team2.spawn-y");
		z = configManager.getDouble("team2.spawn-z");
		yaw = configManager.getDouble("team2.spawn-yaw");
		CACore.team2Spawn = new Location(world, x, y, z, (float)yaw, 0F);
		x = configManager.getDouble("team2.beacon-x");
		y = configManager.getDouble("team2.beacon-y");
		z = configManager.getDouble("team2.beacon-z");
		CACore.team2Beacon = new Location(world, x, y, z);
		x = configManager.getDouble("team2.resource-x");
		y = configManager.getDouble("team2.resource-y");
		z = configManager.getDouble("team2.resource-z");
		CACore.team2Resource = new Location(world, x, y, z);
				
		if (CACore.numTeams == 3){
			CACore.team3Name = configManager.getString("team3.name");
			CACore.team3Color = ChatColor.valueOf(CACore.team3Name.toUpperCase()) + "";
			x = configManager.getDouble("team3.spawn-x");
			y = configManager.getDouble("team3.spawn-y");
			z = configManager.getDouble("team3.spawn-z");
			yaw = configManager.getDouble("team3.spawn-yaw");
			CACore.team3Spawn = new Location(world, x, y, z, (float)yaw, 0F);
			x = configManager.getDouble("team3.beacon-x");
			y = configManager.getDouble("team3.beacon-y");
			z = configManager.getDouble("team3.beacon-z");
			CACore.team3Beacon = new Location(world, x, y, z);
			x = configManager.getDouble("team3.resource-x");
			y = configManager.getDouble("team3.resource-y");
			z = configManager.getDouble("team3.resource-z");
			CACore.team3Resource = new Location(world, x, y, z);
		}
		
		if (CACore.hasMiddleResource == true){
			x = configManager.getDouble("middle.resource-x");
			y = configManager.getDouble("middle.resource-y");
			z = configManager.getDouble("middle.resource-z");
			CACore.middleResource = new Location(world, x, y, z);
		}
		
		CACore.team1Color = ChatColor.valueOf(CACore.team1Name.toUpperCase()) + "";
		CACore.team2Color = ChatColor.valueOf(CACore.team2Name.toUpperCase()) + "";
		CACore.team3Color = ChatColor.valueOf(CACore.team3Name.toUpperCase()) + "";
		
		CACore.team1HelmetColor = Color.fromRGB(configManager.getInt("team1.helmet-color"));
		CACore.team2HelmetColor = Color.fromRGB(configManager.getInt("team2.helmet-color"));
		CACore.team3HelmetColor = Color.fromRGB(configManager.getInt("team3.helmet-color"));
		
		if (CACore.team1Name.contains("_")){
			CACore.team1Name = CACore.team1Name.substring(CACore.team1Name.indexOf("_")+1); // for long names like Dark_Purple will turn to just Purple
		}
		if (CACore.team2Name.contains("_")){
			CACore.team2Name = CACore.team2Name.substring(CACore.team2Name.indexOf("_")+1); // for long names like Dark_Purple will turn to just Purple
		}
		if (CACore.team3Name.contains("_")){
			CACore.team3Name = CACore.team3Name.substring(CACore.team3Name.indexOf("_")+1); // for long names like Dark_Purple will turn to just Purple
		}
	}
	*/
}