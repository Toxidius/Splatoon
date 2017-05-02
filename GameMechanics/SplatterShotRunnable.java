package Splatoon.GameMechanics;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import Splatoon.Main.Core;

public class SplatterShotRunnable implements Runnable{

	public int id;
	
	public SplatterShotRunnable() {
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.thisPlugin, this, 20, 20); // every 1 second give another arrow
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player.getGameMode() == GameMode.SURVIVAL){
				int kit = Core.gameManager.getPlayerKit(player);
				if (kit == 3){
					int team = Core.gameManager.getPlayerTeam(player);
					byte woolColor = Core.gameManager.getPlayerWoolColor(player);
					if (team == 1
							&& player.getLocation().add(0.0, -1.0, 0.0).getBlock().getType() == Material.WOOL
							&& player.getLocation().add(0.0, -1.0, 0.0).getBlock().getData() == woolColor){
						player.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 2));
					}
					else if (team == 2
							&& player.getLocation().add(0.0, -1.0, 0.0).getBlock().getType() == Material.WOOL
							&& player.getLocation().add(0.0, -1.0, 0.0).getBlock().getData() == woolColor){
						player.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 2));
					}
					else{
						player.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 1));
					}
				}
			}
		}
	}

}