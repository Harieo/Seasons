package uk.co.harieo.seasons.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
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

	private SeasonsConfig CONFIG;
	private SeasonsWorlds WORLD_HANDLER;
	private SeasonsLanguageConfiguration LANGUAGE_CONFIG;
	private JavaPlugin PLUGIN;
	private final List<Effect> EFFECTS = new ArrayList<>();

	public Seasons(JavaPlugin plugin, FileConfiguration configuration) {
		INSTANCE = this;
		PLUGIN = plugin;
		CONFIG = new SeasonsConfig(configuration); // Load settings
		LANGUAGE_CONFIG = new SeasonsLanguageConfiguration(this);
	}

	public void startup() {
		setPrefix();
		WORLD_HANDLER = new SeasonsWorlds(this); // Load saved worlds
		new WorldTicker().runTaskTimer(PLUGIN, 0, 20); // Begin the cycles

		ChangeCommand changeCommand = new ChangeCommand();
		Bukkit.getPluginCommand("season").setExecutor(new SeasonsCommand());
		Bukkit.getPluginCommand("changeday").setExecutor(changeCommand);
		Bukkit.getPluginCommand("changeweather").setExecutor(changeCommand);
		Bukkit.getPluginCommand("changeseason").setExecutor(changeCommand);
		Bukkit.getPluginManager().registerEvents(new SeasonalListener(), PLUGIN);

		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			new SeasonsPlaceholderExpansion(this).register();
		}
	}

	public void disable() {
		PLUGIN.getLogger().info("Saving all seasons-enabled worlds...");

		if (WORLD_HANDLER.saveAllWorlds()) {
			PLUGIN.getLogger().info("Saved all worlds successfully");
		} else {
			PLUGIN.getLogger().severe("Failed to save all worlds");
		}

		for (UUID uuid : SeasonsPotionEffect.PENDING.keySet()) {
			Player player = Bukkit.getPlayer(uuid);
			if (player != null) {
				player.removePotionEffect(SeasonsPotionEffect.PENDING.get(uuid));
			}
		}
	}

	public void addEffects(Effect... effects) {
		EFFECTS.addAll(Arrays.asList(effects));
	}

	public JavaPlugin getPlugin() {
		return PLUGIN;
	}

	public SeasonsConfig getSeasonsConfig() {
		return CONFIG;
	}

	public SeasonsLanguageConfiguration getLanguageConfig() {
		return LANGUAGE_CONFIG;
	}

	public void setPrefix() {
		PREFIX = LANGUAGE_CONFIG.getStringOrDefault("misc.prefix",
				ChatColor.GOLD + ChatColor.BOLD.toString() + "Seasons" + ChatColor.GRAY + "∙ " + ChatColor.RESET);
	}

	public List<Cycle> getCycles() {
		return WORLD_HANDLER.getParsedCycles();
	}

	/**
	 * Retrieves the {@link Cycle} specific to a specified world
	 *
	 * @param world to find the cycle of
	 * @return the {@link Cycle} instance or null if none exists
	 */
	public Cycle getWorldCycle(World world) {
		for (Cycle cycle : WORLD_HANDLER.getParsedCycles()) {
			if (cycle.getWorld().equals(world)) {
				return cycle;
			}
		}

		return null;
	}

	public SeasonsWorlds getWorldHandler() {
		return WORLD_HANDLER;
	}

	/**
	 * Adds a {@link Cycle} to the list of used cycles so that the WorldTicker can begin processing it
	 *
	 * @param cycle of the world that is to be imported
	 */
	public void addCycle(Cycle cycle) {
		Validate.notNull(cycle);
		WORLD_HANDLER.getParsedCycles().add(cycle);
	}

	public List<Effect> getEffects() {
		return EFFECTS;
	}

	public static Seasons getInstance() {
		return INSTANCE;
	}
}
