package uk.co.harieo.seasons.effects.good;

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
import uk.co.harieo.seasons.models.Weather;
import uk.co.harieo.seasons.models.effect.SeasonsPotionEffect;

public class FeelsGood extends SeasonsPotionEffect {

	public FeelsGood() {
		super("Feels Good", "Receive Speed 2 when not wearing armour",
				Collections.singletonList(Weather.WARM), true,
				new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
	}

	public boolean shouldGive(Player player) {
		if (isPlayerCycleApplicable(player)) {
			PlayerInventory inventory = player.getInventory();
			for (ItemStack armor : inventory.getArmorContents()) {
				if (armor != null) { // If player is wearing any form of armour
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
		player.sendMessage(Seasons.PREFIX + ChatColor.GREEN
				+ "The sun on your skin without armour gives you energy, Feels Good!");
	}

	@Override
	public void sendRemoveMessage(Player player) {
		player.sendMessage(Seasons.PREFIX + ChatColor.YELLOW
				+ "As you leave the world behind, the energising sunlight wears off...");
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();
			if (shouldGive(player)) {
				giveEffect(player, true);
			} else {
				removeEffect(player, false);
			}
		}
	}
}
