package uk.co.harieo.seasons.effects.bad;

import java.util.Collections;
import uk.co.harieo.seasons.models.Effect;
import uk.co.harieo.seasons.models.Weather;

public class WetMud extends Effect {

	public WetMud() {
		super("Wet Mud", Collections.singletonList(Weather.RAINY), false);
	}

}
