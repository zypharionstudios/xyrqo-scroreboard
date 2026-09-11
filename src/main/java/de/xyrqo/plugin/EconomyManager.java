package de.xyrqo.plugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class EconomyManager {

    private final File file;
    private FileConfiguration config;

    public EconomyManager(JavaPlugin plugin) {
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        this.file = new File(plugin.getDataFolder(), "players.yml");
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public double getBalance(UUID uuid) {
        return config.getDouble("players." + uuid + ".money", 0.0);
    }

    public void setBalance(UUID uuid, double amount) {
        config.set("players." + uuid + ".money", amount);
        save();
    }

    public void addBalance(UUID uuid, double amount) {
        setBalance(uuid, getBalance(uuid) + amount);
    }

    public void removeBalance(UUID uuid, double amount) {
        setBalance(uuid, Math.max(0, getBalance(uuid) - amount));
    }

    public void save() {
        try { config.save(file); } catch (IOException e) { e.printStackTrace(); }
    }
}
