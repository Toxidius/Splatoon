package Splatoon.GameMechanics;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import Splatoon.Main.Core;

public class WoolClimbing implements Runnable{

	private byte playerWoolColor;
	private byte infrontWoolColor;
	private Vector upVelocity;
	public int id;
	
	public WoolClimbing() {
		upVelocity = new Vector(0.0, 0.4, 0.0);
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.thisPlugin, this, 2, 2); // runs every 2 ticks
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player.getGameMode() == GameMode.SURVIVAL){
				Location infront = player.getLocation().add(player.getLocation().getDirection().multiply(0.75));
				infront.setY(player.getLocation().getY()+0.5);
				Block infrontBlock = infront.getBlock();
				if (infrontBlock.getType() == Material.WOOL){
					playerWoolColor = Core.gameManager.getPlayerWoolColor(player);
					infrontWoolColor = infrontBlock.getData();
					if (playerWoolColor == infrontWoolColor){
						player.setVelocity(upVelocity);
					}
				}
			}
		}
	}
}