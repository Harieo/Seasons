package uk.co.harieo.seasons.effects.good;

import org.bukkit.event.EventHandler;

import java.util.Collections;
import uk.co.harieo.seasons.models.Effect;
import uk.co.harieo.seasons.models.Weather;

public class Revitalized extends Effect {

	public Revitalized() {
		super("Revitalized", Collections.singletonList(Weather.BEAUTIFUL), true);
	}

}
