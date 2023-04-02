package uk.co.harieo.seasons.plugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import uk.co.harieo.seasons.plugin.Seasons;
import uk.co.harieo.seasons.plugin.configuration.SeasonsLanguageConfiguration;
import uk.co.harieo.seasons.plugin.configuration.StaticPlaceholders;
import uk.co.harieo.seasons.plugin.models.Cycle;
import uk.co.harieo.seasons.plugin.models.Season;
import uk.co.harieo.seasons.plugin.models.Weather;
import uk.co.harieo.seasons.plugin.models.effect.Effect;

public class SeasonsCommand implements CommandExecutor {

	private static final String WATERMARK =
			ChatColor.GOLD + ChatColor.BOLD.toString() + "Seasons " + ChatColor.GRAY + "v"
					+ Seasons.getInstance().getPlugin().getDescription().getVersion() + " made by " + ChatColor.YELLOW
					+ "Harieo";

	@Override
	@SuppressWarnings("annotation")
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
		if (!(sender instanceof Player)) {
			// Adds an exceptions for commands which console may perform
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("reload")) {
					reloadSeasons(sender);
					return false;
				} else if (args[0].equalsIgnoreCase("config")) {
					SeasonsInfoSubcommand.requestConfigurationDetails(sender);
					return false;
				}
			}

			sender.sendMessage("Only players may use this command!");
			return false;
		}

		Seasons seasons = Seasons.getInstance();
		Player player = (Player) sender;
		Cycle cycle = seasons.getWorldCycle(player.getWorld());
		boolean hasEnabledEffects = seasons.getSeasonsConfig().hasEnabledEffects();

		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("import")) {
				importWorld(player, player.getWorld());
			} else if (args[0].equalsIgnoreCase("effects")) {
				sendEffectsList(player, cycle, hasEnabledEffects);
			} else if (args[0].equalsIgnoreCase("reload")) { // This is specified here if in-case it was not console
				reloadSeasons(player);
			} else if (args[0].equalsIgnoreCase("config")) {
				SeasonsInfoSubcommand.requestConfigurationDetails(sender);
			} else if (args[0].equalsIgnoreCase("list")) {
				listAvailableModels(player, args);
			}
		} else {
			player.sendMessage(WATERMARK);
			sendSeasonInfo(player, cycle, hasEnabledEffects);
		}

		return false;
	}

	/**
	 * Allows the sender to reload all the plugin's configuration files
	 *
	 * @param sender which has requested this reload
	 */
	private void reloadSeasons(CommandSender sender) {
		if (!sender.hasPermission("seasons.reload")) {
			sendPermissionDenied(sender);
			return;
		}

		Seasons seasons = Seasons.getInstance();
		JavaPlugin plugin = seasons.getPlugin();

		seasons.getLanguageConfig().load(plugin); // Reloads the language file
		seasons.getSeasonsConfig().load(plugin); // Reload the config.yml settings
		seasons.getWeatherChanceConfiguration().load(plugin); // Reload chances.yml
		seasons.setPrefix(); // Reloads the prefix separately as that is static
		sender.sendMessage(Seasons.PREFIX + ChatColor.GREEN + "Plugin has been reloaded!");
	}

	/**
	 * Sends a list of all effects which are currently active in a player's world
	 *
	 * @param player which has requested the information
	 * @param cycle of the world which the player is currently in
	 * @param hasEnabledEffects whether effects are enabled in the first place
	 */
	private void sendEffectsList(Player player, Cycle cycle, boolean hasEnabledEffects) {
		if (cycle == null) {
			sendBarrenWorldError(player);
			return;
		}

		SeasonsLanguageConfiguration languageConfiguration = Seasons.getInstance().getLanguageConfig();

		if (hasEnabledEffects) {
			Weather weather = cycle.getWeather();
			player.sendMessage("");

			languageConfiguration.getStringOrDefault("command.effects-list-title",
					ChatColor.GRAY + "For the weather " + ChatColor.YELLOW + weather.getName()
							+ ChatColor.GRAY + " the effects are:").ifPresent(
					message -> player.sendMessage(Seasons.PREFIX + message
							.replace(StaticPlaceholders.WEATHER.toString(), weather.getName())));

			Optional<String> potentialEffectsListElement = languageConfiguration
					.getStringOrDefault("command.effects-list-element",
							ChatColor.GOLD + ChatColor.BOLD.toString() + "%effect%: "
									+ ChatColor.GRAY + "%description%");

			if (potentialEffectsListElement.isPresent()) {
				String effectsListElement = potentialEffectsListElement.get();
				for (Effect effect : weather.getEffects()) {
					String effectName = effect.getName();
					player.sendMessage(effectsListElement
							.replace("%effect%", effect.isEnabled() ? effectName
									: ChatColor.RED + ChatColor.BOLD.toString() + effectName)
							.replace("%description%", effect.getDescription()));
				}
			}
			player.sendMessage("");
		} else {
			languageConfiguration.getStringOrDefault("command.all-effects-disabled",
					ChatColor.RED + "Your administrator has disabled all effects!")
					.ifPresent(message -> player.sendMessage(Seasons.PREFIX + message));
		}
	}

	/**
	 * Sends information about the season, weather and day of the world a player is in
	 *
	 * @param player who has requested the information
	 * @param cycle of the world the player is currently in
	 * @param hasEnabledEffects whether effects are enabled in the player's world
	 */
	private void sendSeasonInfo(Player player, Cycle cycle, boolean hasEnabledEffects) {
		if (cycle == null) {
			sendBarrenWorldError(player);
			return;
		}

		Season season = cycle.getSeason();
		player.sendMessage("");

		Seasons seasons = Seasons.getInstance();
		SeasonsLanguageConfiguration languageConfiguration = seasons.getLanguageConfig();

		List<String> seasonInfo = languageConfiguration.getStringList("command.season-info");

		if (seasonInfo != null && !seasonInfo.isEmpty()) {
			// Need to replace the placeholders, collect them as a new list and send them all to the player
			seasonInfo.stream().map(string -> {
				String replaced = string
						.replace(StaticPlaceholders.SEASON_COLOR.toString(), season.getColor().toString());
				replaced = replaced.replace(StaticPlaceholders.SEASON.toString(), season.getName());
				replaced = replaced.replace(StaticPlaceholders.WEATHER.toString(), cycle.getWeather().getName());
				replaced = replaced.replace(StaticPlaceholders.DAY.toString(), String.valueOf(cycle.getDay()));
				replaced = replaced
						.replace(StaticPlaceholders.MAX_DAYS.toString(),
								String.valueOf(seasons.getSeasonsConfig().getDaysPerSeason()));
				if (seasons.getSeasonsConfig().isYearsEnabled()) {
					replaced = replaced.replace(StaticPlaceholders.YEAR.toString(), String.valueOf(cycle.getYear()));
					replaced = replaced.replace(StaticPlaceholders.SEASON_NUMBER.toString(), String.valueOf(cycle.getSeasonOfYear()));
					replaced = replaced
							.replace(StaticPlaceholders.SEASONS_PER_YEAR.toString(),
									 String.valueOf(seasons.getSeasonsConfig().getSeasonsPerYear()));
				}
				return replaced;
			}).collect(Collectors.toList()).forEach(player::sendMessage);
		} else {
			player.sendMessage(season.getColor() + "Your world is in " + season.getName()
					+ (seasons.getSeasonsConfig().isYearsEnabled()
					   ?
					(ChatColor.GRAY + " - " + ChatColor.RED + cycle.getYear() + ChatColor.GRAY
					+ " - " + ChatColor.GREEN + cycle.getSeasonOfYear() + ChatColor.GRAY + "/"
					+ ChatColor.GREEN + seasons.getSeasonsConfig().getSeasonsPerYear())
					   : ""));
			player.sendMessage(ChatColor.GREEN + "The weather is currently " + ChatColor.DARK_GREEN
					+ cycle.getWeather().getName());
			player.sendMessage(ChatColor.GOLD + "Today is day " + cycle.getDay()
					+ " of " + seasons.getSeasonsConfig().getDaysPerSeason());
		}

		if (hasEnabledEffects) {
			languageConfiguration.getStringOrDefault("command.season-info-footer",
					ChatColor.GRAY + "To see the effects of this weather, use the command " + ChatColor.YELLOW
							+ "/seasons effects").ifPresent(player::sendMessage);
		}
		player.sendMessage("");
	}

	/**
	 * Imports a new world into seasons by force, creating data for it
	 *
	 * @param player which has requested the import
	 * @param world which the player has requested be imported
	 */
	private void importWorld(Player player, World world) {
		if (!player.hasPermission("seasons.import")) {
			sendPermissionDenied(player);
			return;
		}

		Seasons seasons = Seasons.getInstance();
		if (seasons.getWorldCycle(world) != null) {
			player.sendMessage(Seasons.PREFIX + ChatColor.RED + "This world has already been imported!");
		} else if (world.getEnvironment() != Environment.NORMAL) {
			player.sendMessage(Seasons.PREFIX + ChatColor.RED
					+ "This world has an invalid environment, Seasons doesn't use NETHER or END worlds");
		} else {
			seasons.getWorldHandler().addWorld(world);
			player.sendMessage(Seasons.PREFIX + ChatColor.GREEN + "Successfully imported world!");
		}
	}

	/**
	 * Lists all the available weathers and seasons which a player can pick from
	 *
	 * @param player who is requesting the information
	 * @param args the arguments which came with the command
	 */
	private void listAvailableModels(Player player, String[] args) {
		SeasonsLanguageConfiguration languageConfiguration = Seasons.getInstance().getLanguageConfig();
		if (args.length == 1) {
			languageConfiguration.getStringList("command.list-help")
					.stream()
					.map(string -> string.replaceAll(StaticPlaceholders.OPTIONS.toString(), "weather, seasons"))
					.forEach(player::sendMessage);
		} else {
			StringBuilder builder = new StringBuilder();
			builder.append(ChatColor.YELLOW);

			String modelName;
			List<String> allModelNames;
			if (args[1].equalsIgnoreCase("weather")) {
				allModelNames = Weather.getWeatherList();
				modelName = "Weathers";
			} else if (args[1].equalsIgnoreCase("seasons")) {
				allModelNames = Season.getSeasonsList();
				modelName = "Seasons";
			} else {
				languageConfiguration
						.getStringOrDefault("list-error", ChatColor.GRAY + "That is not something that is listed")
						.ifPresent(player::sendMessage);
				return;
			}

			for (int i = 0; i < allModelNames.size(); i++) {
				builder.append(allModelNames.get(i));
				if (i + 1 < allModelNames.size()) { // Only if there is a name after this one
					builder.append(", ");
				}
			}
			player.sendMessage(ChatColor.GRAY + modelName + " available: " + builder.toString());
		}
	}

	/**
	 * Sends a message to the sender that the world they are requesting information on is not managed by seasons
	 *
	 * @param sender to send the message to
	 */
	public static void sendBarrenWorldError(CommandSender sender) {
		Seasons.getInstance().getLanguageConfig().getStringOrDefault("command.barren-world",
				ChatColor.RED + "This world is barren and full of darkness... It has no season!")
				.ifPresent(message -> sendWithPrefix(message, sender));
	}

	/**
	 * Sends a message to the sender that they do not have permission to run the command
	 *
	 * @param sender to send the message to
	 */
	public static void sendPermissionDenied(CommandSender sender) {
		SeasonsLanguageConfiguration languageConfiguration = Seasons.getInstance().getLanguageConfig();
		languageConfiguration.getStringOrDefault("command.permission-denied",
				ChatColor.RED + "You do not have permission to do that!")
				.ifPresent(message -> sendWithPrefix(message, sender));
	}

	/**
	 * Sends the message to the receiver with the {@link Seasons#PREFIX}
	 *
	 * @param message to send to the receiver
	 * @param receiver to send the message to
	 */
	public static void sendWithPrefix(String message, CommandSender receiver) {
		receiver.sendMessage(Seasons.PREFIX + message);
	}

}
