package uk.co.harieo.seasons.plugin.configuration;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class SeasonsConfig {

	private int daysPerSeason; // Days that must go by before the world moves to the next season
	private int secondsPerDamage; // Whether to activate the effects of the seasonal weathers
	private boolean enableEffects; // How many ticks per damage dealt to a player on a harmful weather
	private List<String> disabledWorlds;
	private List<String> disabledWeathers;

	public SeasonsConfig(FileConfiguration config) {
		daysPerSeason = config.getInt("DaysPerSeason");
		secondsPerDamage = config.getInt("SecondsOfDamage");
		enableEffects = config.getBoolean("CustomWeathers");
		disabledWorlds = config.getStringList("disabled-worlds");
		disabledWeathers = config.getStringList("disabled-weathers");
	}

	public int getDaysPerSeason() {
		return daysPerSeason;
	}

	public int getSecondsPerDamage() {
		return secondsPerDamage;
	}

	public boolean hasEnabledEffects() {
		return enableEffects;
	}

	public List<String> getDisabledWorlds() {
		return disabledWorlds;
	}

	public List<String> getDisabledWeathers() {
		return disabledWeathers;
	}

}
