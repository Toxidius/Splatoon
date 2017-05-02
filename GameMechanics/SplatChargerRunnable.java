package Splatoon.GameMechanics;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import Splatoon.Main.Core;

public class SplatChargerRunnable implements Runnable{

	public int id;
	
	public SplatChargerRunnable() {
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.thisPlugin, this, 80, 80); // every 4 seconds give arrow
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player.getGameMode() == GameMode.SURVIVAL){
				int kit = Core.gameManager.getPlayerKit(player);
				if (kit == 2){
					int team = Core.gameManager.getPlayerTeam(player);
					byte woolColor = Core.gameManager.getPlayerWoolColor(player);
					if (team == 1
							&& player.getLocation().add(0.0, -1.0, 0.0).getBlock().getType() == Material.WOOL
							&& player.getLocation().add(0.0, -1.0, 0.0).getBlock().getData() == woolColor){
						player.getInventory().addItem(new ItemStack(Material.ARROW, 2));
					}
					else if (team == 2
							&& player.getLocation().add(0.0, -1.0, 0.0).getBlock().getType() == Material.WOOL
							&& player.getLocation().add(0.0, -1.0, 0.0).getBlock().getData() == woolColor){
						player.getInventory().addItem(new ItemStack(Material.ARROW, 2));
					}
					else{
						player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
					}
				}
			}
		}
	}

}