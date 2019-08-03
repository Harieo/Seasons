package uk.co.harieo.seasons.core.v1_13_R1.good;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import uk.co.harieo.seasons.plugin.Seasons;
import uk.co.harieo.seasons.plugin.models.Weather;
import uk.co.harieo.seasons.plugin.models.effect.SeasonsPotionEffect;

public class Revitalized extends SeasonsPotionEffect {

	public Revitalized() {
		super("Revitalized", "Receive Regeneration 1 until the day ends",
				Collections.singletonList(Weather.BEAUTIFUL), true,
				new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
		Bukkit.broadcastMessage(Weather.BEAUTIFUL.getName());
	}

	@Override
	public String getId() {
		return "revitalized";
	}

	@Override
	public boolean shouldGive(Player player) {
		return isPlayerCycleApplicable(player);
	}

	@Override
	public void sendGiveMessage(Player player) {
		player.sendMessage(Seasons.PREFIX
				+ ChatColor.GREEN + "The sun from this world is so " + ChatColor.YELLOW + "Revitalising"
				+ ChatColor.GREEN + ", it's Regenerating you!");
	}

	@Override
	public void sendRemoveMessage(Player player) {
		player.sendMessage(Seasons.PREFIX + ChatColor.YELLOW + "As the world's light leaves your skin, it's "
				+ ChatColor.GOLD + "Revitalising " + ChatColor.YELLOW + "energy leaves too...");
	}

	@Override
	public void onTrigger(World world) {
		for (Player player : world.getPlayers()) {
			giveEffect(player, false);
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {
				giveEffect(event.getPlayer(), true);
			}
		};
		runnable.runTaskLater(Seasons.getInstance().getPlugin(), 10);
	}
}
