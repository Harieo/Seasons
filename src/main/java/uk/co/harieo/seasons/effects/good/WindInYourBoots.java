package uk.co.harieo.seasons.effects.good;

import java.util.Collections;
import uk.co.harieo.seasons.models.Effect;
import uk.co.harieo.seasons.models.Weather;

public class WindInYourBoots extends Effect {

	public WindInYourBoots() {
		super("Wind in Your Boots", Collections.singletonList(Weather.BREEZY), true);
	}

}
