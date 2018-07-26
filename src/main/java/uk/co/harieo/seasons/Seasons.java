package uk.co.harieo.seasons;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import org.apache.commons.lang.Validate;
import uk.co.harieo.seasons.commands.ChangeCommand;
import uk.co.harieo.seasons.commands.SeasonsCommand;
import uk.co.harieo.seasons.configuration.SeasonsConfig;
import uk.co.harieo.seasons.configuration.SeasonsWorlds;
import uk.co.harieo.seasons.effects.bad.*;
import uk.co.harieo.seasons.effects.good.*;
import uk.co.harieo.seasons.models.Cycle;
import uk.co.harieo.seasons.models.effect.Effect;
import uk.co.harieo.seasons.models.effect.SeasonsPotionEffect;

public class Seasons extends JavaPlugin {

	public static final String PREFIX =
			ChatColor.GOLD + ChatColor.BOLD.toString() + "Seasons" + ChatColor.GRAY + "∙ " + ChatColor.RESET;
	public static final Random RANDOM = new Random();

	private static FileConfiguration CONFIG;
	private static SeasonsWorlds WORLD_HANDLER;
	private static JavaPlugin INSTANCE;
	private static final List<Effect> EFFECTS = new ArrayList<>();

	@Override
	public void onEnable() {
		INSTANCE = this;

		FileConfiguration config = getConfig();
		config.addDefault("DaysPerSeason", 30);
		config.addDefault("SecondsOfDamage", 3);
		config.addDefault("CustomWeathers", true);

		getConfig().options().copyDefaults(true);
		saveConfig();
		CONFIG = getConfig();
		new SeasonsConfig(CONFIG); // Load settings
		WORLD_HANDLER = new SeasonsWorlds(CONFIG); // Load saved worlds

		new WorldTicker().runTaskTimer(this, 0, 20); // Begin the cycles

		ChangeCommand changeCommand = new ChangeCommand();
		Bukkit.getPluginCommand("season").setExecutor(new SeasonsCommand());
		Bukkit.getPluginCommand("changeday").setExecutor(changeCommand);
		Bukkit.getPluginCommand("changeweather").setExecutor(changeCommand);
		Bukkit.getPluginCommand("changeseason").setExecutor(changeCommand);
		Bukkit.getPluginManager().registerEvents(new SeasonalListener(), this);

		// Register effects
		addEffects(new Devastation(), new FeelsGood(), new FluffyCoat(), new Frostbite(), new HoldOntoYourHat(),
				new HotSand(), new Icy(), new PrimitiveHeating(), new Revitalized(), new SolderingIron(),
				new StrongCurrent(), new Sweating(), new TheShivers(), new WarmingStew(), new WetMud(),
				new WindInYourBoots());
	}

	@Override
	public void onDisable() {
		WORLD_HANDLER.saveAllWorlds();
		for (UUID uuid : SeasonsPotionEffect.PENDING.keySet()) {
			Player player = Bukkit.getPlayer(uuid);
			if (player != null) {
				player.removePotionEffect(SeasonsPotionEffect.PENDING.get(uuid));
			}
		}
	}

	private void addEffects(Effect... effects) {
		EFFECTS.addAll(Arrays.asList(effects));
	}

	public static JavaPlugin getPlugin() {
		return INSTANCE;
	}

	public static FileConfiguration getSeasonsConfig() {
		return CONFIG;
	}

	public static List<Cycle> getCycles() {
		return WORLD_HANDLER.getParsedCycles();
	}

	/**
	 * Retrieves the {@link Cycle} specific to a specified world
	 *
	 * @param world to find the cycle of
	 * @return the {@link Cycle} instance or null if none exists
	 */
	public static Cycle getWorldCycle(World world) {
		for (Cycle cycle : WORLD_HANDLER.getParsedCycles()) {
			if (cycle.getWorld().equals(world)) {
				return cycle;
			}
		}

		return null;
	}

	public static SeasonsWorlds getWorldHandler() {
		return WORLD_HANDLER;
	}

	/**
	 * Adds a {@link Cycle} to the list of used cycles so that the WorldTicker can begin processing it
	 *
	 * @param cycle of the world that is to be imported
	 */
	public static void addCycle(Cycle cycle) {
		Validate.notNull(cycle);
		WORLD_HANDLER.getParsedCycles().add(cycle);
	}

	public static List<Effect> getEffects() {
		return EFFECTS;
	}
}
