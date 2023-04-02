package uk.co.harieo.seasons.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Logger;
import org.apache.commons.lang.Validate;
import uk.co.harieo.seasons.plugin.actionbar.TitleMessageHandler;
import uk.co.harieo.seasons.plugin.commands.ChangeCommand;
import uk.co.harieo.seasons.plugin.commands.SeasonsCommand;
import uk.co.harieo.seasons.plugin.configuration.SeasonsConfig;
import uk.co.harieo.seasons.plugin.configuration.SeasonsLanguageConfiguration;
import uk.co.harieo.seasons.plugin.configuration.SeasonsWorlds;
import uk.co.harieo.seasons.plugin.configuration.WeatherChanceConfiguration;
import uk.co.harieo.seasons.plugin.models.Cycle;
import uk.co.harieo.seasons.plugin.models.effect.Effect;
import uk.co.harieo.seasons.plugin.models.effect.SeasonsPotionEffect;
import uk.co.harieo.seasons.plugin.placeholders.SeasonsPlaceholderExpansion;

public class Seasons {

	public static String PREFIX;
	public static final Random RANDOM = new Random();
	private static Seasons INSTANCE;

	private final SeasonsConfig config;
	private final SeasonsLanguageConfiguration languageConfig;
	private final WeatherChanceConfiguration weatherChanceConfiguration;
	private final JavaPlugin plugin;
	private final List<Effect> effects = new ArrayList<>();

	private SeasonsWorlds worldHandler;

	/**
	 * The main executor class for the entire plugin, where all systems are activated and handled. This serves also as
	 * the interface which allows access to the API.
	 *
	 * @param plugin which is running this system
	 */
	public Seasons(JavaPlugin plugin) {
		INSTANCE = this;
		this.plugin = plugin;

		Logger logger = plugin.getLogger();

		Set<String> fileNameErrors = new HashSet<>();
		this.config = new SeasonsConfig(); // Load settings
		if (!config.load(plugin)) {
			fileNameErrors.add(config.getFileName());
		}

		this.languageConfig = new SeasonsLanguageConfiguration();
		if (!languageConfig.load(plugin)) {
			fileNameErrors.add(languageConfig.getFileName());
		}

		this.weatherChanceConfiguration = new WeatherChanceConfiguration();
		if (!weatherChanceConfiguration.load(plugin)) {
			fileNameErrors.add(weatherChanceConfiguration.getFileName());
		}

		for (String fileName : fileNameErrors) {
			logger.severe("Failed to load settings for " + fileName);
		}
	}

	/**
	 * Activates all systems and begins the {@link WorldTicker} to start measuring time
 	 */
	public void startup() {
		setPrefix();
		plugin.getLogger().info("Preparing to load...");
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			this.worldHandler = new SeasonsWorlds(this); // Load saved worlds
			new WorldTicker().runTaskTimer(plugin, 0, 20); // Begin the cycles

			ChangeCommand changeCommand = new ChangeCommand();
			setCommandExecutor("season", new SeasonsCommand());
			setCommandExecutor("changeday", changeCommand);
			setCommandExecutor("changeweather", changeCommand);
			setCommandExecutor("changeseason", changeCommand);
			setCommandExecutor("changeyear", changeCommand);

			PluginManager pluginManager = Bukkit.getPluginManager();
			pluginManager.registerEvents(new SeasonalListener(), plugin);
			pluginManager.registerEvents(new TitleMessageHandler(), plugin);

			if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
				new SeasonsPlaceholderExpansion(this).register();
			}
			plugin.getLogger().info("Seasons has been loaded!");
		});
	}

	/**
	 * Sets the {@link CommandExecutor} for a {@link PluginCommand} with the specified alias, if it exists
	 *
	 * @param command alias of the plugin command
	 * @param executor which executes this command
	 */
	private void setCommandExecutor(String command, CommandExecutor executor) {
		PluginCommand pluginCommand = Bukkit.getPluginCommand(command);
		if (pluginCommand != null) {
			pluginCommand.setExecutor(executor);
		}
	}

	/**
	 * Safely deactivates all systems and saves the loaded worlds
	 */
	public void disable() {
		plugin.getLogger().info("Saving all seasons-enabled worlds...");

		if (worldHandler.saveAllWorlds()) {
			plugin.getLogger().info("Saved all worlds successfully");
		} else {
			plugin.getLogger().severe("Failed to save all worlds");
		}

		for (UUID uuid : SeasonsPotionEffect.PENDING.keySet()) {
			Player player = Bukkit.getPlayer(uuid);
			if (player != null) {
				player.removePotionEffect(SeasonsPotionEffect.PENDING.get(uuid));
			}
		}
	}

	/**
	 * Adds an effect to the list of active effects
	 *
	 * @param effects which are active
	 */
	public void addEffects(Effect... effects) {
		this.effects.addAll(Arrays.asList(effects));
	}

	/**
	 * @return a list of loaded effects
	 */
	public List<Effect> getEffects() {
		return effects;
	}

	/**
	 * @return the plugin which is running seasons
	 */
	public JavaPlugin getPlugin() {
		return plugin;
	}

	/**
	 * @return the handler which stores the plugin settings
	 */
	public SeasonsConfig getSeasonsConfig() {
		return config;
	}

	/**
	 * @return the handler for the language file
	 */
	public SeasonsLanguageConfiguration getLanguageConfig() {
		return languageConfig;
	}

	/**
	 * @return the handler for the chances that weathers might get selected
	 */
	public WeatherChanceConfiguration getWeatherChanceConfiguration() {
		return weatherChanceConfiguration;
	}

	/**
	 * @return the handler which stores data about the status of loaded worlds
	 */
	public SeasonsWorlds getWorldHandler() {
		return worldHandler;
	}

	/**
	 * @return the cycles of all loaded worlds
	 */
	public List<Cycle> getCycles() {
		return worldHandler.getParsedCycles();
	}

	/**
	 * Retrieves the {@link Cycle} specific to a specified world
	 *
	 * @param world to find the cycle of
	 * @return the {@link Cycle} instance or null if none exists
	 */
	public Cycle getWorldCycle(World world) {
		for (Cycle cycle : worldHandler.getParsedCycles()) {
			if (cycle.getWorld().equals(world)) {
				return cycle;
			}
		}

		return null;
	}

	/**
	 * Adds a {@link Cycle} to the list of used cycles so that the WorldTicker can begin processing it
	 *
	 * @param cycle of the world that is to be imported
	 */
	public void addCycle(Cycle cycle) {
		Validate.notNull(cycle);
		worldHandler.getParsedCycles().add(cycle);
	}

	/**
	 * Sets the plugin's prefix, either custom from the language file or default
	 */
	public void setPrefix() {
		PREFIX = languageConfig.getStringOrDefault("misc.prefix",
				ChatColor.GOLD + ChatColor.BOLD.toString() + "Seasons" + ChatColor.GRAY + "∙ " + ChatColor.RESET)
				.orElse("");
	}

	/**
	 * @return the instance of this class created when the plugin starts
	 */
	public static Seasons getInstance() {
		return INSTANCE;
	}
}
