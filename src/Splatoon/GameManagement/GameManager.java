package Splatoon.GameManagement;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;

import Splatoon.GameMechanics.KitSelectionStands;
import Splatoon.GameMechanics.RespawnTimerRunnable;
import Splatoon.GameMechanics.SplatChargerRunnable;
import Splatoon.GameMechanics.SplatRollerRunnable;
import Splatoon.GameMechanics.SplatterShotRunnable;
import Splatoon.GameMechanics.SquidCheckerRunnable;
import Splatoon.GameMechanics.WoolClimbing;
import Splatoon.GameMechanics.WoolCountUpdater;
import Splatoon.Main.Core;
import Splatoon.Main.GameStates.GameState;

public class GameManager {
	
	private Random r;
	public WorldManager worldManager;
	public ScoreboardManager scoreboardManager;
	public GameStarter gameStarter;
	public int team1Blocks;
	public int team2Blocks;
	public int gameTime = 180; // total gameplay time (seconds) -- default 180 (3 mins)
	public int warmupTimeRemaining;
	public int timeRemaining;
	
	public WoolCountUpdater woolCountUpdater;
	public WoolClimbing woolClimbing;
	public SquidCheckerRunnable squidCheckerRunnable;
	public SplatRollerRunnable splatRollerRunnable;
	public SplatChargerRunnable splatChargerRunnable;
	public SplatterShotRunnable splatterShotRunnable;
	public KitSelectionStands kitSelectionStands;
	
	public GameManager(){
		r = new Random();
		worldManager = new WorldManager();
		scoreboardManager = new ScoreboardManager();
		kitSelectionStands = new KitSelectionStands();
		
		gameStarter = new GameStarter();
		gameStarter.start();
	}
	
	public boolean startGame(String worldName){
		// returns whether or not the game started successfully
		
		// check if a game can start
		if (Core.gameStarted == true){
			return false; // game is already in progress!
		}
		
		// reset some values
		gameStarter.stop();
		scoreboardManager = new ScoreboardManager();
		warmupTimeRemaining = 5;
		timeRemaining = gameTime; // gameplay time (seconds) -- default 180 (3 mins)
		team1Blocks = 0;
		team2Blocks = 0;
		
		// create the game world
		//Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "Loading game world...");
		boolean output = createGameWorld(worldName);
		if (output == false){
			return false; // game could not be started because the world couldn't be loaded
		}
		//Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "Done loading game world.");
		
		// update world locations
		Core.team1Spawn.setWorld(Core.gameWorld);
		Core.team2Spawn.setWorld(Core.gameWorld);
		Core.spectatorSpawn.setWorld(Core.gameWorld);
		Core.woolRegionCorner1.setWorld(Core.gameWorld);
		Core.woolRegionCorner2.setWorld(Core.gameWorld);
		Core.team1Stand1Location.setWorld(Core.gameWorld);
		Core.team1Stand2Location.setWorld(Core.gameWorld);
		Core.team1Stand3Location.setWorld(Core.gameWorld);
		Core.team2Stand1Location.setWorld(Core.gameWorld);
		Core.team2Stand2Location.setWorld(Core.gameWorld);
		Core.team2Stand3Location.setWorld(Core.gameWorld);
		
		
		// setup scoreboards
		scoreboardManager.setupScoreboard();
		scoreboardManager.updateScoreboard(); // update the scoreboard with the default values
		
		// generate the player teams
		generateTeams();
		// setup kits based on what the player chose
		setupKits();
		
		// finish up the player and teleport into game
		int team;
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player.isOnline()){
				team = getPlayerTeam(player); // team
				player.setScoreboard(scoreboardManager.scoreboard);
				player.setGameMode(GameMode.SURVIVAL);
				player.setFallDistance(0); // so they don't die if falling
				player.setHealth(20); // full health
				player.setFoodLevel(20); // set food level to full
				player.setSaturation(40); // set saturation to 40
				clearInventory(player);
				givePlayerKit(player);
				givePlayerTeamArmor(player);
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
		scoreboardManager.start();
		// wool count updater
		woolCountUpdater = new WoolCountUpdater();
		woolClimbing = new WoolClimbing();
		squidCheckerRunnable = new SquidCheckerRunnable();
		splatRollerRunnable = new SplatRollerRunnable();
		splatChargerRunnable = new SplatChargerRunnable();
		splatterShotRunnable = new SplatterShotRunnable();
		// load kit selection stands
		kitSelectionStands.generateStands();
		// set some final values
		Core.gameStarted = true;
		Core.gameState = GameState.Warmup;
		
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public void endGameInitiate(int winningTeam){
		// initiates the game end sequence
		int seconds = 10;
		if (winningTeam == -1){
			seconds = 2;
		}
		
		// end all scheduled events
		scoreboardManager.stop();
		Bukkit.getScheduler().cancelTasks(Core.thisPlugin);
		Core.gameState = GameState.Ending;
		
		// set all players in spectate mode
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player.isOnline()
					&& player.getGameMode() == GameMode.SURVIVAL){
				player.setGameMode(GameMode.SPECTATOR);
				Location newLocation = player.getLocation().add(0.0, 10.0, 0.0);
				player.teleport(newLocation);
			}
		}
		
