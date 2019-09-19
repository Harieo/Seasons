package uk.co.harieo.seasons.plugin.configuration;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Optional;
import uk.co.harieo.seasons.plugin.Seasons;

public class SeasonsConfig {

	private int version;
	private int daysPerSeason; // Days that must go by before the world moves to the next season
	private int secondsPerDamage; // Whether to activate the effects of the seasonal weathers
	private boolean enableEffects; // How many ticks per damage dealt to a player on a harmful weather
	private List<String> disabledWorlds;
	private List<String> disabledWeathers;
	private List<String> disabledEffects;

	public SeasonsConfig(FileConfiguration config) {
		version = config.getInt("version");
		daysPerSeason = config.getInt("DaysPerSeason");
		secondsPerDamage = config.getInt("SecondsOfDamage");
		enableEffects = config.getBoolean("CustomWeathers");
		disabledWorlds = config.getStringList("disabled-worlds");
		disabledWeathers = config.getStringList("disabled-weathers");
		disabledEffects = config.getStringList("disabled-effects");

		// This must be set if the language config is ever updated past v1
		if (!config.contains("version") || config.getInt("version") < 2) {
			Seasons.getInstance().getPlugin().getLogger().warning(
					"WARNING: Your config.yml file is out of date, please backup and delete it to receive this update!");
		}
	}

	public int getVersion() {
		return version;
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

	public List<String> getDisabledEffects() {
		return disabledEffects;
	}
}
