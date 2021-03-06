package uk.co.harieo.seasons.plugin.configuration;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import uk.co.harieo.seasons.plugin.actionbar.SeasonsActionBar;
import uk.co.harieo.seasons.plugin.actionbar.TitleMessageHandler;

/**
 * Loads the plugin's settings from the configuration file
 */
public class SeasonsConfig implements ConfigurationProvider {

	private double currentVersion;
	private int daysPerSeason; // Days that must go by before the world moves to the next season
	private int secondsPerDamage; // Whether to activate the effects of the seasonal weathers
	private int roofHeight; // The height of the roof that would prevent an effect from the sky
	private boolean enableEffects; // How many ticks per damage dealt to a player on a harmful weather
	private List<String> disabledWorlds;
	private List<String> disabledWeathers;
	private List<String> disabledEffects;

	@Override
	public boolean load(JavaPlugin plugin) {
		FileConfiguration config;
		try {
			config = getConfiguration(plugin);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		currentVersion = config.getDouble("version");
		if (currentVersion != getLatestVersion() && !attemptVersionInjection(plugin, config)) {
			verifyVersion();
		}

		daysPerSeason = config.getInt("DaysPerSeason");
		secondsPerDamage = config.getInt("SecondsOfDamage");
		roofHeight = config.getInt("RoofHeight");
		enableEffects = config.getBoolean("CustomWeathers");

		if (config.getBoolean("ActionBar")) {
			SeasonsActionBar.start();
		} else {
			SeasonsActionBar.stop();
		}

		TitleMessageHandler.setOnSeasonChange(config.getBoolean("TitleMessages.season"));
		TitleMessageHandler.setOnWeatherChange(config.getBoolean("TitleMessages.weather"));

		disabledWorlds = config.getStringList("disabled-worlds");
		disabledWeathers = config.getStringList("disabled-weathers");
		disabledEffects = config.getStringList("disabled-effects");
		plugin.getLogger().info(disabledWorlds.size() + " worlds have been disabled, " + disabledWeathers.size()
				+ " weathers have been disabled and " + disabledEffects.size() + " have been disabled");

		return true;
	}

	@Override
	public String getFileName() {
		return "config.yml";
	}

	@Override
	public double getLatestVersion() {
		return 4.0;
	}

	@Override
	public double getCurrentVersion() {
		return currentVersion;
	}

	/**
	 * Attempts to inject values into a configuration file to match the latest version without having to manually
	 * regenerate the file
	 *
	 * @param plugin the plugin which is managing the configuration file
	 * @param config to inject values into
	 * @return whether the update was successful. If false, values could not be injected and the config is out of date.
	 */
	public boolean attemptVersionInjection(JavaPlugin plugin, FileConfiguration config) {
		if (getCurrentVersion() == 3) {
			double versionUpdatingTo = 4.0;
			config.set("TitleMessages.season", false);
			config.set("TitleMessages.weather", false);
			config.set("version", versionUpdatingTo);
			try {
				config.save(getFile(plugin));
				plugin.getLogger().info("Your configuration file has been automatically updated to version "
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
	 * @return the amount of days a season should contain
	 */
	public int getDaysPerSeason() {
		return daysPerSeason;
	}

	/**
	 * @return the amount of seconds which should pass between a player being damaged by an effect
	 */
	public int getSecondsPerDamage() {
		return secondsPerDamage;
	}

	/**
	 * @return how many blocks above a player should be scanned to detect a roof
	 */
	public int getRoofHeight() {
		return roofHeight;
	}

	/**
	 * @return whether custom weather effects have been enabled
	 */
	public boolean hasEnabledEffects() {
		return enableEffects;
	}

	/**
	 * @return a list of the names of worlds which seasons should not handle
	 */
	public List<String> getDisabledWorlds() {
		return disabledWorlds;
	}

	/**
	 * @return a list of the names of weathers which seasons should not use
	 */
	public List<String> getDisabledWeathers() {
		return disabledWeathers;
	}

	/**
	 * @return a list of the names of effects which seasons should not use
	 */
	public List<String> getDisabledEffects() {
		return disabledEffects;
	}

}
