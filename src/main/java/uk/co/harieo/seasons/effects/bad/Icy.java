package uk.co.harieo.seasons.effects.bad;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Collections;
import uk.co.harieo.seasons.models.effect.Effect;
import uk.co.harieo.seasons.models.Weather;

public class Icy extends Effect {

	public Icy() {
		super("Icy", Collections.singletonList(Weather.FREEZING), false);
	}

	@Override
	public void onTrigger(World world) {

	}

	@EventHandler
	public void onWaterPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (isPlayerCycleApplicable(player)) {
			Block block = event.getBlockPlaced();
			if (block.getType() == Material.WATER) {
				block.setType(Material.ICE);
			}
		}
	}

}
