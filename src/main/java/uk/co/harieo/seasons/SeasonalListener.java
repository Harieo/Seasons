package uk.co.harieo.seasons;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.world.WorldLoadEvent;

import uk.co.harieo.seasons.events.DayEndEvent;
import uk.co.harieo.seasons.events.SeasonChangeEvent;
import uk.co.harieo.seasons.events.SeasonsWeatherChangeEvent;
import uk.co.harieo.seasons.models.Season;
import uk.co.harieo.seasons.models.Weather;

public class SeasonalListener implements Listener {

	@EventHandler
	public void onSeasonChange(SeasonChangeEvent event) {
		Season season = event.getChangedTo();
		World world = event.getCycle().getWorld();
		for (Player player : world.getPlayers()) {
			player.sendMessage(Seasons.PREFIX + season.getColor() + season.getMessage());
		}
	}

	@EventHandler
	public void onWeatherChange(SeasonsWeatherChangeEvent event) {
		Weather weather = event.getChangedTo();
		World world = event.getCycle().getWorld();

		for (Player player : world.getPlayers()) {
			if (weather.isCatastrophic()) {
				player.sendMessage(Seasons.PREFIX + ChatColor.RED + ChatColor.BOLD.toString()
						+ "CATASTROPHIC WEATHER ALERT - Your chances of dying have increased dramatically");
			}

			player.sendMessage(Seasons.PREFIX + weather.getMessage());
		}
	}

	@EventHandler
	public void onDayEnd(DayEndEvent event) {
		if (event.isNatural()) {
			World world = event.getCycle().getWorld();
			for (Player player : world.getPlayers()) {
				player.sendMessage(Seasons.PREFIX + Weather.NIGHT.getMessage());
			}
		}
	}

	@EventHandler
	public void onWorldLoad(WorldLoadEvent event) {
		World world = event.getWorld();
		if (Seasons.getWorldCycle(world) == null) {
			Seasons.getWorldHandler().addWorld(world); // Method will check environment so we don't need to
		}
	}

}
