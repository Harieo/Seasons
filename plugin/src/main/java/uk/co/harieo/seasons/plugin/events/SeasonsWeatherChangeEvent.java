package uk.co.harieo.seasons.plugin.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import uk.co.harieo.seasons.plugin.models.Cycle;
import uk.co.harieo.seasons.plugin.models.Weather;

public class SeasonsWeatherChangeEvent extends Event {

	private Cycle cycle;
	private Weather changeFrom;
	private Weather changeTo;
	private boolean naturalChange;

	public SeasonsWeatherChangeEvent(Cycle cycle, Weather changeFrom, Weather changeTo, boolean natural) {
		this.cycle = cycle;
		this.changeFrom = changeFrom;
		this.changeTo = changeTo;
		// Note: this event is only called when night ends, therefore it will always changeFrom NIGHT
		this.naturalChange = natural;
	}

	/**
	 * @return the {@link Cycle} this change effects
	 */
	public Cycle getCycle() {
		return cycle;
	}

	/**
	 * @return the {@link Weather} that the {@link Cycle} changed from
	 */
	public Weather getChangeFrom() {
		return changeFrom;
	}

	/**
	 * @return the {@link Weather} that the {@link Cycle} will change to
	 */
	public Weather getChangedTo() {
		return changeTo;
	}

	/**
	 * @return whether the change was made by the automated system
	 */
	public boolean isNaturalChange() {
		return naturalChange;
	}

	private static final HandlerList handlers = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
