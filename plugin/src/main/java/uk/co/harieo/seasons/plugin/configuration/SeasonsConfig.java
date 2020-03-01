package uk.co.harieo.seasons.plugin.configuration;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import uk.co.harieo.seasons.plugin.Seasons;

public class SeasonsConfig {

	private JavaPlugin plugin;

	private int version;
	private int daysPerSeason; // Days that must go by before the world moves to the next season
	private int secondsPerDamage; // Whether to activate the effects of the seasonal weathers
	private int roofHeight; // The height of the roof that would prevent an effect from the sky
	private boolean enableEffects; // How many ticks per damage dealt to a player on a harmful weather
	private List<String> disabledWorlds;
	private List<String> disabledWeathers;
	private List<String> disabledEffects;

	public SeasonsConfig(JavaPlugin plugin) {
		this.plugin = plugin;
		load();
	}

	public void load() {
		FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));
		version = config.getInt("version");
		daysPerSeason = config.getInt("DaysPerSeason");
		secondsPerDamage = config.getInt("SecondsOfDamage");
		roofHeight = config.getInt("RoofHeight");
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

	public int getRoofHeight() {
		return roofHeight;
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
