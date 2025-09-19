package uk.co.harieo.seasons.plugin;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import uk.co.harieo.seasons.plugin.events.DayEndEvent;
import uk.co.harieo.seasons.plugin.events.SeasonChangeEvent;
import uk.co.harieo.seasons.plugin.events.SeasonsWeatherChangeEvent;
import uk.co.harieo.seasons.plugin.models.Season;
import uk.co.harieo.seasons.plugin.models.Weather;
import uk.co.harieo.seasons.plugin.models.winter.ThawHandler;
import uk.co.harieo.seasons.plugin.models.winter.WinterHandler;

public class SeasonalListener implements Listener {

	@EventHandler
	public void onSeasonChange(SeasonChangeEvent event) {
		Season season = event.getChangedTo();
		if(season == Season.WINTER){
			ThawHandler.stop();
			WinterHandler.start();
			for(Chunk loadedChunk : event.getCycle().getWorld().getLoadedChunks()){
				WinterHandler.addChunk(loadedChunk);
			}
		}else{
			WinterHandler.stop();
			ThawHandler.start();
			for(Chunk loadedChunk : event.getCycle().getWorld().getLoadedChunks()){
				ThawHandler.addChunk(loadedChunk);
			}
		}
		World world = event.getCycle().getWorld();
		season.getMessage().ifPresent(message -> {
			for (Player player : world.getPlayers()) {
				player.sendMessage(Seasons.PREFIX + season.getColor() + message);
			}
		});
	}

	@EventHandler
	public void onWeatherChange(SeasonsWeatherChangeEvent event) {
		Weather weather = event.getChangedTo();
		World world = event.getCycle().getWorld();

		for (Player player : world.getPlayers()) {
			if (weather.isCatastrophic()) {
				Seasons.getInstance().getLanguageConfig()
						.getStringOrDefault("misc.catastrophic-alert",
								ChatColor.RED + ChatColor.BOLD.toString()
										+ "CATASTROPHIC WEATHER ALERT - Take care to plan your day")
						.ifPresent(message -> player.sendMessage(Seasons.PREFIX + message));
			}

			weather.getMessage().ifPresent(message -> player.sendMessage(Seasons.PREFIX + message));
		}

		boolean storm = weather.isStorm();
		world.setStorm(storm);
		if (storm) {
			world.setThundering(weather.isThundering());
		}
	}

	@EventHandler
	public void onDayEnd(DayEndEvent event) {
		if (event.isNatural()) {
			World world = event.getCycle().getWorld();
			Weather.NIGHT.getMessage().ifPresent(message -> {
				for (Player player : world.getPlayers()) {
					player.sendMessage(Seasons.PREFIX + message);
				}
			});
		}
	}

	@EventHandler
	public void onWorldLoad(WorldLoadEvent event) {
		World world = event.getWorld();
		Seasons seasons = Seasons.getInstance();
		if (seasons.getWorldCycle(world) == null) {
			seasons.getWorldHandler().addWorld(world); // Method will check environment so we don't need to
		}
	}

	/*
	We dont activate the winter and thaw in the nether or end OR disabled worlds
	 */
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event){
		Chunk chunk = event.getChunk();
		World world = chunk.getWorld();
		String worldName = world.getName().toLowerCase();
		if(WinterHandler.isAllowedWinter(worldName)) {
			if (WinterHandler.isWinter()) {
				WinterHandler.addChunk(chunk);
			} else {
				ThawHandler.addChunk(chunk);
			}
		}
	}

	/*
	We dont want it to rain during winter
	 */
	@EventHandler
	public void onMinecraftWeatherChange(WeatherChangeEvent event){
		if(WinterHandler.isWinter() && event.toWeatherState()){
			event.setCancelled(true);
		}
	}

}
