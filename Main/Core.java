package Splatoon.Main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import Splatoon.GameManagement.GameManager;
import Splatoon.Main.GameStates.GameState;

public class Core extends JavaPlugin{

	public static JavaPlugin thisPlugin;
	public static boolean gameStarted;
	public static GameState gameState;
	public static int gameID;
	
	public static World lobbyWorld;
	public static World gameWorld;
	
	public static Location lobbySpawn;
	public static Location spectatorSpawn;
	public static Location team1Spawn;
	public static Location team2Spawn;
	public static int lobbyYaw;
	public static int team1Yaw;
	public static int team2Yaw;
	
	public static ChatColor team1Color;
	public static ChatColor team2Color;
	
	// global objects
	public static GameManager gameManager;
	
	@Override
	public void onEnable(){
		thisPlugin = this;
		gameStarted = false;
		gameState = GameState.NotStarted;
		gameID = 1;
		
		lobbyWorld = Bukkit.getWorld("world");
		gameWorld = null;
		lobbyYaw = -90;
		lobbySpawn = new Location(lobbyWorld, -137.5, 10, -139.5, lobbyYaw, 0F);
		spectatorSpawn = new Location(lobbyWorld, 7.5, 35, -116.5, 0F, 90F);
		team1Yaw = 0;
		team1Spawn = new Location(lobbyWorld, 7.5, 12, -188.5, team1Yaw, 0F);
		team2Yaw = -180;
		team2Spawn = new Location(lobbyWorld, 7.5, 12, -44.5, team2Yaw, 0F);
		
		// non-global objects
		
		// initialize objects
		gameManager = new GameManager();
		
		// register listeners
	}
	
	@Override
	public void onDisable(){
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if (cmd.getName().equalsIgnoreCase("start")){
			if (!sender.isOp()){
				sender.sendMessage("Must be OP to use this command.");
				return true;
			}
			
			// force start game
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("end")){
			
		}
		
		return false;
	}
	
	
	
	
}