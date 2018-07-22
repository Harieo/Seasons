package uk.co.harieo.seasons.effects.bad;

import java.util.Collections;
import uk.co.harieo.seasons.models.Effect;
import uk.co.harieo.seasons.models.Weather;

public class Devastation extends Effect {

	public Devastation() {
		super("Devastation", Collections.singletonList(Weather.STORMY), false);
	}

}
