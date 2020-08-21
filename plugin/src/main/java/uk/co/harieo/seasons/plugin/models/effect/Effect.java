package uk.co.harieo.seasons.plugin.models.effect;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import uk.co.harieo.seasons.plugin.Seasons;
import uk.co.harieo.seasons.plugin.configuration.SeasonsConfig;
import uk.co.harieo.seasons.plugin.configuration.SeasonsLanguageConfiguration;
import uk.co.harieo.seasons.plugin.events.SeasonsWeatherChangeEvent;
import uk.co.harieo.seasons.plugin.models.Cycle;
import uk.co.harieo.seasons.plugin.models.Weather;

public abstract class Effect implements Listener {

	private static final List<String> cache = new ArrayList<>(); // Prevents multiple registrations of 1 listener

	private final String name;
	private final String description;
	private final List<Weather> weathers;
	private final boolean isGood;
	private boolean ignoreRoof = true;

	public Effect(String name, String description, List<Weather> weathers, boolean good) {
		this.name = name;
		this.description = description;
		this.weathers = weathers;
		this.isGood = good;

		Seasons seasons = Seasons.getInstance();
		if (!cache.contains(name) && seasons.getSeasonsConfig().hasEnabledEffects()) {
			Bukkit.getPluginManager().registerEvents(this, seasons.getPlugin());
			cache.add(name);
		}
	}

	/**
	 * @return a system friendly id to represent this effect
	 */
	public abstract String getId();

	/**
	 * Retrieves the message from the language configuration to be sent when this effect is triggered then sends it to
	 * the applicable world. This will do nothing if no trigger message is found.
	 *
	 * @param world to send the message to, if found
	 */
	private void sendTriggerMessage(World world) {
		String triggerMessage = getMessageOrDefault("on-trigger", "An language configuration error occurred here...");
		if (triggerMessage != null && isEnabled()) {
			for (Player player : world.getPlayers()) {
				player.sendMessage(Seasons.PREFIX + triggerMessage);
			}
		}
	}

	/**
	 * Sends the on-give message from the language configuration, or the default
	 *
	 * @param player to send the message to
	 * @param orElse the default message
	 */
	protected void sendGiveMessage(Player player, String orElse) {
		player.sendMessage(Seasons.PREFIX + getMessageOrDefault("on-give", orElse));
	}

	/**
	 * Sends the on-remove message from the language configuration, or the default
	 *
	 * @param player to send the message to
	 * @param orElse the default message
	 */
	protected void sendRemoveMessage(Player player, String orElse) {
		player.sendMessage(Seasons.PREFIX + getMessageOrDefault("on-remove", orElse));
	}

	/**
	 * Get a configurable message from the configuration system for this effect, or return a default if the message
	 * hasn't been configured yet
	 *
	 * @param messageType string type to get from config
	 * @param orElse the default message
	 * @return the configured message or the default if no configured method exists
	 */
	private String getMessageOrDefault(String messageType, String orElse) {
		SeasonsLanguageConfiguration languageConfiguration = Seasons.getInstance().getLanguageConfig();
		return languageConfiguration.getStringOrDefault("effects." + messageType + "." + getId(), orElse);
	}

	/**
	 * @return the name of this effect, shown to the player
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return a description of this effect, shown to the player
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the list of {@link Weather} to which this effect can be used upon
	 */
	public List<Weather> getWeathers() {
		return weathers;
	}

	/**
	 * @return whether this is a primarily positive effect for the player
	 */
	public boolean isGood() {
		return isGood;
	}

	/**
	 * Checks whether the {@link Weather} is in the list of applicable Weathers attached to this effect
	 *
	 * @param weather to be checked
	 * @return whether this {@link Weather} can be used with this effect
	 */
	public boolean isWeatherApplicable(Weather weather) {
		return getWeathers().contains(weather);
	}

	/**
	 * Show whether this effect ignores whether a player is under a roof. The default value is true.
	 *
	 * @return whether this effect ignores any roof over a player
	 */
	public boolean isIgnoringRoof() {
		return ignoreRoof;
	}

	/**
	 * Sets whether this effect ignores any roof over a player
	 *
	 * @param ignoreRoof to set the value to
	 */
	protected void setIgnoreRoof(boolean ignoreRoof) {
		this.ignoreRoof = ignoreRoof;
	}

	/**
	 * Retrieves the {@link Cycle} and, by extension, {@link Weather} that the {@link Player} is currently affected by
	 * and checks whether the stated {@link Player} is affected by this effect
	 *
	 * @param player to be checked
	 * @return whether the {@link Player} is affected by this effect
	 */
	protected boolean isPlayerCycleApplicable(Player player) {
		Cycle cycle = Seasons.getInstance().getWorldCycle(player.getWorld());
		return cycle != null && isWeatherApplicable(cycle.getWeather()) && checkRoof(player) && isEnabled();
	}

	@EventHandler
	public void onWeatherChange(SeasonsWeatherChangeEvent event) {
		if (isWeatherApplicable(event.getChangedTo())) {
			World world = event.getCycle().getWorld();
			onTrigger(world);
			sendTriggerMessage(world);
		}
	}

	/**
	 * An abstract method to handle when this effect is activated upon a {@link World}
	 *
	 * @param world that this effect is being triggered on
	 */
	public abstract void onTrigger(World world);

	/**
	 * @return whether this effect has been manually disabled via config inverted
	 */
	private boolean isEnabled() {
		for (String disabledEffectName : Seasons.getInstance().getSeasonsConfig().getDisabledEffects()) {
			if (disabledEffectName.equalsIgnoreCase(getName())) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks whether a player is under a roof and verifies whether the effect ignores this.
	 *
	 * @param player to check the roof for
	 * @return true if there is a roof above the player which will interrupt this effect
	 */
	private boolean checkRoof(Player player) {
		return ignoreRoof || !isUnderRoof(player);
	}

	/**
	 * Checks whether a player has a block above them, within 20 blocks, which is considered a "roof" by this system.
	 *
	 * @param player to check the location of
	 * @return whether the player is underneath a roof
	 */
	private static boolean isUnderRoof(Player player) {
		Location playerLocation = player.getLocation().clone();
		SeasonsConfig config = Seasons.getInstance().getSeasonsConfig();
		for (int i = 1; i < config.getRoofHeight(); i++) { // Scans 20 blocks upwards of the player
			Block block = playerLocation.add(0, 1, 0).getBlock();
			if (!block.isEmpty()) { // There is a block within 20 blocks
				return true;
			}
		}

		return false; // There's nothing within 20 blocks
	}
}
