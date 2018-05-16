package Splatoon.GameMechanics;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import Splatoon.Main.Core;

public class SplatterShotRunnable implements Runnable{

	private int maximumSnowballs = 16;
	public int id;
	
	public SplatterShotRunnable() {
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.thisPlugin, this, 20, 20); // every 1 second give another arrow
	}
	
	@Override
	public void run() {
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player.getGameMode() == GameMode.SURVIVAL){
				int kit = Core.gameManager.getPlayerKit(player);
				if (kit == 3){
					int currentAmount = Core.gameManager.getAmountInPlayerInventory(player, Material.SNOW_BALL);
					if (currentAmount == 15){
						player.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 1));
					}
					else if (currentAmount < maximumSnowballs){
						player.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 2));
					}
				}
			}
		}
	}

}