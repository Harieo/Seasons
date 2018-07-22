package uk.co.harieo.seasons.effects.good;

import java.util.Collections;
import uk.co.harieo.seasons.models.Effect;
import uk.co.harieo.seasons.models.Weather;

public class FluffyCoat extends Effect {

	public FluffyCoat() {
		super("Fluffy Coat", Collections.singletonList(Weather.SNOWY), true);
	}

}
