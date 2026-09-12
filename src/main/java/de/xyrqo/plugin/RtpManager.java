package de.xyrqo.plugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Random;

public class RtpManager {

    private static final Random RANDOM = new Random();
    private static final int RANGE = 5000;
    private static final int MAX_ATTEMPTS = 300;

    public static Location findSafeLocation(World world) {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            int x = RANDOM.nextInt(RANGE * 2) - RANGE;
            int z = RANDOM.nextInt(RANGE * 2) - RANGE;

            // Chunk laden, damit getHighestBlockYAt funktioniert
            if (!world.isChunkLoaded(x >> 4, z >> 4)) {
                world.getChunkAt(x >> 4, z >> 4).load();
            }

            int y = world.getHighestBlockYAt(x, z);
            if (y <= world.getMinHeight()) continue;

            Block ground = world.getBlockAt(x, y - 1, z);
            Block feet   = world.getBlockAt(x, y, z);
            Block head   = world.getBlockAt(x, y + 1, z);

            Material groundType = ground.getType();

            // Boden muss solide sein und kein Gefahrenblock
            if (!groundType.isSolid()) continue;
            if (groundType == Material.WATER) continue;
            if (groundType == Material.LAVA) continue;
            if (groundType == Material.MAGMA_BLOCK) continue;
            if (groundType == Material.CACTUS) continue;
            if (groundType == Material.FIRE) continue;
            if (groundType == Material.SOUL_FIRE) continue;
            if (groundType == Material.CAMPFIRE) continue;
            if (groundType == Material.SWEET_BERRY_BUSH) continue;
            if (groundType == Material.POWDER_SNOW) continue;
            if (groundType == Material.SCULK_SHRIEKER) continue;
            if (groundType == Material.SCULK_CATALYST) continue;
            if (groundType == Material.SCULK_SENSOR) continue;
            if (groundType == Material.COBWEB) continue;
            if (groundType == Material.SNOW) continue;
            if (groundType == Material.ICE) continue;
            if (groundType == Material.PACKED_ICE) continue;
            if (groundType == Material.BLUE_ICE) continue;
            if (groundType == Material.FROSTED_ICE) continue;

            // Füße und Kopf müssen Luft sein
            if (!feet.getType().isAir()) continue;
            if (!head.getType().isAir()) continue;

            // Y-Bereich prüfen
            if (y < world.getMinHeight() + 1) continue;
            if (y > world.getMaxHeight() - 2) continue;

            // Sicher! Position zurückgeben
            return new Location(world, x + 0.5, y, z + 0.5);
        }
        return null;
    }
}
