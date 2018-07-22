package uk.co.harieo.seasons.effects.bad;

import java.util.Collections;
import uk.co.harieo.seasons.models.Effect;
import uk.co.harieo.seasons.models.Weather;

public class HoldOntoYourHat extends Effect {

	public HoldOntoYourHat() {
		super("Hold onto Your Hat", Collections.singletonList(Weather.BREEZY), false);
	}

}
