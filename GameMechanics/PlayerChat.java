package Splatoon.GameMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import Splatoon.Main.Core;

public class PlayerChat implements Listener{
	
	public PlayerChat() {
		
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e){
		e.setCancelled(true); // complete overriding of message behavior
		
		if (Core.gameStarted == false){
			Bukkit.getServer().broadcastMessage(ChatColor.GOLD + e.getPlayer().getName() + ChatColor.WHITE + " " + e.getMessage());
		}
		else{
			// TODO colored name messages
		}
	}
}