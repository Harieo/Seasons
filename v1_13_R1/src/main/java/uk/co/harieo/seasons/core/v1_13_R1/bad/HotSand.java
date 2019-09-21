package uk.co.harieo.seasons.core.v1_13_R1.bad;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Collections;
import uk.co.harieo.seasons.plugin.Seasons;
import uk.co.harieo.seasons.plugin.models.Weather;
import uk.co.harieo.seasons.plugin.models.effect.Effect;

public class HotSand extends Effect {

	public HotSand() {
		super("Hot Sand", "A moderate chance that you take damage walking on sand",
				Collections.singletonList(Weather.SCORCHING), false);
		setIgnoreRoof(false);
	}

	@Override
	public String getId() {
		return "hot-sand";
	}

	@Override
	public void onTrigger(World world) {
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (isPlayerCycleApplicable(player)) {
			Block block = event.getTo().clone().subtract(0, 1, 0).getBlock();
			if (block.getType() == Material.SAND) {
				if (Seasons.RANDOM.nextInt(100) < 25) {
					player.damage(1);
				}
			}
		}
	}

}
