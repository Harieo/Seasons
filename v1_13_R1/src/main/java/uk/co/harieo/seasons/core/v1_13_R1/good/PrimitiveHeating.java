package uk.co.harieo.seasons.core.v1_13_R1.good;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import uk.co.harieo.seasons.plugin.Seasons;
import uk.co.harieo.seasons.plugin.models.Weather;
import uk.co.harieo.seasons.plugin.models.effect.SeasonsPotionEffect;

public class PrimitiveHeating extends SeasonsPotionEffect {

	private static final Material[] HOT_MATERIALS = {Material.LAVA_BUCKET, Material.BLAZE_POWDER, Material.BLAZE_ROD,
			Material.DRAGON_BREATH, Material.MAGMA_CREAM};

	public PrimitiveHeating() {
		super("Primitive Heating", "Receive Resistance 1 when your inventory contains a very hot item",
				Collections.singletonList(Weather.COLD), true,
				new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
	}

	@Override
	public String getId() {
		return "primitive-heating";
	}

	@Override
	public boolean shouldGive(Player player) {
		if (isPlayerCycleApplicable(player)) {
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
		sendGiveMessage(player, ChatColor.GREEN
				+ "A hot item in your inventory warms your heart and makes you Resistant to the cold world");
	}

	@Override
	public void sendRemoveMessage(Player player) {
		sendRemoveMessage(player, ChatColor.YELLOW + "Your body is no longer warmed and it's Resistance wears away...");
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		// This event fires before the action happens, meaning a delay is required
		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {
				if (event.getWhoClicked() instanceof Player) {
					Player player = (Player) event.getWhoClicked();
					if (shouldGive(player)) {
						giveEffect(player, false);
					} else {
						removeEffect(player, false);
					}
				}
			}
		};
		runnable.runTaskLater(Seasons.getInstance().getPlugin(), 10);
	}

	@EventHandler
	public void onItemPickup(EntityPickupItemEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			BukkitRunnable runnable = new BukkitRunnable() {
				@Override
				public void run() {
					giveEffect(player, true);
				}
			};
			runnable.runTaskLater(Seasons.getInstance().getPlugin(), 10);
		}
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		if (!shouldGive(player) && isHotItem(event.getItemDrop().getItemStack())) {
			removeEffect(player, false, true);
		}
	}

	/**
	 * Checks whether an item is considered a hot material in this effect
	 *
	 * @param item to be checked
	 * @return whether the item is considered a hot material
	 */
	private boolean isHotItem(ItemStack item) {
		for (Material material : HOT_MATERIALS) {
			if (item.getType() == material) {
				return true;
			}
		}

		return false;
	}

}
