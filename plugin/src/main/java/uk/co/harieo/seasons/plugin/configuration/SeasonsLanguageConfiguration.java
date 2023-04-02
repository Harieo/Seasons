package uk.co.harieo.seasons.plugin.configuration;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Loads customisable messages from the language file
 */
public class SeasonsLanguageConfiguration implements ConfigurationProvider {

	private static final String CANCEL_KEY = "none"; // Used to stop a message being sent

	private FileConfiguration config;
	private double currentVersion;

	@Override
	public String getFileName() {
		return "lang.yml";
	}

	@Override
	public boolean load(JavaPlugin plugin) {
		try {
			config = getConfiguration(plugin);
			currentVersion = config.getDouble("version");
			if (currentVersion != getLatestVersion() && !attemptVersionInjection(plugin, config)) {
				verifyVersion();
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public double getLatestVersion() {
		return 4.1;
	}

	@Override
	public double getCurrentVersion() {
		return currentVersion;
	}

	public boolean attemptVersionInjection(JavaPlugin plugin, FileConfiguration config) {
		if (getCurrentVersion() < 4.1) {
			if (getCurrentVersion() == 3) {
				config.set("misc.weather-change", "&7The weather has changed");
				config.set("misc.season-change", "&7The season has changed");
			} else if (getCurrentVersion() == 4) {
				config.set("misc.year-change", "&7The year has changed");
				config.set("misc.year-color", "&c");
				config.set("command.force-year", "&7Time shatters before you and now it is &bYear %year%");
				List<String> season_info = config.getStringList("command.season-info");
				season_info.add("&4If you have enabled the year please add the year here! Example: &c%year% &7- &a%season-number%&7/&c%seasons-per-year%");
				config.set("command.season-info", season_info);
				config.set("year-trigger", "&aEntered the %year%! Good luck!");
			} else return false;

			double versionUpdatingTo = 4.1;
			config.set("version", versionUpdatingTo);
			try {
				config.save(getFile(plugin));
				plugin.getLogger().info("Your language file has been automatically updated to version "
						+ versionUpdatingTo);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Gets a String from the configuration file and converts colour codes where applicable
	 *
	 * @param key which represents this string in the config file
	 * @return the found string
	 */
	public Optional<String> getString(String key) {
		String value = config.getString(key);
		if (value == null || isCancelled(value)) {
			return Optional.empty();
		} else {
			return Optional.of(ChatColor.translateAlternateColorCodes('&', value));
		}
	}

	/**
	 * Calls {@link #getString(String)} and returns the value if it is not null. If the value is null, this method
	 * will return the alternative string provided.
	 *
	 * @param key which is the parameter for {@link #getString(String)}
	 * @param orElse the alternative string which is returned if the main string is null
	 * @return the found string from the config file or the alternative string if null
	 */
	public Optional<String> getStringOrDefault(String key, String orElse) {
		if (config.isSet(key)) {
			return getString(key);
		} else {
			return Optional.of(orElse);
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

	/**
	 * Checks whether a value from the configuration file indicates that the message should not be sent
	 *
	 * @param value string value from the configuration file
	 * @return whether to use this value in a message
	 */
	private boolean isCancelled(String value) {
		return value.equalsIgnoreCase(CANCEL_KEY);
	}

}
