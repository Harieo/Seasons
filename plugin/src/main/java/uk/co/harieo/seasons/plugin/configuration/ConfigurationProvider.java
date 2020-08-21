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
	 * Retrieves a {@link FileConfiguration} from the provided plugin and creates the file from a resource, matching the file name, if it does
	 * not exist
	 *
	 * @param plugin to create and read files under
	 * @return the parsed configuration file
	 * @throws IOException if a file error occurs attempting to write data
	 */
	default FileConfiguration getConfigurationFile(JavaPlugin plugin) throws IOException {
		File dataFolder = plugin.getDataFolder();
		if (!dataFolder.exists()) {
			if (!dataFolder.mkdir()) {
				throw new IOException("Failed to create plugin data folder");
			}
		}

		String fileName = getFileName();
		File configFile = new File(dataFolder, fileName);
		if (!configFile.exists()) {
			try (InputStream stream = plugin.getResource(fileName)) {
				Files.copy(stream, configFile.toPath());
			}
		}

		return YamlConfiguration.loadConfiguration(configFile);
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
