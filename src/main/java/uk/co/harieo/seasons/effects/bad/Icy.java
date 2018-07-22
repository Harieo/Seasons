package uk.co.harieo.seasons.effects.bad;

import java.util.Collections;
import uk.co.harieo.seasons.models.Effect;
import uk.co.harieo.seasons.models.Weather;

public class Icy extends Effect {

	public Icy() {
		super("Icy", Collections.singletonList(Weather.FREEZING), false);
	}

}
