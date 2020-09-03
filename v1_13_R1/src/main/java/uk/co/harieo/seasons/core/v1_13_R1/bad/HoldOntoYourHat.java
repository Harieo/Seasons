package uk.co.harieo.seasons.core.v1_13_R1.bad;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.time.LocalTime;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import uk.co.harieo.seasons.plugin.Seasons;
import uk.co.harieo.seasons.plugin.models.Weather;
import uk.co.harieo.seasons.plugin.models.effect.Effect;

public class HoldOntoYourHat extends Effect {

	private static final Cache<Player, LocalTime> buffer = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES)
			.build();

	public HoldOntoYourHat() {
		super("Hold onto Your Hat", "A small chance that your hat will fall off when you put it on",
				Collections.singletonList(Weather.BREEZY), false);
		setIgnoreRoof(false);
	}

	@Override
	public String getId() {
		return "hold-onto-your-hat";
	}

	/**
	 * Checks whether a player has a helmet on and that the helmet is leather If this condition is met, gives a 10%
	 * chance that the helmet will be dropped
	 *
	 * @param player to randomise for
	 */
	private void chanceHat(Player player) {
		if (isPlayerCycleApplicable(player)) {
			PlayerInventory inventory = player.getInventory();
			ItemStack helmet = inventory.getHelmet();
			if (helmet == null) {
				return;
			}

			if (helmet.getType() == Material.LEATHER_HELMET) {
				int random = Seasons.RANDOM.nextInt(100);
				if (random < 25) { // 10% chance
					player.getWorld().dropItem(player.getLocation(), helmet);
					// Remove both to prevent duplication glitches
					inventory.setHelmet(null);
					sendGiveMessage(player, ChatColor.YELLOW + "Your hat just blew off, oh dear!");
				}
			}
		}
	}

	@Override
	public void onTrigger(World world) {
		for (Player player : world.getPlayers()) {
			chanceHat(player);
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (buffer.getIfPresent(player) == null) {
			chanceHat(player);
			buffer.put(player, LocalTime.now());
		}
	}

}
