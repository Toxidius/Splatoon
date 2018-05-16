package Splatoon.GameMechanics;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;

import Splatoon.Main.Core;

public class SplatChargerArrowWatcher implements Runnable{

	private Arrow arrow;
	private byte woolColor;
	private World world;
	private Location temp;
	private int id;
	
	public SplatChargerArrowWatcher(Arrow arrow, byte woolColor) {
		this.arrow = arrow;
		this.woolColor = woolColor;
		world = arrow.getWorld();
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.thisPlugin, this, 1, 1); // every tick
	}
	
	@Override
	public void run() {
		if (arrow.isDead()
				|| arrow.isOnGround()){
			arrow.remove();
			Bukkit.getScheduler().cancelTask(id);
			return;
		}
		
		Location arrowLocation = arrow.getLocation();
		for (int x = arrowLocation.getBlockX()-1; x <= arrowLocation.getBlockX()+1; x++){
			for (int z = arrowLocation.getBlockZ()-1; z <= arrowLocation.getBlockZ()+1; z++){
				colorizeAllWoolBlocks(x, arrowLocation.getY(), z);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void colorizeAllWoolBlocks(double x, double y, double z){
		while(y > 0){
			temp = new Location(world, x, y, z);
			if (temp.getBlock().getType() == Material.WOOL
					&& temp.getBlock().getData() != woolColor){
				temp.getBlock().setData(woolColor); // turn wool's color to the players team color
			}
			y--;
		}
	}

}
