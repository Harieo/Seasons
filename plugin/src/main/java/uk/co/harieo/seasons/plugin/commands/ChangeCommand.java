package uk.co.harieo.seasons.plugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import uk.co.harieo.seasons.plugin.Seasons;
import uk.co.harieo.seasons.plugin.events.DayEndEvent;
import uk.co.harieo.seasons.plugin.events.SeasonChangeEvent;
import uk.co.harieo.seasons.plugin.events.SeasonsWeatherChangeEvent;
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

				change(sender, s, args[0], cycle);
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
			sender.sendMessage(
					Seasons.PREFIX + ChatColor.RED + "That world is too barren to be affected by Seasons...");
			return false;
		}

		change(sender, s, args[0], cycle);
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
	private void change(CommandSender sender, String command, String name, Cycle cycle) {
		World world = cycle.getWorld();
		PluginManager manager = Bukkit.getPluginManager();
		if (command.equalsIgnoreCase("changeday")) {
			if (hasInsufficientPermissions(sender, "seasons.change.day")) {
				sender.sendMessage(Seasons.PREFIX + ChatColor.RED + "You don't have permission to change the day!");
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
			broadcast(world,
					Seasons.PREFIX + ChatColor.GRAY + "Time shatters before you, days fly by and it is now Day "
							+ ChatColor.LIGHT_PURPLE + newDay);
			// Confirm to the sender, who may not be in the world
			sender.sendMessage(
					Seasons.PREFIX + ChatColor.GREEN + "Successfully " + ChatColor.GRAY + "changed the day to "
							+ ChatColor.YELLOW + newDay + ChatColor.GRAY + " in " + ChatColor.LIGHT_PURPLE + world
							.getName());
		} else if (command.equalsIgnoreCase("changeweather")) {
			if (hasInsufficientPermissions(sender, "seasons.change.weather")) {
				sender.sendMessage(Seasons.PREFIX + ChatColor.RED + "You don't have permission to change the weather!");
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

			Weather oldWeather = Weather.fromName(cycle.getWeather().getName());
			cycle.setWeather(weather);
			broadcast(world, Seasons.PREFIX + ChatColor.GRAY
					+ "The skies grow silent and with a great rumble the weather turns to " + ChatColor.GREEN + weather
					.getName());
			// Confirm to the sender, who may not be in the world
			sender.sendMessage(
					Seasons.PREFIX + ChatColor.GREEN + "Successfully " + ChatColor.GRAY + "changed the weather to "
							+ ChatColor.YELLOW + weather.getName() + ChatColor.GRAY + " in " + ChatColor.LIGHT_PURPLE
							+ world.getName());
			manager.callEvent(new DayEndEvent(cycle, oldWeather, false));
			manager.callEvent(new SeasonsWeatherChangeEvent(cycle, oldWeather, weather, false));
		} else if (command.equalsIgnoreCase("changeseason")) {
			if (hasInsufficientPermissions(sender, "seasons.change.season")) {
				sender.sendMessage(Seasons.PREFIX + ChatColor.RED + "You don't have permission to change the season!");
				return;
			}

			Season season = Season.fromName(name);
			if (season == null) {
				sender.sendMessage(Seasons.PREFIX + ChatColor.RED + "We couldn't find a season called " + name);
				return;
			}

			manager.callEvent(new SeasonChangeEvent(cycle, season, cycle.getSeason(), false));
			cycle.setSeason(season);
			broadcast(world, Seasons.PREFIX + ChatColor.GRAY + "The air around you changes mystically and becomes "
					+ ChatColor.GOLD + season.getName());
			// Confirm to the sender, who may not be in the world
			sender.sendMessage(
					Seasons.PREFIX + ChatColor.GREEN + "Successfully " + ChatColor.GRAY + "changed the season to "
							+ ChatColor.YELLOW + season.getName() + ChatColor.GRAY + " in " + ChatColor.LIGHT_PURPLE
							+ world.getName());
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
