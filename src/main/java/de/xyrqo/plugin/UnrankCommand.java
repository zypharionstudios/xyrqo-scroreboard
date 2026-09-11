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

public class UnrankCommand implements CommandExecutor, TabCompleter {

    private final RankManager ranks;

    public UnrankCommand(RankManager ranks) {
        this.ranks = ranks;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("xyrqo.rank")) {
            sender.sendMessage("§5§l✦ §cKeine Berechtigung!");
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage("§5§l✦ §cBenutzung: /unrank <spieler>");
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
            sender.sendMessage("§5§l✦ §cSpieler nicht gefunden.");
            return true;
        }
        Rank current = ranks.getRank(target.getUniqueId());
        if (current == null) {
            sender.sendMessage("§5§l✦ §c" + target.getName() + " hat keinen Rang.");
            return true;
        }
        ranks.removeRank(target.getUniqueId());
        sender.sendMessage("§5§l✦ §aRang entfernt von §f" + target.getName() + " §7(war: " + current.getDisplay() + "§7)");
        Player online = target.getPlayer();
        if (online != null) {
            online.sendMessage("§5§l✦ §cDein Rang wurde entfernt.");
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
        }
        return list;
    }
}
