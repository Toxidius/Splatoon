package Splatoon.GameMechanics;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import Splatoon.Main.Core;
import Splatoon.Main.GameStates.GameState;

public class SquidCheckerRunnable implements Runnable{

	private int id;
	
	public SquidCheckerRunnable() {
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.thisPlugin, this, 2L, 2L);
	}
	
	public void stop(){
		Bukkit.getScheduler().cancelTask(id);
	}

	@Override
	public void run() {
		if (Core.gameStarted == false || Core.gameState != GameState.Running){
			return; // game currently not running
		}
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player.getGameMode() == GameMode.SURVIVAL
					&& (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR)){
				if (player.hasPotionEffect(PotionEffectType.INVISIBILITY) == false){
					// startup a invisible squid watcher for this player
					@SuppressWarnings("unused")
					SquidWatcher watcher = new SquidWatcher(player);
				}
			}
		}
	}
}
