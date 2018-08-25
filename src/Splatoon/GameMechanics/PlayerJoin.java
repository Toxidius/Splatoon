package Splatoon.GameMechanics;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import Splatoon.Main.Core;

public class PlayerJoin implements Listener{
	
	public PlayerJoin(){
		Core.registerListener(this);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		Player player = e.getPlayer();
		if (Core.gameStarted == false){
			player.teleport(Core.lobbySpawn);
			player.setGameMode(GameMode.SURVIVAL);
			player.setScoreboard(Core.gameManager.scoreboardManager.emptyScoreboard);
			Core.gameManager.clearInventory(e.getPlayer());
		}
		else{
			// game in progress
			int playerTeam = Core.gameManager.getPlayerTeam(player);
			if (playerTeam == -1){
				// has no team, set them in spectator mode
				Core.gameManager.clearInventory(player);
				player.setGameMode(GameMode.SPECTATOR);
				player.teleport(Core.spectatorSpawn);
				player.setScoreboard(Core.gameManager.scoreboardManager.scoreboard);
			}
			else{
				// they are already in the game and have a team, teleport to their team's spawn
				if (playerTeam == 1){
					player.teleport(Core.team1Spawn);
				}
				else if (playerTeam == 2){
					player.teleport(Core.team2Spawn);
				}
				player.setScoreboard(Core.gameManager.scoreboardManager.scoreboard);
			}
		}
	}
}