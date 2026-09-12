package de.xyrqo.plugin;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class RtpGUI implements InventoryHolder {

    private final Inventory inventory;

    public RtpGUI() {
        this.inventory = Bukkit.createInventory(this, 27, "§5§l✦ §d§lRTP §5§l✦");
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
