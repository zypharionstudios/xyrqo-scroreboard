package de.xyrqo.plugin;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class BalanceCommand implements CommandExecutor {

    private final EconomyManager economy;
    private final DecimalFormat fmt = new DecimalFormat("#,##0.00");

    public BalanceCommand(EconomyManager economy) {
        this.economy = economy;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length >= 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
                sender.sendMessage("§5§l✦ §cSpieler nicht gefunden.");
                return true;
            }
            double bal = economy.getBalance(target.getUniqueId());
            sender.sendMessage("§5§l✦ §7Guthaben von §d" + target.getName() + "§7: §a$" + fmt.format(bal));
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cBenutzung: /balance <spieler>");
            return true;
        }
        Player p = (Player) sender;
        double bal = economy.getBalance(p.getUniqueId());
        p.sendMessage("§5§l✦ §7Dein Guthaben: §a$" + fmt.format(bal));
        return true;
    }
}
