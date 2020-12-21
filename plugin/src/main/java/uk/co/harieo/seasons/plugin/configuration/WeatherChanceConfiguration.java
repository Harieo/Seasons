package uk.co.harieo.seasons.plugin.configuration;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.*;
import uk.co.harieo.seasons.plugin.Seasons;
import uk.co.harieo.seasons.plugin.models.Weather;

public class WeatherChanceConfiguration implements ConfigurationProvider {

	private final Map<Weather, Integer> loadedChances = new HashMap<>();
	private double currentVersion;

	@Override
	public String getFileName() {
		return "chances.yml";
	}

	@Override
	public boolean load(JavaPlugin plugin) {
		try {
			FileConfiguration configuration = getConfiguration(plugin);
			currentVersion = configuration.getDouble("version");
			loadedChances.clear(); // In-case this is a reload
			for (Weather weather : Weather.values()) {
				String key = weather.name().toLowerCase();
				if (configuration.isSet(key)) {
					loadedChances.put(weather, configuration.getInt(key));
				} else if (weather.getDefaultChance() > 0) { // Don't allow chance to be set for unnatural weathers
					configuration.set(key, weather.getDefaultChance());
				}
			}
			configuration.save(getFile(plugin));

			verifyVersion();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Retrieves the chance of a weather occurring from the configuration file or the default if the configuration file
	 * did not specify a value
	 *
	 * @param weather to get the chance for
	 * @return the chance of the specified weather occurring
	 */
	public int getChance(Weather weather) {
		return loadedChances.getOrDefault(weather, weather.getDefaultChance());
	}

	/**
	 * Selects a random weather from a collection of possibilities
	 *
	 * @param possibilities a collection of weathers to select from
	 * @return the chosen weather
	 */
	public Weather pickRandomWeather(Collection<Weather> possibilities) {
		possibilities.removeIf(weather -> weather.getDefaultChance() <= 0);

		int highestBound = 0;
		for (Weather weather : possibilities) {
			highestBound += getChance(weather);
		}

		int randomNumber = Seasons.RANDOM.nextInt(highestBound);
		int sum = 0;
		for (Weather weather : possibilities) {
			sum += getChance(weather);
			if (randomNumber < sum) {
				return weather;
			}
		}

		throw new IllegalStateException("Sum of chances did not produce a result");
	}

	@Override
	public double getLatestVersion() {
		return 1;
	}

	@Override
	public double getCurrentVersion() {
		return currentVersion;
	}

}
