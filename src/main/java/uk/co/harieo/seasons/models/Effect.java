package uk.co.harieo.seasons.models;

import org.bukkit.event.Listener;

import java.util.List;

public abstract class Effect implements Listener {

	private String name;
	private List<Weather> weathers;
	private boolean isGood;

	public Effect(String name, List<Weather> weathers, boolean good) {
		this.name = name;
		this.weathers = weathers;
		this.isGood = good;
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
