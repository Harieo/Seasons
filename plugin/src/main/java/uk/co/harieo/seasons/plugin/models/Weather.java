package uk.co.harieo.seasons.plugin.models;

import org.bukkit.ChatColor;

import java.util.*;

import uk.co.harieo.seasons.plugin.Seasons;
import uk.co.harieo.seasons.plugin.configuration.SeasonsConfig;
import uk.co.harieo.seasons.plugin.configuration.StaticPlaceholders;
import uk.co.harieo.seasons.plugin.models.effect.Effect;

public enum Weather {

	BEAUTIFUL("Beautiful",
			ChatColor.GREEN + "The sun in shining, the grass is green and the weather is " + ChatColor.YELLOW
					+ "Beautiful",
			false, false, false, 20,
			Arrays.asList(Season.SPRING, Season.SUMMER)),
	BREEZY("Breezy",
			ChatColor.GRAY + "A cool breeze touches your skin, it's going to be " + ChatColor.GREEN + "Breezy",
			false, false, false, 15,
			Arrays.asList(Season.SPRING, Season.AUTUMN)),
	CHILLY("Chilly",
			ChatColor.BLUE + "You shiver as frost glistens around you, it's very " + ChatColor.DARK_BLUE + "Chilly "
					+ ChatColor.BLUE + "today",
			false, false, false, 15,
			Collections.singletonList(Season.SPRING)),
	RAINY("Rainy",
			ChatColor.BLUE + "A loud rain falls and wets the ground, it's " + ChatColor.DARK_BLUE + "Rainy",
			false, true, false, 10,
			Arrays.asList(Season.SPRING, Season.AUTUMN, Season.WINTER)),
	SCORCHING("Scorching",
			ChatColor.YELLOW + "The sun burns your skin and the ground hurts to touch, it's " + ChatColor.GOLD
					+ "Scorching",
			false, false, false, 10,
			Collections.singletonList(Season.SUMMER)),
	HOT("Hot",
			ChatColor.YELLOW + "It's going to be very " + ChatColor.GOLD + "Hot " + ChatColor.YELLOW
					+ "today",
			false, false, false, 20,
			Collections.singletonList(Season.SUMMER)),
	WARM("Warm",
			ChatColor.YELLOW + "A soothing warmth hugs you as you move, it's a " + ChatColor.GOLD + "Warm "
					+ ChatColor.YELLOW + "day",
			false, false, false, 25,
			Collections.singletonList(Season.SUMMER)),
	COLD("Cold",
			ChatColor.BLUE + "The water is so very " + ChatColor.DARK_BLUE + "Cold " + ChatColor.BLUE + "today",
			false, false, false, 40,
			Arrays.asList(Season.AUTUMN, Season.WINTER)),
	STORMY("Stormy",
			ChatColor.RED + "A great " + ChatColor.DARK_RED + "Storm " + ChatColor.RED
					+ "brews, the Gods in this place are angry... Brace yourself!",
			true, true, true, 10,
			Collections.singletonList(Season.AUTUMN)),
	FREEZING("Freezing",
			ChatColor.BLUE + "The water freezes with a sheet of ice and you feel a great cold, it's "
					+ ChatColor.DARK_BLUE + "Freezing",
			true, false, false, 15,
			Collections.singletonList(Season.WINTER)),
	SNOWY("Snowy",
			ChatColor.GRAY + "A great white blanket covers the world, it's " + ChatColor.WHITE + "Snowy",
			false, true, false, 15,
			Collections.singletonList(Season.WINTER)),
	NIGHT("Calm", // Night is a weather with no effect, to give people a break
			ChatColor.GRAY + "The world rests with the sun and all is calm... Until the mobs come to eat you!",
			false, false, false);

	private static final Random random = new Random();

	private final String name; // Default name shown to players
	private final String message; // Default message to show the weather changing
	private final boolean catastrophic; // Is there is a high risk of this weather killing a player?
	private final boolean storm; // Whether there should be rain
	private final boolean thundering; // Whether the rain should be thunderous
	private final int defaultChance;
	private final List<Season> seasons; // List of seasons this weather can be triggered on

	/**
	 * A weather which comes with certain effects
	 *
	 * @param name default name of the weather
	 * @param broadcast the message which is broadcast when this weather is chosen
	 * @param catastrophic whether this weather has a high chance to kill players
	 * @param storm whether this weather should make the world rain
	 * @param seasons the list of seasons in which this weather can appear
	 */
	Weather(String name, String broadcast, boolean catastrophic, boolean storm, boolean thundering, int defaultChance, List<Season> seasons) {
		this.name = name;
		this.message = broadcast;
		this.catastrophic = catastrophic;
		this.storm = storm;
		this.thundering = thundering;
		this.defaultChance = defaultChance;
		this.seasons = seasons;
	}

