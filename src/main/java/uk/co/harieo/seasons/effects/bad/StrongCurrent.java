package uk.co.harieo.seasons.effects.bad;

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
		super("Strong Current", Collections.singletonList(Weather.STORMY), false,
				new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1));
	}

	@Override
	public boolean shouldGive(Player player) {
		if (isPlayerCycleApplicable(player)) {
			Block block = player.getLocation().getBlock();
			return block.getType() == Material.WATER;
		} else {
			return false;
		}
	}

	@Override
	public void sendGiveMessage(Player player) {
		player.sendMessage(
				Seasons.PREFIX + ChatColor.RED + "The current crashes against you and you struggle to fight it...");
	}

	@Override
	public void sendRemoveMessage(Player player) {
		player.sendMessage(Seasons.PREFIX + ChatColor.GREEN
				+ "You are free from the devastating currents but you are far from safe yet...");
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (shouldGive(player)) {
			giveEffect(player, true);
		} else {
			removeEffect(player, false);
		}
	}

}
