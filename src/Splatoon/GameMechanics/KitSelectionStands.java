package Splatoon.GameMechanics;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;

import Splatoon.Main.Core;

public class KitSelectionStands implements Listener{

	public KitSelectionStands() {
		Core.registerListener(this);
	}
	
	public void generateStands(){
		spawnStand(Core.team1Stand1Location, ChatColor.GOLD + "Splattershot");
		spawnStand(Core.team1Stand2Location, ChatColor.GOLD + "Splat Charger");
		spawnStand(Core.team1Stand3Location, ChatColor.GOLD + "Splat Roller");
		
		spawnStand(Core.team2Stand1Location, ChatColor.GOLD + "Splattershot");
		spawnStand(Core.team2Stand2Location, ChatColor.GOLD + "Splat Charger");
		spawnStand(Core.team2Stand3Location, ChatColor.GOLD + "Splat Roller");
	}
	
	public void spawnStand(Location location, String text){
		ArmorStand stand = (ArmorStand) Core.gameWorld.spawnEntity(location, EntityType.ARMOR_STAND);
		stand.setCustomName(text);
		stand.setCustomNameVisible(true);
		stand.setArms(true);
		
		if (text.contains("Splattershot")){
			stand.setItemInHand(new ItemStack(Material.SNOW_BALL, 1));
		}
		else if (text.contains("Splat Charger")){
			ItemStack bow = new ItemStack(Material.BOW, 1);
			bow.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
			stand.setItemInHand(bow);
		}
		else if (text.contains("Splat Roller")){
			ItemStack stick = new ItemStack(Material.STICK, 1);
			stick.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
			stand.setItemInHand(stick);
		}
	}
	
	@EventHandler
	public void onPlayerInteractKitStand(PlayerInteractAtEntityEvent e){
		if (Core.gameStarted == true
				&& e.getRightClicked() instanceof ArmorStand
				&& e.getRightClicked().getCustomName() != null){
			// player is interacting with kit selection armor stand
			e.setCancelled(true); // prevent regular minecraft interaction code
			
			String standName = e.getRightClicked().getCustomName();
			Player player = e.getPlayer();
			int currentKitID = Core.gameManager.getPlayerKitID(player);
			
			// give the player the new kit equipment
			if (standName.contains("Splattershot")){
				if (currentKitID == 3){
					return; // already has this kit
				}
				Core.gameManager.clearInventory(player);
				Core.gameManager.setPlayerKit(player, 3);
				Core.gameManager.givePlayerKit(player);
				Core.gameManager.givePlayerTeamArmor(player);
			}
			else if (standName.contains("Splat Charger")){
				if (currentKitID == 2){
					return; // already has this kit
				}
				Core.gameManager.clearInventory(player);
				Core.gameManager.setPlayerKit(player, 2);
				Core.gameManager.givePlayerKit(player);
				Core.gameManager.givePlayerTeamArmor(player);
			}
			else if (standName.contains("Splat Roller")){
				if (currentKitID == 1){
					return; // already has this kit
				}
				Core.gameManager.clearInventory(player);
				Core.gameManager.setPlayerKit(player, 1);
				Core.gameManager.givePlayerKit(player);
				Core.gameManager.givePlayerTeamArmor(player);
			}
		}
	}
	
	@EventHandler
	public void onStandDamage(EntityDamageEvent e){
		if (e.getEntity() instanceof ArmorStand
				&& Core.gameStarted == true
				&& e.getCause() != DamageCause.VOID){
			// prevent kit selection stands from being damaged
			e.setCancelled(true);
		}
	}
}
