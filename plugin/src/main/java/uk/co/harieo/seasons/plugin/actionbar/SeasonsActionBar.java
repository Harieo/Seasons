package uk.co.harieo.seasons.plugin.actionbar;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import uk.co.harieo.seasons.plugin.Seasons;
import uk.co.harieo.seasons.plugin.models.Cycle;
import uk.co.harieo.seasons.plugin.models.Season;
import uk.co.harieo.seasons.plugin.models.Weather;

public class SeasonsActionBar implements Runnable {

	private static final SeasonsActionBar instance = new SeasonsActionBar();

	private BukkitTask task;

	@Override
	public void run() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			World world = player.getWorld();
			Cycle cycle = Seasons.getInstance().getWorldCycle(world);
			if (cycle != null) {
				Season season = cycle.getSeason();
				Weather weather = cycle.getWeather();
				ComponentBuilder builder = new ComponentBuilder();
				if (Seasons.getInstance().getSeasonsConfig().isYearsEnabled()) {
					builder.append(Seasons.getInstance().getLanguageConfig().getString("misc.year-color")
										   .orElse(String.valueOf(ChatColor.RED))
										   + cycle.getYear())
							.append("∙ ").color(ChatColor.GRAY);
				}
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
											builder.append(season.getName()).color(season.getColor().asBungee())
													.append("∙ ").color(ChatColor.GRAY)
													.append(weather.getName())
													.create());
			}
		}
	}

	public static void start() {
		if (instance.task == null) {
			instance.task = Bukkit.getScheduler().runTaskTimer(Seasons.getInstance().getPlugin(), instance, 0, 20 * 2);
		}
	}

	public static void stop() {
		if (instance.task != null) {
			instance.task.cancel();
			instance.task = null;
		}
	}

}
