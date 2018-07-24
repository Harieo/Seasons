package uk.co.harieo.seasons;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
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
				newNight(cycle, world);
			} else if (isUnregisteredNight || shouldProgressDay) {
				newDay(cycle, world);
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

	private void newDay(Cycle cycle, World world) {
		int day = cycle.getDay();
		Season season;

		// If the next day will advance past the amount of days in a season, switch to new season
		if (day + 1 > SeasonsConfig.get().getDaysPerSeason()) {
			cycle.setDay(1);
			season = Season.next(cycle.getSeason());
			Bukkit.getPluginManager().callEvent(new SeasonChangeEvent(cycle, cycle.getSeason(), season, true));
			cycle.setSeason(season);
			broadcastSeasonMessage(season, world);
		} else {
			cycle.setDay(day + 1);
			season = cycle.getSeason();
		}

		Weather newWeather = Weather.randomWeather(season);
		cycle.setWeather(newWeather);

		broadcastWeatherMessage(newWeather, world);
		Bukkit.getPluginManager()
				.callEvent(new SeasonsWeatherChangeEvent(cycle, newWeather, true));
	}

	private void newNight(Cycle cycle, World world) {
		Bukkit.getPluginManager().callEvent(new DayEndEvent(cycle, cycle.getWeather()));
		cycle.setWeather(Weather.NIGHT);
		broadcastWeatherMessage(Weather.NIGHT, world);
	}

	/**
	 * Broadcasts the trigger message for the given {@link Weather} to all players in the {@link World} in which the
	 * given {@link Weather} affects
	 *
	 * @param weather that is being triggered
	 * @param world that the {@param weather} is being triggered on
	 */
	private void broadcastWeatherMessage(Weather weather, World world) {
		for (Player player : world.getPlayers()) {
			if (weather.isCatastrophic()) {
				player.sendMessage(Seasons.PREFIX + ChatColor.RED + ChatColor.BOLD.toString()
						+ "CATASTROPHIC WEATHER ALERT - Your chances of dying have increased dramatically");
			}

			player.sendMessage(Seasons.PREFIX + weather.getMessage());
		}
	}

	/**
	 * Broadcasts the trigger message for the given {@link Season} to all players in the {@link World} in which the
	 * given {@link Season} affects
	 *
	 * @param season that is being triggered
	 * @param world that the {@param season} is being triggered on
	 */
	private void broadcastSeasonMessage(Season season, World world) {
		for (Player player : world.getPlayers()) {
			player.sendMessage(Seasons.PREFIX + season.getMessage());
		}
	}

}
