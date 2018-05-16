package Splatoon.GameMechanics;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Squid;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import Splatoon.Main.Core;
import Splatoon.Main.GameStates.GameState;

public class SquidWatcher implements Runnable{

	private int id;
	private int calls = 0;
	private Player player;
	private Squid squid;
	private byte playerWoolData;
	
	public SquidWatcher(Player player) {
		this.player = player;
		removeArmor();
		playerWoolData = Core.gameManager.getPlayerWoolColor(player);
		player.removePotionEffect(PotionEffectType.INVISIBILITY);
		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20, 0, true, false)); // invis lvl 1 for 20 ticks with ambient particles
		if (playerIsOnTeamWool(player)){
			player.removePotionEffect(PotionEffectType.SPEED);
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 0, false, false)); // speed lvl 1 for 20 ticks with no particles
		}
		squid = (Squid) player.getWorld().spawnEntity(player.getLocation(), EntityType.SQUID);
		squid.setMaxHealth(100.0);
		squid.setHealth(100.0);
		squid.getLocation().setDirection(player.getLocation().getDirection());
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.thisPlugin, this, 1L, 1L); // runs every second
	}
	
	public void end(){
		if (player != null
				&& player.isOnline()){
			player.removePotionEffect(PotionEffectType.INVISIBILITY);
			player.removePotionEffect(PotionEffectType.SPEED);
			equipArmor();
		}
		squid.remove();
		Bukkit.getScheduler().cancelTask(id);
	}
	
	public void removeArmor(){
		ItemStack air = new ItemStack(Material.AIR);
		player.getInventory().setHelmet(air);
		player.getInventory().setChestplate(air);
		player.getInventory().setLeggings(air);
		player.getInventory().setBoots(air);
	}
	
	public void equipArmor(){
		Core.gameManager.givePlayerTeamArmor(player);
	}
	
	@SuppressWarnings("deprecation")
	public boolean playerIsOnTeamWool(Player player){
		if (player.getLocation().add(0.0, -1.0, 0.0).getBlock().getType() == Material.WOOL
				&& player.getLocation().add(0.0, -1.0, 0.0).getBlock().getData() == playerWoolData){
			return true; // player is standing on their wool color
		}
		return false;
	}

	@Override
	public void run() {
		calls++;
		if (Core.gameStarted == false
				|| Core.gameState != GameState.Running){
			end();
		}
		if (player == null
				|| player.isOnline() == false){
			// player logged out
			end();
			return;
		}
		if (player.getItemInHand() == null
				|| player.getItemInHand().getType() == Material.AIR){
			squid.setHealth(100.0);
			squid.setFireTicks(0);
			squid.setRemainingAir(2000);
			squid.teleport(player.getLocation());
			squid.getLocation().setDirection(player.getLocation().getDirection());
			player.removePotionEffect(PotionEffectType.INVISIBILITY);
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20, 0)); // invis lvl 1 for 30 ticks with ambient particles
			player.removePotionEffect(PotionEffectType.SPEED);
			if (playerIsOnTeamWool(player)){
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 0, false, false)); // speed lvl 1 for 30 ticks with no particles
			}
			if (calls%5 == 0){
				player.getWorld().playSound(player.getLocation(), Sound.SLIME_WALK, 0.5F, 1.0F);
			}
			if (calls%2 == 0){
				Core.gameManager.woolDust(playerWoolData, player.getLocation(), 0.5F, 100);
			}
		}else{
			// player is no longer holding an empty slot
			end();
		}
	}
}
