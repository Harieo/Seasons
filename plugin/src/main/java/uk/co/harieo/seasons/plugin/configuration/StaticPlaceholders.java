package uk.co.harieo.seasons.plugin.configuration;

public enum StaticPlaceholders {

	YEAR("year"),
	SEASON("season"),
	WEATHER("weather"),
	DAY("day"),
	MAX_DAYS("max-days"),
	SEASON_NUMBER("season-number"),
	SEASONS_PER_YEAR("seasons-per-year"),
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
