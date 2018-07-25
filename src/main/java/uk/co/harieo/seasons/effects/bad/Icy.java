package uk.co.harieo.seasons.effects.bad;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import uk.co.harieo.seasons.Seasons;
import uk.co.harieo.seasons.models.effect.Effect;
import uk.co.harieo.seasons.models.Weather;

public class Icy extends Effect {

	public Icy() {
		super("Icy", "Any water you place down will turn to ice",
				Collections.singletonList(Weather.FREEZING), false);
	}

	@Override
	public void onTrigger(World world) {
	}

	@EventHandler
	public void onWaterPlace(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		if (isPlayerCycleApplicable(player)) {
			Block block = event.getBlockClicked().getRelative(event.getBlockFace());
			BukkitRunnable runnable = new BukkitRunnable() {
				@Override
				public void run() {
					if (block.getType() == Material.STATIONARY_WATER) {
						block.setType(Material.ICE);
					}
				}
			};
			runnable.runTaskLater(Seasons.getPlugin(), 10);
		}
	}

	@EventHandler
	public void onWaterPickup(PlayerBucketFillEvent event) {
		Player player = event.getPlayer();
		if (isPlayerCycleApplicable(player)) {
			Block block = event.getBlockClicked().getRelative(event.getBlockFace());
			if (block.getType() == Material.STATIONARY_WATER) {
				event.setItemStack(new ItemStack(Material.BUCKET));
				player.sendMessage(Seasons.PREFIX + ChatColor.RED
						+ "The water freezes in the bucket so you decide to throw it away...");
			}
		}
	}

}
