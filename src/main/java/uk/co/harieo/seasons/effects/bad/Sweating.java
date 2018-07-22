package uk.co.harieo.seasons.effects.bad;

import java.util.Collections;
import uk.co.harieo.seasons.models.Effect;
import uk.co.harieo.seasons.models.Weather;

public class Sweating extends Effect {

	public Sweating() {
		super("Sweating", Collections.singletonList(Weather.HOT), false);
	}

}
