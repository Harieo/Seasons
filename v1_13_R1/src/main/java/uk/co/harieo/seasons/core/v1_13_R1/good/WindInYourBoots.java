package uk.co.harieo.seasons.core.v1_13_R1.good;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import uk.co.harieo.seasons.plugin.Seasons;
import uk.co.harieo.seasons.plugin.models.Cycle;
import uk.co.harieo.seasons.plugin.models.Weather;
import uk.co.harieo.seasons.plugin.models.effect.Effect;
import uk.co.harieo.seasons.plugin.models.effect.TickableEffect;

public class WindInYourBoots extends Effect implements TickableEffect {

	private int secondsPast = 0;

	public WindInYourBoots() {
		super("Wind in Your Boots", "Random chance to gain Speed 1 for 20 seconds every 2 minutes",
				Collections.singletonList(Weather.BREEZY), true);
	}

	@Override
	public String getId() {
		return "wind-in-your-boots";
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
						sendGiveMessage(player, ChatColor.GREEN
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
