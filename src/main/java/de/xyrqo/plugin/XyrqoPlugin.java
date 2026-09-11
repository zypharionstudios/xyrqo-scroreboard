package de.xyrqo.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.text.DecimalFormat;
import java.util.Map;

public class XyrqoPlugin extends JavaPlugin implements Listener {

    private EconomyManager economy;
    private RankManager ranks;
    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0.00");

    private static final String[] LINES = {
        "§5§m━━━━━━━━━━━━━━━━",
        "§d§l► §fSpieler",
        "§7  %player%",
        "§r",
        "§d§l► §fRang",
        "§7  %rank%",
        "§r§r",
        "§d§l► §fGeld",
        "§7  §a$§f%money%",
        "§r§r§r",
        "§d§l► §fOnline",
        "§7  §e%online%",
        "§r§r§r§r",
        "§d§l► §fPing",
        "§7  %ping%ms",
        "§r§r§r§r§r",
        "§5§m━━━━━━━━━━━━━━━━",
        "§d  xyrqosmp.tkmc.net"
    };

    @Override
    public void onEnable() {
        economy = new EconomyManager(this);
        ranks = new RankManager(this);
        getServer().getPluginManager().registerEvents(this, this);

        getCommand("sell").setExecutor(new SellCommand(economy));
        getCommand("balance").setExecutor(new BalanceCommand(economy));

        BalResetCommand reset = new BalResetCommand(economy);
        getCommand("balreset").setExecutor(reset);
        getCommand("balreset").setTabCompleter(reset);

        RankCommand rc = new RankCommand(ranks);
        getCommand("rank").setExecutor(rc);
        getCommand("rank").setTabCompleter(rc);

        UnrankCommand uc = new UnrankCommand(ranks);
        getCommand("unrank").setExecutor(uc);
        getCommand("unrank").setTabCompleter(uc);

        getLogger().info("XyrqoPlugin aktiviert!");

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    update(p);
                    updateTab(p);
                    applyNametag(p);
                }
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    @Override
    public void onDisable() {
        if (economy != null) economy.save();
        if (ranks != null) ranks.save();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskLater(this, () -> {
            update(e.getPlayer());
            updateTab(e.getPlayer());
            applyNametag(e.getPlayer());
        }, 20L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        economy.save();
        ranks.save();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!(e.getInventory().getHolder() instanceof SellGUI)) return;
        if (!(e.getPlayer() instanceof Player)) return;

        Player p = (Player) e.getPlayer();
        SellGUI gui = (SellGUI) e.getInventory().getHolder();

        double total = 0;
        int count = 0;

        ItemStack[] contents = gui.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack it = contents[i];
            if (it == null || it.getType() == Material.AIR) continue;

            double price = SellCommand.getPrice(it.getType());
            if (price <= 0) {
                giveBack(p, it);
                continue;
            }

            total += price * it.getAmount();
            count += it.getAmount();
        }

        if (total <= 0) {
            p.sendMessage("§5§l✦ §cKeine verkaufbaren Items in der Truhe!");
            return;
        }

        economy.addBalance(p.getUniqueId(), total);
        p.sendMessage("§5§l✦ §aVerkauft: §f" + count + " Items §afür §a$" + moneyFormat.format(total));
        p.sendMessage("§5§l✦ §7Neues Guthaben: §a$" + moneyFormat.format(economy.getBalance(p.getUniqueId())));
        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.2f);
    }

    private void giveBack(Player p, ItemStack item) {
        Map<Integer, ItemStack> leftover = p.getInventory().addItem(item);
        for (ItemStack drop : leftover.values()) {
            p.getWorld().dropItemNaturally(p.getLocation(), drop);
        }
    }

    private void update(Player player) {
        ScoreboardManager mgr = Bukkit.getScoreboardManager();
        Scoreboard board = mgr.getNewScoreboard();

        Objective obj = board.registerNewObjective("xyrqo", "dummy", "§5§l✦ §d§lXyrqoSMP §5§l✦");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        int ping = player.getPing();
        String pingColor = ping < 50 ? "§a" : ping < 100 ? "§e" : ping < 200 ? "§6" : "§c";
        String money = moneyFormat.format(economy.getBalance(player.getUniqueId()));

        Rank rank = ranks.getRank(player.getUniqueId());
        String rankDisplay = rank != null ? rank.getDisplay() : "§7Spieler";

        int score = LINES.length;
        for (String template : LINES) {
            String text = template
                .replace("%player%", player.getName())
                .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                .replace("%ping%", pingColor + ping)
                .replace("%money%", money)
                .replace("%rank%", rankDisplay);
            setLine(board, obj, text, score);
            score--;
        }

        player.setScoreboard(board);
    }

    private void setLine(Scoreboard board, Objective obj, String text, int score) {
        String entry = "§" + "0123456789abcdef".charAt(score % 16);
        Team team = board.getTeam("x" + score);
        if (team == null) team = board.registerNewTeam("x" + score);
        if (!team.hasEntry(entry)) team.addEntry(entry);

        if (text.length() <= 16) {
            team.setPrefix(text);
            team.setSuffix("");
        } else {
            String prefix = text.substring(0, 16);
            String suffix = text.substring(16);
            if (prefix.endsWith("§")) {
                prefix = prefix.substring(0, 15);
                suffix = text.substring(15);
            }
            team.setPrefix(prefix);
            team.setSuffix(suffix.length() > 16 ? suffix.substring(0, 16) : suffix);
        }
        obj.getScore(entry).setScore(score);
    }

    private void updateTab(Player player) {
        Rank rank = ranks.getRank(player.getUniqueId());
        String tabPrefix = rank != null ? rank.getTabPrefix() : "§7";
        String coloredName = tabPrefix + player.getName();
        player.setPlayerListName(coloredName);

        String header = "\n§5§l✦ §d§lXyrqoSMP §5§l✦\n§7Willkommen, " + coloredName + "\n";
        String footer = "\n§d§l► §fOnline: §e" + Bukkit.getOnlinePlayers().size()
                      + "\n§d§l► §fxyrqosmp.tkmc.net\n";

        player.setPlayerListHeaderFooter(header, footer);
    }

    private void applyNametag(Player player) {
        Scoreboard main = Bukkit.getScoreboardManager().getMainScoreboard();
        Rank rank = ranks.getRank(player.getUniqueId());

        for (Rank r : Rank.values()) {
            Team t = main.getTeam("rank_" + r.getId());
            if (t != null && t.hasEntry(player.getName())) {
                t.removeEntry(player.getName());
            }
        }

        if (rank == null) return;

        Team team = main.getTeam("rank_" + rank.getId());
        if (team == null) {
            team = main.registerNewTeam("rank_" + rank.getId());
            team.setPrefix(rank.getTabPrefix());
        }
        if (!team.hasEntry(player.getName())) {
            team.addEntry(player.getName());
        }
    }
}
