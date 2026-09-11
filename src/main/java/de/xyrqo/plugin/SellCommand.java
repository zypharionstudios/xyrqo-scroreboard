package de.xyrqo.plugin;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SellCommand implements CommandExecutor {

    private final EconomyManager economy;

    public static final double DEFAULT_PRICE = 0.50;

    private static final Set<Material> BLACKLIST = EnumSet.of(
        Material.AIR, Material.CAVE_AIR, Material.VOID_AIR,
        Material.BEDROCK, Material.BARRIER,
        Material.COMMAND_BLOCK, Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK,
        Material.STRUCTURE_BLOCK, Material.STRUCTURE_VOID, Material.JIGSAW,
        Material.LIGHT, Material.DEBUG_STICK, Material.SPAWNER, Material.KNOWLEDGE_BOOK,
        Material.PLAYER_HEAD, Material.PLAYER_WALL_HEAD
    );

    public static final Map<Material, Double> PRICES = new HashMap<>();

    static {
        PRICES.put(Material.DRAGON_EGG, 5000.0);
        PRICES.put(Material.NETHER_STAR, 4000.0);
        PRICES.put(Material.BEACON, 3000.0);
        PRICES.put(Material.ELYTRA, 2500.0);
        PRICES.put(Material.NETHERITE_INGOT, 1000.0);
        PRICES.put(Material.ENCHANTED_GOLDEN_APPLE, 800.0);
        PRICES.put(Material.NETHERITE_SCRAP, 500.0);
        PRICES.put(Material.ANCIENT_DEBRIS, 400.0);
        PRICES.put(Material.NETHERITE_BLOCK, 9000.0);
        PRICES.put(Material.CONDUIT, 1200.0);
        PRICES.put(Material.TOTEM_OF_UNDYING, 1500.0);

        PRICES.put(Material.DIAMOND_BLOCK, 1350.0);
        PRICES.put(Material.DIAMOND, 150.0);
        PRICES.put(Material.DEEPSLATE_DIAMOND_ORE, 130.0);
        PRICES.put(Material.DIAMOND_ORE, 120.0);
        PRICES.put(Material.EMERALD_BLOCK, 900.0);
        PRICES.put(Material.EMERALD, 100.0);
        PRICES.put(Material.DEEPSLATE_EMERALD_ORE, 100.0);
        PRICES.put(Material.EMERALD_ORE, 90.0);
        PRICES.put(Material.SHULKER_SHELL, 200.0);
        PRICES.put(Material.HEART_OF_THE_SEA, 500.0);
        PRICES.put(Material.ENCHANTED_BOOK, 300.0);
        PRICES.put(Material.EXPERIENCE_BOTTLE, 60.0);

        PRICES.put(Material.GOLD_BLOCK, 450.0);
        PRICES.put(Material.GOLD_INGOT, 50.0);
        PRICES.put(Material.DEEPSLATE_GOLD_ORE, 45.0);
        PRICES.put(Material.GOLD_ORE, 40.0);
        PRICES.put(Material.RAW_GOLD, 35.0);
        PRICES.put(Material.NETHER_GOLD_ORE, 30.0);
        PRICES.put(Material.LAPIS_BLOCK, 135.0);
        PRICES.put(Material.LAPIS_LAZULI, 15.0);
        PRICES.put(Material.GHAST_TEAR, 50.0);
        PRICES.put(Material.BLAZE_ROD, 25.0);
        PRICES.put(Material.ENDER_PEARL, 15.0);
        PRICES.put(Material.NAUTILUS_SHELL, 75.0);
        PRICES.put(Material.PHANTOM_MEMBRANE, 30.0);

        PRICES.put(Material.IRON_BLOCK, 225.0);
        PRICES.put(Material.IRON_INGOT, 25.0);
        PRICES.put(Material.DEEPSLATE_IRON_ORE, 22.0);
        PRICES.put(Material.IRON_ORE, 20.0);
        PRICES.put(Material.RAW_IRON, 18.0);
        PRICES.put(Material.REDSTONE_BLOCK, 72.0);
        PRICES.put(Material.REDSTONE, 8.0);
        PRICES.put(Material.COPPER_INGOT, 5.0);
        PRICES.put(Material.RAW_COPPER, 4.0);
        PRICES.put(Material.COAL_BLOCK, 45.0);
        PRICES.put(Material.COAL, 5.0);
        PRICES.put(Material.QUARTZ, 8.0);
        PRICES.put(Material.AMETHYST_SHARD, 6.0);

        PRICES.put(Material.OBSIDIAN, 5.0);
        PRICES.put(Material.END_STONE, 1.5);
        PRICES.put(Material.STONE, 1.0);
        PRICES.put(Material.DEEPSLATE, 1.0);
        PRICES.put(Material.GRANITE, 0.75);
        PRICES.put(Material.DIORITE, 0.75);
        PRICES.put(Material.ANDESITE, 0.75);
        PRICES.put(Material.COBBLESTONE, 0.5);
        PRICES.put(Material.COBBLED_DEEPSLATE, 0.5);
        PRICES.put(Material.SAND, 0.3);
        PRICES.put(Material.RED_SAND, 0.3);
        PRICES.put(Material.GRAVEL, 0.3);
        PRICES.put(Material.NETHERRACK, 0.2);
        PRICES.put(Material.DIRT, 0.1);
        PRICES.put(Material.GRASS_BLOCK, 0.2);
        PRICES.put(Material.SOUL_SAND, 0.8);
        PRICES.put(Material.SOUL_SOIL, 0.8);
        PRICES.put(Material.GLOWSTONE, 3.0);

        PRICES.put(Material.CRIMSON_STEM, 3.5);
        PRICES.put(Material.WARPED_STEM, 3.5);
        PRICES.put(Material.MANGROVE_LOG, 3.0);
        PRICES.put(Material.CHERRY_LOG, 3.0);
        PRICES.put(Material.JUNGLE_LOG, 2.5);
        PRICES.put(Material.DARK_OAK_LOG, 2.5);
        PRICES.put(Material.OAK_LOG, 2.0);
        PRICES.put(Material.SPRUCE_LOG, 2.0);
        PRICES.put(Material.BIRCH_LOG, 2.0);
        PRICES.put(Material.ACACIA_LOG, 2.0);

        PRICES.put(Material.COOKED_BEEF, 8.0);
        PRICES.put(Material.COOKED_PORKCHOP, 8.0);
        PRICES.put(Material.COOKED_CHICKEN, 6.0);
        PRICES.put(Material.BREAD, 5.0);
        PRICES.put(Material.GOLDEN_APPLE, 40.0);
        PRICES.put(Material.APPLE, 3.0);
        PRICES.put(Material.PUMPKIN, 3.0);
        PRICES.put(Material.WHEAT, 3.0);
        PRICES.put(Material.BEETROOT, 2.5);
        PRICES.put(Material.CARROT, 2.0);
        PRICES.put(Material.POTATO, 2.0);
        PRICES.put(Material.SUGAR_CANE, 1.5);
        PRICES.put(Material.MELON_SLICE, 1.0);
        PRICES.put(Material.COCOA_BEANS, 1.5);
        PRICES.put(Material.SWEET_BERRIES, 1.0);

        PRICES.put(Material.GUNPOWDER, 5.0);
        PRICES.put(Material.SLIME_BALL, 4.0);
        PRICES.put(Material.SPIDER_EYE, 3.0);
        PRICES.put(Material.LEATHER, 3.0);
        PRICES.put(Material.BONE, 2.0);
        PRICES.put(Material.STRING, 2.0);
        PRICES.put(Material.FEATHER, 1.0);
        PRICES.put(Material.EGG, 1.0);
        PRICES.put(Material.ROTTEN_FLESH, 0.5);
        PRICES.put(Material.INK_SAC, 2.0);
        PRICES.put(Material.BLAZE_POWDER, 12.0);
    }

    public SellCommand(EconomyManager economy) {
        this.economy = economy;
    }

    public static double getPrice(Material mat) {
        if (mat == null || BLACKLIST.contains(mat)) return 0;
        if (PRICES.containsKey(mat)) return PRICES.get(mat);
        return DEFAULT_PRICE;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cNur Spieler können das nutzen.");
            return true;
        }
        Player p = (Player) sender;
        SellGUI gui = new SellGUI(p.getUniqueId());
        p.openInventory(gui.getInventory());
        p.sendMessage("§5§l✦ §7Lege Items in die Truhe und §dschließe sie§7, um zu verkaufen.");
        return true;
    }
}
