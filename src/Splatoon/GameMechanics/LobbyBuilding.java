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
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import Splatoon.Main.Core;

public class LobbyBuilding implements Listener{
	
	@SuppressWarnings({"deprecation"})
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChooseBlock(PlayerInteractEvent e){
		// Bukkit.getServer().broadcastMessage("" + ((Core)Bukkit.getPluginManager().getPlugin("Splatoon")).gameID );
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
	
	@EventHandler(priority = EventPriority.HIGHEST) // gets last say in whether it's canceled or not
	public void onPlayerBuildInBuildArea(BlockPlaceEvent e){
		if (Core.gameStarted == false
				&& e.getPlayer().getItemInHand() != null
				&& e.getPlayer().getItemInHand().getType() != Material.AIR
				&& e.getPlayer().getItemInHand().getType() != Material.GLASS
				&& e.getPlayer().getItemInHand().getType() != Material.OBSIDIAN){
			Location location = e.getBlock().getLocation();
			if (isInBuildArea(location) == true){
				e.setCancelled(false); // allow this block to be placed
				
				Player player = e.getPlayer();
				ItemStack stack = e.getPlayer().getItemInHand().clone();
				Bukkit.getScheduler().scheduleSyncDelayedTask(Core.thisPlugin, new Runnable(){
					@Override
					public void run() {
						player.setItemInHand(stack); // reset the item in the players hand so that they keep it when placing blocks
					}
				}, 1); // 1 tick later
				
				//location.getBlock().setType(e.getPlayer().getItemInHand().getType(), false);
				//location.getBlock().setData((byte)e.getPlayer().getItemInHand().getDurability(), false);
				//location.getWorld().playEffect(location, Effect.STEP_SOUND, location.getBlock().getTypeId(), 10); // block break effect
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerBuildInClassicBuildArea(PlayerInteractEvent e){
		if (Core.gameStarted == false
				&& e.getAction() == Action.RIGHT_CLICK_BLOCK
				&& e.getPlayer().getItemInHand() != null
				&& e.getPlayer().getItemInHand().getType() != Material.AIR
				&& e.getPlayer().getItemInHand().getType() != Material.GLASS
				&& e.getPlayer().getItemInHand().getType() != Material.OBSIDIAN
				&& e.getPlayer().getItemInHand().getType().isBlock()){
			Location location = e.getClickedBlock().getRelative(e.getBlockFace()).getLocation();
			if (isInClassicBuildArea(location) == true
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
					&& e.getClickedBlock().getType() != Material.NETHER_BRICK
					&& (isInBuildArea(location) == true
							|| isInClassicBuildArea(location) == true)){
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
			else if (temp.getBlock().getType() == Material.OBSIDIAN
					&& temp.getBlock().getRelative(BlockFace.DOWN).getType() == Material.BEDROCK){
				return true; // is in build area
			}
			y--;
		}
		return false;
	}
	
	public boolean isInClassicBuildArea(Location locationToPlaceBlock){
		Location temp;
		
		int y = locationToPlaceBlock.getBlockY();
		while (y > 0){
			temp = new Location(locationToPlaceBlock.getWorld(), locationToPlaceBlock.getBlockX(), y, locationToPlaceBlock.getBlockZ());
			if (temp.getBlock().getType() == Material.GLASS){
				return false; // players can't build above glass region
			}
			else if (temp.getBlock().getType() == Material.NETHER_BRICK
					&& temp.getBlock().getRelative(BlockFace.DOWN).getType() == Material.BEDROCK){
				return true; // is in classic build area
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