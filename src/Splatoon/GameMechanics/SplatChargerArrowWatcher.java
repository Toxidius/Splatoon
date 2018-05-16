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
		for (double x = arrowLocation.getBlockX()-0.5; x <= arrowLocation.getBlockX()+0.5; x++){
			for (double z = arrowLocation.getBlockZ()-0.5; z <= arrowLocation.getBlockZ()+0.5; z++){
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
				Core.gameManager.woolDust(woolColor, temp.getBlock().getLocation().add(0.0, 1.0, 0.0), 0.5F, 50);
			}
			y--;
		}
	}

}
