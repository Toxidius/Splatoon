package Splatoon.GameMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import Splatoon.Main.Core;

public class PlayerDeath implements Listener{

	public PlayerDeath() {
		Core.registerListener(this);
	}
	
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
		
		// player name colorizing
		int spaceIndex = e.getDeathMessage().indexOf(" ") + 1;
		String cause = e.getDeathMessage().substring(spaceIndex); // returns the text after the players name (cause of death)
		
		Player otherPlayer;
		for (String string : cause.split(" ")){
			if (isStringAPlayerName(string) == true){
				otherPlayer = Bukkit.getPlayer(string);
				if (otherPlayer != null){
					int otherPlayerTeam = Core.gameManager.getPlayerTeam(otherPlayer);
					if (otherPlayerTeam == 1){
						cause = cause.replace(string, Core.team1Color + otherPlayer.getName() + ChatColor.GRAY);
					}
					else if (otherPlayerTeam == 2){
						cause = cause.replace(string, Core.team2Color + otherPlayer.getName() + ChatColor.GRAY);
					}
				}
			}
		}
		
		int team = Core.gameManager.getPlayerTeam(player);
		if (team == 1){
			e.setDeathMessage(Core.team1Color + player.getName() + " " + ChatColor.GRAY + cause);
		}
		else if (team == 2){
			e.setDeathMessage(Core.team2Color + player.getName() + " " + ChatColor.GRAY + cause);
		}
		else{
			e.setDeathMessage(ChatColor.GRAY + player.getName() + " " + ChatColor.GRAY + cause);
		}
	}
	
	public boolean isStringAPlayerName(String input){
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player.getName().equals(input)){
				return true;
			}
		}
		return false;
	}
}