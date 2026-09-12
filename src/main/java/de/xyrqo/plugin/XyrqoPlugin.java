package de.xyrqo.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Map;

public class XyrqoPlugin extends JavaPlugin implements Listener {

    private EconomyManager economy;
    private RankManager ranks;
    private NpcManager npcManager;
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
        npcManager = new NpcManager(this);
        getServer().getPluginManager().registerEvents(this, this);

        getCommand("sell").setExecutor(new SellCommand(economy));
        getCommand("balance").setExecutor(new BalanceCommand(economy));

        BalResetCommand reset = new BalResetCommand(economy);
        getCommand("balreset").setExecutor(reset);
        getCommand("balreset").setTabCompleter(reset);

        RankCommand rc = new RankCommand(ranks, this);
        getCommand("rank").setExecutor(rc);
        getCommand("rank").setTabCompleter(rc);

        UnrankCommand uc = new UnrankCommand(ranks, this);
        getCommand("unrank").setExecutor(uc);
        getCommand("unrank").setTabCompleter(uc);

        NpcCommand nc = new NpcCommand(npcManager);
        if (getCommand("npcload") != null) getCommand("npcload").setExecutor(nc);
        if (getCommand("npcremove") != null) getCommand("npcremove").setExecutor(nc);

        getLogger().info("XyrqoPlugin v1.4 aktiviert!");

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    update(p);
                }
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    @Override
    public void onDisable() {
        if (economy != null) economy.save();
        if (ranks != null) ranks.save();
        if (npcManager != null) npcManager.save();
    }

    public void refreshAllPlayers() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            Rank r = ranks.getRank(p.getUniqueId());
            p.setPlayerListName(r != null ? r.getTabPrefix() + p.getName() : "§7" + p.getName());
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            update(p);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskLater(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                update(p);
            }
        }, 20L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent e) {
        Bukkit.getScheduler().runTaskLater(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                update(p);
            }
        }, 1L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        economy.save();
        ranks.save();
        npcManager.save();
        Bukkit.getScheduler().runTaskLater(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                update(p);
            }
        }, 1L);
    }

    // ================= NPC KLICK (Rechtsklick) =================
    @EventHandler
    public void onNpcRightClick(PlayerInteractEntityEvent e) {
        if (!npcManager.isNpc(e.getRightClicked())) return;
        e.setCancelled(true);
        openRtpMenu(e.getPlayer());
    }

    // ================= NPC KLICK (Linksklick / Schlagen) =================
    @EventHandler
    public void onNpcLeftClick(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        if (!(damager instanceof Player)) return;
        if (!npcManager.isNpc(e.getEntity())) return;
        e.setCancelled(true);
        openRtpMenu((Player) damager);
    }

    private void openRtpMenu(Player player) {
        RtpGUI gui = new RtpGUI();
        Inventory inv = gui.getInventory();

        ItemStack grass = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta meta = grass.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§a§lOverworld");
            meta.setLore(Arrays.asList(
                "§7Klicke, um zufällig",
                "§7in die Overworld zu teleportieren."
            ));
            grass.setItemMeta(meta);
        }
        inv.setItem(13, grass);

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1f, 1.2f);
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof RtpGUI)) return;
        e.setCancelled(true);

        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();

        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getType() != Material.GRASS_BLOCK) return;

        p.closeInventory();
        p.playSound(p.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1f, 1.2f);

        startRtpCountdown(p);
    }

    private void startRtpCountdown(Player player) {
        new BukkitRunnable() {
            int count = 5;

            @Override
            public void run() {
                if (count > 0) {
                    player.sendActionBar("§d§l► §fRTP in §e§l" + count + "§f...");
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1f, 1f);
                    count--;
                } else {
                    Location safe = RtpManager.findSafeLocation(player.getWorld());
                    if (safe != null) {
                        player.teleport(safe);
                        player.sendMessage("§5§l✦ §aDu wurdest zufällig teleportiert!");
                        player.sendTitle("§d§lRTP", "§7Willkommen am neuen Ort!", 10, 40, 10);
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
                    } else {
                        player.sendMessage("§5§l✦ §cKein sicherer Ort gefunden. Versuch es nochmal!");
                        player.sendActionBar("§c§lRTP fehlgeschlagen");
                    }
                    cancel();
                }
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    // ================= SELL GUI =================
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

    // ================= SCOREBOARD =================
    private void update(Player viewer) {
        ScoreboardManager mgr = Bukkit.getScoreboardManager();
        Scoreboard board = mgr.getNewScoreboard();

        Objective obj = board.registerNewObjective("xyrqo", "dummy", "§5§l✦ §d§lXyrqoSMP §5§l✦");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        int ping = viewer.getPing();
        String pingColor = ping < 50 ? "§a" : ping < 100 ? "§e" : ping < 200 ? "§6" : "§c";
        String money = moneyFormat.format(economy.getBalance(viewer.getUniqueId()));

        Rank viewerRank = ranks.getRank(viewer.getUniqueId());
        String rankDisplay = viewerRank != null ? viewerRank.getDisplay() : "§7Spieler";

        int score = LINES.length;
        for (String template : LINES) {
            String text = template
                .replace("%player%", viewer.getName())
                .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                .replace("%ping%", pingColor + ping)
                .replace("%money%", money)
                .replace("%rank%", rankDisplay);
            setLine(board, obj, text, score);
            score--;
        }

        for (Player target : Bukkit.getOnlinePlayers()) {
            Rank targetRank = ranks.getRank(target.getUniqueId());

            if (targetRank == null) {
                for (Rank r : Rank.values()) {
                    Team t = board.getTeam("r_" + r.getId());
                    if (t != null && t.hasEntry(target.getName())) {
                        t.removeEntry(target.getName());
                    }
                }
                continue;
            }

            for (Rank r : Rank.values()) {
                if (r == targetRank) continue;
                Team other = board.getTeam("r_" + r.getId());
                if (other != null && other.hasEntry(target.getName())) {
                    other.removeEntry(target.getName());
                }
            }

            Team team = board.getTeam("r_" + targetRank.getId());
            if (team == null) {
                team = board.registerNewTeam("r_" + targetRank.getId());
                team.setPrefix(targetRank.getTabPrefix());
            }
            if (!team.hasEntry(target.getName())) {
                team.addEntry(target.getName());
            }
        }

        viewer.setScoreboard(board);

        viewer.setPlayerListName(viewerRank != null
                ? viewerRank.getTabPrefix() + viewer.getName()
                : "§7" + viewer.getName());

        String header = "\n§5§l✦ §d§lXyrqoSMP §5§l✦\n§7Willkommen, §f" + viewer.getName() + "\n";
        String footer = "\n§d§l► §fOnline: §e" + Bukkit.getOnlinePlayers().size()
                      + "\n§d§l► §fxyrqosmp.tkmc.net\n";

        viewer.setPlayerListHeaderFooter(header, footer);
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
}
