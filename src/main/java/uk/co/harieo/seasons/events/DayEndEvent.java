package uk.co.harieo.seasons.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import uk.co.harieo.seasons.models.Cycle;
import uk.co.harieo.seasons.models.Weather;

public class DayEndEvent extends Event {

	private Cycle cycle;
	private Weather changeFrom;

	public DayEndEvent(Cycle cycle, Weather changeFrom) {
		this.changeFrom = changeFrom;
	}

	public Cycle getCycle() {
		return cycle;
	}

	public Weather getChangeFrom() {
		return changeFrom;
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
