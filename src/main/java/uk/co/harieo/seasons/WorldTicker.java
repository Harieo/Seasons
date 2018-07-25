package uk.co.harieo.seasons;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import uk.co.harieo.seasons.configuration.SeasonsConfig;
import uk.co.harieo.seasons.events.DayEndEvent;
import uk.co.harieo.seasons.events.SeasonChangeEvent;
import uk.co.harieo.seasons.events.SeasonsWeatherChangeEvent;
import uk.co.harieo.seasons.models.*;
import uk.co.harieo.seasons.models.effect.Effect;
import uk.co.harieo.seasons.models.effect.TickableEffect;

public class WorldTicker extends BukkitRunnable {

	@Override
	public void run() {
		for (Cycle cycle : Seasons.getCycles()) {
			World world = cycle.getWorld();
			boolean isNight = cycle.getWeather() == Weather.NIGHT;
			boolean shouldProgressDay = world.getTime() >= 23850 && world.getTime() > 0 && isNight;
			boolean shouldProgressNight = world.getTime() >= 12300 && world.getTime() < 12400;
			boolean isUnregisteredDay = !isNight && world.getTime() > 12400 && world.getTime() < 23850;
			boolean isUnregisteredNight = isNight && world.getTime() > 0 && world.getTime() < 12300;

			// If the world is entering night and not already handled
			if ((shouldProgressNight & !isNight) | isUnregisteredDay) {
				newNight(cycle);
			} else if (isUnregisteredNight || shouldProgressDay) {
				newDay(cycle);
			} else {
				for (Effect effect : Seasons.getEffects()) {
					if (effect.isWeatherApplicable(cycle.getWeather()) && effect instanceof TickableEffect) {
						TickableEffect tickableEffect = (TickableEffect) effect;
						tickableEffect.onTick(cycle);
					}
				}
			}
		}
	}

	private void newDay(Cycle cycle) {
		int day = cycle.getDay();
		Season season;

		// If the next day will advance past the amount of days in a season, switch to new season
		if (day + 1 > SeasonsConfig.get().getDaysPerSeason()) {
			cycle.setDay(1);
			season = Season.next(cycle.getSeason());
			Bukkit.getPluginManager().callEvent(new SeasonChangeEvent(cycle, cycle.getSeason(), season, true));
			cycle.setSeason(season);
		} else {
			cycle.setDay(day + 1);
			season = cycle.getSeason();
		}

		Weather oldWeather = Weather.fromName(cycle.getWeather().getName());
		Weather newWeather = Weather.randomWeather(season);
		cycle.setWeather(newWeather);

		Bukkit.getPluginManager()
				.callEvent(new SeasonsWeatherChangeEvent(cycle, oldWeather, newWeather, true));
	}

	private void newNight(Cycle cycle) {
		Weather oldWeather = Weather.fromName(cycle.getWeather().getName());
		cycle.setWeather(Weather.NIGHT);

		PluginManager manager = Bukkit.getPluginManager();
		manager.callEvent(new DayEndEvent(cycle, oldWeather, true));
	}

}
