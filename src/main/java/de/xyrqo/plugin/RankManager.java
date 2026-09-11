package de.xyrqo.plugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class RankManager {

    private final File file;
    private FileConfiguration config;

    public RankManager(JavaPlugin plugin) {
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        this.file = new File(plugin.getDataFolder(), "ranks.yml");
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public Rank getRank(UUID uuid) {
        String id = config.getString("ranks." + uuid);
        return Rank.fromId(id);
    }

    public void setRank(UUID uuid, Rank rank) {
        if (rank == null) {
            config.set("ranks." + uuid, null);
        } else {
            config.set("ranks." + uuid, rank.getId());
        }
        save();
    }

    public void removeRank(UUID uuid) {
        config.set("ranks." + uuid, null);
        save();
    }

    public void save() {
        try { config.save(file); } catch (IOException e) { e.printStackTrace(); }
    }
}
