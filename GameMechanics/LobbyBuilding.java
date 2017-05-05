package Splatoon.GameMechanics;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import Splatoon.Main.Core;

public class LobbyBuilding implements Listener{
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChooseBlock(PlayerInteractEvent e){
		if (Core.gameStarted == false
				&& (e.getAction() == Action.RIGHT_CLICK_BLOCK
					|| e.getAction() == Action.LEFT_CLICK_BLOCK)){
			Block clickedBlock = e.getClickedBlock();
			if (clickedBlock.getRelative(BlockFace.DOWN).getType() == Material.EMERALD_BLOCK
					|| clickedBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() == Material.EMERALD_BLOCK){
				ItemStack item = null;
				if (clickedBlock.getType() == Material.LEAVES){
					item = new ItemStack(clickedBlock.getType(), 1, (short)0);
				}
				else{
					item = new ItemStack(clickedBlock.getType(), 1, (short)clickedBlock.getData());
				}
				e.getPlayer().getInventory().addItem(item);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerBuildInBuildArea(PlayerInteractEvent e){
		if (Core.gameStarted == false
				&& e.getAction() == Action.RIGHT_CLICK_BLOCK
				&& e.getPlayer().getItemInHand() != null
				&& e.getPlayer().getItemInHand().getType() != Material.AIR
				&& e.getPlayer().getItemInHand().getType().isBlock() == true){
			Location location = e.getClickedBlock().getRelative(e.getBlockFace()).getLocation();
			if (isInBuildArea(location) == true
					&& location.getBlock().getType() == Material.AIR
					&& isPlayerInBlock(location) == false){
				location.getBlock().setType(e.getPlayer().getItemInHand().getType(), false);
				location.getBlock().setData((byte)e.getPlayer().getItemInHand().getDurability(), false);
				location.getWorld().playEffect(location, Effect.STEP_SOUND, location.getBlock().getTypeId(), 10); // block break effect
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerBreakInBuildArea(PlayerInteractEvent e){
		if (Core.gameStarted == false
				&& e.getAction() == Action.LEFT_CLICK_BLOCK){
			
			Location location = e.getClickedBlock().getLocation();
			if (e.getClickedBlock().getType() != Material.GLASS
					&& e.getClickedBlock().getType() != Material.OBSIDIAN
					&& isInBuildArea(location) == true){
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
			if (temp.getBlock().getType() == Material.GLASS){
				return false; // players can't build above glass region
			}
			else if (temp.getBlock().getType() == Material.OBSIDIAN){
				return true; // 
			}
			y--;
		}
		return false;
	}
	
	public boolean isPlayerInBlock(Location locationToPlaceBlock){
		boolean playerFound = false;
		
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player != null
					&& player.isOnline()
					&& player.getWorld().equals(locationToPlaceBlock.getWorld())
					&& player.getLocation().getBlockX() == locationToPlaceBlock.getBlockX()
					&& player.getLocation().getBlockZ() == locationToPlaceBlock.getBlockZ()
					&& (player.getLocation().getBlockY() == locationToPlaceBlock.getBlockY() 
						|| player.getLocation().getBlockY() == locationToPlaceBlock.getBlockY()-1) ){
				playerFound = true;
			}
		}

		return playerFound;
	}
		
}