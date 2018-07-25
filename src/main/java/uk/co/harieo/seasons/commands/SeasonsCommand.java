package uk.co.harieo.seasons.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.harieo.seasons.Seasons;
import uk.co.harieo.seasons.configuration.SeasonsConfig;
import uk.co.harieo.seasons.models.Cycle;
import uk.co.harieo.seasons.models.Season;
import uk.co.harieo.seasons.models.Weather;
import uk.co.harieo.seasons.models.effect.Effect;

public class SeasonsCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only players may use this command!");
			return false;
		}

		Player player = (Player) sender;
		player.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Seasons " + ChatColor.GRAY + "v"
				+ Seasons.getPlugin().getDescription().getVersion() + " made by " + ChatColor.YELLOW + "Harieo");

		Cycle cycle = Seasons.getWorldCycle(player.getWorld());
		if (cycle == null) {
			player.sendMessage(ChatColor.RED + "This world is barren and full of darkness... It has no season!");
			return false;
		}

		boolean hasEnabledEffects = SeasonsConfig.get().hasEnabledEffects();

		if (args.length > 0 && args[0].equalsIgnoreCase("effects")) {
			if (hasEnabledEffects) {
				Weather weather = cycle.getWeather();
				player.sendMessage(
						ChatColor.GRAY + "For the weather " + ChatColor.YELLOW + weather.getName()
								+ ChatColor.GRAY + " the effects are:");
				for (Effect effect : weather.getEffects()) {
					player.sendMessage(
							ChatColor.GOLD + ChatColor.BOLD.toString() + effect.getName() + ": "
									+ ChatColor.GRAY + effect.getDescription());
				}
			} else {
				player.sendMessage(
						ChatColor.RED + "Your server administrator has decreed that all effects be disabled...");
			}
		} else {
			Season season = cycle.getSeason();
			player.sendMessage(season.getColor() + "Your world is in " + season.getName());
			player.sendMessage(ChatColor.GREEN + "The weather is currently " + ChatColor.DARK_GREEN
					+ cycle.getWeather().getName());
			player.sendMessage(ChatColor.GOLD + "Today is day " + cycle.getDay()
					+ " of " + SeasonsConfig.get().getDaysPerSeason());
			if (hasEnabledEffects) {
				player.sendMessage(
						ChatColor.GRAY + "To see the effects of this weather, use the command " + ChatColor.YELLOW
								+ "/seasons effects");
			}
		}

		return false;
	}

}
