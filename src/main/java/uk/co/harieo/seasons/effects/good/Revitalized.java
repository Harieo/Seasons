package uk.co.harieo.seasons.effects.good;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import uk.co.harieo.seasons.Seasons;
import uk.co.harieo.seasons.events.DayEndEvent;
import uk.co.harieo.seasons.events.SeasonsWeatherChangeEvent;
import uk.co.harieo.seasons.models.Cycle;
import uk.co.harieo.seasons.models.Effect;
import uk.co.harieo.seasons.models.Weather;

public class Revitalized extends Effect {

	public Revitalized() {
		super("Revitalized", Collections.singletonList(Weather.BEAUTIFUL), true);
	}

	private void giveEffect(Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
	}

	private void removeEffect(Player player) {
		player.removePotionEffect(PotionEffectType.REGENERATION);
	}

	@Override
	public void onWeatherChange(SeasonsWeatherChangeEvent event) {
		// If the weather uses this effect
		if (isWeatherApplicable(event.getChangedTo())) {
			World world = event.getCycle().getWorld();
			for (Player player : world.getPlayers()) {
				giveEffect(player);
				player.sendMessage(Seasons.PREFIX
						+ ChatColor.GREEN + "As the sun rises, you feel it's Regenerating energy "
						+ ChatColor.YELLOW + "Revitalise " + ChatColor.GREEN + "you!");
			}
		}
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		if (isPlayerCycleApplicable(player)) {
			giveEffect(player);
			player.sendMessage(Seasons.PREFIX
					+ "The sun from this world is so " + ChatColor.YELLOW + "Revitalising"
					+ ChatColor.GREEN + ", it's Regenerating you!");
		} else {
			removeEffect(player);
			player.sendMessage(Seasons.PREFIX + ChatColor.YELLOW + "As you leave this world, it's "
					+ ChatColor.GOLD + "Revitalising " + ChatColor.YELLOW + "energy leaves too...");
		}
	}

	@EventHandler
	public void onDayEnd(DayEndEvent event) {
		if (isWeatherApplicable(event.getChangeFrom())) {
			World world = event.getCycle().getWorld();
			for (Player player : world.getPlayers()) {
				removeEffect(player);
				player.sendMessage(
						Seasons.PREFIX + ChatColor.GRAY + "As the sun rests for the night, it's " + ChatColor.GREEN
								+ "Revitalisation " + ChatColor.GRAY + " wears off...");
			}
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		if (isPlayerCycleApplicable(player)) {
			giveEffect(player);
		}
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (isPlayerCycleApplicable(player)) {
			removeEffect(player);
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (isPlayerCycleApplicable(player)) {
			giveEffect(player);
			player.sendMessage(Seasons.PREFIX
					+ "The sun from this world is so " + ChatColor.YELLOW + "Revitalising"
					+ ChatColor.GREEN + ", it's Regenerating you!");
		}
	}
}
