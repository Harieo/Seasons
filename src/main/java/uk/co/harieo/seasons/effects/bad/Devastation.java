package uk.co.harieo.seasons.effects.bad;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import java.util.Collections;
import uk.co.harieo.seasons.Seasons;
import uk.co.harieo.seasons.models.effect.Effect;
import uk.co.harieo.seasons.models.Weather;

public class Devastation extends Effect {

	public Devastation() {
		super("Devastation", Collections.singletonList(Weather.STORMY), false);
	}

	@Override
	public void onTrigger(World world) {
		for (Player player : world.getPlayers()) {
			player.sendMessage(Seasons.PREFIX + ChatColor.RED
					+ "Your hearts beats rapidly, yours legs tremble and you find you cannot regenerate health until this Devastation passes!");
		}
	}

	@EventHandler
	public void onRegeneration(EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (isPlayerCycleApplicable(player)) {
				event.setCancelled(true);
			}
		}
	}
}
