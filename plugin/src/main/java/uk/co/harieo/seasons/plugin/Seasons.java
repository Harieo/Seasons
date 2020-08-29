package uk.co.harieo.seasons.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Logger;
import org.apache.commons.lang.Validate;
import uk.co.harieo.seasons.plugin.commands.ChangeCommand;
import uk.co.harieo.seasons.plugin.commands.SeasonsCommand;
import uk.co.harieo.seasons.plugin.configuration.SeasonsConfig;
import uk.co.harieo.seasons.plugin.configuration.SeasonsLanguageConfiguration;
import uk.co.harieo.seasons.plugin.configuration.SeasonsWorlds;
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
	private final JavaPlugin plugin;
	private final List<Effect> effects = new ArrayList<>();

	private SeasonsWorlds worldHandler;

	public Seasons(JavaPlugin plugin) {
		INSTANCE = this;
		this.plugin = plugin;

		Logger logger = plugin.getLogger();
		this.config = new SeasonsConfig(); // Load settings
		if (!config.load(plugin)) {
			logger.severe("Failed to load settings for " + config.getFileName());
		}

		this.languageConfig = new SeasonsLanguageConfiguration();
		if (!languageConfig.load(plugin)) {
			logger.severe("Failed to load settings for " + languageConfig.getFileName());
		}
	}

	public void startup() {
		setPrefix();
		this.worldHandler = new SeasonsWorlds(this); // Load saved worlds
		new WorldTicker().runTaskTimer(plugin, 0, 20); // Begin the cycles

		ChangeCommand changeCommand = new ChangeCommand();
		Bukkit.getPluginCommand("season").setExecutor(new SeasonsCommand());
		Bukkit.getPluginCommand("changeday").setExecutor(changeCommand);
		Bukkit.getPluginCommand("changeweather").setExecutor(changeCommand);
		Bukkit.getPluginCommand("changeseason").setExecutor(changeCommand);
		Bukkit.getPluginManager().registerEvents(new SeasonalListener(), plugin);

		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			new SeasonsPlaceholderExpansion(this).register();
		}
	}

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

	public void addEffects(Effect... effects) {
		this.effects.addAll(Arrays.asList(effects));
	}

	public JavaPlugin getPlugin() {
		return plugin;
	}

	public SeasonsConfig getSeasonsConfig() {
		return config;
	}

	public SeasonsLanguageConfiguration getLanguageConfig() {
		return languageConfig;
	}

	public void setPrefix() {
		PREFIX = languageConfig.getStringOrDefault("misc.prefix",
				ChatColor.GOLD + ChatColor.BOLD.toString() + "Seasons" + ChatColor.GRAY + "∙ " + ChatColor.RESET)
				.orElse("");
	}

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

	public SeasonsWorlds getWorldHandler() {
		return worldHandler;
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

	public List<Effect> getEffects() {
		return effects;
	}

	public static Seasons getInstance() {
		return INSTANCE;
	}
}
