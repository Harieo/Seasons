package uk.co.harieo.seasons.effects.bad;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Collections;
import uk.co.harieo.seasons.Seasons;
import uk.co.harieo.seasons.models.effect.Effect;
import uk.co.harieo.seasons.models.Weather;

public class HotSand extends Effect {

	public HotSand() {
		super("Hot Sand", "A small chance that you take damage walking on sand",
				Collections.singletonList(Weather.SCORCHING), false);
	}

	@Override
	public void onTrigger(World world) {
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (isPlayerCycleApplicable(player)) {
			Block block = event.getTo().getBlock();
			if (block.getType() == Material.SAND) {
				if (Seasons.RANDOM.nextInt(100) < 5) {
					player.damage(1);
				}
			}
		}
	}

}
