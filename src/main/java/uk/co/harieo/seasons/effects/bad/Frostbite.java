package uk.co.harieo.seasons.effects.bad;

import java.util.Arrays;
import java.util.Collections;
import uk.co.harieo.seasons.models.Effect;
import uk.co.harieo.seasons.models.Weather;

public class Frostbite extends Effect {

	public Frostbite() {
		super("Frostbite", Arrays.asList(Weather.FREEZING, Weather.SNOWY), false);
	}

}
