package uk.co.harieo.seasons.effects.good;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.Random;
import uk.co.harieo.seasons.Seasons;
import uk.co.harieo.seasons.events.SeasonsWeatherChangeEvent;
import uk.co.harieo.seasons.models.Cycle;
import uk.co.harieo.seasons.models.Effect;
import uk.co.harieo.seasons.models.TickableEffect;
import uk.co.harieo.seasons.models.Weather;

public class WindInYourBoots extends Effect implements TickableEffect {

	private static final Random RANDOM = new Random();

	private int secondsPast = 0;

	public WindInYourBoots() {
		super("Wind in Your Boots", Collections.singletonList(Weather.BREEZY), true);
	}

	@Override
	public void onWeatherChange(SeasonsWeatherChangeEvent event) {
	}

	@Override
	public void onTick(Cycle cycle) {
		if (secondsPast >= 60 * 2) {
			int random = RANDOM.nextInt(10);
			if (random < 1) {
				World world = cycle.getWorld();
				for (Player player : world.getPlayers()) {
					if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
						player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 20, 0));
						player.sendMessage(Seasons.PREFIX + ChatColor.GREEN
								+ "You feel a rush of Wind in your Boots and go hurtling forwards!");
					}
				}
			}

			secondsPast = 0;
		} else {
			secondsPast++;
		}
	}
}
