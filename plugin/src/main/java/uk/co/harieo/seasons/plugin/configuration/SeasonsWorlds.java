package uk.co.harieo.seasons.plugin.configuration;

import com.google.gson.JsonNull;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import uk.co.harieo.seasons.plugin.Seasons;
import uk.co.harieo.seasons.plugin.models.Cycle;
import uk.co.harieo.seasons.plugin.models.Season;
import uk.co.harieo.seasons.plugin.models.Weather;

public class SeasonsWorlds {

	// These values will be used when the config contains either invalid values or none of these values
	private static final Season DEFAULT_SEASON = Season.SPRING;
	private static final Weather DEFAULT_WEATHER = Weather.BEAUTIFUL;
	private static final String CHILD_DIRECTORY_PATH = "/worlds";

	private final Seasons core;
	private final JavaPlugin plugin;
	private final List<Cycle> cycles;

	public SeasonsWorlds(Seasons core) {
		this.core = core;
		this.plugin = core.getPlugin();
		this.cycles = new ArrayList<>();
		loadAll();
	}

	/**
	 * Loops through all available worlds and performs {@link #addWorld(World)} on them
	 */
	private void loadAll() {
		List<String> disabledWorlds = core.getSeasonsConfig().getDisabledWorlds();
		for (World world : Bukkit.getWorlds()) { // Scan all loaded worlds
			if (!disabledWorlds.contains(world.getName())) { // Ensure server owner hasn't specified a manual disable
				addWorld(world);
			}
		}
	}

	/**
	 * Parses the configuration file for previously saved cycles in all applicable worlds. If no saved cycle for a world
	 * is found, it will be assumed the world is new and set with default values
	 *
	 * @param world to be added
	 */
	public void addWorld(World world) {
		if (world.getEnvironment() == Environment.NORMAL) { // Only normal worlds are subject to cycles
			String worldName = world.getName();
			Logger logger = plugin.getLogger();

			Cycle cycle = parseWorldSave(world);
			cycles.add(cycle);

			world.setStorm(cycle.getWeather().isStorm());
			logger.info("Loaded world '" + worldName + "' into Seasons!");
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
	 * Creates a blank cycle with the static default values: {@link #DEFAULT_SEASON} and {@link #DEFAULT_WEATHER} with
	 * default year as {@link SeasonsConfig#getStartingYear()} and day and season of year as 1
	 *
	 * @param world to create the cycle for
	 * @return a blank, default cycle for the specified world
	 */
	private Cycle createDefaultCycle(World world) {
		return new Cycle(world, DEFAULT_SEASON,
				world.getTime() > 12400 && world.getTime() < 23850 ? Weather.NIGHT : DEFAULT_WEATHER,
				Seasons.getInstance().getSeasonsConfig().getStartingYear(), 1, 1);
	}

	/**
	 * Saves all worlds currently stored in the configuration file so they can be loaded on server restart
	 *
	 * @return whether the save was successful in its entirety
	 */
	public boolean saveAllWorlds() {
		boolean errorEncountered = false;
		for (Cycle cycle : core.getCycles()) {
			try {
				saveWorld(cycle);
			} catch (FileAlreadyExistsException e) {
				e.printStackTrace();
				errorEncountered = true;
			}
		}

		return !errorEncountered; // Returns true for success, false for error
	}

	/**
	 * @return the default directory for world data to be saved in
	 */
	private File getDefaultDirectory() {
		File file = new File(plugin.getDataFolder() + CHILD_DIRECTORY_PATH);
		if (!file.exists()) {
			if (!file.mkdirs()) {
				plugin.getLogger()
						.severe("Failed to create necessary child directories, Seasons will be unable to save files!");
			}
		}
		return file;
	}

	/**
	 * Formats a world name into the proper data file name to ensure consistency
	 *
	 * @param worldName of the world the data file will represent
	 * @return the file name that should be assigned to the world
	 */
	private String getWorldFileName(String worldName) {
		return worldName + ".json";
	}

	/**
	 * Sets the configuration values to save the given Cycles and their worlds
	 *
	 * @param cycle to save to the configuration file
	 */
	private void saveWorld(Cycle cycle) throws FileAlreadyExistsException {
		String worldName = cycle.getWorld().getName();
		String fileName = getWorldFileName(worldName);

		File file = new File(getDefaultDirectory(), fileName);
		if (file.exists() && !file.delete()) {
			throw new FileAlreadyExistsException("Cannot overwrite file where necessary: " + fileName);
		}

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("worldName", worldName);
		jsonObject.addProperty("day", cycle.getDay());
		jsonObject.addProperty("season", cycle.getSeason().name());
		jsonObject.addProperty("seasonOfYear", cycle.getSeasonOfYear());
		jsonObject.addProperty("year", cycle.getYear());
		jsonObject.addProperty("weather", cycle.getWeather().name());

		try (FileWriter writer = new FileWriter(file)) {
			writer.write(jsonObject.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks whether there is a data file saved for the specified world
	 *
	 * @param world to check for the data file of
	 * @return whether the world has a data file
	 */
	private boolean isWorldSaved(World world) {
		return new File(getDefaultDirectory(), getWorldFileName(world.getName())).exists();
	}

	/**
	 * Parses a world save file for saved information on the previous instance of seasons that ran on that world. Note:
	 * This will return default values if no such file exists, which usually means the world didn't have a previous
	 * version of seasons running on it.
	 *
	 * @param world to parse the data file for
	 * @return the gathered information formatted as {@link Cycle}
	 */
	private Cycle parseWorldSave(World world) {
		int year = Seasons.getInstance().getSeasonsConfig().getStartingYear();
		int seasonOfYear = 1;
		int day = 1;
		Season season = DEFAULT_SEASON;
		Weather weather = DEFAULT_WEATHER;

		if (isWorldSaved(world)) { // Make sure we're reading from a real file
			File file = new File(getDefaultDirectory(), getWorldFileName(world.getName()));
			try (FileReader reader = new FileReader(file)) {
				JsonObject jsonObject = new JsonParser().parse(reader).getAsJsonObject();
				day = jsonObject.get("day").getAsInt();
				season = Season.valueOf(jsonObject.get("season").getAsString());
				if (jsonObject.get("year") == null || jsonObject.get("year") instanceof JsonNull) {
					year = Seasons.getInstance().getSeasonsConfig().getStartingYear();
					seasonOfYear = season.ordinal() + 1;
				} else {
					year = jsonObject.get("year").getAsInt();
					seasonOfYear = jsonObject.get("seasonOfYear").getAsInt();
				}
				weather = Weather.valueOf(jsonObject.get("weather").getAsString());
			} catch (IOException | IllegalArgumentException e) {
				e.printStackTrace();
				plugin.getLogger().severe("World data was invalid in file " + file.getName());
				return createDefaultCycle(world);
			}
		}

		return new Cycle(world, season,
				world.getTime() > 12400 && world.getTime() < 23850 ? Weather.NIGHT : weather,
				year, seasonOfYear, day);
	}

}
