package uk.co.harieo.seasons.plugin.models;

import org.bukkit.ChatColor;

import java.util.*;

import uk.co.harieo.seasons.plugin.Seasons;
import uk.co.harieo.seasons.plugin.configuration.SeasonsConfig;
import uk.co.harieo.seasons.plugin.models.effect.Effect;

public enum Weather {

    BEAUTIFUL("Beautiful",
            ChatColor.GREEN + "The sun in shining, the grass is green and the weather is " + ChatColor.YELLOW
                    + "Beautiful",
            false, false,
            Arrays.asList(Season.SPRING, Season.SUMMER)),
    BREEZY("Breezy",
            ChatColor.GRAY + "A cool breeze touches your skin, it's going to be " + ChatColor.GREEN + "Breezy",
            false, false,
            Arrays.asList(Season.SPRING, Season.AUTUMN)),
    CHILLY("Chilly",
            ChatColor.BLUE + "You shiver as frost glistens around you, it's very " + ChatColor.DARK_BLUE + "Chilly "
                    + ChatColor.BLUE + "today",
            false, false,
            Collections.singletonList(Season.SPRING)),
    RAINY("Rainy",
            ChatColor.BLUE + "A loud rain falls and wets the ground, it's " + ChatColor.DARK_BLUE + "Rainy",
            false, true,
            Arrays.asList(Season.SPRING, Season.AUTUMN, Season.WINTER)),
    SCORCHING("Scorching",
            ChatColor.YELLOW + "The sun burns your skin and the ground hurts to touch, it's " + ChatColor.GOLD
                    + "Scorching",
            false, false,
            Collections.singletonList(Season.SUMMER)),
    HOT("Hot",
            ChatColor.YELLOW + "It's going to be very " + ChatColor.GOLD + "Hot " + ChatColor.YELLOW
                    + "today",
            false, false,
            Collections.singletonList(Season.SUMMER)),
    WARM("Warm",
            ChatColor.YELLOW + "A soothing warmth hugs you as you move, it's a " + ChatColor.GOLD + "Warm "
                    + ChatColor.YELLOW + "day",
            false, false,
            Collections.singletonList(Season.SUMMER)),
    COLD("Cold",
            ChatColor.BLUE + "The water is so very " + ChatColor.DARK_BLUE + "Cold " + ChatColor.BLUE + "today",
            false, false,
            Arrays.asList(Season.AUTUMN, Season.WINTER)),
    STORMY("Stormy",
            ChatColor.RED + "A great " + ChatColor.DARK_RED + "Storm " + ChatColor.RED
                    + "brews, the Gods in this place are angry... Brace yourself!",
            true, true,
            Collections.singletonList(Season.AUTUMN)),
    FREEZING("Freezing",
            ChatColor.BLUE + "The water freezes with a sheet of ice and you feel a great cold, it's "
                    + ChatColor.DARK_BLUE + "Freezing",
            true, false,
            Collections.singletonList(Season.WINTER)),
    SNOWY("Snowy",
            ChatColor.GRAY + "A great white blanket covers the world, it's " + ChatColor.WHITE + "Snowy",
            false, true,
            Collections.singletonList(Season.WINTER)),
    NIGHT("Calm", // Night is a weather with no effect, to give people a break
            ChatColor.GRAY + "The world rests with the sun and all is calm... Until the mobs come to eat you!",
            false, false);

    private static final Random random = new Random();

    private final String name; // Name shown to players
    private final String message; // Initial broadcast on weather trigger (Now only triggered if lang file is invalid)
    private final boolean catastrophic; // Is there is a high risk of this weather killing a player?
    private final boolean storm;
    private final List<Season> seasons; // List of seasons this weather can be triggered on

    Weather(String name, String broadcast, boolean catastrophic, boolean storm, List<Season> seasons) {
        this.name = name;
        this.message = broadcast;
        this.catastrophic = catastrophic;
        this.storm = storm;
        this.seasons = seasons;
    }

    Weather(String name, String broadcast, boolean catastrophic, boolean storm) {
        this.name = name;
        this.message = broadcast;
        this.catastrophic = catastrophic;
        this.storm = storm;
        this.seasons = Collections.emptyList();
    }

    public String getName() {
        return name;
    }

    public Optional<String> getMessage() {
        return Seasons.getInstance().getLanguageConfig().getStringOrDefault("weathers." + name().toLowerCase(), message);
    }

    public boolean isCatastrophic() {
        return catastrophic;
    }

    public boolean isStorm() {
        return storm;
    }

    public List<Season> getAffectedSeasons() {
        return seasons;
    }

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
            if (weather.getName().equalsIgnoreCase(name.toLowerCase())) {
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
        List<Weather> applicableWeathers = new ArrayList<>();
        for (Weather weather : values()) {
            if (weather.seasons.contains(season) && !isManuallyDisabled(weather)) { // Whether the weather can be used with the season
                applicableWeathers.add(weather);
            }
        }

        return applicableWeathers.get(random.nextInt(applicableWeathers.size()));
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
            if (weatherName.equalsIgnoreCase(weather.getName().toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    public static List<String> getWeatherList() {
        List<String> list = new ArrayList<>();
        for (Weather weather : Weather.values()) {
            list.add(weather.getName().toLowerCase());
        }
        return list;
    }

}
