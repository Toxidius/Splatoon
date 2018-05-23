package Splatoon.GameManagement;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import Splatoon.Main.Core;

public class WorldManager {
	
	public WorldTools worldTools;
	private ConfigManager configManager;
	private File worldsDir;
	private File chosenWorldConfig;
	private ArrayList<File> worlds;
	private String pathSeparator = File.separator;
	
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
	
	public boolean createGameWorld(String worldName){
		if (worlds == null
				|| worlds.isEmpty()
				|| worlds.size() < 1){
			Bukkit.getServer().broadcastMessage("No game worlds able to be loaded!");
			return false;
		}
		
		// delete game world if already exists
		if (worldTools.checkWorldExists("world_game") == true){
			for (Player player : Bukkit.getOnlinePlayers()){
				player.teleport(Core.lobbySpawn);
			}
			deleteGameWorld();
		}
		
		// choose the game world
		File chosenGameWorld = null;
		if (worldName == null){
			// choose random game world
			chosenGameWorld = worlds.get(Core.r.nextInt(worlds.size()));
		}
		else{
			// worldName was specified, get it's directory
			for (File file : worlds){
				if (file.getName().contains(worldName)){
					chosenGameWorld = file;
					break;
				}
			}
			if (chosenGameWorld == null){
				// specified world could not be found, choose the first one in the list
				chosenGameWorld = worlds.get(0);
			}
		}
		
		// determine the config file for this world
		chosenWorldConfig = new File(worldsDir.getPath() + pathSeparator + chosenGameWorld.getName() + ".yml");
		if (chosenWorldConfig.exists() == false){
			Bukkit.getServer().broadcastMessage("The world config file doesn't exist!");
			return false;
		}
		
		// copy game world
		try{
			worldTools.copyWorld(chosenGameWorld.getPath(), "world_game");
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
		
		// load config values
		loadConfig();
		return true;
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
	
	public void loadConfig(){
		// loads in all values from the config, and sets up the necessary values in the main class	
		double x, y, z, yaw;
		World world = Core.lobbyWorld;
		configManager = new ConfigManager(chosenWorldConfig);
		
		// spawn locations
		x = configManager.getDouble("spectator.spawn-x");
		y = configManager.getDouble("spectator.spawn-y");
		z = configManager.getDouble("spectator.spawn-z");
		Core.spectatorSpawn = new Location(world, x, y, z);
		
		x = configManager.getDouble("team1.spawn-x");
		y = configManager.getDouble("team1.spawn-y");
		z = configManager.getDouble("team1.spawn-z");
		yaw = configManager.getDouble("team1.spawn-yaw");
		Core.team1Spawn = new Location(world, x, y, z, (float)yaw, 0F);
		
		x = configManager.getDouble("team2.spawn-x");
		y = configManager.getDouble("team2.spawn-y");
		z = configManager.getDouble("team2.spawn-z");
		yaw = configManager.getDouble("team2.spawn-yaw");
		Core.team2Spawn = new Location(world, x, y, z, (float)yaw, 0F);
		
		// wool regions
		x = configManager.getDouble("woolRegionCorner1.spawn-x");
		y = configManager.getDouble("woolRegionCorner1.spawn-y");
		z = configManager.getDouble("woolRegionCorner1.spawn-z");
		Core.woolRegionCorner1 = new Location(world, x, y, z);
		
		x = configManager.getDouble("woolRegionCorner2.spawn-x");
		y = configManager.getDouble("woolRegionCorner2.spawn-y");
		z = configManager.getDouble("woolRegionCorner2.spawn-z");
		Core.woolRegionCorner2 = new Location(world, x, y, z);
		
		// stands
		x = configManager.getDouble("team1.stand1-x");
		y = configManager.getDouble("team1.stand1-y");
		z = configManager.getDouble("team1.stand1-z");
		yaw = configManager.getDouble("team1.spawn-yaw");
		Core.team1Stand1Location = new Location(world, x, y, z, (float)yaw, 0F);
		
		x = configManager.getDouble("team1.stand2-x");
		y = configManager.getDouble("team1.stand2-y");
		z = configManager.getDouble("team1.stand2-z");
		yaw = configManager.getDouble("team1.spawn-yaw");
		Core.team1Stand2Location = new Location(world, x, y, z, (float)yaw, 0F);
		
		x = configManager.getDouble("team1.stand3-x");
		y = configManager.getDouble("team1.stand3-y");
		z = configManager.getDouble("team1.stand3-z");
		yaw = configManager.getDouble("team1.spawn-yaw");
		Core.team1Stand3Location = new Location(world, x, y, z, (float)yaw, 0F);
		
		x = configManager.getDouble("team2.stand1-x");
		y = configManager.getDouble("team2.stand1-y");
		z = configManager.getDouble("team2.stand1-z");
		yaw = configManager.getDouble("team2.spawn-yaw");
		Core.team2Stand1Location = new Location(world, x, y, z, (float)yaw, 0F);
		
		x = configManager.getDouble("team2.stand2-x");
		y = configManager.getDouble("team2.stand2-y");
		z = configManager.getDouble("team2.stand2-z");
		yaw = configManager.getDouble("team2.spawn-yaw");
		Core.team2Stand2Location = new Location(world, x, y, z, (float)yaw, 0F);
		
		x = configManager.getDouble("team2.stand3-x");
		y = configManager.getDouble("team2.stand3-y");
		z = configManager.getDouble("team2.stand3-z");
		yaw = configManager.getDouble("team2.spawn-yaw");
		Core.team2Stand3Location = new Location(world, x, y, z, (float)yaw, 0F);
	}
}