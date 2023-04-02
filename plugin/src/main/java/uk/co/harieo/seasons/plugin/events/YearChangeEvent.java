package uk.co.harieo.seasons.plugin.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import uk.co.harieo.seasons.plugin.models.Cycle;

public class YearChangeEvent extends Event {

    private Cycle cycle;
    private int changeTo;
    private int changeFrom;
    private boolean naturalChange;

    public YearChangeEvent(Cycle cycle, int changeTo, int changeFrom, boolean natural) {
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
     * @return the year that the {@link Cycle} will change to
     */
    public int getChangedTo() {
        return changeTo;
    }

    /**
     * @return the year that the {@link Cycle} has changed from
     */
    public int getChangedFrom() {
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