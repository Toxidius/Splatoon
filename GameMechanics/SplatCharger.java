package Splatoon.GameMechanics;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import Splatoon.Main.Core;

public class SplatCharger implements Listener{
	
	@EventHandler
	public void onArrowShoot(ProjectileLaunchEvent e){
		if (Core.gameStarted == true
				&& e.getEntity() instanceof Arrow
				&& e.getEntity().getShooter() instanceof Player){
			Arrow arrow = (Arrow) e.getEntity();
			Player player = (Player) e.getEntity().getShooter();
			byte woolColor = Core.gameManager.getPlayerWoolColor(player);
			
			// start a watcher for the arrow
			// this watcher colorizes the wool blocks below the arrow's path
			@SuppressWarnings("unused")
			SplatChargerArrowWatcher watcher = new SplatChargerArrowWatcher(arrow, woolColor);
		}
	}
	
	@EventHandler
	public void onPlayerDamageByArrow(EntityDamageByEntityEvent e){
		if (Core.gameStarted == true
				&& e.getEntity() instanceof Player
				&& e.getDamager() instanceof Arrow){
			Arrow arrow = (Arrow) e.getDamager();
			if (arrow.getShooter() instanceof Player){
				Player player = (Player) e.getEntity();
				Player shooter = (Player) arrow.getShooter();
				int playerTeam = Core.gameManager.getPlayerTeam(player);
				int shooterTeam = Core.gameManager.getPlayerTeam(shooter);
				if (playerTeam == shooterTeam){
					e.setCancelled(true);
				}
				else{
					e.setDamage(e.getDamage()*2.0); // full powered shot should kill a player in 1 hit
				}
			}
		}
	}
	
}