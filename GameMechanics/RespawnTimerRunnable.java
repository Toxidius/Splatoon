package Splatoon.GameMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import Splatoon.Main.Core;

public class RespawnTimerRunnable implements Runnable{

	private Player player;
	private String playerName;
	private int calls;
	private int id;
	private int respawnTime;
	private ItemStack[] keepArmor;
	private ItemStack[] keepInventory;
	private boolean playerLoggedOut;
	private boolean teleportSuccess;
	
	public RespawnTimerRunnable(Player player, ItemStack[] keepArmor, ItemStack[] keepInventory) {
		this.player = player;
		this.keepArmor = keepArmor;
		this.keepInventory = keepInventory;
		playerName = player.getName();
		calls = 0;
		respawnTime = 5;
		playerLoggedOut = false;
		teleportSuccess = false;
		
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.thisPlugin, this, 20L, 20L); // run every second
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		if (calls >= respawnTime){
			// end runnable and respawn the player if they're still logged in -- otherwise keep the runnable going until they log back in
			if (player.isOnline() && playerLoggedOut && !teleportSuccess){
				// player logged out mid respawn
				playerRespawn();
				return;
			}
			else if (player.isOnline()){
				// player never logged out
				playerRespawn();
				return;
			}
			else{
				// player just logged out, so remember this so the runnable doesn't end
				playerLoggedOut = true;
			}
			
		}
		else if (calls == 1){
			// set the players keep inventory
			player.getInventory().setArmorContents(keepArmor);
			player.getInventory().setContents(keepInventory);
		}
		
		calls++;
		if (player.isOnline() && playerLoggedOut == false){
			player.sendTitle("", ChatColor.GOLD + "" + (respawnTime-calls) + "...");
		}
		
	}
	
	@SuppressWarnings("deprecation")
	public void playerRespawn(){
		player = Bukkit.getPlayer(playerName);
		int team = Core.gameManager.getPlayerTeam(player);
		player.setGameMode(GameMode.SURVIVAL);
		player.setHealth(20.0);
		player.setFoodLevel(20);
		player.setSaturation(20);
		player.setFallDistance(0);
		player.setVelocity(new Vector(0.0, 0.0, 0.0));
		player.getInventory().setArmorContents(keepArmor);
		player.getInventory().setContents(keepInventory);
		player.sendTitle("", "");
		player.resetTitle();
		
		if (team == 1){
			teleportSuccess = player.teleport(Core.team1Spawn);
		}
		else if (team == 2){
			teleportSuccess = player.teleport(Core.team2Spawn);
		}
		
		if (teleportSuccess == true){
			Bukkit.getScheduler().cancelTask(id);
		}
	}
}