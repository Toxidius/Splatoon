package Splatoon.GameManagement;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;

import Splatoon.GameMechanics.RespawnTimerRunnable;
import Splatoon.Main.Core;
import Splatoon.Main.GameStates.GameState;

public class GameManager {
	
	private Random r;
	public WorldManager worldManager;
	public ScoreboardManager scoreboardManager;
	public int purpleBlocks;
	public int greenBlocks;
	public int warmupTimeRemaining;
	public int timeRemaining;
	
	public GameManager(){
		r = new Random();
		worldManager = new WorldManager();
		scoreboardManager = new ScoreboardManager();
		
		//PluginManager pluginMan = Bukkit.getPluginManager();
	}
	
	public boolean startGame(){
		// returns whether or not the game started successfully
		
		// check if a game can start
		if (Core.gameStarted == true){
			return false; // game is already in progress!
		}
		
		// reset some values
		scoreboardManager = new ScoreboardManager();
		warmupTimeRemaining = 5;
		timeRemaining = 120;
		purpleBlocks = 0;
		greenBlocks = 0;
		
		// create the game world
		Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "Loading game world...");
		createGameWorld();
		Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "Done loading game world.");
		
		// update world locations
		Core.team1Spawn.setWorld(Core.gameWorld);
		Core.team2Spawn.setWorld(Core.gameWorld);
		Core.spectatorSpawn.setWorld(Core.gameWorld);
		
		// setup scoreboards
		scoreboardManager.setupScoreboard();
		String title, line1, line2, line3, line4;
		title = ChatColor.GOLD + "Paint Percent";
		line1 = ChatColor.WHITE + "Time Remaining: " + timeRemaining;
		line2 = ChatColor.DARK_PURPLE + "Purple: " + ChatColor.WHITE + "0";
		line3 = ChatColor.GREEN + "Green: " + ChatColor.WHITE + "0";
		line4 = "";
		scoreboardManager.setupSidebar(title, line1, line2, line3, line4);
		
		// generate the player teams
		generateTeams();
		
		// finish up the player and teleport into game
		int team;
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player.isOnline()){
				team = player.getMetadata("game" + Core.gameID + "team").get(0).asInt();
				player.setScoreboard(scoreboardManager.scoreboard);
				player.setGameMode(GameMode.SURVIVAL);
				player.setFallDistance(0); // so they don't die if falling
				player.setHealth(20); // full health
				player.setFoodLevel(20); // set food level to full
				player.setSaturation(40); // set saturation to 40
				clearInventory(player);
				if (team == 1){
					player.teleport(Core.team1Spawn);
				}
				else if (team == 2){
					player.teleport(Core.team2Spawn);
				}
			}
		}
		
		// game start messages
		Bukkit.getServer().broadcastMessage(ChatColor.AQUA + "All chat is global.");
		
		// start scheduled events
		// scoreboard updater
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.thisPlugin, scoreboardManager, 20L, 20L);
		// set some final values
		Core.gameStarted = true;
		Core.gameState = GameState.Running;
		
		return true;
	}
	
	public void endGameInitiate(int winningTeam){
		// initiates the game end sequence
		Core.gameState = GameState.Ending;
		int seconds = 10;
		
		Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "-----------------------------------");
		Bukkit.getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "The game is over!");
		Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "-----------------------------------");
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Core.thisPlugin, new Runnable(){
			@Override
			public void run() {
				Core.gameManager.endGame();
			}
		}, seconds*20L); // 10 second delay
	}
	
	public boolean endGame(){
		// return whether or not the game ended successfully
		
		// end all scheduled events
		Bukkit.getScheduler().cancelAllTasks();
		
		// teleport all players to lobby and reset their inventory and scoreboard
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player.isOnline()){
				if (player.hasMetadata("game" + Core.gameID + "team")){
					player.removeMetadata("game" + Core.gameID + "team", Core.thisPlugin);
				}
				player.setScoreboard(scoreboardManager.emptyScoreboard);
				player.setGameMode(GameMode.SURVIVAL);
				player.setFallDistance(0); // so they don't die if falling
				player.setHealth(20); // full health
				player.setFoodLevel(20); // set food level to full
				player.setSaturation(40); // set saturation to 40
				player.setWalkSpeed(0.2F); // default walk speed
				for (PotionEffect effect : player.getActivePotionEffects()){ // remove all potion effects
					player.removePotionEffect(effect.getType());
				}
				clearInventory(player);
				player.teleport(Core.lobbySpawn);
			}
		}
		
		// reset some values
		Core.gameState = GameState.NotStarted;
		
		// delete the game world
		Bukkit.getScheduler().scheduleSyncDelayedTask(Core.thisPlugin, new Runnable(){
			@Override
			public void run() {
				worldManager.deleteGameWorld();
				
				Core.gameStarted = false; // update this so the next game can start
			}
		}, 40L);
		
		return true;
	}
	
	public void createGameWorld(){
		worldManager.createGameWorld();
	}
	
	public void generateTeams(){
		// give all players a random number and team of -1
		for (Player player : Bukkit.getOnlinePlayers()){
			player.setMetadata("game" + Core.gameID + "team", new FixedMetadataValue(Core.thisPlugin, new Integer(-1)));
			player.setMetadata( "randomNumber", new FixedMetadataValue(Core.thisPlugin, new Integer(r.nextInt(1000))) );
		}
		
		// loop through the players finding the next player (without team) with the smallest random number and place them on placedTeam
		int teamToBe = 1;
		while (true){
			int currentLowest = 1000000; // arbitrary value (greater than the maximum random) to start off with
			Player lowestPlayer = null;
			int random;
			int team;
			for (Player player : Bukkit.getOnlinePlayers()){
				random = player.getMetadata("randomNumber").get(0).asInt();
				team = player.getMetadata("game" + Core.gameID + "team").get(0).asInt();
				
				if ( (random < currentLowest) && (team == -1) ){
					currentLowest = random;
					lowestPlayer = player;
				}
			}
			
			if (lowestPlayer == null){
				// no player was choosen (all are on teams)
				// done looping through the array. all players should be on teams now...
				return;
			}
			
			lowestPlayer.removeMetadata("game" + Core.gameID + "team", Core.thisPlugin);
			lowestPlayer.setMetadata("game" + Core.gameID + "team", new FixedMetadataValue(Core.thisPlugin, new Integer(teamToBe)));
			
			if (teamToBe == 1){
				scoreboardManager.addPlayerToTeam(lowestPlayer.getName(), 1);
			}
			else if (teamToBe == 2){
				scoreboardManager.addPlayerToTeam(lowestPlayer.getName(), 2);
			}
			
			// update teamToBe for next player
			teamToBe++;
			if (teamToBe > 2){
				teamToBe = 1;
			}
		}
	}
	
	public void setPlayerTeam(Player player, int team, boolean teleport){
		// set meta
		player.removeMetadata("game" + Core.gameID + "team", Core.thisPlugin);
		player.setMetadata("game" + Core.gameID + "team", new FixedMetadataValue(Core.thisPlugin, new Integer(team)));
		
		// set scoreboard
		scoreboardManager.removePlayerFromTeam(player.getName(), team);
		scoreboardManager.addPlayerToTeam(player.getName(), team);
		
		// teleport to new team spawn
		if (teleport == true){
			if (team == 1){
				player.teleport(Core.team1Spawn);
			}
			else if (team == 2){
				player.teleport(Core.team2Spawn);
			}
		}
		
	}
	
	public int getPlayerTeam(String playerName){
		Player player = Bukkit.getPlayer(playerName);
		if (player == null){
			return -1; // no player with this name online
		}
		else{
			if (player.hasMetadata("game" + Core.gameID + "team") == true){
				return player.getMetadata("game" + Core.gameID + "team").get(0).asInt();
			}
			else{
				return -1; // no team
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void simulateDeath(Player player){
		// simulated death stuff
		player.setHealth(20); // respawn the player
		player.setFallDistance(0F);
		player.setFoodLevel(20); // set food level to full
		player.setSaturation(20); // set saturation to 20
		for (PotionEffect effect : player.getActivePotionEffects()){ // remove all potion effects
			player.removePotionEffect(effect.getType());
		}
		player.setGameMode(GameMode.SPECTATOR);
		Location newLocation = player.getLocation();
		newLocation.setY(newLocation.getY() + 10);
		player.teleport(newLocation);
		
		// set the equipment the player will respawn with here
		
		// start the countdown timer thingy
		ItemStack[] keepArmor = player.getEquipment().getArmorContents();
		ItemStack[] keepInventory = player.getInventory().getContents();
		@SuppressWarnings("unused")
		RespawnTimerRunnable respawnTimer = new RespawnTimerRunnable(player, keepArmor, keepInventory);
		
		// send title "Respawning..."
		player.sendTitle(ChatColor.GOLD + "Respawning...", "");
	}
	
	public void removeItemWithName(Player player, String itemName){
		// removes the first instance of a item with a particular name from the players inventory
		
		PlayerInventory inv = player.getInventory();
		ItemStack[] contents = inv.getContents();
		for (int i = 0; i < contents.length; i++){
			if (contents[i] != null 
					&& contents[i].hasItemMeta() 
					&& contents[i].getItemMeta().hasDisplayName() 
					&& contents[i].getItemMeta().getDisplayName().equals(itemName)){
				inv.clear(i);
				return;
			}
		}
	}
	
	public void clearInventory(Player player){
		player.setExp(0F);
		player.setLevel(0);
		
		// clears the player's usable inventory
		player.getInventory().clear();
		
		// remove armor slot contents as getInventory().clear doesn't clear this
		PlayerInventory playerInvenotory = player.getInventory();
		ItemStack air = new ItemStack(Material.AIR);
		playerInvenotory.setHelmet(air);
		playerInvenotory.setChestplate(air);
		playerInvenotory.setLeggings(air);
		playerInvenotory.setBoots(air);
		
		// remove the item player has on their cursor
		player.setItemOnCursor(air);
		
		// remove any items in crafting window
		Inventory craftingInventory = player.getOpenInventory().getTopInventory();
		craftingInventory.setItem(1, air);
		craftingInventory.setItem(2, air);
		craftingInventory.setItem(3, air);
		craftingInventory.setItem(4, air);
		
	}

}