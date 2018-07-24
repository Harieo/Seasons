package uk.co.harieo.seasons.effects.good;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import uk.co.harieo.seasons.Seasons;
import uk.co.harieo.seasons.models.Cycle;
import uk.co.harieo.seasons.models.effect.Effect;
import uk.co.harieo.seasons.models.effect.TickableEffect;
import uk.co.harieo.seasons.models.Weather;

public class WindInYourBoots extends Effect implements TickableEffect {

	private int secondsPast = 0;

	public WindInYourBoots() {
		super("Wind in Your Boots", Collections.singletonList(Weather.BREEZY), true);
	}

	@Override
	public void onTrigger(World world) {
		secondsPast = 0;
	}

	@Override
	public void onTick(Cycle cycle) {
		if (secondsPast >= 60 * 2) {
			int random = Seasons.RANDOM.nextInt(10);
			if (random < 5) {
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
