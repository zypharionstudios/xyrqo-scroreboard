package de.xyrqo.plugin;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

public class SellGUI implements InventoryHolder {

    private final Inventory inventory;
    private final UUID owner;

    public SellGUI(UUID owner) {
        this.owner = owner;
        this.inventory = Bukkit.createInventory(this, 27, "§5§l✦ §dVerkaufen §5§l✦");
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public UUID getOwner() {
        return owner;
    }
}
