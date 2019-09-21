package uk.co.harieo.seasons.plugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;
import uk.co.harieo.seasons.plugin.Seasons;
import uk.co.harieo.seasons.plugin.configuration.SeasonsConfig;

class SeasonsInfoSubcommand {

	static void requestConfigurationDetails(CommandSender sender) {
		if (!sender.hasPermission("seasons.config")) {
			sender.sendMessage(Seasons.PREFIX + ChatColor.RED + "You do not have permission to do that!");
			return;
		}

		SeasonsConfig config = Seasons.getInstance().getSeasonsConfig();
		sender.sendMessage(Seasons.PREFIX + ChatColor.GRAY + "Your Configuration Settings (from config.yml):");

		sender.sendMessage(createOptionMessage("Days per Season", String.valueOf(config.getDaysPerSeason())));
		sender.sendMessage(createOptionMessage("Seconds per Damage", String.valueOf(config.getSecondsPerDamage())));
		sender.sendMessage(createOptionMessage("Roof Height", String.valueOf(config.getRoofHeight())));
		sender.sendMessage(createOptionMessage("Effects are Enabled", String.valueOf(config.hasEnabledEffects())));
		sender.sendMessage(createOptionMessage("Disabled Worlds", listContentsToString(config.getDisabledWorlds())));
		sender.sendMessage(createOptionMessage("Disabled Weathers", listContentsToString(config.getDisabledWeathers())));
		sender.sendMessage(createOptionMessage("Disabled Effects", listContentsToString(config.getDisabledEffects())));
		sender.sendMessage(createOptionMessage("Config Version", String.valueOf(config.getVersion())));
	}

	private static String createOptionMessage(String key, String value) {
		return ChatColor.YELLOW + key + ": " + ChatColor.LIGHT_PURPLE + value;
	}

	private static String listContentsToString(List<String> list) {
		if (list.isEmpty()) {
			return "None";
		} else {
			StringBuilder builder = new StringBuilder();
			for (String s : list) {
				builder.append(s);
				if (list.indexOf(s) < list.size() - 1) { // Make sure this isn't the last element
					builder.append(", ");
				}
			}
			return builder.toString();
		}
	}

}
