package Splatoon.GameMechanics;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import Splatoon.Main.Core;

public class DisabledEvents implements Listener{
	
	public DisabledEvents() {
	}
	
	@EventHandler
	public void onPlayerLobbyDamage(EntityDamageEvent e){
		if (e.getEntity() instanceof Player
				&& Core.gameStarted == false){
			e.setCancelled(true); // no lobby damage
			
			if (e.getCause() == DamageCause.VOID){
				e.getEntity().teleport(Core.lobbySpawn);
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		if (e.getPlayer().getGameMode() != GameMode.CREATIVE){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPortalTravel(PlayerPortalEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onHungerDeplete(FoodLevelChangeEvent e){
		if (e.getEntity() instanceof Player){
			e.setCancelled(true); // cancel the regular health depleat so the following modifications take place
			Player player = (Player) e.getEntity();
			player.setFoodLevel(20); // set food level to full
			player.setSaturation(20); // set saturation to 20
		}
	}
	
	@EventHandler
	public void spectatorVoidDamage(EntityDamageEvent e){
		if (e.getEntity() instanceof Player){
			Player player = (Player) e.getEntity();
			if (player.getGameMode() == GameMode.SPECTATOR){
				e.setCancelled(true); // cancel spectator damage by void
			}
		}
	}
}