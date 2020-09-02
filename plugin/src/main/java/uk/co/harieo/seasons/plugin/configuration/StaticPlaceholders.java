package uk.co.harieo.seasons.plugin.configuration;

public enum StaticPlaceholders {

	SEASON("season"),
	WEATHER("weather"),
	DAY("day"),
	MAX_DAYS("max-days"),
	OPTIONS("options"),
	SEASON_COLOR("season-color");

	private final String placeholder;

	/**
	 * A placeholder used in the language configuration file to represent a variable
	 *
	 * @param key a unique identifier to put between the placeholder characters
	 */
	StaticPlaceholders(String key) {
		this.placeholder = '%' + key + '%';
	}

	@Override
	public String toString() {
		return placeholder;
	}

}
