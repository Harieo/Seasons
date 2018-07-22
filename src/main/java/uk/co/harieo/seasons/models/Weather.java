package uk.co.harieo.seasons.models;

import org.bukkit.ChatColor;

import java.util.*;
import uk.co.harieo.seasons.effects.bad.*;
import uk.co.harieo.seasons.effects.good.*;

public enum Weather {

	BEAUTIFUL("Beautiful",
			ChatColor.GREEN + "The sun in shining, the grass is green and the weather is " + ChatColor.YELLOW
					+ "Beautiful",
			false,
			Collections.singletonList(new Revitalized()),
			Arrays.asList(Season.SPRING, Season.SUMMER)),
	BREEZY("Breezy",
			ChatColor.GRAY + "A cool breeze touches your skin, it's going to be " + ChatColor.GREEN + "Breezy",
			false,
			Arrays.asList(new HoldOntoYourHat(), new WindInYourBoots()),
			Arrays.asList(Season.SPRING, Season.AUTUMN)),
	CHILLY("Chilly",
			ChatColor.BLUE + "You shiver as frost glistens around you, it's very " + ChatColor.DARK_BLUE + "Chilly "
					+ ChatColor.BLUE + "today",
			false,
			Collections.singletonList(new TheShivers()),
			Collections.singletonList(Season.SPRING)),
	RAINY("Rainy",
			ChatColor.BLUE + "A loud rain falls and wets the ground, it's " + ChatColor.DARK_BLUE + "Rainy",
			false,
			Collections.singletonList(new WetMud()),
			Arrays.asList(Season.SPRING, Season.AUTUMN, Season.WINTER)),
	SCORCHING("Scorching",
			ChatColor.YELLOW + "The sun burns your skin and the ground hurts to touch, it's " + ChatColor.GOLD
					+ "Scorching",
			true,
			Arrays.asList(new HotSand(), new SolderingIron()),
			Collections.singletonList(Season.SUMMER)),
	HOT("Hot",
			ChatColor.YELLOW + "It's going to be a very " + ChatColor.GOLD + "Hot " + ChatColor.YELLOW
					+ "today",
			false,
			Collections.singletonList(new Sweating()),
			Collections.singletonList(Season.SUMMER)),
	WARM("Warm",
			ChatColor.YELLOW + "A soothing warmth hugs you as you move, it's a " + ChatColor.GOLD + "Warm "
					+ ChatColor.YELLOW + "day",
			false,
			Collections.singletonList(new FeelsGood()),
			Collections.singletonList(Season.SUMMER)),
	COLD("Cold",
			ChatColor.BLUE + "The water is so very " + ChatColor.DARK_BLUE + "Cold " + ChatColor.BLUE + "today",
			false,
			Arrays.asList(new PrimitiveHeating(), new WarmingStew()),
			Arrays.asList(Season.AUTUMN, Season.WINTER)),
	STORMY("Stormy",
			ChatColor.RED + "A great " + ChatColor.DARK_RED + "Storm " + ChatColor.RED
					+ "brews, the Gods in this place are angry... Brace yourself!",
			true,
			Arrays.asList(new Devastation(), new StrongCurrent()),
			Collections.singletonList(Season.AUTUMN)),
	FREEZING("Freezing",
			ChatColor.BLUE + "The water freezes with a sheet of ice and you feel a great cold, it's "
					+ ChatColor.DARK_BLUE + "Freezing",
			true,
			Arrays.asList(new Icy(), new Frostbite()),
			Collections.singletonList(Season.WINTER)),
	SNOWY("Snowy",
			ChatColor.GRAY + "A great white blanket covers the world, it's " + ChatColor.WHITE + "Snowy",
			false,
			Collections.singletonList(new FluffyCoat()),
			Collections.singletonList(Season.WINTER)),
	NIGHT("Night", // Night is a weather with no effect, to give people a break
			ChatColor.GRAY + "The world rests with the sun and all is calm... Until the mobs come to eat you!",
			false);

	private static final Random random = new Random();

	private String name; // Name shown to players
	private String message; // Initial broadcast on weather trigger
	private boolean catastrophic; // Is there is a high risk of this weather killing a player?
	private List<Effect> effects; // List of affects that happen if this weather is active
	private List<Season> seasons; // List of seasons this weather can be triggered on

	Weather(String name, String broadcast, boolean catastrophic, List<Effect> effects, List<Season> seasons) {
		this.name = name;
		this.message = broadcast;
		this.catastrophic = catastrophic;
		this.effects = effects;
		this.seasons = seasons;
	}

	Weather(String name, String broadcast, boolean catastrophic) {
		this.name = name;
		this.message = broadcast;
		this.catastrophic = catastrophic;
		this.effects = Collections.emptyList();
		this.seasons = Collections.emptyList();
	}

	public String getName() {
		return name;
	}

	public String getMessage() {
		return message;
	}

	public boolean isCatastrophic() {
		return catastrophic;
	}

	public List<Effect> getEffects() {
		return effects;
	}

	public List<Season> getAffectedSeasons() {
		return seasons;
	}

	/**
	 * Finds the relevant {@link Weather} based on the name property Note: This will ignore cases for user friendliness
	 *
	 * @param name to check for
	 * @return the relevant {@link Weather} or null if none found
	 */
	public static Weather fromName(String name) {
		for (Weather weather : values()) {
			if (weather.getName().equalsIgnoreCase(name.toLowerCase())) {
				return weather;
			}
		}

		return null;
	}

	public static Weather randomWeather() {
		return values()[random.nextInt(values().length)];
	}

	public static Weather randomWeather(Season season) {
		List<Weather> applicableWeathers = new ArrayList<>();
		for (Weather weather : values()) {
			if (weather.seasons.contains(season)) { // Whether the weather can be used with the season
				applicableWeathers.add(weather);
			}
		}

		return applicableWeathers.get(random.nextInt(applicableWeathers.size()));
	}

}
