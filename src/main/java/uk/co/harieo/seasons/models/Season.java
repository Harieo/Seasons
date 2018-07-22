package uk.co.harieo.seasons.models;

import org.bukkit.ChatColor;

public enum Season {

	SPRING("Spring", ChatColor.YELLOW),
	SUMMER("Summer", ChatColor.GOLD),
	AUTUMN("Autumn", ChatColor.DARK_GREEN),
	WINTER("Winter", ChatColor.BLUE);

	private String name; // Unique identifier for the season that will look nice in chat (front and back end)
	private ChatColor color; // Color to be used when the season is referenced

	Season(String name, ChatColor seasonColor) {
		this.name = name;
		this.color = seasonColor;
	}

	public String getName() {
		return name;
	}

	public ChatColor getColor() {
		return color;
	}

	@Override
	public String toString() {
		return color + name;
	}
}
