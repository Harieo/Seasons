package uk.co.harieo.seasons.plugin.models;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.Optional;
import org.apache.commons.lang.Validate;
import uk.co.harieo.seasons.plugin.Seasons;
import uk.co.harieo.seasons.plugin.configuration.StaticPlaceholders;

public enum Season {

	SPRING("Spring", "The sun rises, the flowers smell fresh and there is a Spring in your step... literally!",
			ChatColor.YELLOW),
	SUMMER("Summer", "Today feels warmer than the others, maybe even too warm... Summer has arrived!", ChatColor.GOLD),
	AUTUMN("Autumn", "A cool breeze whispers to the leaves that fall from the trees, \"it's Autumn!\"",
			ChatColor.DARK_GREEN),
	WINTER("Winter", "A few snowflakes fall and the world grows cold, time for Winter to make it's mark...",
			ChatColor.BLUE);

	private final String name; // Unique identifier for the season that will look nice in chat (front and back end)
	private final String message;
	private final ChatColor color; // Color to be used when the season is referenced

	/**
	 * A season which has the possibility of multiple different weathers happening each day
	 *
	 * @param name default name of the season
	 * @param message which is broadcast when this season is activated
	 * @param seasonColor the colour to format this season as in chat
	 */
	Season(String name, String message, ChatColor seasonColor) {
		this.name = name;
		this.message = message;
		this.color = seasonColor;
	}

	/**
	 * @return the formatted name of this season
	 */
	public String getName() {
		return Seasons.getInstance().getLanguageConfig()
				.getStringOrDefault("seasons.name." + name().toLowerCase(), name)
				.orElse("");
	}

	/**
	 * @return the name of this season stripped of formatting
	 */
	public String getRawName() {
		return ChatColor.stripColor(getName());
	}

	/**
	 * @return the possible message which should be broadcast when this season is activated
	 */
	public Optional<String> getMessage() {
		return Seasons.getInstance().getLanguageConfig()
				.getStringOrDefault("seasons.on-trigger." + name().toLowerCase(), message)
				.map(message -> message.replaceAll(StaticPlaceholders.SEASON.toString(), getName()));
	}

	/**
	 * @return the colour to format this season as in chat
	 */
	public ChatColor getColor() {
		return color;
	}

	@Override
	public String toString() {
		return color + name;
	}

	/**
	 * Finds the relevant {@link Season} based on the name property Note: This will ignore cases for user friendliness
	 *
	 * @param name to check for
	 * @return the relevant {@link Season} or null if none found
	 */
	public static Season fromName(String name) {
		for (Season season : values()) {
			if (season.getRawName().equalsIgnoreCase(name.toLowerCase())) {
				return season;
			}
		}

		return null;
	}

	/**
	 * Returns the next {@link Season} in the list in ascending order or the start of the list if there are no more
	 * seasons after the index
	 *
	 * @param currentSeason that the cycle is currently on
	 * @return the next season
	 */
	public static Season next(Season currentSeason) {
		Validate.notNull(currentSeason);

		List<Season> seasons = Arrays.asList(values());
		int index = seasons.indexOf(currentSeason);
		if (index + 1 >= seasons.size()) { // If there are no more seasons after the current one
			return seasons.get(0); // Return the start of the list
		} else {
			return seasons.get(index + 1); // Return the next season
		}
	}

	/**
	 * @return a list of all of the available seasons' names
	 */
	public static List<String> getSeasonsList() {
		List<String> list = new ArrayList<>();
		for (Season season : Season.values()) {
			list.add(season.getName().toLowerCase());
		}
		return list;
	}
}
