package uk.co.harieo.seasons.plugin.configuration;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import uk.co.harieo.seasons.plugin.Seasons;

public class SeasonsLanguageConfiguration {

	private Seasons seasons;
	private FileConfiguration config;

	public SeasonsLanguageConfiguration(Seasons seasons) {
		this.seasons = seasons;
		loadConfig();

		// This must be set if the language config is ever updated past v1
		if (!config.contains("version") || config.getInt("version") < 1) {
			seasons.getPlugin().getLogger().warning(
					"WARNING: Your lang.yml file is out of date, please backup and delete it to receive this update!");
		}
	}

	public String getString(String key) {
		return ChatColor.translateAlternateColorCodes('&', config.getString(key));
	}

	public String getStringOrDefault(String key, String orElse) {
		if (config.isSet(key)) {
			return getString(key);
		} else {
			return orElse;
		}
	}

	public List<String> getStringList(String key) {
		return config.getStringList(key).stream()
				.map(string -> ChatColor.translateAlternateColorCodes('&', string))
				.collect(Collectors.toList());
	}

	public FileConfiguration getLanguageConfig() {
		return config;
	}

	public void loadConfig() {
		JavaPlugin plugin = seasons.getPlugin();
		File file = new File(plugin.getDataFolder(), "lang.yml");

		if (!file.exists()) {
			plugin.saveResource("lang.yml", false);
		}

		config = new YamlConfiguration();
		try {
			config.load(file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
			plugin.getLogger().severe("Failed to load lang.yml");
		}
	}

}
