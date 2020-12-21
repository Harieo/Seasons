package uk.co.harieo.seasons.plugin.actionbar;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.function.Consumer;
import uk.co.harieo.seasons.plugin.Seasons;
import uk.co.harieo.seasons.plugin.events.SeasonChangeEvent;
import uk.co.harieo.seasons.plugin.events.SeasonsWeatherChangeEvent;
import uk.co.harieo.seasons.plugin.models.Cycle;
import uk.co.harieo.seasons.plugin.models.Season;
import uk.co.harieo.seasons.plugin.models.Weather;

public class TitleMessageHandler implements Listener {

	private static boolean SEASON_CHANGE = false;
	private static boolean WEATHER_CHANGE = false;

	@EventHandler
	public void onWeatherChange(SeasonsWeatherChangeEvent event) {
		if (WEATHER_CHANGE) {
			Weather weather = event.getChangedTo();
			consumeCyclePlayers(player -> player.sendTitle(weather.getName(),
					Seasons.getInstance().getLanguageConfig().getString("misc.weather-change")
							.orElse(ChatColor.GRAY + "The weather has changed"),
					10, 70, 20), event.getCycle());
		}
	}

	@EventHandler
	public void onSeasonChange(SeasonChangeEvent event) {
		if (SEASON_CHANGE) {
			Season season = event.getChangedTo();
			consumeCyclePlayers(player -> player.sendTitle(season.getName(),
					Seasons.getInstance().getLanguageConfig().getString("misc.season-change")
							.orElse(ChatColor.GRAY + "The season has changed"),
					10, 70, 20), event.getCycle());
		}
	}

	private void consumeCyclePlayers(Consumer<Player> playerConsumer, Cycle cycle) {
		cycle.getWorld().getPlayers().forEach(playerConsumer);
	}

	public static void setOnSeasonChange(boolean enabled) {
		SEASON_CHANGE = enabled;
	}

	public static void setOnWeatherChange(boolean enabled) {
		WEATHER_CHANGE = enabled;
	}

}
