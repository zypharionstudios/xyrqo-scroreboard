package de.xyrqo.plugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NpcCommand implements CommandExecutor {

    private final NpcManager npcManager;

    public NpcCommand(NpcManager npcManager) {
        this.npcManager = npcManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cNur Spieler können das nutzen.");
            return true;
        }

        if (!sender.hasPermission("xyrqo.npc")) {
            sender.sendMessage("§5§l✦ §cKeine Berechtigung!");
            return true;
        }

        Player p = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("npc-remove")) {
            npcManager.removeAll();
            p.sendMessage("§5§l✦ §aAlle NPCs entfernt.");
            return true;
        }

        // /npc-load
        Location loc = p.getLocation();
        npcManager.spawnNpc(loc, "§e§lRTP");
        p.sendMessage("§5§l✦ §aRTP-NPC wurde erstellt!");
        return true;
    }
}
