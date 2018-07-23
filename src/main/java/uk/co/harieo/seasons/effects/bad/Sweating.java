package uk.co.harieo.seasons.effects.bad;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import uk.co.harieo.seasons.Seasons;
import uk.co.harieo.seasons.models.effect.Effect;
import uk.co.harieo.seasons.models.Weather;
import uk.co.harieo.seasons.models.effect.SeasonsPotionEffect;

public class Sweating extends SeasonsPotionEffect {

	public Sweating() {
		super("Sweating", Collections.singletonList(Weather.HOT), false,
				new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 0));
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
				+ "You feel the breeze on your exposed skin and the heat becomes more bearable!");
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();
			if (shouldGive(player)) {
				giveEffect(player, true);
			} else {
				removeEffect(player, true);
			}
		}
	}

}
