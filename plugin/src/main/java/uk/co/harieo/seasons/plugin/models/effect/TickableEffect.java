package uk.co.harieo.seasons.plugin.models.effect;

import uk.co.harieo.seasons.plugin.models.Cycle;

public interface TickableEffect {

	/**
	 * This will be called every 20 ticks / 1 second
	 *
	 * @param cycle that this is being called on behalf of
	 */
	void onTick(Cycle cycle);

}
