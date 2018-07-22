package uk.co.harieo.seasons.models;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import uk.co.harieo.seasons.effects.bad.*;
import uk.co.harieo.seasons.effects.good.*;

public enum Weather {

	BEAUTIFUL("Beautiful", false, Collections.singletonList(new Revitalized()), Season.SPRING, Season.SUMMER),
	BREEZY("Breezy", false, Arrays.asList(new HoldOntoYourHat(), new WindInYourBoots()), Season.SPRING, Season.AUTUMN),
	CHILLY("Chilly", false, Collections.singletonList(new TheShivers()), Season.SPRING),
	RAINY("Rainy", false, Collections.singletonList(new WetMud())),
	SCORCHING("Scorching", true, Arrays.asList(new HotSand(), new SolderingIron()), Season.SUMMER),
	HOT("Hot", false, Collections.singletonList(new Sweating()), Season.SUMMER),
	WARM("Warm", false, Collections.singletonList(new FeelsGood()), Season.SUMMER),
	COLD("Cold", false, Arrays.asList(new PrimitiveHeating(), new WarmingStew()), Season.AUTUMN, Season.WINTER),
	STORMY("Stormy", true, Arrays.asList(new Devastation(), new StrongCurrent()), Season.AUTUMN),
	FREEZING("Freezing", true, Arrays.asList(new Icy(), new Frostbite()), Season.WINTER),
	SNOWY("Snowy", false, Arrays.asList(new Frostbite(), new FluffyCoat()), Season.WINTER);

	private String name; // Name shown to players
	private boolean catastrophic; // Is there is a high risk of this weather killing a player?
	private List<Effect> effects; // List of affects that happen if this weather is active
	private Season[] seasons; // List of seasons this weather can be triggered on

	Weather(String name, boolean catastrophic, List<Effect> effects, Season... seasons) {
		this.name = name;
		this.catastrophic = catastrophic;
		this.effects = effects;
		this.seasons = seasons;
	}

	public String getName() {
		return name;
	}

	public boolean isCatastrophic() {
		return catastrophic;
	}

	public List<Effect> getEffects() {
		return effects;
	}

	public Season[] getAffectedSeasons() {
		return seasons;
	}

}
