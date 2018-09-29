package uk.co.harieo.seasons.plugin.models;

import org.bukkit.World;

public class Cycle {

	/**
	 * This class represents a cycle of a season and weather in a world based on Minecraft days.
	 * The purpose of this constructor is to easily represent the data; it will NOT handle the mechanics of this
	 * system.
	 */

	private World world;
	private Season season;
	private Weather weather;
	private int day;

	public Cycle(World world, Season season, Weather weather, int day) {
		this.world = world;
		this.season = season;
		this.weather = weather;
		this.day = day;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public Season getSeason() {
		return season;
	}

	public void setSeason(Season season) {
		this.season = season;
	}

	public Weather getWeather() {
		return weather;
	}

	public void setWeather(Weather weather) {
		this.weather = weather;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}
}
