package Splatoon.GameMechanics;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import Splatoon.Main.Core;

public class LobbyBuilding implements Listener{

	@EventHandler
	public void onPlayerChooseBlock(PlayerInteractEvent e){
		if (Core.gameStarted == false
				&& (e.getAction() == Action.RIGHT_CLICK_BLOCK
					|| e.getAction() == Action.LEFT_CLICK_BLOCK)){
			Block clickedBlock = e.getClickedBlock();
			if (clickedBlock.getRelative(BlockFace.DOWN).getType() == Material.EMERALD_BLOCK
					|| clickedBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() == Material.EMERALD_BLOCK){
				ItemStack item = new ItemStack(clickedBlock.getType(), 1);
				e.getPlayer().getInventory().addItem(item);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerBuildInBuildArea(PlayerInteractEvent e){
		if (Core.gameStarted == false
				&& e.getAction() == Action.RIGHT_CLICK_BLOCK
				&& e.getPlayer().getItemInHand() != null
				&& e.getPlayer().getItemInHand().getType() != Material.AIR
				&& e.getPlayer().getItemInHand().getType().isBlock() == true){
			Location location = e.getClickedBlock().getRelative(e.getBlockFace()).getLocation();
			if (isInBuildArea(location) == true){
				location.getBlock().setType(e.getPlayer().getItemInHand().getType(), false);
				location.getBlock().setData((byte)0, false);
				location.getWorld().playEffect(location, Effect.STEP_SOUND, location.getBlock().getTypeId(), 10); // block break effect
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerBreakInBuildArea(PlayerInteractEvent e){
		if (Core.gameStarted == false
				&& e.getAction() == Action.LEFT_CLICK_BLOCK){
			Location location = e.getClickedBlock().getLocation();
			if (isInBuildArea(location) == true
					&& e.getClickedBlock().getType() != Material.OBSIDIAN
					&& e.getClickedBlock().getType() != Material.GLASS){
				location.getWorld().playEffect(location, Effect.STEP_SOUND, location.getBlock().getTypeId(), 10); // block break effect
				location.getBlock().setType(Material.AIR);
			}
		}
	}

	public boolean isInBuildArea(Location locationToPlaceBlock){
		Location temp;
		
		int y = locationToPlaceBlock.getBlockY();
		while (y > 0){
			temp = new Location(locationToPlaceBlock.getWorld(), locationToPlaceBlock.getBlockX(), y, locationToPlaceBlock.getBlockZ());
			if (temp.getBlock().getType() == Material.OBSIDIAN){
				return true;
			}
			y--;
		}
		return false;
	}
		
}