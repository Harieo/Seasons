package uk.co.harieo.seasons.effects.good;

import java.util.Collections;
import uk.co.harieo.seasons.models.Effect;
import uk.co.harieo.seasons.models.Weather;

public class FeelsGood extends Effect {

	public FeelsGood() {
		super("Feels Good", Collections.singletonList(Weather.WARM), true);
	}

}
