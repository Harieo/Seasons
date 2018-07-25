package uk.co.harieo.seasons.effects.bad;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collections;
import uk.co.harieo.seasons.Seasons;
import uk.co.harieo.seasons.models.effect.Effect;
import uk.co.harieo.seasons.models.Weather;

public class HoldOntoYourHat extends Effect {

	public HoldOntoYourHat() {
		super("Hold onto Your Hat", "A small chance that your hat will fall off when you put it on",
				Collections.singletonList(Weather.BREEZY), false);
	}

	private void chanceHat(Player player) {
		PlayerInventory inventory = player.getInventory();
		ItemStack helmet = inventory.getHelmet();
		if (helmet == null) {
			return;
		}

		if (helmet.getType() == Material.LEATHER_HELMET) {
			int random = Seasons.RANDOM.nextInt(100);
			if (random < 10) { // 10% chance
				player.getWorld().dropItem(player.getLocation(), helmet);
				// Remove both to prevent duplication glitches
				inventory.setHelmet(null);
				player.setItemOnCursor(null);
				player.sendMessage(Seasons.PREFIX + ChatColor.YELLOW + "Your hat just blew off, oh dear!");
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
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();
			if (isPlayerCycleApplicable(player)) {
				chanceHat(player);
			}
		}
	}

}
