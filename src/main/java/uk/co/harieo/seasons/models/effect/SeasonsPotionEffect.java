package uk.co.harieo.seasons.models.effect;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import uk.co.harieo.seasons.events.DayEndEvent;
import uk.co.harieo.seasons.models.Weather;

public abstract class SeasonsPotionEffect extends Effect {

	private PotionEffect effect;

	public SeasonsPotionEffect(String name, List<Weather> weathers, boolean good, PotionEffect effect) {
		super(name, weathers, good);
		this.effect = effect;
	}

	public abstract boolean shouldGive(Player player);

	protected void giveEffect(Player player, boolean sendMessage) {
		if (shouldGive(player) && !player.hasPotionEffect(effect.getType())) {
			player.addPotionEffect(effect);
			if (sendMessage) {
				sendGiveMessage(player);
			}
		}
	}

	protected void removeEffect(Player player, boolean sendMessage) {
		player.removePotionEffect(effect.getType());
		if (sendMessage) {
			sendRemoveMessage(player);
		}
	}

	public abstract void sendGiveMessage(Player player);

	public abstract void sendRemoveMessage(Player player);

	@Override
	public void onTrigger(World world) {
		for (Player player : world.getPlayers()) {
			giveEffect(player, true);
		}
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		if (isPlayerCycleApplicable(player)) {
			// Some servers may have plugins to keep inventories separate to the GameRule
			giveEffect(event.getPlayer(), false);
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (isPlayerCycleApplicable(player)) {
			giveEffect(player, true);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (isPlayerCycleApplicable(player)) {
			removeEffect(player, false);
		}
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		if (isPlayerCycleApplicable(player)) {
			if (shouldGive(player)) {
				giveEffect(player, true);
				return;
			}
		}

		removeEffect(player, true);
	}

	@EventHandler
	public void onDayEnd(DayEndEvent event) {
		if (isWeatherApplicable(event.getChangeFrom())) {
			World world = event.getCycle().getWorld();
			for (Player player : world.getPlayers()) {
				removeEffect(player, true);
			}
		}
	}

}
