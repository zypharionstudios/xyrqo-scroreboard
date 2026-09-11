package de.xyrqo.plugin;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RankCommand implements CommandExecutor, TabCompleter {

    private final RankManager ranks;

    public RankCommand(RankManager ranks) {
        this.ranks = ranks;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("xyrqo.rank")) {
            sender.sendMessage("§5§l✦ §cKeine Berechtigung!");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage("§5§l✦ §cBenutzung: /rank <spieler> <rang>");
            sender.sendMessage("§5§l✦ §7Ränge: " + Rank.listAll());
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
            sender.sendMessage("§5§l✦ §cSpieler nicht gefunden.");
            return true;
        }
        Rank rank = Rank.fromId(args[1]);
        if (rank == null) {
            sender.sendMessage("§5§l✦ §cUnbekannter Rang: §f" + args[1]);
            sender.sendMessage("§5§l✦ §7Verfügbar: " + Rank.listAll());
            return true;
        }
        ranks.setRank(target.getUniqueId(), rank);
        sender.sendMessage("§5§l✦ §aRang gesetzt: §f" + target.getName() + " §7→ " + rank.getDisplay());
        Player online = target.getPlayer();
        if (online != null) {
            online.sendMessage("§5§l✦ §7Du hast den Rang " + rank.getDisplay() + " §7erhalten!");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            String start = args[0].toLowerCase();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(start)) list.add(p.getName());
            }
        } else if (args.length == 2) {
            String start = args[1].toLowerCase();
            for (Rank r : Rank.values()) {
                if (r.getId().startsWith(start)) list.add(r.getId());
            }
        }
        return list;
    }
}
