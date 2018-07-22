package uk.co.harieo.seasons.effects.bad;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collections;
import java.util.Random;
import uk.co.harieo.seasons.events.SeasonsWeatherChangeEvent;
import uk.co.harieo.seasons.models.Effect;
import uk.co.harieo.seasons.models.Weather;

public class HoldOntoYourHat extends Effect {

	private static final Random RANDOM = new Random();

	public HoldOntoYourHat() {
		super("Hold onto Your Hat", Collections.singletonList(Weather.BREEZY), false);
	}

	@Override
	public void onWeatherChange(SeasonsWeatherChangeEvent event) {
		if (isWeatherApplicable(event.getChangedTo())) {
			World world = event.getCycle().getWorld();
			for (Player player : world.getPlayers()) {
				PlayerInventory inventory = player.getInventory();
				ItemStack helmet = inventory.getHelmet();

				if (helmet.getType() == Material.LEATHER_HELMET) {
					int random = RANDOM.nextInt(3);
					if (random == 2) {
						world.dropItem(player.getLocation(), helmet);
						inventory.setHelmet(null);
					}
				}
			}
		}
	}

	// TODO Instead of on equip, do a miniscule chance on any inventory click as looks far nicer
}
