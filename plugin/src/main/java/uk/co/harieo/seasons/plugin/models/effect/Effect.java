package uk.co.harieo.seasons.plugin.models.effect;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import uk.co.harieo.seasons.plugin.Seasons;
import uk.co.harieo.seasons.plugin.configuration.SeasonsConfig;
import uk.co.harieo.seasons.plugin.events.SeasonsWeatherChangeEvent;
import uk.co.harieo.seasons.plugin.models.Cycle;
import uk.co.harieo.seasons.plugin.models.Weather;

public abstract class Effect implements Listener {

	private static final List<String> cache = new ArrayList<>(); // Prevents multiple registrations of 1 listener

	private String name;
	private String description;
	private List<Weather> weathers;
	private boolean isGood;

	public Effect(String name, String description, List<Weather> weathers, boolean good) {
		this.name = name;
		this.description = description;
		this.weathers = weathers;
		this.isGood = good;

		Seasons seasons = Seasons.getInstance();
		if (!cache.contains(name) && seasons.getSeasonsConfig().hasEnabledEffects()) {
			Bukkit.getPluginManager().registerEvents(this, seasons.getPlugin());
			cache.add(name);
		}
	}

	/**
	 * @return the name of this effect, shown to the player
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return a description of this effect, shown to the player
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the list of {@link Weather} to which this effect can be used upon
	 */
	public List<Weather> getWeathers() {
		return weathers;
	}

	/**
	 * @return whether this is a primarily positive effect for the player
	 */
	public boolean isGood() {
		return isGood;
	}

	/**
	 * Checks whether the {@link Weather} is in the list of applicable Weathers attached to this effect
	 *
	 * @param weather to be checked
	 * @return whether this {@link Weather} can be used with this effect
	 */
	public boolean isWeatherApplicable(Weather weather) {
		return getWeathers().contains(weather);
	}

	/**
	 * Retrieves the {@link Cycle} and, by extension, {@link Weather} that the {@link Player} is currently affected by
	 * and checks whether the stated {@link Player} is affected by this effect
	 *
	 * @param player to be checked
	 * @return whether the {@link Player} is affected by this effect
	 */
	protected boolean isPlayerCycleApplicable(Player player) {
		Cycle cycle = Seasons.getInstance().getWorldCycle(player.getWorld());
		return cycle != null && isWeatherApplicable(cycle.getWeather());
	}

	@EventHandler
	public void onWeatherChange(SeasonsWeatherChangeEvent event){
		if (isWeatherApplicable(event.getChangedTo())) {
			onTrigger(event.getCycle().getWorld());
		}
	}

	/**
	 * An abstract method to handle when this effect is activated upon a {@link World}
	 *
	 * @param world that this effect is being triggered on
	 */
	public abstract void onTrigger(World world);
}
