package uk.co.harieo.seasons.core.v1_13_R1.bad;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import uk.co.harieo.seasons.plugin.Seasons;
import uk.co.harieo.seasons.plugin.models.Weather;
import uk.co.harieo.seasons.plugin.models.effect.SeasonsPotionEffect;

public class Sweating extends SeasonsPotionEffect {

	public Sweating() {
		super("Sweating", "Receive Weakness 2 when you have full armour on",
				Collections.singletonList(Weather.HOT), false,
				new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 1));
	}

	@Override
	public boolean shouldGive(Player player) {
		if (isPlayerCycleApplicable(player)) {
			PlayerInventory inventory = player.getInventory();
			for (ItemStack armor : inventory.getArmorContents()) {
				if (armor == null) { // If player is missing any form of armour
					return false;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void sendGiveMessage(Player player) {
		player.sendMessage(
				Seasons.PREFIX + ChatColor.RED + "Sweat drips from your forehead, it is too Hot for armour today...");
	}

	@Override
	public void sendRemoveMessage(Player player) {
		player.sendMessage(Seasons.PREFIX + ChatColor.GREEN
				+ "The air turns cooler and you wipe the sweat from your forehead...");
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();
			BukkitRunnable runnable = new BukkitRunnable() {
				@Override
				public void run() {
					if (shouldGive(player)) {
						giveEffect(player, true);
					} else {
						removeEffect(player, false);
					}
				}
			};
			runnable.runTaskLater(Seasons.getPlugin(), 10);
		}
	}

}
