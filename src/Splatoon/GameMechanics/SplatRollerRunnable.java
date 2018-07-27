package Splatoon.GameMechanics;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import Splatoon.Main.Core;

public class SplatRollerRunnable implements Runnable{

	public int id;
	
	public SplatRollerRunnable() {
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.thisPlugin, this, 2, 2); // delay, interval
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player.getGameMode() == GameMode.SURVIVAL
					&& player.getItemInHand() != null
					&& player.getItemInHand().getType() == Material.STICK){
				// this player is a splat roller
				byte woolColor = 0;
				int playerTeam = Core.gameManager.getPlayerTeam(player);
				if (playerTeam == 1){
					woolColor = Core.team1WoolColor;
				}
				else if (playerTeam == 2){
					woolColor = Core.team2WoolColor;
				}
				
				// wool color change checks
				Location center = player.getLocation();
				Location temp;
				
				int startX = center.getBlockX()-1;
				int startY = center.getBlockY()-1;
				int startZ = center.getBlockZ()-1;
				int x, y, z;
				int endX = center.getBlockX()+1;
				int endY = center.getBlockY();
				int endZ = center.getBlockZ()+1;
				
				for (y = startY; y <= endY; y++){
					for (x = startX; x <= endX; x++){
						for (z = startZ; z <= endZ; z++){
							temp = new Location(center.getWorld(), x, y, z);
							if (temp.getBlock().getType() == Material.WOOL
									&& temp.getBlock().getData() != woolColor){
								temp.getBlock().setData(woolColor);
								
								// wool particle effect
								Core.gameManager.woolDust(woolColor, temp.getBlock().getLocation().add(0.0, 1.0, 0.0), 0.5F, 50);
							}
						}
					}
				}
				
				// kill nearby opponents
				int otherPlayerTeam;
				int otherPlayerKit;
				for (Entity entity : player.getNearbyEntities(2.0, 2.0, 2.0)){
					if (entity instanceof Player){
						Player otherPlayer = (Player) entity;
						if (otherPlayer.isDead() == false
								&& otherPlayer.getGameMode() == GameMode.SURVIVAL){
							otherPlayerTeam = Core.gameManager.getPlayerTeam(otherPlayer);
							otherPlayerKit = Core.gameManager.getPlayerKitID(otherPlayer);
							
							if (otherPlayerTeam != playerTeam){
								// these two player are on opposite teams perfrom steam roller damage check operations
								// check if the other player within this player's range is also a splat roller player
								if (otherPlayerKit == 1
										&& otherPlayer.getItemInHand() != null
										&& otherPlayer.getItemInHand().getType() == Material.STICK){
									// the other player is splat roller too and both have the stick equiped, thus, kill the other player too
									player.damage(30.0, otherPlayer);
								}
								// kill the other player by default
								otherPlayer.damage(30.0, player); // this player isn't on the same team, and within range. they get killed by the splat roller
							}
						}
					}
				}
			}
		}
	}
}