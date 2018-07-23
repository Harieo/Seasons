package uk.co.harieo.seasons.configuration;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import uk.co.harieo.seasons.Seasons;
import uk.co.harieo.seasons.models.Cycle;
import uk.co.harieo.seasons.models.Season;
import uk.co.harieo.seasons.models.Weather;

public class SeasonsWorlds {

	private static final FileConfiguration CONFIG = Seasons.getPlugin().getConfig();
	private static final String DEFAULT_PATH = "world.";

	// These values will be used when the config contains either invalid values or none of these values
	private static final Season DEFAULT_SEASON = Season.SPRING;
	private static final Weather DEFAULT_WEATHER = Weather.BEAUTIFUL;

	/**
	 * Parses the default configuration file for any previously saved worlds and loads those settings if found
	 *
	 * @return a list of cycles of all applicable worlds, regardless of whether they were found in the configuration
	 * file
	 */
	public static List<Cycle> parseWorldsAutosave() {
		List<Cycle> cycles = new ArrayList<>();

		for (World world : Bukkit.getWorlds()) { // Scan all loaded worlds
			if (world.getEnvironment().equals(Environment.NORMAL)) { // Only normal worlds are subject to cycles
				String worldName = world.getName();
				String path = DEFAULT_PATH + worldName;

				if (CONFIG.contains(path)) { // Config knows of this world, load settings
					int day = CONFIG.getInt(path + ".day");
					Season season = Season.fromName(CONFIG.getString(path + ".season"));
					Weather weather = Weather.fromName(CONFIG.getString(path + ".weather"));

					if (season == null || weather == null || day < 1) { // One or more saved values are invalid
						Seasons.getPlugin().getLogger().severe("World " + worldName
								+ " has one or more invalid configuration parameters, world will load from default settings");
						cycles.add(new Cycle(world, DEFAULT_SEASON, DEFAULT_WEATHER, 1));
					} else { // Values successfully loaded
						cycles.add(new Cycle(world, season, weather, day));
					}
				} else {
					Cycle cycle = new Cycle(world, DEFAULT_SEASON, DEFAULT_WEATHER, 1);
					cycles.add(cycle);
					setWorldSave(cycle);
				}
			}
		}

		return cycles;
	}

	/**
	 * Saves all worlds currently stored in the configuration file so they can be loaded on server restart
	 */
	public static void saveAllWorlds() {
		for (Cycle cycle : Seasons.getCycles()) {
			setWorldSave(cycle);
		}
	}

	/**
	 * Sets the configuration values to save the given Cycles and their worlds
	 *
	 * @param cycle to save to the configuration file
	 */
	private static void setWorldSave(Cycle cycle) {
		String worldName = cycle.getWorld().getName();
		CONFIG.set(DEFAULT_PATH + worldName + ".day", cycle.getDay());
		CONFIG.set(DEFAULT_PATH + worldName + ".season", cycle.getSeason());
		CONFIG.set(DEFAULT_PATH + worldName + ".weather", cycle.getWeather());
	}

}
