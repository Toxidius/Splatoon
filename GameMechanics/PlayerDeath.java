package Splatoon.GameMechanics;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import Splatoon.Main.Core;

public class PlayerDeath implements Listener{

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e){
		e.getDrops().clear(); // clear out all items in the drops
		Core.gameManager.clearInventory(e.getEntity()); // clear player's inventory
		Player player = e.getEntity();
		player.setHealth(20); // auto-respawn/cancel-respawn the player
		
		if (player.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.VOID){
			Location newLocation = player.getLocation();
			newLocation.setY(15);
			player.teleport(newLocation);
		}
		
		Core.gameManager.simulateDeath(player);
		e.setDeathMessage(ChatColor.GRAY + e.getDeathMessage());
	}
}