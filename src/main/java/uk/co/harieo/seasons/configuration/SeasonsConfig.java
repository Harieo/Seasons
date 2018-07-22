package uk.co.harieo.seasons.configuration;

import org.bukkit.configuration.file.FileConfiguration;

public class SeasonsConfig {

	private static SeasonsConfig INSTANCE;

	private int daysPerSeason; // Days that must go by before the world moves to the next season
	private int ticksPerDamage; // Whether to activate the effects of the seasonal weathers
	private boolean enableEffects; // How many ticks per damage dealt to a player on a harmful weather

	public SeasonsConfig(FileConfiguration config) {
		daysPerSeason = config.getInt("Days-Per-Season");
		ticksPerDamage = config.getInt("Ticks-Of-Damage");
		enableEffects = config.getBoolean("Custom-Weathers");
		INSTANCE = this;
	}

	public int getDaysPerSeason() {
		return daysPerSeason;
	}

	public int getTicksPerDamage() {
		return ticksPerDamage;
	}

	public boolean hasEnabledEffects() {
		return enableEffects;
	}

	public static SeasonsConfig get() {
		return INSTANCE;
	}

}
