package uk.co.harieo.seasons.core.v1_13_R1.bad;

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
import uk.co.harieo.seasons.plugin.models.Cycle;
import uk.co.harieo.seasons.plugin.models.Weather;
import uk.co.harieo.seasons.plugin.models.effect.Effect;
import uk.co.harieo.seasons.plugin.models.effect.TickableEffect;

public class TheShivers extends Effect implements TickableEffect {

	private Map<Player, Integer> secondsPast = new HashMap<>();

	public TheShivers() {
		super("The Shivers", "Take damage when moving in water and be warned, this effect will last until death",
				Collections.singletonList(Weather.CHILLY), false);
	}

	@Override
	public String getId() {
		return "the-shivers";
	}

	/**
	 * Checks whether a player is standing in water and is not in a boat
	 * If this condition is met, they are marked to be damaged
	 * If this condition is NOT met, the damage marker is removed
	 *
	 * @param player to check and mark
	 */
	private void damage(Player player) {
		if (isPlayerCycleApplicable(player) && !player.isInsideVehicle()) {
			Block block = player.getLocation().getBlock();
			if (block.getType() == Material.WATER && !(player.getVehicle() instanceof Boat)) {
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
					sendGiveMessage(player,
							ChatColor.RED + "The freezing water is killing you, get out of it if you want to live!");
					secondsPast.replace(player, 0);
				} else {
					secondsPast.replace(player, seconds + 1);
				}
			}
		}
	}

}
