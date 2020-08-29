package uk.co.harieo.seasons.plugin.placeholders;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import uk.co.harieo.seasons.plugin.Seasons;
import uk.co.harieo.seasons.plugin.models.Cycle;
import uk.co.harieo.seasons.plugin.models.Season;

public class SeasonsPlaceholderExpansion extends PlaceholderExpansion {

	private final Seasons seasons;

	public SeasonsPlaceholderExpansion(Seasons seasons) {
		this.seasons = seasons;
	}

	@Override
	public boolean persist() {
		return true;
	}

	@Override
	public boolean canRegister() {
		return true;
	}

	@Override
	public String getAuthor() {
		return "Harieo";
	}

	@Override
	public String getIdentifier() {
		return "seasons";
	}

	@Override
	public String getVersion() {
		return seasons.getPlugin().getDescription().getVersion();
	}

	@Override
	public String onPlaceholderRequest(Player player, String identifier) {
		if (player == null) {
			return "";
		}

		RequestType type;
		if (identifier.startsWith("season")) {
			type = RequestType.SEASON;
		} else if (identifier.startsWith("weather")) {
			type = RequestType.WEATHER;
		} else if (identifier.startsWith("day")) {
			type = RequestType.DAY;
		} else {
			return null;
		}

		return makeAttempt(player, identifier, type);
	}

	/**
	 * This method will attempt to return the correct value, otherwise it will return a formatted error (not null)
	 *
	 * @param player that has made the request
	 * @param identifier of the placeholder
	 * @param type of request being made
	 * @return the result of the attempt
	 */
	private String makeAttempt(Player player, String identifier, RequestType type) {
		String[] split = identifier.split("_");

		World world;
		if (split.length < 2) {
			world = player.getWorld();
		} else {
			StringBuilder builder = new StringBuilder();
			for (int i = 1; i < split.length; i++) {
				builder.append(split[i]);
				if (i + 1 < split.length) { // Only add if there is a word after this one
					builder.append("_");
				}
			}

			String worldName = builder.toString();
			world = Bukkit.getWorld(worldName);
		}

		if (world == null) {
			return "Invalid world";
		}

		Cycle cycle = seasons.getWorldCycle(world);
		if (cycle == null) {
			return "None";
		} else {
			if (type == RequestType.SEASON) {
				return cycle.getSeason().getName();
			} else if (type == RequestType.WEATHER) {
				return cycle.getWeather().getName();
			} else {
				return String.valueOf(cycle.getDay());
			}
		}
	}

	public enum RequestType {
		SEASON, WEATHER, DAY
	}

}
