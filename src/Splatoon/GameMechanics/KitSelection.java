package Splatoon.GameMechanics;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import Splatoon.Main.Core;

public class KitSelection implements Listener{

	public KitSelection() {
		Core.registerListener(this);
	}
	
	@EventHandler
	public void onRightClickSign(PlayerInteractEvent e){
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK
				&& (e.getClickedBlock().getType() == Material.SIGN
						|| e.getClickedBlock().getType() == Material.SIGN_POST
						|| e.getClickedBlock().getType() == Material.WALL_SIGN)){
			Sign sign = (Sign) e.getClickedBlock().getState();
			
			if (sign.getLine(1).equals(ChatColor.BOLD + "Splat Roller")){
				e.getPlayer().getInventory().setHelmet(new ItemStack(Material.STICK, 1));
				e.getPlayer().sendMessage(ChatColor.GRAY + "Equipped Splat Roller Kit");
			}
			else if (sign.getLine(1).equals(ChatColor.BOLD + "Splat Charger")){
				e.getPlayer().getInventory().setHelmet(new ItemStack(Material.BOW, 1));
				e.getPlayer().sendMessage(ChatColor.GRAY + "Equipped Splat Charger Kit");
			}
			else if (sign.getLine(1).equals(ChatColor.BOLD + "Splattershot")){
				e.getPlayer().getInventory().setHelmet(new ItemStack(Material.SNOW_BALL, 64));
				e.getPlayer().sendMessage(ChatColor.GRAY + "Equipped Splattershot Kit");
			}
		}
	}
	
	@EventHandler
	public void onSignCreate(SignChangeEvent e){
		String[] lines = new String[4];
		lines = e.getLines();
		
		for (int i = 0; i < 4; i++){
			lines[i] = lines[i].replace("*b", ChatColor.BOLD.toString());
			lines[i] = lines[i].replace("*u", ChatColor.UNDERLINE.toString());
			lines[i] = lines[i].replace("*i", ChatColor.ITALIC.toString());
		}
		
		for (int i = 0; i < 4; i++){
			e.setLine(i, lines[i]);
		}
		
	}
}