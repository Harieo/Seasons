package uk.co.harieo.seasons.core.v1_13_R1.good;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import uk.co.harieo.seasons.plugin.Seasons;
import uk.co.harieo.seasons.plugin.models.Weather;
import uk.co.harieo.seasons.plugin.models.effect.SeasonsPotionEffect;

public class FluffyCoat extends SeasonsPotionEffect {

	public FluffyCoat() {
		super("Fluffy Coat", "Receive Resistance 1 when wearing full armour",
				Collections.singletonList(Weather.SNOWY), true,
				new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
	}

	@Override
	public String getId() {
		return "fluffy-coat";
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
		player.sendMessage(Seasons.PREFIX + ChatColor.GREEN
				+ "Your armour gives you a soothing warmth and makes you more Resistant to the world");
	}

	@Override
	public void sendRemoveMessage(Player player) {
		player.sendMessage(Seasons.PREFIX + ChatColor.RED + "Without armour, your body feels the cold once again and is no longer resistant to it");
	}

	@Override
	public void onTrigger(World world) {
		for (Player player : world.getPlayers()) {
			giveEffect(player, true);
		}
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
