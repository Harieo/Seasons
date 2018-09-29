package uk.co.harieo.seasons.core.v1_13_R1.bad;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import uk.co.harieo.seasons.plugin.Seasons;
import uk.co.harieo.seasons.plugin.configuration.SeasonsConfig;
import uk.co.harieo.seasons.plugin.models.Cycle;
import uk.co.harieo.seasons.plugin.models.Weather;
import uk.co.harieo.seasons.plugin.models.effect.Effect;
import uk.co.harieo.seasons.plugin.models.effect.TickableEffect;

public class Frostbite extends Effect implements TickableEffect {

	public Frostbite() {
		super("Frostbite", "Harms you if you do not have full armour on after 10 seconds down to half a heart",
				Arrays.asList(Weather.FREEZING, Weather.SNOWY), false);
	}

	private int secondsPast = 0;
	private boolean active = false;

	@Override
	public void onTrigger(World world) {
		secondsPast = 0;
		for (Player player : world.getPlayers()) {
			player.sendMessage(Seasons.PREFIX + ChatColor.RED
					+ "The world is freezing over and so will you if you don't get armour on!");
		}
	}

	/**
	 * Damages all players in a world if they are missing any armour
	 *
	 * @param world to damage players in
	 */
	private void damage(World world) {
		for (Player player : world.getPlayers()) {
			if (player.getHealth() > 1) {
				List<ItemStack> armour = Arrays.asList(player.getInventory().getArmorContents());
				if (armour.contains(null)) {
					player.damage(1);
				}
			}
		}
	}

	@Override
	public void onTick(Cycle cycle) {
		if (active) {
			if (secondsPast >= SeasonsConfig.get().getSecondsPerDamage()) {
				damage(cycle.getWorld());
				secondsPast = 0;
			} else {
				secondsPast++;
			}
		} else {
			if (secondsPast >= 10) {
				active = true;
				secondsPast = 0;
			} else {
				secondsPast++;
			}
		}
	}
}
