package uk.co.harieo.seasons.plugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.Optional;
import uk.co.harieo.seasons.plugin.Seasons;
import uk.co.harieo.seasons.plugin.configuration.SeasonsLanguageConfiguration;
import uk.co.harieo.seasons.plugin.configuration.StaticPlaceholders;
import uk.co.harieo.seasons.plugin.events.DayEndEvent;
import uk.co.harieo.seasons.plugin.events.SeasonChangeEvent;
import uk.co.harieo.seasons.plugin.events.SeasonsWeatherChangeEvent;
import uk.co.harieo.seasons.plugin.events.YearChangeEvent;
import uk.co.harieo.seasons.plugin.models.Cycle;
import uk.co.harieo.seasons.plugin.models.Season;
import uk.co.harieo.seasons.plugin.models.Weather;

public class ChangeCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		Seasons seasons = Seasons.getInstance();
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length == 1) {
				Cycle cycle = seasons.getWorldCycle(player.getWorld());
				if (cycle == null) {
					player.sendMessage(
							Seasons.PREFIX + ChatColor.RED + "You are not in a world that can have it's day changed!");
					return false;
				}

				change(sender, command, args[0], cycle);
				return false;
			}
		}

		if (args.length < 2) {
			sender.sendMessage(
					Seasons.PREFIX + ChatColor.RED + "Insufficient arguments: Expected /" + s + " <new value> <world>");
			return false;
		}

		World world = Bukkit.getWorld(args[1]);
		if (world == null) {
			sender.sendMessage(Seasons.PREFIX + ChatColor.RED + "We couldn't find the world called " + args[1]);
			return false;
		}

		Cycle cycle = seasons.getWorldCycle(world);
		if (cycle == null) {
			SeasonsCommand.sendBarrenWorldError(sender);
			return false;
		}

		change(sender, command, args[0], cycle);
		return false;
	}

	/**
	 * Basic processing of specified parameters that are variable and cannot be predicted This method handles 3
	 * different commands in 1
	 *
	 * @param sender of the original command
	 * @param command sent by the sender
	 * @param name of the value they wish to change, specific to the command executed
	 * @param cycle that they are attempting to edit
	 */
	private void change(CommandSender sender, Command command, String name, Cycle cycle) {
		World world = cycle.getWorld();
		PluginManager manager = Bukkit.getPluginManager();
		String commandLabel = command.getLabel();
		SeasonsLanguageConfiguration languageConfiguration = Seasons.getInstance().getLanguageConfig();

		if (commandLabel.equalsIgnoreCase("changeday")) {
			if (hasInsufficientPermissions(sender, "seasons.change.day")) {
				SeasonsCommand.sendPermissionDenied(sender);
				return;
			}

			int newDay;
			try {
				newDay = Integer.parseInt(name);
			} catch (NumberFormatException ignored) {
				sender.sendMessage(Seasons.PREFIX
						+ ChatColor.RED + "Invalid argument: Expected number value /changeday <value>");
				return;
			}

			cycle.setDay(newDay);
			languageConfiguration.getStringOrDefault("command.force-day",
					ChatColor.GRAY + "Time shatters before you, days fly by and it is now Day " + ChatColor.LIGHT_PURPLE
							+ newDay)
					.ifPresent(message -> broadcast(world,
							Seasons.PREFIX + message
									.replaceAll(StaticPlaceholders.DAY.toString(), String.valueOf(newDay))));

			// Confirm to the sender, who may not be in the world
			sender.sendMessage(
					Seasons.PREFIX + ChatColor.GREEN + "Successfully " + ChatColor.GRAY + "changed the day to "
							+ ChatColor.YELLOW + newDay + ChatColor.GRAY + " in " + ChatColor.LIGHT_PURPLE + world
							.getName());
		} else if (commandLabel.equalsIgnoreCase("changeweather")) {
			if (hasInsufficientPermissions(sender, "seasons.change.weather")) {
				SeasonsCommand.sendPermissionDenied(sender);
				return;
			}

			Weather weather = Weather.fromName(name);
			if (weather == null) {
				sender.sendMessage(Seasons.PREFIX + ChatColor.RED + "We couldn't find a weather called " + name);
				return;
			} else if (Weather.isManuallyDisabled(weather)) {
				sender.sendMessage(Seasons.PREFIX + ChatColor.RED
						+ "That weather has been disabled by a server administrator via the config!");
				return;
			}

			Weather oldWeather = cycle.getWeather();
			cycle.setWeather(weather);

			String weatherName = weather.getName();
			languageConfiguration.getStringOrDefault("command.force-weather",
					"The skies grow silent and with a great rumble the weather turns to " + ChatColor.GREEN
							+ weatherName)
					.ifPresent(message -> broadcast(world,
							Seasons.PREFIX + message
									.replaceAll(StaticPlaceholders.WEATHER.toString(), ChatColor.GREEN + weatherName)));

			// Confirm to the sender, who may not be in the world
			sender.sendMessage(
					Seasons.PREFIX + ChatColor.GREEN + "Successfully " + ChatColor.GRAY + "changed the weather to "
							+ ChatColor.YELLOW + weatherName + ChatColor.GRAY + " in " + ChatColor.LIGHT_PURPLE
							+ world.getName());

			manager.callEvent(new DayEndEvent(cycle, oldWeather, false));
			manager.callEvent(new SeasonsWeatherChangeEvent(cycle, oldWeather, weather, false));
		} else if (commandLabel.equalsIgnoreCase("changeseason")) {
			if (hasInsufficientPermissions(sender, "seasons.change.season")) {
				SeasonsCommand.sendPermissionDenied(sender);
				return;
			}

			Season season = Season.fromName(name);
			if (season == null) {
				sender.sendMessage(Seasons.PREFIX + ChatColor.RED + "We couldn't find a season called " + name);
				return;
			}
			String seasonName = season.getName();

			manager.callEvent(new SeasonChangeEvent(cycle, season, cycle.getSeason(), false));

			cycle.setSeason(season);
			languageConfiguration.getStringOrDefault("command.force-season",
					ChatColor.GRAY + "The air around you changes mystically and becomes " + ChatColor.GOLD + seasonName)
					.ifPresent(message -> broadcast(world,
							Seasons.PREFIX + message
									.replaceAll(StaticPlaceholders.SEASON.toString(), ChatColor.YELLOW + seasonName)));

			// Confirm to the sender, who may not be in the world
			sender.sendMessage(
					Seasons.PREFIX + ChatColor.GREEN + "Successfully " + ChatColor.GRAY + "changed the season to "
							+ ChatColor.YELLOW + season.getName() + ChatColor.GRAY + " in " + ChatColor.LIGHT_PURPLE
							+ world.getName());
		} else if (commandLabel.equalsIgnoreCase("changeyear")) {
			if (hasInsufficientPermissions(sender, "seasons.change.year")) {
				SeasonsCommand.sendPermissionDenied(sender);
				return;
			}

			if (!Seasons.getInstance().getSeasonsConfig().isYearsEnabled()) {
				sender.sendMessage(Seasons.PREFIX + ChatColor.RED + "Years are disabled in the config!");
				return;
			}

			int newYear;
			try {
				newYear = Integer.parseInt(name);
			} catch (NumberFormatException ignored) {
				sender.sendMessage(Seasons.PREFIX
										   + ChatColor.RED + "Invalid argument: Expected number value /changeyear <value>");
				return;
			}

			manager.callEvent(new YearChangeEvent(cycle, newYear, cycle.getYear(), false));
			cycle.setYear(newYear);
			languageConfiguration.getStringOrDefault("command.force-year",
													 ChatColor.GRAY + "Time shatters before you, years fly by and it is now Year " + ChatColor.LIGHT_PURPLE
															 + newYear)
					.ifPresent(message -> broadcast(world,
													Seasons.PREFIX + message
															.replaceAll(StaticPlaceholders.YEAR.toString(), String.valueOf(newYear))));

			// Confirm to the sender, who may not be in the world
			sender.sendMessage(
					Seasons.PREFIX + ChatColor.GREEN + "Successfully " + ChatColor.GRAY + "changed the year to "
							+ ChatColor.YELLOW + newYear + ChatColor.GRAY + " in " + ChatColor.LIGHT_PURPLE + world
							.getName());
		} else {
			throw new IllegalArgumentException("A command was sent called " + command + " but couldn't be processed");
		}
	}

	/**
	 * Broadcasta a message to all players in a world
	 *
	 * @param world to send the message to
	 * @param message to be sent
	 */
	private void broadcast(World world, String message) {
		for (Player player : world.getPlayers()) {
			player.sendMessage(message);
		}
	}

	/**
	 * Checks whether a sender has insufficient permission to perform a command Note: Only checks a Player as all other
	 * senders have permission
	 *
	 * @param sender to check the permissions of
	 * @param permission that is required
	 * @return whether the sender does NOT have the permission
	 */
	private boolean hasInsufficientPermissions(CommandSender sender, String permission) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			return !player.isOp() && !player.hasPermission(permission);
		}

		return false;
	}

}
