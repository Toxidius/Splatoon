package Splatoon.GameMechanics;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import Splatoon.Main.Core;

public class SplatterShot implements Listener{

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onSnowballHitWall(ProjectileHitEvent e){
		if (Core.gameStarted == true
				&& e.getEntity() instanceof Snowball
				&& e.getEntity().getShooter() instanceof Player){
			Player player = (Player) e.getEntity().getShooter();
			byte woolColor = Core.gameManager.getPlayerWoolColor(player);
			Location center = e.getEntity().getLocation();
			Location temp;
			
			int startX = center.getBlockX()-2;
			int startY = center.getBlockY()-2;
			int startZ = center.getBlockZ()-2;
			int x, y, z;
			int endX = center.getBlockX()+2;
			int endY = center.getBlockY();
			int endZ = center.getBlockZ()+2;
			
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
	
	@EventHandler
	public void onPlayerDamageBySnowball(EntityDamageByEntityEvent e){
		if (Core.gameStarted == true
				&& e.getEntity() instanceof Player
				&& e.getDamager() instanceof Snowball){
			Snowball snowball = (Snowball) e.getDamager();
			if (snowball.getShooter() instanceof Player){
				Player player = (Player) e.getEntity();
				Player shooter = (Player) snowball.getShooter();
				int playerTeam = Core.gameManager.getPlayerTeam(player);
				int shooterTeam = Core.gameManager.getPlayerTeam(shooter);
				if (playerTeam == shooterTeam){
					e.setCancelled(true);
				}
				else{
					e.setDamage(10.0); // 3 hits will kill a player
				}
			}
		}
	}
	
}