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
        if(WinterHandler.isWinter()){
            Bukkit.getLogger().info("ThawHandler detected it was winter so stopping thaw");
            stop();
            WinterHandler.start();
        }
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
        //Clear the temp lists
        iceToRemove.clear();
        snowToRemove.clear();

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

    public static void scanForThaw(Chunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 50; y < 100; y++) {
                    Block block = chunk.getBlock(x, y, z);
                    Block above = chunk.getBlock(x, y + 1, z);

                    if (isThawableIce(block, above)) {
                        iceToBeScanned.add(block.getLocation());
                    }

                    if (isThawableSnow(block)) {
                        snowToBeScanned.add(block.getLocation());
                    }
                }
            }
        }
    }

    private static boolean isThawableIce(Block ice, Block above) {
        return ice.getType() == Material.FROSTED_ICE &&
                above.getType().isAir();
    }

    private static boolean isThawableSnow(Block snow) {
        Biome biome = snow.getWorld().getBiome(snow.getX(), snow.getY(), snow.getZ());
        return snow.getType() == Material.SNOW &&
                !coldBiomes.contains(biome);
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
