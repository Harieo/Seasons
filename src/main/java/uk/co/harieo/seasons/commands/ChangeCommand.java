package uk.co.harieo.seasons.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.harieo.seasons.Seasons;
import uk.co.harieo.seasons.models.Cycle;
import uk.co.harieo.seasons.models.Season;
import uk.co.harieo.seasons.models.Weather;

public class ChangeCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (!player.isOp() && !player.hasPermission("seasons.change.day")) {
				player.sendMessage(Seasons.PREFIX + ChatColor.RED + "You do not have permission to change the day!");
				return false;
			} else if (args.length == 1) {
				Cycle cycle = Seasons.getWorldCycle(player.getWorld());
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
					Seasons.PREFIX + ChatColor.RED + "Insufficient arguments: Expected /" + s + " <key> <world>");
			return false;
		}

		World world = Bukkit.getWorld(args[1]);
		if (world == null) {
			sender.sendMessage(Seasons.PREFIX + ChatColor.RED + "We couldn't find the world called " + args[1]);
			return false;
		}

		Cycle cycle = Seasons.getWorldCycle(world);
		if (cycle == null) {
			sender.sendMessage(
					Seasons.PREFIX + ChatColor.RED + "That world is too barren to be affected by Seasons...");
			return false;
		}

		change(sender, s, args[0], cycle);

		return false;
	}

	private void change(CommandSender sender, String command, String name, Cycle cycle) {
		World world = cycle.getWorld();
		if (command.equalsIgnoreCase("changeday")) {
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
		} else if (command.equalsIgnoreCase("changeweather")) {
			Weather weather = Weather.fromName(name);
			if (weather == null) {
				sender.sendMessage(Seasons.PREFIX + ChatColor.RED + "We couldn't find a weather called " + name);
				return;
			}

			cycle.setWeather(weather);
			broadcast(world, Seasons.PREFIX + ChatColor.GRAY
					+ "The skies grow silent and with a great rumble the weather turns to " + ChatColor.GREEN + weather
					.getName());
		} else if (command.equalsIgnoreCase("changeseason")) {
			Season season = Season.fromName(name);
			if (season == null) {
				sender.sendMessage(Seasons.PREFIX + ChatColor.RED + "We couldn't find a season called " + name);
				return;
			}

			cycle.setSeason(season);
			broadcast(world,
					Seasons.PREFIX + ChatColor.GRAY + "The air around you changes mystically and becomes "
							+ ChatColor.GOLD + season.getName());
		} else {
			throw new IllegalArgumentException("A command was sent called " + command + " but couldn't be processed");
		}
	}

	private void broadcast(World world, String message) {
		for (Player player : world.getPlayers()) {
			player.sendMessage(message);
		}
	}

}
