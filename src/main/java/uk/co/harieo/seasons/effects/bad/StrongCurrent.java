package uk.co.harieo.seasons.effects.bad;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import uk.co.harieo.seasons.Seasons;
import uk.co.harieo.seasons.models.Weather;
import uk.co.harieo.seasons.models.effect.SeasonsPotionEffect;

public class StrongCurrent extends SeasonsPotionEffect {

	public StrongCurrent() {
		super("Strong Current", "Receive Slowness 2 for 20 seconds when you enter water",
				Collections.singletonList(Weather.STORMY), false,
				new PotionEffect(PotionEffectType.SLOW, 20 * 20, 1));
	}

	@Override
	public boolean shouldGive(Player player) {
		if (isPlayerCycleApplicable(player)) {
			Block block = player.getLocation().getBlock();
			return block.getType() == Material.STATIONARY_WATER;
		} else {
			return false;
		}
	}

	@Override
	public void sendGiveMessage(Player player) {
		player.sendMessage(
				Seasons.PREFIX + ChatColor.RED + "The current crashes against you and your muscles cry in pain...");
	}

	@Override
	public void sendRemoveMessage(Player player) {
		player.sendMessage(Seasons.PREFIX + ChatColor.GREEN
				+ "The waters die down and grow still...");
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		giveEffect(player, true);
	}

}
