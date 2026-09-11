package de.xyrqo.plugin;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BalResetCommand implements CommandExecutor, TabCompleter {

    private final EconomyManager economy;
    private final DecimalFormat fmt = new DecimalFormat("#,##0.00");

    public BalResetCommand(EconomyManager economy) {
        this.economy = economy;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("xyrqo.balreset")) {
            sender.sendMessage("§5§l✦ §cKeine Berechtigung!");
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage("§5§l✦ §cBenutzung: /balreset <spieler>");
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
            sender.sendMessage("§5§l✦ §cSpieler nicht gefunden.");
            return true;
        }
        double before = economy.getBalance(target.getUniqueId());
        economy.setBalance(target.getUniqueId(), 0.0);
        sender.sendMessage("§5§l✦ §aGuthaben von §d" + target.getName() + " §azurückgesetzt.");
        sender.sendMessage("§5§l✦ §7Vorher: §c$" + fmt.format(before) + " §7→ Jetzt: §a$0.00");
        Player online = target.getPlayer();
        if (online != null) {
            online.sendMessage("§5§l✦ §cDein Guthaben wurde von einem Admin zurückgesetzt.");
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
