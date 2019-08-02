package uk.co.harieo.seasons.core.v1_12_2_R1.bad;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import uk.co.harieo.seasons.plugin.Seasons;
import uk.co.harieo.seasons.plugin.models.Weather;
import uk.co.harieo.seasons.plugin.models.effect.Effect;

public class Legacy_Icy extends Effect {

	public Legacy_Icy() {
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
			runnable.runTaskLater(Seasons.getInstance().getPlugin(), 10);
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
