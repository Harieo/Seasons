package uk.co.harieo.seasons;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import uk.co.harieo.seasons.commands.ChangeCommand;
import uk.co.harieo.seasons.commands.SeasonsCommand;
import uk.co.harieo.seasons.configuration.SeasonsConfig;
import uk.co.harieo.seasons.configuration.SeasonsWorlds;
import uk.co.harieo.seasons.models.Cycle;

public class Seasons extends JavaPlugin {

	public static final String PREFIX =
			ChatColor.GOLD + ChatColor.BOLD.toString() + "Seasons" + ChatColor.GRAY + "∙ " + ChatColor.RESET;
	public static final Random RANDOM = new Random();

	private static List<Cycle> CYCLES = new ArrayList<>();
	private static Seasons INSTANCE;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		new SeasonsConfig(getConfig()); // Load settings
		CYCLES = SeasonsWorlds.parseWorldsAutosave(); // Load saved worlds

		new WorldTicker().runTaskTimer(this, 0, 20); // Begin the cycles

		ChangeCommand changeCommand = new ChangeCommand();
		Bukkit.getPluginCommand("season").setExecutor(new SeasonsCommand());
		Bukkit.getPluginCommand("changeday").setExecutor(changeCommand);
		Bukkit.getPluginCommand("changeweather").setExecutor(changeCommand);
		Bukkit.getPluginCommand("changeseason").setExecutor(changeCommand);

		INSTANCE = this;
	}

	@Override
	public void onDisable() {
		SeasonsWorlds.saveAllWorlds();
	}

	public static JavaPlugin getPlugin() {
		return INSTANCE;
	}

	public static List<Cycle> getCycles() {
		return CYCLES;
	}

	/**
	 * Retrieves the {@link Cycle} specific to a specified world
	 *
	 * @param world to find the cycle of
	 * @return the {@link Cycle} instance or null if none exists
	 */
	public static Cycle getWorldCycle(World world) {
		for (Cycle cycle : CYCLES) {
			if (cycle.getWorld().equals(world)) {
				return cycle;
			}
		}

		return null;
	}
}
