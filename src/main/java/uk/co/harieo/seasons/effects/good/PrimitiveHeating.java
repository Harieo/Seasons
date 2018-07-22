package uk.co.harieo.seasons.effects.good;

import org.bukkit.ChatColor;
import org.bukkit.Material;
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

public class PrimitiveHeating extends Effect {

	private static final Material[] HOT_MATERIALS = {Material.LAVA_BUCKET, Material.BLAZE_POWDER, Material.BLAZE_ROD,
			Material.DRAGONS_BREATH, Material.MAGMA_CREAM};

	public PrimitiveHeating() {
		super("Primitive Heating", Collections.singletonList(Weather.COLD), true);
	}

	private void checkInventory(Player player) {
		if (!player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
			PlayerInventory inventory = player.getInventory();
			for (Material material : HOT_MATERIALS) {
				if (inventory.contains(material)) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
					player.sendMessage(Seasons.PREFIX + ChatColor.GREEN
							+ "The heat from your inventory fuels your heart, you are now more Resistant");
				}
			}
		}
	}

	@Override
	public void onWeatherChange(SeasonsWeatherChangeEvent event) {
		if (isWeatherApplicable(event.getChangedTo())) {
			World world = event.getCycle().getWorld();
			for (Player player : world.getPlayers()) {
				checkInventory(player);
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();
			if (isPlayerCycleApplicable(player)) {
				checkInventory(player);
			}
		}
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		if (isPlayerCycleApplicable(player)) {
			checkInventory(event.getPlayer()); // Some servers may have plugins to keep inventories separate to the GameRule
		}
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (isPlayerCycleApplicable(player)) {
			player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (isPlayerCycleApplicable(player)) {
			checkInventory(player);
		}
	}
}
