package uk.co.harieo.seasons.effects.bad;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import uk.co.harieo.seasons.Seasons;
import uk.co.harieo.seasons.models.Cycle;
import uk.co.harieo.seasons.models.effect.Effect;
import uk.co.harieo.seasons.models.effect.TickableEffect;
import uk.co.harieo.seasons.models.Weather;

public class SolderingIron extends Effect implements TickableEffect {

	private static final Material[] SOLDERING_ITEMS = {Material.BUCKET, Material.IRON_INGOT, Material.IRON_BLOCK,
			Material.IRON_DOOR, Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS,
			Material.IRON_BOOTS, Material.ANVIL, Material.IRON_NUGGET, Material.IRON_FENCE, Material.IRON_TRAPDOOR};

	private Map<Player, Integer> secondsPast = new HashMap<>();

	public SolderingIron() {
		super("Soldering Iron", Collections.singletonList(Weather.SCORCHING), false);
	}

	private void checkHotbar(Player player) {
		PlayerInventory inventory = player.getInventory();
		boolean containsKey = secondsPast.containsKey(player);

		for (Material material : SOLDERING_ITEMS) {
			boolean containsMaterial = inventory.getItemInMainHand().getType() == material
					|| inventory.getItemInOffHand().getType() == material;
			if (containsMaterial && !containsKey) { // They are holding a soldering item and weren't last time checked
				secondsPast.put(player, 0);
				player.sendMessage(
						Seasons.PREFIX + ChatColor.RED + "The iron is soldering hot, don't hold it for too long!");
			}
		}

		if (containsKey) { // They were holding no soldering items but were last time a check was done
			secondsPast.remove(player);
		}
	}

	@Override
	public void onTrigger(World world) {
		secondsPast.clear();
		for (Player player : world.getPlayers()) {
			checkHotbar(player);
		}
	}

	@Override
	public void onTick(Cycle cycle) {
		for (Player player : secondsPast.keySet()) {
			int seconds = secondsPast.get(player);
			if (seconds >= 5) {
				player.damage(1);
			} else {
				secondsPast.replace(player, seconds + 1);
			}
		}
	}

	@EventHandler
	public void onHotbarSwitch(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		if (isPlayerCycleApplicable(player)) {
			checkHotbar(player);
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();
			if (isPlayerCycleApplicable(player)) {
				checkHotbar(player);
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		secondsPast.remove(event.getPlayer());
	}

}
