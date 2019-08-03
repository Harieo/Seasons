package uk.co.harieo.seasons.core.v1_12_2_R1.bad;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import uk.co.harieo.seasons.plugin.Seasons;
import uk.co.harieo.seasons.plugin.models.Weather;
import uk.co.harieo.seasons.plugin.models.effect.SeasonsPotionEffect;

public class Legacy_WetMud extends SeasonsPotionEffect {

	private static final Material[] MUDDY = {Material.DIRT, Material.SOIL};

	public Legacy_WetMud() {
		super("Wet Mud", "Receive Slowness 2 when walking on dirt",
				Collections.singletonList(Weather.RAINY), false,
				new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1));
	}

	@Override
	public String getId() {
		return "wet-mud";
	}

	@Override
	public boolean shouldGive(Player player) {
		if (isPlayerCycleApplicable(player)) {
			Block block = player.getLocation().clone().subtract(0, 1, 0).getBlock();
			for (Material material : MUDDY) {
				if (block.getType() == material) {
					return true;
				}
			}

			return false;
		} else {
			return false;
		}
	}

	@Override
	public void sendGiveMessage(Player player) {
		player.sendMessage(Seasons.PREFIX + ChatColor.YELLOW + "The mud sticks to your boots, slowing you down...");
	}

	@Override
	public void sendRemoveMessage(Player player) {
		// No remove message, would spam too much
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (shouldGive(player)) {
			giveEffect(player, false);
		} else {
			removeEffect(player, false);
		}
	}

}
