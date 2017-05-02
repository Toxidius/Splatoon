package Splatoon.GameManagement;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import Splatoon.Main.Core;
import Splatoon.Main.GameStates.GameState;

public class ScoreboardManager implements Runnable{
	
	public Scoreboard scoreboard;
	public Scoreboard emptyScoreboard;
	
	private Team team1;
	private Team team2;
	private Objective sidebarObjective;
	private Objective healthObjective;
	private Objective killsObjective;
	private Score line1;
	private Score line2;
	private Score line3;
	private Score line4;
	
	public int time = 0;
	public int peaceTime = 5 * 60; // 5 mins
	public int regenTime = 60 * 60; // 60 mins
	
	public ScoreboardManager(){
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		emptyScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
	}
	
	public void setupScoreboard(){
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		emptyScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		
		team1 = scoreboard.registerNewTeam("Team1");
		team1.setAllowFriendlyFire(false);
		team1.setPrefix(Core.team1Color + "");
		
		team2 = scoreboard.registerNewTeam("Team2");
		team2.setAllowFriendlyFire(false);
		team2.setPrefix(Core.team2Color + "");
		
		healthObjective = scoreboard.registerNewObjective("Health", "health");
		healthObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);
		
		killsObjective = scoreboard.registerNewObjective("Kills", "playerKillCount");
		killsObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		
		// reset the current time(calls) for scoreboard runnable
		time = 0;
	}
	
	public void addPlayerToTeam(String playerName, int team){
		if (team == 1){
			team1.addEntry(playerName);
		}
		else if (team == 2){
			team2.addEntry(playerName);
		}
	}
	
	public void removePlayerFromTeam(String playerName, int team){
		if (team == 1){
			team1.removeEntry(playerName);
		}
		else if (team == 2){
			team2.removeEntry(playerName);
		}
	}
	
	public void setupSidebar(String title, String line1, String line2, String line3, String line4){
		sidebarObjective = scoreboard.registerNewObjective("gameObjective", "dummy");
		sidebarObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
		sidebarObjective.setDisplayName(title);
		
		this.line1 = sidebarObjective.getScore(line1);
		this.line1.setScore(4);
		this.line2 = sidebarObjective.getScore(line2);
		this.line2.setScore(3);
		if (line3.equals("") == false){
			this.line3 = sidebarObjective.getScore(line3);
			this.line3.setScore(2);
		}
		if (line4.equals("") == false){
			this.line4 = sidebarObjective.getScore(line4);
			this.line4.setScore(1);
		}	
	}
	
	public void updateSidebar(String title, String line1, String line2, String line3, String line4){
		scoreboard.clearSlot(DisplaySlot.SIDEBAR);
		sidebarObjective.unregister();
		sidebarObjective = scoreboard.registerNewObjective("gameObjective", "dummy");
		sidebarObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
		sidebarObjective.setDisplayName(title); // ChatColor.GOLD + "Paint Percent";
		
		this.line1 = sidebarObjective.getScore(line1); // time remaining
		this.line1.setScore(4);
		this.line2 = sidebarObjective.getScore(line2); // purple score
		this.line2.setScore(3);
		if (line3.length() > 1){
			this.line3 = sidebarObjective.getScore(line3); // green score
			this.line3.setScore(2);
		}
		if (line4.length() > 1){
			this.line4 = sidebarObjective.getScore(line4);
			this.line4.setScore(1);
		}
	}
	
	public void refresh(){
		updateScoreboard(); // refreshes the scoreboard
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		// this runnable is called every second and is in charge of updating the scoreboard and controling the flow of the game
		
		if (Core.gameState == GameState.Ending){
			return; // do nothing here the game is already ending
		}
		
		updateScoreboard();
		
		if (Core.gameManager.warmupTimeRemaining != -1){
			// currently in warmup period
			for (Player player : Bukkit.getOnlinePlayers()){
				if (Core.gameManager.warmupTimeRemaining == 0){
					player.sendTitle(ChatColor.BOLD + "" + ChatColor.GREEN + "Go!", "");
				}
				else{
					player.sendTitle(ChatColor.GOLD + "" + Core.gameManager.warmupTimeRemaining, "");
				}
			}
			
			if (Core.gameManager.warmupTimeRemaining == 5){
				// setup glass boxes
				Core.gameManager.createGlassBoxes();
			}
			else if (Core.gameManager.warmupTimeRemaining == 0){
				// remove glass boxes
				Core.gameManager.removeGlassBoxes();
			}
			
			// decrement the warmup time
			Core.gameManager.warmupTimeRemaining--;
		}
		else if (Core.gameManager.timeRemaining != 0){
			// currently in game play period
			Core.gameManager.timeRemaining--;
		}
		else{
			// round is currently over
			
			// check who won here
			int winningTeam = -1;
			if (Core.gameManager.team1Blocks > Core.gameManager.team2Blocks){
				winningTeam = 1;
			}
			else if (Core.gameManager.team2Blocks > Core.gameManager.team1Blocks){
				winningTeam = 2;
			}
			else{
				winningTeam = 0; // tie
			}
			
			// start game end sequence
			Core.gameManager.endGameInitiate(winningTeam);
		}
	}
	
	public void updateScoreboard(){
		String title, line1, line2, line3, line4;
		title = ChatColor.GOLD + "Paint Percent";
		line1 = ChatColor.WHITE + "Time Remaining: " + Core.gameManager.timeRemaining;
		line2 = Core.team1Color + "Purple: " + ChatColor.WHITE + Core.gameManager.team1Blocks;
		line3 = Core.team2Color + "Green: " + ChatColor.WHITE + Core.gameManager.team2Blocks;
		line4 = "";
		
		updateSidebar(title, line1, line2, line3, line4);
		time++;
	}
	
	public boolean adminOnline(){
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player.isOp()){
				return true;
			}
		}
		return false;
	}

}