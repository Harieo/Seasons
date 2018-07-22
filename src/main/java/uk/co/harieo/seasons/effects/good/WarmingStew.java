package uk.co.harieo.seasons.effects.good;

import java.util.Collections;
import uk.co.harieo.seasons.models.Effect;
import uk.co.harieo.seasons.models.Weather;

public class WarmingStew extends Effect {

	public WarmingStew() {
		super("Warming Stew", Collections.singletonList(Weather.COLD), true);
	}

}
