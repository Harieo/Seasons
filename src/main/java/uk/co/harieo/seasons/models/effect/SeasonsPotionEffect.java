package uk.co.harieo.seasons.models.effect;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import uk.co.harieo.seasons.events.DayEndEvent;
import uk.co.harieo.seasons.events.SeasonsWeatherChangeEvent;
import uk.co.harieo.seasons.models.Weather;

public abstract class SeasonsPotionEffect extends Effect {

	// Stores the UUID of players that have effects that need to be removed onDisable in the executor //
	public static final Map<UUID, PotionEffectType> PENDING = new HashMap<>();

	private PotionEffect effect;

	public SeasonsPotionEffect(String name, String description, List<Weather> weathers, boolean good,
			PotionEffect effect) {
		super(name, description, weathers, good);
		this.effect = effect;
	}

	/**
	 * An abstract method containing the conditions that calculate whether the {@link PotionEffect} should be given to
	 * the {@link Player} stated
	 *
	 * @param player that is being affected
	 * @return whether the {@link PotionEffect} should be given to the {@link Player}
	 */
	public abstract boolean shouldGive(Player player);

	/**
	 * Gives the registered {@link PotionEffect} after checking that all conditions are met successfully
	 *
	 * @param player to give the effect to
	 * @param sendMessage whether or not to call {@link #sendGiveMessage(Player)}
	 */
	protected void giveEffect(Player player, boolean sendMessage) {
		if (shouldGive(player) && !player.hasPotionEffect(effect.getType())) {
			player.addPotionEffect(effect);
			PENDING.put(player.getUniqueId(), effect.getType()); // Indicate that the effect was given
			if (sendMessage) {
				sendGiveMessage(player);
			}
		}
	}

	/**
	 * Removes the registered {@link PotionEffect} regardless of any conditions
	 *
	 * @param player to remove the effect from
	 * @param sendMessage whether or not to call {@link #sendRemoveMessage(Player)}
	 * @param force whether to ignore the player cycle and remove regardless of conditions
	 */
	protected void removeEffect(Player player, boolean sendMessage, boolean force) {
		if (isPlayerCycleApplicable(player) || force) {
			player.removePotionEffect(effect.getType());
			PENDING.remove(player.getUniqueId()); // Indicate that the effect was removed
		}

		if (sendMessage) {
			sendRemoveMessage(player);
		}
	}

	/**
	 * Removes effects on the assumption that this is not a forceful action Calls {@link #removeEffect(Player, boolean,
	 * boolean)} with force as false
	 *
	 * @param player to remove the effect from
	 * @param sendMessage whether or not to call {@link #sendRemoveMessage(Player)}
	 */
	protected void removeEffect(Player player, boolean sendMessage) {
		removeEffect(player, sendMessage, false);
	}

	/**
	 * Sends a message to the {@link Player} on the assumption that {@link #giveEffect(Player, boolean)} was called
	 * before this method
	 *
	 * @param player to send the message to
	 */
	public abstract void sendGiveMessage(Player player);

	/**
	 * Sends a message to the {@link Player} on the assumption that {@link #removeEffect(Player, boolean, boolean)} was
	 * called before this method
	 *
	 * @param player to send the message to
	 */
	public abstract void sendRemoveMessage(Player player);

	public PotionEffect getEffect() {
		return effect;
	}

	// The events that are global to all classes that extend this class for handling effects //

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
			removeEffect(player, false, false);
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

		removeEffect(player, true, true);
	}

	@EventHandler
	public void onDayEnd(DayEndEvent event) {
		if (isWeatherApplicable(event.getChangeFrom())) {
			World world = event.getCycle().getWorld();
			for (Player player : world.getPlayers()) {
				removeEffect(player, true, false);
			}
		}
	}

	@EventHandler
	public void onWeatherChange(SeasonsWeatherChangeEvent event) {
		if (isWeatherApplicable(event.getChangeFrom()) && !isWeatherApplicable(event.getChangedTo())) {
			World world = event.getCycle().getWorld();
			for (Player player : world.getPlayers()) {
				removeEffect(player, true, true);
			}
		}
	}

}
