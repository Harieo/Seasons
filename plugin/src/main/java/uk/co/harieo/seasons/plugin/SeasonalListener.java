package uk.co.harieo.seasons.plugin;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import uk.co.harieo.seasons.plugin.events.DayEndEvent;
import uk.co.harieo.seasons.plugin.events.SeasonChangeEvent;
import uk.co.harieo.seasons.plugin.events.SeasonsWeatherChangeEvent;
import uk.co.harieo.seasons.plugin.models.Season;
import uk.co.harieo.seasons.plugin.models.Weather;

public class SeasonalListener implements Listener {

	@EventHandler
	public void onSeasonChange(SeasonChangeEvent event) {
		Season season = event.getChangedTo();
		World world = event.getCycle().getWorld();
		season.getMessage().ifPresent(message -> {
			for (Player player : world.getPlayers()) {
				player.sendMessage(Seasons.PREFIX + season.getColor() + message);
			}
		});
	}

	@EventHandler
	public void onWeatherChange(SeasonsWeatherChangeEvent event) {
		Weather weather = event.getChangedTo();
		World world = event.getCycle().getWorld();

		for (Player player : world.getPlayers()) {
			if (weather.isCatastrophic()) {
				Seasons.getInstance().getLanguageConfig()
						.getStringOrDefault("misc.catastrophic-alert",
								ChatColor.RED + ChatColor.BOLD.toString()
										+ "CATASTROPHIC WEATHER ALERT - Take care to plan your day")
						.ifPresent(message -> player.sendMessage(Seasons.PREFIX + message));
			}

			weather.getMessage().ifPresent(message -> player.sendMessage(Seasons.PREFIX + message));
		}

		world.setStorm(weather.isStorm());
	}

	@EventHandler
	public void onDayEnd(DayEndEvent event) {
		if (event.isNatural()) {
			World world = event.getCycle().getWorld();
			Weather.NIGHT.getMessage().ifPresent(message -> {
				for (Player player : world.getPlayers()) {
					player.sendMessage(Seasons.PREFIX + message);
				}
			});
		}
	}

	@EventHandler
	public void onWorldLoad(WorldLoadEvent event) {
		World world = event.getWorld();
		Seasons seasons = Seasons.getInstance();
		if (seasons.getWorldCycle(world) == null) {
			seasons.getWorldHandler().addWorld(world); // Method will check environment so we don't need to
		}
	}

}
