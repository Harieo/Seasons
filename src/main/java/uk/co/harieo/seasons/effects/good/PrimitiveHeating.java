package uk.co.harieo.seasons.effects.good;

import java.util.Collections;
import uk.co.harieo.seasons.models.Effect;
import uk.co.harieo.seasons.models.Weather;

public class PrimitiveHeating extends Effect {

	public PrimitiveHeating() {
		super("Primitive Heating", Collections.singletonList(Weather.COLD), true);
	}

}
