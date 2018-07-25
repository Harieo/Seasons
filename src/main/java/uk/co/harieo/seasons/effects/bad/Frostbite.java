package uk.co.harieo.seasons.effects.bad;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import uk.co.harieo.seasons.Seasons;
import uk.co.harieo.seasons.configuration.SeasonsConfig;
import uk.co.harieo.seasons.models.Cycle;
import uk.co.harieo.seasons.models.effect.Effect;
import uk.co.harieo.seasons.models.effect.TickableEffect;
import uk.co.harieo.seasons.models.Weather;

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
