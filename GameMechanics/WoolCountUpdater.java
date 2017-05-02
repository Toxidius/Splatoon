package Splatoon.GameMechanics;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import Splatoon.Main.Core;

public class WoolCountUpdater implements Runnable{

	public int id;
	private int startX, startY, startZ;
	private int x, y, z;
	private int endX, endY, endZ;
	private Location temp;
	
	public WoolCountUpdater() {
		if (Core.woolRegionCorner1.getBlockX() <= Core.woolRegionCorner2.getBlockX()){
			startX = Core.woolRegionCorner1.getBlockX();
			endX = Core.woolRegionCorner2.getBlockX();
		}
		else{
			startX = Core.woolRegionCorner2.getBlockX();
			endX = Core.woolRegionCorner1.getBlockX();
		}
		if (Core.woolRegionCorner1.getBlockZ() <= Core.woolRegionCorner2.getBlockZ()){
			startZ = Core.woolRegionCorner1.getBlockZ();
			endZ = Core.woolRegionCorner2.getBlockZ();
		}
		else{
			startZ = Core.woolRegionCorner2.getBlockZ();
			endZ = Core.woolRegionCorner1.getBlockZ();
		}
		if (Core.woolRegionCorner1.getBlockY() <= Core.woolRegionCorner2.getBlockY()){
			startY = Core.woolRegionCorner1.getBlockY();
			endY = Core.woolRegionCorner2.getBlockY();
		}
		else{
			startY = Core.woolRegionCorner2.getBlockY();
			endY = Core.woolRegionCorner1.getBlockY();
		}
		
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.thisPlugin, this, 40, 40); // runs every 2 seconds
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void run() {	
		// updates the wool block counts every 2 seconds
		byte woolData;
		int team1Blocks = 0;
		int team2Blocks = 0;
		
		for (y = startY; y <= endY; y++){
			for (x = startX; x <= endX; x++){
				for (z = startZ; z <= endZ; z++){
					temp = new Location(Core.gameWorld, x, y, z);
					if (temp.getBlock().getType() == Material.WOOL){
						woolData = temp.getBlock().getData();
						if (woolData == Core.team1WoolColor){
							team1Blocks++;
						}
						else if (woolData == Core.team2WoolColor){
							team2Blocks++;
						}
					}
				}
			}
		}
		
		Core.gameManager.team1Blocks = team1Blocks;
		Core.gameManager.team2Blocks = team2Blocks;
	}
}