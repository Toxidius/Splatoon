package Splatoon.GameMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import Splatoon.Main.Core;
import Splatoon.Main.GameStates.GameState;

public class PlayerChat implements Listener{
	
	public PlayerChat(){
		Core.registerListener(this);
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e){
		e.setCancelled(true); // complete overriding of message behavior
		
		if (Core.gameStarted == false){
			// pregame message
			Bukkit.getServer().broadcastMessage(ChatColor.GOLD + e.getPlayer().getName() + ChatColor.WHITE + " " + e.getMessage());
		}
		else if (Core.gameState == GameState.Ending){
			// game ending (global chat)
			String playerName = getPlayerColor(e.getPlayer()) + e.getPlayer().getName();
			
			Bukkit.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "!" + ChatColor.WHITE + "] " 
					+ playerName + " " + ChatColor.WHITE + e.getMessage()); // sends out the player's message removing the first ! character
		}
		else{
			// game message
			int playerTeam = Core.gameManager.getPlayerTeam(e.getPlayer());
			
			String playerName = getPlayerColor(e.getPlayer()) + e.getPlayer().getName();
			
			if (e.getMessage().startsWith("!")){
				// global message
				Bukkit.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "!" + ChatColor.WHITE + "] " 
						+ playerName + " " + ChatColor.WHITE + e.getMessage().substring(1)); // sends out the player's message removing the first ! character
			}
			else{
				// team message
				String message = ChatColor.WHITE + "[" + ChatColor.DARK_PURPLE + "Team" + ChatColor.WHITE + "] " 
						+ playerName + " " + ChatColor.WHITE + e.getMessage();
				Core.gameManager.teamMessage(playerTeam, message);
			}
		}
	}
	
	public ChatColor getPlayerColor(Player player){
		int playerTeam = Core.gameManager.getPlayerTeam(player);
		
		if (playerTeam == 1){
			return Core.team1Color;
		}
		else if (playerTeam == 2){
			return Core.team2Color;
		}
		else if (playerTeam == -1){
			return ChatColor.GRAY;
		}
		else{
			return ChatColor.GRAY;
		}
	}
}