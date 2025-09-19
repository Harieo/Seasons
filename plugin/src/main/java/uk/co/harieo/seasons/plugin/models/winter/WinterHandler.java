package uk.co.harieo.seasons.plugin.models.winter;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import uk.co.harieo.seasons.plugin.Seasons;

import java.util.*;

public class WinterHandler implements Runnable{

    private BukkitTask task;
    private static final WinterHandler handler = new WinterHandler();
    public static final Set<Chunk> scannedChunks = new HashSet<>();
    private static final List<Location> waterToFreeze = new ArrayList<>();
    private static final List<Location> snowable = new ArrayList<>();
    private static boolean isWinter = false;

    private Random random = new Random();

    public static boolean isWinter(){
        return isWinter;
    }

    public static boolean isAllowedWinter(String world){

        if(world.contains("end") || world.contains("nether") || Seasons.getInstance().getSeasonsConfig().getDisabledWorlds().contains(world)){
            return false;
        }else{
            return true;
        }
    }

    @Override
    public void run() {
        int updates = 200;
        int queueSize = waterToFreeze.size();
        Bukkit.getLogger().info("Queue size for water to be frozen " + queueSize);
        Bukkit.getLogger().info("Snowable Locations number " + snowable.size());

        List<Location> snowToRemove = new ArrayList<>();
        List<Location> iceToRemove = new ArrayList<>();

        for(int i = 0;i < updates && !waterToFreeze.isEmpty();i++){
            Block iced = waterToFreeze.get(random.nextInt(waterToFreeze.size())).getBlock();
            iced.setType(Material.FROSTED_ICE, false);
            iceToRemove.add(iced.getLocation());
            //waterToFreeze.remove(iced.getLocation());
        }
        for(int i = 0; i < updates && !snowable.isEmpty();i++){
            Block snow = snowable.get(random.nextInt(snowable.size())).getBlock();
            Block below = snow.getRelative(BlockFace.DOWN);
            if(snow.getType().isAir() &&
                    below.getType().isSolid() &&
                    !below.getType().toString().toLowerCase().contains("leaves")) {
                snow.setType(Material.SNOW);
            }
            snowToRemove.add(snow.getLocation());
            //snowable.remove(snow.getLocation());
        }

        for(Player player : Bukkit.getOnlinePlayers()){
            Location loc = player.getLocation().add(
                    Math.random() * 6 - 3, // X offset
                    2 + Math.random(),     // Y offset
                    Math.random() * 6 - 3  // Z offset
            );
            BlockData snowData = Material.SNOW_BLOCK.createBlockData();
            player.getWorld().spawnParticle(
                    Particle.FALLING_DUST,
                    loc,
                    20, // count
                    5, 3, 5, // spread
                    0.1, // speed
                    snowData
            );
        }
        waterToFreeze.removeAll(iceToRemove);
        snowable.removeAll(snowToRemove);
    }

    public static void addChunk(Chunk chunk){
        if(scannedChunks.add(chunk)) {
            scanChunk(chunk);
        }
    }

    private static boolean isBannedBiome(Biome biome){
        String biomeName = biome.name().toLowerCase();
        return biomeName.contains("ocean") || biomeName.contains("river");
    }

    private static void scanChunk(Chunk chunk) {
        World world = chunk.getWorld();
        int chunkX = chunk.getX() * 16;
        int chunkZ = chunk.getZ() * 16;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkX + x;
                int worldZ = chunkZ + z;

                // Surface snow scan
                Block highest = world.getHighestBlockAt(worldX, worldZ);
                Block above = highest.getRelative(BlockFace.UP);

                if (!isBannedBiome(highest.getBiome()) &&
                        highest.getType().isSolid() &&
                        above.getType().isAir()) {
                    snowable.add(above.getLocation());
                }

                // Water freeze scan
                for (int y = 50; y < 100; y++) {
                    Block potential = world.getBlockAt(worldX, y, worldZ);
                    Block abovePotential = world.getBlockAt(worldX, y + 1, worldZ);

                    if (potential.getLightFromSky() > 0 && potential.getType().isSolid()) {
                        snowable.add(abovePotential.getLocation());
                    }

                    if (potential.getType() == Material.WATER &&
                            abovePotential.getType().isAir() &&
                            !isBannedBiome(potential.getBiome())) {
                        waterToFreeze.add(potential.getLocation());
                    }
                }
            }
        }
    }


    /*private static void scanChunk(Chunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                *//*Block highest = chunk.getWorld().getHighestBlockAt(x,z);
                Block above = highest.getRelative(BlockFace.UP);
                if(highest.getType().isSolid() && above.getType().isAir()){
                    snowable.add(above.getLocation());
                }*//*
                for(int y = 50; y <  100; y++) {
                    Block potential = chunk.getBlock(x, y, z);
                    Block abovePotential = chunk.getBlock(x, y + 1, z);
                    if(potential.getLightFromSky() > 0 && potential.getType().isSolid()){
                        snowable.add(abovePotential.getLocation());
                    }
                    if (potential.getType() == Material.WATER && abovePotential.getType().isAir()) {
                        if(!isBannedBiome(potential.getBiome())) {
                            waterToFreeze.add(potential.getLocation());
                        }
                    }
                }
            }
        }
    }*/

    public static void start() {
        if (handler.task == null) {
            Bukkit.getLogger().info("Winter has been started");
            isWinter = true;
            handler.task = Bukkit.getScheduler().runTaskTimer(Seasons.getInstance().getPlugin(), handler, 0, 20);
        }
    }

    public static void stop() {
        if (handler.task != null) {
            isWinter = false;
            handler.task.cancel();
            handler.task = null;
        }
    }
}
