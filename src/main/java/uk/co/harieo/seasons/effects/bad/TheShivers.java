package uk.co.harieo.seasons.effects.bad;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import uk.co.harieo.seasons.Seasons;
import uk.co.harieo.seasons.models.Cycle;
import uk.co.harieo.seasons.models.effect.Effect;
import uk.co.harieo.seasons.models.Weather;
import uk.co.harieo.seasons.models.effect.TickableEffect;

public class TheShivers extends Effect implements TickableEffect {

	private Map<Player, Integer> secondsPast = new HashMap<>();

	public TheShivers() {
		super("The Shivers", "Take damage when moving in water and be warned, this effect will last until death",
				Collections.singletonList(Weather.CHILLY), false);
	}

	private void damage(Player player) {
		if (isPlayerCycleApplicable(player)) {
			Block block = player.getLocation().getBlock();
			if (block.getType() == Material.STATIONARY_WATER && !(player.getVehicle() instanceof Boat)) {
				player.damage(1);
				if (!secondsPast.containsKey(player)) {
					secondsPast.put(player, 10);
				}
			} else {
				secondsPast.remove(player);
			}
		}
	}

	@Override
	public void onTrigger(World world) {
		secondsPast.clear();
		for (Player player : world.getPlayers()) {
			damage(player);
			player.sendMessage(Seasons.PREFIX + ChatColor.RED
					+ "You shiver from the icy water, plan your next move carefully or it will be your last...");
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		damage(event.getPlayer());
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		secondsPast.remove(event.getEntity());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		secondsPast.remove(event.getPlayer());
	}

	@Override
	public void onTick(Cycle cycle) {
		for (Player player : cycle.getWorld().getPlayers()) {
			if (secondsPast.containsKey(player)) {
				int seconds = secondsPast.get(player);
				if (seconds >= 15) {
					player.sendMessage(Seasons.PREFIX
							+ ChatColor.RED + "The freezing water is killing you, get out of it if you want to live!");
					secondsPast.replace(player, 0);
				} else {
					secondsPast.replace(player, seconds + 1);
				}
			}
		}
	}

}
