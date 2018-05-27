package Splatoon.GameMechanics;

import org.bukkit.GameMode;
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
		if (Core.gameStarted == false){
			e.getPlayer().teleport(Core.lobbySpawn);
			e.getPlayer().setGameMode(GameMode.SURVIVAL);
			Core.gameManager.clearInventory(e.getPlayer());
		}
		else{
			// game in progress
			int playerTeam = Core.gameManager.getPlayerTeam(e.getPlayer());
			if (playerTeam == -1){
				// has no team, set them in spectator mode
				e.getPlayer().setGameMode(GameMode.SPECTATOR);
				e.getPlayer().teleport(Core.spectatorSpawn);
				e.getPlayer().setScoreboard(Core.gameManager.scoreboardManager.scoreboard);
			}
			else{
				// they are already in the game and have a team, teleport to their team's spawn
				if (playerTeam == 1){
					e.getPlayer().teleport(Core.team1Spawn);
				}
				else if (playerTeam == 2){
					e.getPlayer().teleport(Core.team2Spawn);
				}
				e.getPlayer().setScoreboard(Core.gameManager.scoreboardManager.scoreboard);
			}
		}
	}
}