	/**
	 * An overload of {@link Weather} where the list of seasons is empty and the chance of it spawning is 0
	 *
	 * @param name default name of the weather
	 * @param broadcast the message which is broadcast when this weather is chosen
	 * @param catastrophic whether this weather has a high chance to kill players
	 * @param storm whether this weather should make the world rain
	 */
	Weather(String name, String broadcast, boolean catastrophic, boolean storm, boolean thundering) {
		this(name, broadcast, catastrophic, storm, thundering, 0, Collections.emptyList());
	}

	/**
	 * @return the formatted name of this weather
	 */
	public String getName() {
		return Seasons.getInstance().getLanguageConfig().getString("weathers.name." + name().toLowerCase())
				.orElse(name);
	}

	/**
	 * @return the name of this weather stripped of formatting
	 */
	public String getRawName() {
		return ChatColor.stripColor(getName()).toLowerCase();
	}

	/**
	 * @return the formatted message which will appear when this weather is chosen
	 */
	public Optional<String> getMessage() {
		return Seasons.getInstance().getLanguageConfig()
				.getStringOrDefault("weathers.on-trigger." + name().toLowerCase(), message)
				.map(message -> message.replaceAll(StaticPlaceholders.WEATHER.toString(), getName()));
	}

	/**
	 * @return whether this weather has a high chance to kill a player
	 */
	public boolean isCatastrophic() {
		return catastrophic;
	}

	/**
	 * @return whether this weather involves rain/snow
	 */
	public boolean isStorm() {
		return storm;
	}

	/**
	 * @return whether this weather involves thunder
	 */
	public boolean isThundering() {
		return thundering;
	}

	/**
	 * @return the default chance that this weather will be chosen compared to others
	 */
	public int getDefaultChance() {
		return defaultChance;
	}

	/**
	 * @return the seasons in which this weather can appear
	 */
	public List<Season> getAffectedSeasons() {
		return seasons;
	}

	/**
	 * @return the effects which can come with this weather
	 */
	public List<Effect> getEffects() {
		List<Effect> effects = new ArrayList<>();

		Seasons seasons = Seasons.getInstance();
		for (Effect effect : seasons.getEffects()) {
			if (effect.isWeatherApplicable(this)) {
				effects.add(effect);
			}
		}

		return effects;
	}

	/**
	 * Finds the relevant {@link Weather} based on the name property Note: This will ignore cases for user friendliness
	 *
	 * @param name to check for
	 * @return the relevant {@link Weather} or null if none found
	 */
	public static Weather fromName(String name) {
		for (Weather weather : values()) {
			if (weather.getRawName().equalsIgnoreCase(name)) {
				return weather;
			}
		}

		return null;
	}

	/**
	 * Gets a random {@link Weather} from the list of values without any constraints
	 *
	 * @return a randomly selected {@link Weather}
	 */
	public static Weather randomWeather() {
		return values()[random.nextInt(values().length)];
	}

	/**
	 * Gets a random {@link Weather} that is applicable in the stated {@link Season}
	 *
	 * @param season as a constraint to what weathers will be used
	 * @return a random {@link Weather} that can be used in the stated {@link Season}
	 */
	public static Weather randomWeather(Season season) {
		Set<Weather> applicableWeathers = new HashSet<>();
		for (Weather weather : values()) {
			// Check whether the weather can be used with the season
			if (weather.getAffectedSeasons().contains(season) && !isManuallyDisabled(weather)) {
				applicableWeathers.add(weather);
			}
		}

		return Seasons.getInstance().getWeatherChanceConfiguration().pickRandomWeather(applicableWeathers);
	}

	/**
	 * Checks whether a weather has been manually disabled via config
	 *
	 * @param weather to check
	 * @return whether the weather is manually disabled
	 */
	public static boolean isManuallyDisabled(Weather weather) {
		SeasonsConfig config = Seasons.getInstance().getSeasonsConfig();
		for (String weatherName : config.getDisabledWeathers()) {
			if (weatherName.equalsIgnoreCase(weather.getRawName())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return a list of weathers by their name
	 */
	public static List<String> getWeatherList() {
		List<String> list = new ArrayList<>();
		for (Weather weather : Weather.values()) {
			list.add(weather.getName());
		}
		return list;
	}

}
