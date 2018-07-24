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

	private static final String DEFAULT_PATH = "worlds.";

	// These values will be used when the config contains either invalid values or none of these values
	private static final Season DEFAULT_SEASON = Season.SPRING;
	private static final Weather DEFAULT_WEATHER = Weather.BEAUTIFUL;

	private FileConfiguration config;
	private List<Cycle> cycles;

	public SeasonsWorlds(FileConfiguration config) {
		this.config = config;
		this.cycles = new ArrayList<>();
		parse();
	}

	/**
	 * Parses the configuration file for previously saved cycles in all applicable worlds If no saved cycle for a world
	 * is found, it will be assumed the world is new and set with default values
	 */
	private void parse() {
		for (World world : Bukkit.getWorlds()) { // Scan all loaded worlds
			if (world.getEnvironment().equals(Environment.NORMAL)) { // Only normal worlds are subject to cycles
				String worldName = world.getName();
				String path = DEFAULT_PATH + worldName;

				int day = 1;
				Season season = DEFAULT_SEASON;
				Weather weather = DEFAULT_WEATHER;
				boolean save = false;

				if (config.contains(path)) { // Config knows of this world, load settings
					day = config.getInt(path + ".day");
					season = Season.fromName(config.getString(path + ".season"));
					weather = Weather.fromName(config.getString(path + ".weather"));

					if (season == null || weather == null || day < 1) { // One or more saved values are invalid
						Seasons.getPlugin().getLogger().severe("World " + worldName
								+ " has one or more invalid configuration parameters, world will load from default settings");
						day = 1;
						season = DEFAULT_SEASON;
						weather = DEFAULT_WEATHER;
					}
				} else {
					save = true;
				}

				Cycle cycle = new Cycle(world, season,
						world.getTime() > 12400 && world.getTime() < 23850 ? Weather.NIGHT : weather, day);
				cycles.add(cycle);
				if (save) {
					saveWorld(cycle);
				}
			}
		}
	}

	/**
	 * Parses the default configuration file for any previously saved worlds and loads those settings if found
	 *
	 * @return a list of cycles of all applicable worlds, regardless of whether they were found in the configuration
	 * file
	 */
	public List<Cycle> getParsedCycles() {
		return cycles;
	}

	/**
	 * Saves all worlds currently stored in the configuration file so they can be loaded on server restart
	 */
	public void saveAllWorlds() {
		for (Cycle cycle : Seasons.getCycles()) {
			saveWorld(cycle);
		}
	}

	/**
	 * Sets the configuration values to save the given Cycles and their worlds
	 *
	 * @param cycle to save to the configuration file
	 */
	private void saveWorld(Cycle cycle) {
		String worldName = cycle.getWorld().getName();
		config.set(DEFAULT_PATH + worldName + ".day", cycle.getDay());
		config.set(DEFAULT_PATH + worldName + ".season", cycle.getSeason().getName());
		config.set(DEFAULT_PATH + worldName + ".weather", cycle.getWeather().getName());
		Seasons.getPlugin().saveConfig();
	}

}
