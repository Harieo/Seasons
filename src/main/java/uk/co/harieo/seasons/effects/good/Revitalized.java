package uk.co.harieo.seasons.effects.good;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import uk.co.harieo.seasons.Seasons;
import uk.co.harieo.seasons.models.Weather;
import uk.co.harieo.seasons.models.effect.SeasonsPotionEffect;

public class Revitalized extends SeasonsPotionEffect {

	public Revitalized() {
		super("Revitalized", Collections.singletonList(Weather.BEAUTIFUL), true,
				new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
		Bukkit.broadcastMessage(Weather.BEAUTIFUL.getName());
	}

	@Override
	public boolean shouldGive(Player player) {
		return true;
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
			player.sendMessage(Seasons.PREFIX
					+ ChatColor.GREEN + "As the sun rises, you feel it's Regenerating energy "
					+ ChatColor.YELLOW + "Revitalise " + ChatColor.GREEN + "you!");
		}
	}
}