		String winningMessage = "";
		
		if (winningTeam == 1){
			winningMessage = ChatColor.BOLD + "" + Core.team1Color + "Purple Team won the game!";
		}
		else if (winningTeam == 2){
			winningMessage = ChatColor.BOLD + "" + Core.team2Color + "Green Team won the game!";
		}
		else if (winningTeam == 0){
			winningMessage = ChatColor.BOLD + "" + ChatColor.WHITE + "It's a tie! :O";
		}
		else{
			winningMessage = ChatColor.DARK_RED + "" + ChatColor.BOLD + "The game was terminated!";
		}
		
		Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "-----------------------------------");
		Bukkit.getServer().broadcastMessage(winningMessage);
		Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "-----------------------------------");
		
		for (Player player : Bukkit.getOnlinePlayers()){
			player.sendTitle("", winningMessage);
		}
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Core.thisPlugin, new Runnable(){
			@Override
			public void run() {
				Core.gameManager.endGame();
			}
		}, seconds*20L); // 10 second delay
	}
	
	public boolean endGame(){
		// return whether or not the game ended successfully
		
		// teleport all players to lobby and reset their inventory and scoreboard
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player.isOnline()){
				if (player.hasMetadata("game" + Core.gameID + "team")){
					player.removeMetadata("game" + Core.gameID + "team", Core.thisPlugin);
				}
				if (player.hasMetadata("game" + Core.gameID + "kit")){
					player.removeMetadata("game" + Core.gameID + "kit", Core.thisPlugin);
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
		
		// startup game starter
		gameStarter.start();
		
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
	
	public boolean createGameWorld(String worldName){
		return worldManager.createGameWorld(worldName);
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
	
	public void setupKits(){
		ItemStack stack;
		for (Player player : Bukkit.getOnlinePlayers()){
			stack = player.getInventory().getHelmet();
			if (stack == null
					|| stack.getType() == Material.AIR){
				// air -- no kit selected, choose a random one for them
				int randomKitID = Core.r.nextInt(3)+1; // 1 to 3
				setPlayerKit(player, randomKitID);
			}
			else if (stack.getType() == Material.STICK){
				setPlayerKit(player, 1); // splat roller id
			}
			else if (stack.getType() == Material.BOW){
				setPlayerKit(player, 2); // splat charger id
			}
			else if (stack.getType() == Material.SNOW_BALL){
				setPlayerKit(player, 3); // splatter shot id
			}
		}
	}
	
	public void setPlayerKit(Player player, int kitID){
		player.removeMetadata("game" + Core.gameID + "kit", Core.thisPlugin);
		player.setMetadata("game" + Core.gameID + "kit", new FixedMetadataValue(Core.thisPlugin, new Integer(kitID)));
	}
	
	public void givePlayerKit(Player player){
		int kit = getPlayerKitID(player);
		if (kit == 1){
			// splat roller
			ItemStack stick = new ItemStack(Material.STICK, 1);
			stick.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
			player.getInventory().addItem(stick);
		}
		else if (kit == 2){
			// splat charger
			ItemStack bow = new ItemStack(Material.BOW, 1);
			bow.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
			player.getInventory().addItem(bow);
			player.getInventory().addItem(new ItemStack(Material.ARROW, 3));
		}
		else if (kit == 3){
			// splatter shot
			player.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 16));
		}
	}
	
	public void givePlayerTeamArmor(Player player){
		LeatherArmorMeta meta1;
		LeatherArmorMeta meta2;
		
		ItemStack team1Chestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
		meta1 = (LeatherArmorMeta) team1Chestplate.getItemMeta();
		meta1.setColor(Core.team1LeatherColor);
		team1Chestplate.setItemMeta(meta1);
		ItemStack team2Chestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
		meta2 = (LeatherArmorMeta) team2Chestplate.getItemMeta();
		meta2.setColor(Core.team2LeatherColor);
		team2Chestplate.setItemMeta(meta2);
		
		ItemStack team1Leggings = new ItemStack(Material.LEATHER_LEGGINGS, 1);
		meta1 = (LeatherArmorMeta) team1Leggings.getItemMeta();
		meta1.setColor(Core.team1LeatherColor);
		team1Leggings.setItemMeta(meta1);
		ItemStack team2Leggings = new ItemStack(Material.LEATHER_LEGGINGS, 1);
		meta2 = (LeatherArmorMeta) team2Leggings.getItemMeta();
		meta2.setColor(Core.team2LeatherColor);
		team2Leggings.setItemMeta(meta2);
		
		ItemStack team1Boots = new ItemStack(Material.LEATHER_BOOTS, 1);
		meta1 = (LeatherArmorMeta) team1Boots.getItemMeta();
		meta1.setColor(Core.team1LeatherColor);
		team1Boots.setItemMeta(meta1);
		ItemStack team2Boots = new ItemStack(Material.LEATHER_BOOTS, 1);
		meta2 = (LeatherArmorMeta) team2Boots.getItemMeta();
		meta2.setColor(Core.team2LeatherColor);
		team2Boots.setItemMeta(meta2);
		
		int team = getPlayerTeam(player);
		if (team == 1){
			player.getInventory().setChestplate(team1Chestplate);
			player.getInventory().setLeggings(team1Leggings);
			player.getInventory().setBoots(team1Boots);
		}
		else if (team == 2){
			player.getInventory().setChestplate(team2Chestplate);
			player.getInventory().setLeggings(team2Leggings);
			player.getInventory().setBoots(team2Boots);
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
		return getPlayerTeam(player);
	}
	
	public int getPlayerTeam(Player player){
		if (player.hasMetadata("game" + Core.gameID + "team") == true){
			return player.getMetadata("game" + Core.gameID + "team").get(0).asInt();
		}
		else{
			return -1; // no team
		}
	}
	
	public int getPlayerKitID(Player player){
		if (player.hasMetadata("game" + Core.gameID + "kit") == true){
			return player.getMetadata("game" + Core.gameID + "kit").get(0).asInt();
		}
		else{
			return -1; // no kit
		}
	}
	
	public byte getPlayerWoolColor(Player player){
		int team = getPlayerTeam(player);
		if (team == 1){
			return Core.team1WoolColor;
		}
		else if (team == 2){
			return Core.team2WoolColor;
		}
		else{
			return 0;
		}
	}
	
	public int getAmountInPlayerInventory(Player player, Material material){
		// gets the amount of a specific material in the players inventory
		int amount = 0;
		for (ItemStack stack : player.getInventory().getContents()){
			if (stack == null
					|| stack.getType() == Material.AIR){
				continue; // skip
			}
			if (stack.getType() == material){
				amount += stack.getAmount();
			}
		}
		return amount;
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
		Location newLocation = player.getLocation().add(0.0, 5.0, 0.0);
		player.teleport(newLocation);
		
		// set the equipment the player will respawn with here
		givePlayerKit(player);
		givePlayerTeamArmor(player);
		
		// start the countdown timer thingy
		ItemStack[] keepArmor = player.getEquipment().getArmorContents();
		ItemStack[] keepInventory = player.getInventory().getContents();
		@SuppressWarnings("unused")
		RespawnTimerRunnable respawnTimer = new RespawnTimerRunnable(player, keepArmor, keepInventory);
		
		// send title "Respawning..."
		player.sendTitle(ChatColor.GOLD + "Respawning...", "");
	}
	
	public void createGlassBoxes(){
		createBox(Core.team1Spawn);
		createBox(Core.team2Spawn);
	}
	
	public void createBox(Location center){
		Location temp;
		
		int startX = center.getBlockX()-3;
		int startY = center.getBlockY();
		int startZ = center.getBlockZ()-3;
		int x, y, z;
		int endX = center.getBlockX()+3;
		int endY = center.getBlockY()+3;
		int endZ = center.getBlockZ()+3;
		
		for(y = startY; y <= endY; y++){
			for (x = startX; x <= endX; x++){
				for (z = startZ; z <= endZ; z++){
					if (y == endY){
						temp = new Location(center.getWorld(), x, y, z);
						temp.getBlock().setType(Material.GLASS);
					}
					else if (x == startX
							|| x == endX
							|| z == startZ
							|| z == endZ){
						temp = new Location(center.getWorld(), x, y, z);
						temp.getBlock().setType(Material.GLASS);
					}
				}
			}
		}
	}
	
	public void removeGlassBoxes(){
		removeBox(Core.team1Spawn);
		removeBox(Core.team2Spawn);
	}
	
	public void removeBox(Location center){
		Location temp;
		
		int startX = center.getBlockX()-3;
		int startY = center.getBlockY();
		int startZ = center.getBlockZ()-3;
		int x, y, z;
		int endX = center.getBlockX()+3;
		int endY = center.getBlockY()+3;
		int endZ = center.getBlockZ()+3;
		
		for(y = startY; y <= endY; y++){
			for (x = startX; x <= endX; x++){
				for (z = startZ; z <= endZ; z++){
					temp = new Location(center.getWorld(), x, y, z);
					if (temp.getBlock().getType() == Material.GLASS){
						temp.getBlock().setType(Material.AIR);
					}
				}
			}
		}
	}
	
	public void woolDust(byte woolColor, Location location, float radius, int amount){
		ParticleEffect.BLOCK_DUST.display(new ParticleEffect.BlockData(Material.WOOL, woolColor), radius, radius, radius, 0.0F, amount, location, 25); // 25 is block distance it can be seen
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