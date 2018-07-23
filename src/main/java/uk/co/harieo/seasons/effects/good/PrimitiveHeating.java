package uk.co.harieo.seasons.effects.good;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import uk.co.harieo.seasons.Seasons;
import uk.co.harieo.seasons.models.Weather;
import uk.co.harieo.seasons.models.effect.SeasonsPotionEffect;

public class PrimitiveHeating extends SeasonsPotionEffect {

	private static final Material[] HOT_MATERIALS = {Material.LAVA_BUCKET, Material.BLAZE_POWDER, Material.BLAZE_ROD,
			Material.DRAGONS_BREATH, Material.MAGMA_CREAM};

	public PrimitiveHeating() {
		super("Primitive Heating", Collections.singletonList(Weather.COLD), true,
				new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
	}

	@Override
	public boolean shouldGive(Player player) {
		if (!isPlayerCycleApplicable(player)) {
			PlayerInventory inventory = player.getInventory();
			for (Material material : HOT_MATERIALS) {
				if (inventory.contains(material)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void sendGiveMessage(Player player) {
		player.sendMessage(Seasons.PREFIX + ChatColor.GREEN
				+ "A hot item in your inventory warms your heart and makes you Resistant to the cold world");
	}

	@Override
	public void sendRemoveMessage(Player player) {
		player.sendMessage(
				Seasons.PREFIX + ChatColor.YELLOW + "Your body is no longer warmed and it's Resistance wears away...");
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
