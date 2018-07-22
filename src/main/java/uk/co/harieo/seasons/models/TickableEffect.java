package uk.co.harieo.seasons.models;

public interface TickableEffect {

	/**
	 * This will be called every 20 ticks / 1 second
	 *
	 * @param cycle that this is being called on behalf of
	 */
	void onTick(Cycle cycle);

}
