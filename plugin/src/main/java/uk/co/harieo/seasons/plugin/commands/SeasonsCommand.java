package uk.co.harieo.seasons.plugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

import uk.co.harieo.seasons.plugin.Seasons;
import uk.co.harieo.seasons.plugin.configuration.SeasonsLanguageConfiguration;
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
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
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
                if (args.length == 1) {
                    player.sendMessage(ChatColor.GRAY + "Currently you can list: " + ChatColor.YELLOW + "weather, seasons");
                    player.sendMessage(ChatColor.GRAY + "Usage: " + ChatColor.YELLOW + "/seasons list <option>");
                } else {
                    StringBuilder sb = new StringBuilder();
                    if (args[1].equalsIgnoreCase("weather")) {
                        sb.append(ChatColor.YELLOW);
                        for (String string : Weather.getWeatherList()) {
                            sb.append(string).append(", ");
                        }
                        sb.delete(sb.length() - 2,
                                sb.length() - 1); // Should remove trailing ', ' which includes the space
                        player.sendMessage(ChatColor.GRAY + "Weathers available:- " + sb.toString());
                    } else if (args[1].equalsIgnoreCase("seasons")) {
                        sb.append(ChatColor.YELLOW);
                        for (String string : Season.getSeasonsList()) {
                            sb.append(string).append(", ");
                        }
                        sb.delete(sb.length() - 2,
                                sb.length() - 1);
                        player.sendMessage(ChatColor.GRAY + "Seasons available:- " + sb.toString());
                    } else {
                        player.sendMessage(ChatColor.GRAY + "That is not something that is listed");
                    }
                }
            }
        } else {
            player.sendMessage(WATERMARK);
            sendSeasonInfo(player, cycle, hasEnabledEffects);
        }

        return false;
    }

    private void reloadSeasons(CommandSender sender) {
        if (!sender.hasPermission("seasons.reload")) {
            sender.sendMessage(Seasons.PREFIX + ChatColor.RED + "You do not have permission to do that!");
            return;
        }

        Seasons seasons = Seasons.getInstance();
        seasons.getLanguageConfig().loadConfig(); // Reloads the language file
        seasons.setPrefix(); // Reloads the prefix separately as that is static
        sender.sendMessage(Seasons.PREFIX + ChatColor.GREEN + "Plugin has been reloaded!");
    }

    private void sendEffectsList(Player player, Cycle cycle, boolean hasEnabledEffects) {
        if (cycle == null) {
            sendBarrenWorldError(player);
            return;
        }

        SeasonsLanguageConfiguration languageConfiguration = Seasons.getInstance().getLanguageConfig();

        if (hasEnabledEffects) {
            Weather weather = cycle.getWeather();
            player.sendMessage("");

            String effectsListTitle = languageConfiguration.getStringOrDefault("command.effects-list-title",
                    ChatColor.GRAY + "For the weather " + ChatColor.YELLOW + weather.getName()
                            + ChatColor.GRAY + " the effects are:");
            player.sendMessage(Seasons.PREFIX + effectsListTitle.replace("%weather%", weather.getName()));

            String effectsListElement = languageConfiguration.getStringOrDefault("command.effects-list-element",
                    ChatColor.GOLD + ChatColor.BOLD.toString() + "%effect%: "
                            + ChatColor.GRAY + "%description%");

            for (Effect effect : weather.getEffects()) {
                player.sendMessage(effectsListElement.replace("%effect%", effect.getName())
                        .replace("%description%", effect.getDescription()));
            }
            player.sendMessage("");
        } else {
            player.sendMessage(
                    ChatColor.RED + "Your server administrator has decreed that all effects be disabled...");
        }
    }

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
                String replaced = string.replace("%season-color%", season.getColor().toString());
                replaced = replaced.replace("%season%", season.getName());
                replaced = replaced.replace("%weather%", cycle.getWeather().getName());
                replaced = replaced.replace("%day%", String.valueOf(cycle.getDay()));
                replaced = replaced
                        .replace("%max-days%", String.valueOf(seasons.getSeasonsConfig().getDaysPerSeason()));
                return replaced;
            }).collect(Collectors.toList()).forEach(player::sendMessage);
        } else {
            player.sendMessage(season.getColor() + "Your world is in " + season.getName());
            player.sendMessage(ChatColor.GREEN + "The weather is currently " + ChatColor.DARK_GREEN
                    + cycle.getWeather().getName());
            player.sendMessage(ChatColor.GOLD + "Today is day " + cycle.getDay()
                    + " of " + seasons.getSeasonsConfig().getDaysPerSeason());
        }

        if (hasEnabledEffects) {
            player.sendMessage(languageConfiguration.getStringOrDefault("command.season-info-footer",
                    ChatColor.GRAY + "To see the effects of this weather, use the command " + ChatColor.YELLOW
                            + "/seasons effects"));
        }
        player.sendMessage("");
    }

    private void importWorld(Player player, World world) {
        if (!player.hasPermission("seasons.import")) {
            player.sendMessage(Seasons.PREFIX + ChatColor.RED + "You do not have permission to do that!");
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

    private void sendBarrenWorldError(Player player) {
        player.sendMessage(ChatColor.RED + "This world is barren and full of darkness... It has no season!");
    }

}
