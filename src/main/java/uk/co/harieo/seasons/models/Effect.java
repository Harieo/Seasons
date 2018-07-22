package uk.co.harieo.seasons.models;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.List;
import uk.co.harieo.seasons.Seasons;

public abstract class Effect implements Listener {

	private String name;
	private List<Weather> weathers;
	private boolean isGood;

	public Effect(String name, List<Weather> weathers, boolean good) {
		this.name = name;
		this.weathers = weathers;
		this.isGood = good;

		Bukkit.getPluginManager().registerEvents(this, Seasons.getPlugin());
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
}
