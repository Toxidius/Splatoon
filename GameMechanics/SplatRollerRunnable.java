package Splatoon.GameMechanics;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
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
					&& player.getItemInHand().getType() == Material.STICK){
				// this player is a splat roller
				byte woolColor = 0;
				int team = Core.gameManager.getPlayerTeam(player);
				if (team == 1){
					woolColor = Core.team1WoolColor;
				}
				else if (team == 2){
					woolColor = Core.team2WoolColor;
				}
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
							}
						}
					}
				}
			}
		}
	}
}