package Splatoon.Main;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import Splatoon.GameManagement.GameManager;
import Splatoon.GameMechanics.DisabledEvents;
import Splatoon.GameMechanics.KitSelection;
import Splatoon.GameMechanics.LobbyBuilding;
import Splatoon.GameMechanics.PlayerChat;
import Splatoon.GameMechanics.PlayerDeath;
import Splatoon.GameMechanics.PlayerJoin;
import Splatoon.GameMechanics.SpawnInvulerability;
import Splatoon.GameMechanics.SplatCharger;
import Splatoon.GameMechanics.SplatterShot;
import Splatoon.Main.GameStates.GameState;

public class Core extends JavaPlugin{

	public static JavaPlugin thisPlugin;
	public static Random r;
	public static boolean gameStarted;
	public static GameState gameState;
	public static int gameID;
	public static String MOTD;
	
	public static World lobbyWorld;
	public static World gameWorld;
	
	public static Location lobbySpawn;
	public static Location spectatorSpawn;
	public static Location team1Spawn;
	public static Location team2Spawn;
	public static Location woolRegionCorner1;
	public static Location woolRegionCorner2;
	public static Location team1Stand1Location;
	public static Location team1Stand2Location;
	public static Location team1Stand3Location;
	public static Location team2Stand1Location;
	public static Location team2Stand2Location;
	public static Location team2Stand3Location;
	public static int lobbyYaw;
	public static int team1Yaw;
	public static int team2Yaw;
	
	public static ChatColor team1Color;
	public static ChatColor team2Color;
	public static Color team1LeatherColor;
	public static Color team2LeatherColor;
	public static byte team1WoolColor;
	public static byte team2WoolColor;
	
	// global objects
	public static GameManager gameManager;
	public static PluginManager pluginMan;
	
	@SuppressWarnings("unused")
	@Override
	public void onEnable(){
		thisPlugin = this;
		r = new Random();
		gameStarted = false;
		gameState = GameState.NotStarted;
		gameID = 1;
		MOTD = "Woah new game?? :O";
		pluginMan = Bukkit.getPluginManager();
		
		lobbyWorld = Bukkit.getWorld("world");
		gameWorld = null;
		lobbyYaw = -90;
		lobbySpawn = new Location(lobbyWorld, -137.5, 10, -139.5, lobbyYaw, 0F);
		spectatorSpawn = new Location(lobbyWorld, 7.5, 35, -116.5, 0F, 90F);
		team1Yaw = 0;
		team1Spawn = new Location(lobbyWorld, 7.5, 12, -188.5, team1Yaw, 0F);
		team2Yaw = -180;
		team2Spawn = new Location(lobbyWorld, 7.5, 12, -44.5, team2Yaw, 0F);
		woolRegionCorner1 = new Location(lobbyWorld, -18, 5, -195);
		woolRegionCorner2 = new Location(lobbyWorld, 33, 10, -37);
		team1Stand1Location = new Location(lobbyWorld, 5.5, 11, -194.5, 0F, 0F);
		team1Stand2Location = new Location(lobbyWorld, 7.5, 11, -194.5, 0F, 0F);
		team1Stand3Location = new Location(lobbyWorld, 9.5, 11, -194.5, 0F, 0F);
		team2Stand1Location = new Location(lobbyWorld, 9.5, 11, -38.5, 180F, 0F);
		team2Stand2Location = new Location(lobbyWorld, 7.5, 11, -38.5, 180F, 0F);
		team2Stand3Location = new Location(lobbyWorld, 5.5, 11, -38.5, 180F, 0F);
		
		team1Color = ChatColor.DARK_PURPLE;
		team2Color = ChatColor.GREEN;
		team1LeatherColor = Color.PURPLE;
		team2LeatherColor = Color.GREEN;
		team1WoolColor = 10;
		team2WoolColor = 5;
		
		// non-global objects
		PlayerJoin playerJoin = new PlayerJoin();
		PlayerChat playerChat = new PlayerChat();
		PlayerDeath playerDeath = new PlayerDeath();
		DisabledEvents disabledEvents = new DisabledEvents();
		LobbyBuilding lobbyBuilding = new LobbyBuilding();
		SpawnInvulerability spawnInvulerability = new SpawnInvulerability();
		KitSelection kitSelection = new KitSelection();
		SplatCharger splatCharger = new SplatCharger();
		SplatterShot splatterShot = new SplatterShot();
		
		// initialize objects
		gameManager = new GameManager();
		
		// register listeners
		// no longer needed -- each listener class is responsible for registering as a listener by calling registerListener()
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
			gameManager.startGame();
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("end")){
			if (!sender.isOp()){
				sender.sendMessage("Must be OP to use this command.");
				return true;
			}
			
			// force end game
			gameManager.endGameInitiate(-1); // end with no team winning
			return true;
		}
		
		return false;
	}
	
	public static void registerListener(Listener listener){
		pluginMan.registerEvents(listener, thisPlugin);
	}
	
	
}