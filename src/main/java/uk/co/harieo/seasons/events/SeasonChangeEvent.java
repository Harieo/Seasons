package uk.co.harieo.seasons.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import uk.co.harieo.seasons.models.Cycle;
import uk.co.harieo.seasons.models.Season;

public class SeasonChangeEvent extends Event {

	private Cycle cycle;
	private Season changeTo;
	private Season changeFrom;
	private boolean naturalChange;

	public SeasonChangeEvent(Cycle cycle, Season changeTo, Season changeFrom, boolean natural) {
		this.cycle = cycle;
		this.changeTo = changeTo;
		this.changeFrom = changeFrom;
		this.naturalChange = natural;
	}

	/**
	 * @return the {@link Cycle} this change effects
	 */
	public Cycle getCycle() {
		return cycle;
	}

	/**
	 * @return the {@link Season} that the {@link Cycle} will change to
	 */
	public Season getChangedTo() {
		return changeTo;
	}

	/**
	 * @return the {@link Season} that the {@link Cycle} has changed from
	 */
	public Season getChangedFrom() {
		return changeFrom;
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
