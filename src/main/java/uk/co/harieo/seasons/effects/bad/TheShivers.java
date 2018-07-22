package uk.co.harieo.seasons.effects.bad;

import java.util.Collections;
import uk.co.harieo.seasons.models.Effect;
import uk.co.harieo.seasons.models.Weather;

public class TheShivers extends Effect {

	public TheShivers() {
		super("The Shivers", Collections.singletonList(Weather.CHILLY), false);
	}

}
