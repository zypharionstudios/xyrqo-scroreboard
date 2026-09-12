package de.xyrqo.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;
import java.util.function.Consumer;

public class RtpManager {

    private static final Random RANDOM = new Random();
    private static final int RANGE = 3000;
    private static final int MAX_ATTEMPTS = 15;

    public static void findSafeLocationAsync(JavaPlugin plugin, Player player, Consumer<Location> callback) {
        tryNext(plugin, player, callback, 0);
    }

    private static void tryNext(JavaPlugin plugin, Player player, Consumer<Location> callback, int attempt) {
        if (!player.isOnline()) return;
        if (attempt >= MAX_ATTEMPTS) {
            callback.accept(null);
            return;
        }

        World world = player.getWorld();
        int x = RANDOM.nextInt(RANGE * 2) - RANGE;
        int z = RANDOM.nextInt(RANGE * 2) - RANGE;

        world.getChunkAtAsync(x >> 4, z >> 4).thenAccept(chunk -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (!player.isOnline()) return;

                int y = world.getHighestBlockYAt(x, z);
                if (y <= world.getMinHeight()) {
                    tryNext(plugin, player, callback, attempt + 1);
                    return;
                }

                Block ground = world.getBlockAt(x, y - 1, z);
                Block feet   = world.getBlockAt(x, y, z);
                Block head   = world.getBlockAt(x, y + 1, z);

                Material gt = ground.getType();

                boolean safe = gt.isSolid()
                        && gt != Material.WATER
                        && gt != Material.LAVA
                        && gt != Material.MAGMA_BLOCK
                        && gt != Material.CACTUS
                        && gt != Material.FIRE
                        && gt != Material.SOUL_FIRE
                        && gt != Material.CAMPFIRE
                        && gt != Material.SOUL_CAMPFIRE
                        && gt != Material.SWEET_BERRY_BUSH
                        && gt != Material.POWDER_SNOW
                        && gt != Material.COBWEB
                        && gt != Material.SNOW
                        && gt != Material.ICE
                        && gt != Material.PACKED_ICE
                        && gt != Material.BLUE_ICE
                        && gt != Material.FROSTED_ICE
                        && gt != Material.SCULK_SHRIEKER
                        && gt != Material.SCULK_CATALYST
                        && feet.getType().isAir()
                        && head.getType().isAir();

                if (!safe) {
                    tryNext(plugin, player, callback, attempt + 1);
                    return;
                }

                callback.accept(new Location(world, x + 0.5, y, z + 0.5));
            });
        }).exceptionally(ex -> {
            Bukkit.getScheduler().runTask(plugin, () -> tryNext(plugin, player, callback, attempt + 1));
            return null;
        });
    }
}
