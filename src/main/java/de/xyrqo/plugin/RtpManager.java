package de.xyrqo.plugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Random;

public class RtpManager {

    private static final Random RANDOM = new Random();
    private static final int RANGE = 5000;      // Radius (x/z je -5000 bis +5000)
    private static final int MAX_ATTEMPTS = 100;

    /**
     * Sucht eine sichere Position in der Overworld.
     * Gibt null zurück, wenn keine gefunden wurde.
     */
    public static Location findSafeLocation(World world) {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            int x = RANDOM.nextInt(RANGE * 2) - RANGE;
            int z = RANDOM.nextInt(RANGE * 2) - RANGE;

            // Höchster Block an dieser Stelle (Y-Koordinate der Oberfläche)
            int y = world.getHighestBlockYAt(x, z);

            // Blöcke holen
            Block ground = world.getBlockAt(x, y - 1, z);
            Block feet   = world.getBlockAt(x, y, z);
            Block head   = world.getBlockAt(x, y + 1, z);

            // Boden-Check: solide, kein Wasser/Lava/Gefahr
            Material groundType = ground.getType();
            if (!groundType.isSolid()) continue;
            if (groundType == Material.WATER || groundType == Material.LAVA) continue;
            if (groundType == Material.MAGMA_BLOCK) continue;
            if (groundType == Material.CACTUS) continue;
            if (groundType == Material.FIRE || groundType == Material.SOUL_FIRE) continue;
            if (groundType == Material.CAMPFIRE) continue;
            if (groundType == Material.SWEET_BERRY_BUSH) continue;
            if (groundType == Material.POWDER_SNOW) continue;
            if (groundType == Material.SCULK_SHRIEKER) continue;

            // Füße und Kopf müssen Luft sein
            if (!feet.getType().isAir()) continue;
            if (!head.getType().isAir()) continue;

            // Y-Bereich prüfen (nicht im Void, nicht über Weltgrenze)
            if (y < world.getMinHeight() + 1) continue;
            if (y > world.getMaxHeight() - 2) continue;

            // Position in Blockmitte
            Location loc = new Location(world, x + 0.5, y, z + 0.5);
            return loc;
        }
        return null;
    }
}
