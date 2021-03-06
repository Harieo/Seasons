package uk.co.harieo.seasons.core;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.harieo.seasons.core.v1_12_2_R1.bad.*;
import uk.co.harieo.seasons.core.v1_12_2_R1.good.Legacy_PrimitiveHeating;
import uk.co.harieo.seasons.core.v1_12_2_R1.good.Legacy_WarmingStew;
import uk.co.harieo.seasons.core.v1_13_R1.bad.*;
import uk.co.harieo.seasons.core.v1_13_R1.good.*;
import uk.co.harieo.seasons.plugin.Seasons;

public class SeasonsCore extends JavaPlugin {

	private Seasons seasons;

	@Override
	public void onEnable() {
		seasons = new Seasons(this);
		seasons.startup();

		String version = Bukkit.getVersion();
		if (version.contains("1.12") || version.contains("1.11") || version.contains("1.10") || version
				.contains("1.9")) {
			getLogger().info("Seasons has detected legacy Spigot (pre-1.13) and will adapt to use 1.12 implementation");
			seasons.addEffects(new Legacy_Icy(), new Legacy_PrimitiveHeating(), new Legacy_SolderingIron(),
					new Legacy_StrongCurrent(), new Legacy_TheShivers(), new Legacy_WetMud(), new Legacy_WarmingStew());
		} else {
			getLogger().info("Seasons has detected modern Spigot (1.13+) and will adapt to use 1.13 implementation");
			seasons.addEffects(new Icy(), new PrimitiveHeating(), new SolderingIron(), new StrongCurrent(),
					new TheShivers(),
					new WetMud(), new WarmingStew());
		}

		// Register effects that are compatible with modern and legacy spigot
		seasons.addEffects(new Devastation(), new FeelsGood(), new FluffyCoat(), new Frostbite(), new HoldOntoYourHat(),
				new HotSand(), new Revitalized(), new Sweating(), new WindInYourBoots());
	}

	@Override
	public void onDisable() {
		seasons.disable();
	}

}
