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
        // Alle gespeicherten NPCs laden
        if (config.getConfigurationSection("npcs") == null) return;
        for (String key : config.getConfigurationSection("npcs").getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                String name = config.getString("npcs." + key + ".name", "§e§lRTP");

                // Prüfen, ob Entity noch existiert
                Entity e = Bukkit.getEntity(uuid);
                if (e != null && e instanceof Villager) {
                    npcs.put(uuid, name);
                    prepareNpc((Villager) e, name);
                } else {
                    // NPC existiert nicht mehr → aus Config entfernen
                    config.set("npcs." + key, null);
                }
            } catch (Exception ex) {
                // Ignorieren
            }
        }
        save();
    }

    public Villager spawnNpc(Location loc, String name) {
        World world = loc.getWorld();
        if (world == null) return null;

        Villager npc = (Villager) world.spawnEntity(loc, EntityType.VILLAGER);
        prepareNpc(npc, name);

        npcs.put(npc.getUniqueId(), name);
        config.set("npcs." + npc.getUniqueId() + ".name", name);
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
        for (UUID uuid : npcs.keySet()) {
            Entity e = Bukkit.getEntity(uuid);
            if (e != null) e.remove();
        }
        npcs.clear();
        config.set("npcs", null);
        save();
    }

    public void save() {
        try { config.save(file); } catch (IOException e) { e.printStackTrace(); }
    }
}
