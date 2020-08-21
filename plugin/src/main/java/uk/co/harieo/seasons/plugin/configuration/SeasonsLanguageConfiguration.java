package uk.co.harieo.seasons.plugin.configuration;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Loads customisable messages from the language file
 */
public class SeasonsLanguageConfiguration implements ConfigurationProvider {

	private FileConfiguration config;
	private double currentVersion;

	@Override
	public String getFileName() {
		return "lang.yml";
	}

	@Override
	public boolean load(JavaPlugin plugin) {
		try {
			config = getConfigurationFile(plugin);
			currentVersion = config.getDouble("version");
			verifyVersion();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public double getLatestVersion() {
		return 3.0;
	}

	@Override
	public double getCurrentVersion() {
		return currentVersion;
	}

	/**
	 * Gets a String from the configuration file and converts colour codes where applicable
	 *
	 * @param key which represents this string in the config file
	 * @return the found string
	 */
	public String getString(String key) {
		return ChatColor.translateAlternateColorCodes('&', config.getString(key));
	}

	/**
	 * Calls {@link #getString(String)} and returns the value if it is not null. If the value is null, this method
	 * will return the alternative string provided.
	 *
	 * @param key which is the parameter for {@link #getString(String)}
	 * @param orElse the alternative string which is returned if the main string is null
	 * @return the found string from the config file or the alternative string if null
	 */
	public String getStringOrDefault(String key, String orElse) {
		if (config.isSet(key)) {
			return getString(key);
		} else {
			return orElse;
		}
	}

	/**
	 * Gets a list of strings from the configuration file, translating colour codes on each row where applicable
	 *
	 * @param key which represents this list in the config file
	 * @return the string list from the configuration file
	 */
	public List<String> getStringList(String key) {
		return config.getStringList(key).stream()
				.map(string -> ChatColor.translateAlternateColorCodes('&', string))
				.collect(Collectors.toList());
	}

}
