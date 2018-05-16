package Splatoon.GameMechanics;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import Splatoon.Main.Core;

public class SpawnInvulerability implements Listener{
	
	@EventHandler
	public void onEntityDamageNearSpawn(EntityDamageByEntityEvent e){
		if (Core.gameStarted == true
				&& e.getEntity() instanceof Player){
			Player player = (Player) e.getEntity();
			int team = Core.gameManager.getPlayerTeam(player);
			double distance;
			if (team == 1){
				distance = player.getLocation().distance(Core.team1Spawn);
				if (distance <= 3.0){
					e.setCancelled(true); // no damage when the player is at spawn
				}
			}
			else if (team == 2){
				distance = player.getLocation().distance(Core.team2Spawn);
				if (distance <= 3.0){
					e.setCancelled(true); // no damage when the player is at spawn
				}
			}
		}
	}
}