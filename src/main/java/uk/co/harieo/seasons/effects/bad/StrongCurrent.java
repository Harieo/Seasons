package uk.co.harieo.seasons.effects.bad;

import java.util.Collections;
import uk.co.harieo.seasons.models.Effect;
import uk.co.harieo.seasons.models.Weather;

public class StrongCurrent extends Effect {

	public StrongCurrent() {
		super("Strong Current", Collections.singletonList(Weather.STORMY), false);
	}

}
