package uk.co.harieo.seasons.plugin.configuration;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import uk.co.harieo.seasons.plugin.Seasons;

/**
 * An interface for a class which will read and write data to a YAML configuration file which is present as a resource
 */
public interface ConfigurationProvider {

	/**
	 * @return the name of the configuration file, matching an accessible resource
	 */
	String getFileName();

	/**
	 * Load data from the configuration file
	 *
	 * @param plugin which is loading this configuration file
	 * @return whether the load was successful
	 */
	boolean load(JavaPlugin plugin);

	/**
	 * @return the version number which represents the latest update of this configuration
	 */
	double getLatestVersion();

	/**
	 * @return the version number which the configuration file is currently at
	 */
	double getCurrentVersion();

	/**
	 * Creates an instance of {@link File} inside the provided plugin's data folder with {@link #getFileName()}
	 *
	 * @param plugin which handles the file
	 * @return the configuration file
	 */
	default File getFile(JavaPlugin plugin) {
		return new File(plugin.getDataFolder(), getFileName());
	}

	/**
	 * Retrieves a {@link FileConfiguration} from the provided plugin and creates the file from a resource, matching the file name, if it does
	 * not exist
	 *
	 * @param plugin to create and read files under
	 * @return the parsed configuration file
	 * @throws IOException if a file error occurs attempting to write data
	 */
	default FileConfiguration getConfiguration(JavaPlugin plugin) throws IOException {
		File dataFolder = plugin.getDataFolder();
		if (!dataFolder.exists()) {
			if (!dataFolder.mkdir()) {
				throw new IOException("Failed to create plugin data folder");
			}
		}

		String fileName = getFileName();
		File configFile = getFile(plugin);
		if (!configFile.exists()) {
			try (InputStream stream = plugin.getResource(fileName)) {
				if (stream != null) {
					Files.copy(stream, configFile.toPath());
				} else {
					throw new IOException("Failed to get resource stream for " + fileName);
				}
			}
		}

		// If no version is stated, mark it as the latest version
		FileConfiguration configuration = YamlConfiguration.loadConfiguration(configFile);
		if (!configuration.isSet("version")) {
			configuration.set("version", getLatestVersion());
			configuration.save(configFile);
		}

		return configuration;
	}

	/**
	 * Checks the current version against the latest available version for the configuration file and reports if there
	 * is a newer update to the file
	 */
	default void verifyVersion() {
		if (getCurrentVersion() < getLatestVersion()) {
			Seasons.getInstance().getPlugin().getLogger().warning(
					"WARNING: Your " + getFileName()
							+ " file is out of date, please backup and delete it to receive this update!");
		}
	}

}
