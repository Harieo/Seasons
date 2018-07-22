package uk.co.harieo.seasons;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import uk.co.harieo.seasons.configuration.SeasonsConfig;
import uk.co.harieo.seasons.models.Cycle;
import uk.co.harieo.seasons.models.Season;
import uk.co.harieo.seasons.models.Weather;

public class WorldTicker extends BukkitRunnable {

	@Override
	public void run() {
		for (Cycle cycle : Seasons.getCycles()) {
			World world = cycle.getWorld();
			if (world.getTime() >= 12300 && world.getTime() < 12400
					&& cycle.getWeather() != Weather.NIGHT) { // If the world is entering night and not already handled

				cycle.setWeather(Weather.NIGHT);
				broadcastWeatherMessage(Weather.NIGHT);

			} else if (world.getTime() >= 23850 && world.getTime() > 0) {
				int day = cycle.getDay();
				Season season;
				// If the next day will advance past the amount of days in a season, switch to new season
				if (day + 1 > SeasonsConfig.get().getDaysPerSeason()) {
					cycle.setDay(1);
					season = Season.next(cycle.getSeason());
					cycle.setSeason(season);
					broadcastSeasonMessage(season);
				} else {
					cycle.setDay(day + 1);
					season = cycle.getSeason();
				}


				Weather newWeather = Weather.randomWeather(season);
				cycle.setWeather(newWeather);

				broadcastWeatherMessage(newWeather);
			}
		}
	}

	private void broadcastWeatherMessage(Weather weather) {
		if (weather.isCatastrophic()) {
			Bukkit.broadcastMessage(Seasons.PREFIX + ChatColor.RED + ChatColor.BOLD.toString()
					+ "CATASTROPHIC WEATHER ALERT - Your chances of dying have increased dramatically");
		}

		Bukkit.broadcastMessage(weather.getMessage());
	}

	private void broadcastSeasonMessage(Season season) {
		Bukkit.broadcastMessage(season.getMessage());
	}

}
