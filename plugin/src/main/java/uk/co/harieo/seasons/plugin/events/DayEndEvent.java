package uk.co.harieo.seasons.plugin.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import uk.co.harieo.seasons.plugin.models.Cycle;
import uk.co.harieo.seasons.plugin.models.Weather;

public class DayEndEvent extends Event {

	private Cycle cycle;
	private Weather changeFrom;
	private boolean natural;

	public DayEndEvent(Cycle cycle, Weather changeFrom, boolean natural) {
		this.cycle = cycle;
		this.changeFrom = changeFrom;
		this.natural = natural;
	}

	public Cycle getCycle() {
		return cycle;
	}

	public Weather getChangeFrom() {
		return changeFrom;
	}

	public boolean isNatural() {
		return natural;
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
