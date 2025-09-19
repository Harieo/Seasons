package uk.co.harieo.seasons.plugin.models.winter;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;
import uk.co.harieo.seasons.plugin.Seasons;

import java.util.*;

public class ThawHandler implements Runnable{

    private BukkitTask task;
    private static final ThawHandler handler = new ThawHandler();

    private static final Set<Chunk> scannedChunks = new HashSet<>();
    public static List<Location> iceToBeScanned = new ArrayList<>();
    public static List<Location> snowToBeScanned = new ArrayList<>();
    private static final Random random = new Random();

    private static final List<Biome> coldBiomes = new ArrayList<>();
    private static final List<Location> iceToRemove = new ArrayList<>();
    private static final List<Location> snowToRemove = new ArrayList<>();

    @Override
    public void run() {
        int updates = 200;
        for(int i = 0; i < updates && !iceToBeScanned.isEmpty(); i++){
            Location location = iceToBeScanned.get(random.nextInt(iceToBeScanned.size()));
            location.getBlock().setType(Material.WATER);
            scannedChunks.add(location.getChunk());
            iceToRemove.add(location);
        }

        for(int i = 0; i < updates && !snowToBeScanned.isEmpty(); i++){
            Location location = snowToBeScanned.get(random.nextInt(snowToBeScanned.size()));
            location.getBlock().setType(Material.AIR);
            scannedChunks.add(location.getChunk());
            snowToRemove.add(location);
        }
        //Lets remove from the list AFTER we have iterated
        iceToBeScanned.removeAll(iceToRemove);
        snowToBeScanned.removeAll(snowToRemove);

    }

    public static void addChunk(Chunk chunk){
        if(scannedChunks.add(chunk)){
            scanForThaw(chunk);
        }
    }

    public static void createBiomeList(){
        for(Biome biome : Biome.values()){
            if(biome.name().toLowerCase().contains("snow") ||
                    biome.name().toLowerCase().contains("ice") ||
                    biome.name().toLowerCase().contains("jagged") ||
                    biome.name().toLowerCase().contains("frozen")){
                coldBiomes.add(biome);
            }
        }
    }

    public static void scanForThaw(Chunk chunk){
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for(int y = 50; y <  100; y++) {
                    Block potential = chunk.getBlock(x, y, z);
                    Block abovePotential = chunk.getBlock(x, y + 1, z);
                    if (potential.getType() == Material.FROSTED_ICE && abovePotential.getType().isAir()) {
                        iceToBeScanned.add(potential.getLocation());
                    }
                    if(potential.getType() == Material.SNOW && !coldBiomes.contains(potential.getBiome())){
                        snowToBeScanned.add(potential.getLocation());
                    }
                }
            }
        }
    }

    public static void start() {
        if (handler.task == null) {
            handler.task = Bukkit.getScheduler().runTaskTimer(Seasons.getInstance().getPlugin(), handler, 0, 20 * 2);
        }
    }

    public static void stop() {
        if (handler.task != null) {
            handler.task.cancel();
            handler.task = null;
        }
    }
}
