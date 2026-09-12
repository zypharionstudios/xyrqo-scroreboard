package de.xyrqo.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NpcManager {

    private final Map<UUID, String> npcs = new HashMap<>();
    private final File file;
    private final FileConfiguration config;
    private final JavaPlugin plugin;

    public NpcManager(JavaPlugin plugin) {
        this.plugin = plugin;
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        this.file = new File(plugin.getDataFolder(), "npcs.yml");
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
        loadAll();
    }

    public void loadAll() {
        if (config.getConfigurationSection("npcs") == null) return;
        for (String key : config.getConfigurationSection("npcs").getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                String name = config.getString("npcs." + key + ".name", "§e§lRTP");
                String worldName = config.getString("npcs." + key + ".world");
                double x = config.getDouble("npcs." + key + ".x");
                double y = config.getDouble("npcs." + key + ".y");
                double z = config.getDouble("npcs." + key + ".z");

                Entity existing = Bukkit.getEntity(uuid);
                if (existing instanceof Villager) {
                    npcs.put(uuid, name);
                    prepareNpc((Villager) existing, name);
                } else if (worldName != null) {
                    World w = Bukkit.getWorld(worldName);
                    if (w != null) {
                        // Chunk laden, dann Entity suchen
                        w.getChunkAt((int) x >> 4, (int) z >> 4).load();
                        Entity loaded = Bukkit.getEntity(uuid);
                        if (loaded instanceof Villager) {
                            npcs.put(uuid, name);
                            prepareNpc((Villager) loaded, name);
                        } else {
                            // NPC existiert nicht mehr → neu spawnen
                            Villager npc = (Villager) w.spawnEntity(new Location(w, x, y, z), EntityType.VILLAGER);
                            prepareNpc(npc, name);
                            npcs.put(npc.getUniqueId(), name);
                            // Alte UUID-Einträge in Config aktualisieren
                            config.set("npcs." + key, null);
                            config.set("npcs." + npc.getUniqueId() + ".name", name);
                            config.set("npcs." + npc.getUniqueId() + ".world", worldName);
                            config.set("npcs." + npc.getUniqueId() + ".x", x);
                            config.set("npcs." + npc.getUniqueId() + ".y", y);
                            config.set("npcs." + npc.getUniqueId() + ".z", z);
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        save();
    }

    public Villager spawnNpc(Location loc, String name) {
        World world = loc.getWorld();
        if (world == null) return null;

        // Chunk laden, damit die Entity sicher existiert
        world.getChunkAt(loc.getBlockX() >> 4, loc.getBlockZ() >> 4).load();

        Villager npc = (Villager) world.spawnEntity(loc, EntityType.VILLAGER);
        prepareNpc(npc, name);

        npcs.put(npc.getUniqueId(), name);
        config.set("npcs." + npc.getUniqueId() + ".name", name);
        config.set("npcs." + npc.getUniqueId() + ".world", world.getName());
        config.set("npcs." + npc.getUniqueId() + ".x", loc.getX());
        config.set("npcs." + npc.getUniqueId() + ".y", loc.getY());
        config.set("npcs." + npc.getUniqueId() + ".z", loc.getZ());
        save();
        return npc;
    }

    private void prepareNpc(Villager npc, String name) {
        npc.setCustomName(name);
        npc.setCustomNameVisible(true);
        npc.setAI(false);
        npc.setGravity(false);
        npc.setInvulnerable(true);
        npc.setSilent(true);
        npc.setCollidable(false);
        npc.setPersistent(true);
        npc.setRemoveWhenFarAway(false);
        try {
            npc.setProfession(Villager.Profession.NONE);
            npc.setVillagerType(Villager.Type.PLAINS);
            npc.setVillagerLevel(1);
        } catch (Exception ignored) {}
    }

    public boolean isNpc(Entity e) {
        return e != null && npcs.containsKey(e.getUniqueId());
    }

    public String getNpcName(Entity e) {
        return npcs.get(e.getUniqueId());
    }

    public void removeAll() {
        // Alle UUIDs kopieren, um ConcurrentModificationException zu vermeiden
        ArrayList<UUID> uuids = new ArrayList<>(npcs.keySet());

        for (UUID uuid : uuids) {
            Entity e = Bukkit.getEntity(uuid);

            // Falls nicht gefunden: Chunk aus Config laden
            if (e == null) {
                String worldName = config.getString("npcs." + uuid + ".world");
                if (worldName != null) {
                    World w = Bukkit.getWorld(worldName);
                    if (w != null) {
                        int x = (int) config.getDouble("npcs." + uuid + ".x");
                        int z = (int) config.getDouble("npcs." + uuid + ".z");
                        w.getChunkAt(x >> 4, z >> 4).load();
                        e = Bukkit.getEntity(uuid);
                    }
                }
            }

            if (e != null) {
                e.remove();
            }
        }

        // Zusätzlich: alle Villager mit CustomName "§e§lRTP" entfernen (Fallback)
        for (World w : Bukkit.getWorlds()) {
            for (Entity e : w.getEntities()) {
                if (e instanceof Villager) {
                    String cn = e.getCustomName();
                    if (cn != null && cn.equals("§e§lRTP")) {
                        e.remove();
                    }
                }
            }
        }

        npcs.clear();
        config.set("npcs", null);
        save();
    }

    public void save() {
        try { config.save(file); } catch (IOException e) { e.printStackTrace(); }
    }
}
