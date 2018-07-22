package uk.co.harieo.seasons.effects.bad;

import java.util.Collections;
import uk.co.harieo.seasons.models.Effect;
import uk.co.harieo.seasons.models.Weather;

public class HotSand extends Effect {

	public HotSand() {
		super("Hot Sand", Collections.singletonList(Weather.SCORCHING), false);
	}

}
