package uk.co.harieo.seasons.effects.good;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import uk.co.harieo.seasons.Seasons;
import uk.co.harieo.seasons.events.SeasonsWeatherChangeEvent;
import uk.co.harieo.seasons.models.Effect;
import uk.co.harieo.seasons.models.Weather;

public class FluffyCoat extends Effect {

	public FluffyCoat() {
		super("Fluffy Coat", Collections.singletonList(Weather.SNOWY), true);
	}

	private void checkArmour(Player player) {
		PlayerInventory inventory = player.getInventory();
		for (ItemStack armor : inventory.getArmorContents()) {
			if (armor == null) { // If player is missing any form of armour
				return;
			}
		}

		player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
		player.sendMessage(Seasons.PREFIX + ChatColor.GREEN
				+ "Your armour blocks out the snow and see it in a new light...");
	}

	@Override
	public void onWeatherChange(SeasonsWeatherChangeEvent event) {
		if (isWeatherApplicable(event.getChangedTo())) {
			World world = event.getCycle().getWorld();
			for (Player player : world.getPlayers()) {
				player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();
			if (isPlayerCycleApplicable(player)) {
				checkArmour(player);
			}
		}
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		if (isPlayerCycleApplicable(player)) {
			checkArmour(event.getPlayer()); // Some servers may have plugins to keep inventories separate to the GameRule
		}
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (isPlayerCycleApplicable(player)) {
			player.removePotionEffect(PotionEffectType.SPEED);
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (isPlayerCycleApplicable(player)) {
			checkArmour(player);
		}
	}

}
