package Splatoon.GameMechanics;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import Splatoon.Main.Core;

public class SplatChargerRunnable implements Runnable{

	private int maximumArrows = 3;
	public int id;
	
	public SplatChargerRunnable() {
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.thisPlugin, this, 80, 80); // every 4 seconds give arrow
	}
	
	@Override
	public void run() {
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player.getGameMode() == GameMode.SURVIVAL){
				int kit = Core.gameManager.getPlayerKit(player);
				if (kit == 2){
					int currentAmount = Core.gameManager.getAmountInPlayerInventory(player, Material.ARROW);
					if (currentAmount < maximumArrows){
						player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
					}
				}
			}
		}
	}

}