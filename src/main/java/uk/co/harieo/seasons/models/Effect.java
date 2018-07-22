package uk.co.harieo.seasons.models;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import uk.co.harieo.seasons.Seasons;
import uk.co.harieo.seasons.configuration.SeasonsConfig;
import uk.co.harieo.seasons.events.SeasonsWeatherChangeEvent;

public abstract class Effect implements Listener {

	private static final List<String> cache = new ArrayList<>(); // Prevents multiple registrations of 1 listener

	private String name;
	private List<Weather> weathers;
	private boolean isGood;

	public Effect(String name, List<Weather> weathers, boolean good) {
		this.name = name;
		this.weathers = weathers;
		this.isGood = good;

		if (!cache.contains(name) && SeasonsConfig.get().hasEnabledEffects()) {
			Bukkit.getPluginManager().registerEvents(this, Seasons.getPlugin());
			cache.add(name);
		}
	}

	public String getName() {
		return name;
	}

	public List<Weather> getWeathers() {
		return weathers;
	}

	public boolean isGood() {
		return isGood;
	}

	protected boolean isWeatherApplicable(Weather weather) {
		return getWeathers().contains(weather);
	}

	protected boolean isPlayerCycleApplicable(Player player) {
		Cycle cycle = Seasons.getWorldCycle(player.getWorld());
		return cycle != null && isWeatherApplicable(cycle.getWeather());
	}

	@EventHandler
	public abstract void onWeatherChange(SeasonsWeatherChangeEvent event);
}
