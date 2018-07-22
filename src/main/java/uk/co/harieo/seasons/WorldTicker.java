package uk.co.harieo.seasons;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import uk.co.harieo.seasons.configuration.SeasonsConfig;
import uk.co.harieo.seasons.events.DayEndEvent;
import uk.co.harieo.seasons.events.SeasonChangeEvent;
import uk.co.harieo.seasons.events.SeasonsWeatherChangeEvent;
import uk.co.harieo.seasons.models.*;

public class WorldTicker extends BukkitRunnable {

	@Override
	public void run() {
		for (Cycle cycle : Seasons.getCycles()) {
			World world = cycle.getWorld();

			// If the world is entering night and not already handled
			if (world.getTime() >= 12300 && world.getTime() < 12400 && cycle.getWeather() != Weather.NIGHT) {
				Bukkit.getPluginManager().callEvent(new DayEndEvent(cycle, cycle.getWeather()));
				cycle.setWeather(Weather.NIGHT);
				broadcastWeatherMessage(Weather.NIGHT);
			} else if (world.getTime() >= 23850 && world.getTime() > 0) {
				int day = cycle.getDay();
				Season season;

				// If the next day will advance past the amount of days in a season, switch to new season
				if (day + 1 > SeasonsConfig.get().getDaysPerSeason()) {
					cycle.setDay(1);
					season = Season.next(cycle.getSeason());
					Bukkit.getPluginManager().callEvent(new SeasonChangeEvent(cycle, cycle.getSeason(), season, true));
					cycle.setSeason(season);
					broadcastSeasonMessage(season);
				} else {
					cycle.setDay(day + 1);
					season = cycle.getSeason();
				}

				Weather newWeather = Weather.randomWeather(season);
				Bukkit.getPluginManager()
						.callEvent(new SeasonsWeatherChangeEvent(cycle, newWeather, true));
				cycle.setWeather(newWeather);

				broadcastWeatherMessage(newWeather);
			} else {
				for (Effect effect : cycle.getWeather().getEffects()) {
					if (effect instanceof TickableEffect) {
						TickableEffect tickableEffect = (TickableEffect) effect;
						tickableEffect.onTick(cycle);
					}
				}
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
