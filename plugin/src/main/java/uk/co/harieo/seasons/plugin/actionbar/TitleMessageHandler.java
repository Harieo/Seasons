package uk.co.harieo.seasons.plugin.actionbar;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import uk.co.harieo.seasons.plugin.Seasons;
import uk.co.harieo.seasons.plugin.events.SeasonChangeEvent;
import uk.co.harieo.seasons.plugin.events.SeasonsWeatherChangeEvent;
import uk.co.harieo.seasons.plugin.events.YearChangeEvent;
import uk.co.harieo.seasons.plugin.models.Cycle;
import uk.co.harieo.seasons.plugin.models.Season;
import uk.co.harieo.seasons.plugin.models.Weather;

public class TitleMessageHandler implements Listener {

	private static boolean YEAR_CHANGE = false;
	private static boolean SEASON_CHANGE = false;
	private static boolean WEATHER_CHANGE = false;

	private final Set<UUID> buffer = new HashSet<>();

	@EventHandler
	public void onWeatherChange(SeasonsWeatherChangeEvent event) {
		if (WEATHER_CHANGE) {
			Weather weather = event.getChangedTo();
			consumeCyclePlayers(player -> {
				UUID uuid = player.getUniqueId();
				if (buffer.contains(uuid)) { // If a season/year change title is showing
					buffer.remove(uuid); // Buffer only applies once
					return; // As they are buffered, skip this title because another is already showing
				}

				player.sendTitle(weather.getName(),
						Seasons.getInstance().getLanguageConfig().getString("misc.weather-change")
								.orElse(ChatColor.GRAY + "The weather has changed"),
						10, 70, 20);
			}, event.getCycle());
		}
	}

	@EventHandler
	public void onSeasonChange(SeasonChangeEvent event) {
		if (SEASON_CHANGE) {
			Season season = event.getChangedTo();
			consumeCyclePlayers(player -> {
				UUID uuid = player.getUniqueId();
				if (buffer.contains(uuid)) { // If a year change title is showing
					buffer.remove(uuid); // Buffer only applies once
					return; // As they are buffered, skip this title because another is already showing
				}

				player.sendTitle(season.getName(),
						Seasons.getInstance().getLanguageConfig().getString("misc.season-change")
								.orElse(ChatColor.GRAY + "The season has changed"),
						10, 70, 20);
				buffer.add(player.getUniqueId());
			}, event.getCycle());
		}
	}

	@EventHandler
	public void onYearChange(YearChangeEvent event) {
		if (YEAR_CHANGE) {
			int year = event.getChangedTo();
			consumeCyclePlayers(player -> {
				player.sendTitle(Seasons.getInstance().getLanguageConfig().getString("misc.year-color")
										 .orElse(String.valueOf(ChatColor.RED)) + year,
								 Seasons.getInstance().getLanguageConfig().getString("misc.year-change")
										 .orElse(ChatColor.GRAY + "The year has changed"),
								 10, 70, 20);
				buffer.add(player.getUniqueId());
			}, event.getCycle());
		}
	}

	private void consumeCyclePlayers(Consumer<Player> playerConsumer, Cycle cycle) {
		cycle.getWorld().getPlayers().forEach(playerConsumer);
	}

	public static void setOnYearChange(boolean enabled) {
		YEAR_CHANGE = enabled;
	}

	public static void setOnSeasonChange(boolean enabled) {
		SEASON_CHANGE = enabled;
	}

	public static void setOnWeatherChange(boolean enabled) {
		WEATHER_CHANGE = enabled;
	}

}
