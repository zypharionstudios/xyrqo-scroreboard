package de.xyrqo.plugin;

public enum Rank {

    OWNER      ("owner",      "§6§lO§e§lW§6§lN§e§lE§6§lR",            "§6§l[§e§lOWNER§6§l] §r", 100),
    MANAGEMENT ("management", "§9§lM§b§lA§9§lN§b§lA§9§lG§b§lE§9§lR",  "§9§l[§b§lMGR§9§l] §r",   90),
    DEVELOPER  ("developer",  "§d§lD§5§lE§d§lV§5§lE§d§lL§5§lO§d§lP§5§lE§d§lR", "§d§l[§5§lDEV§d§l] §r", 80),
    ADMIN      ("admin",      "§c§lA§4§lD§c§lM§4§lI§c§lN",            "§c§l[§4§lADMIN§c§l] §r", 70),
    MODERATOR  ("moderator",  "§a§lM§2§lO§a§lD§2§lE§a§lR§2§lA§a§lT§2§lO§a§lR", "§a§l[§2§lMOD§a§l] §r", 60),
    MEDIA      ("media",      "§e§lM§6§lE§e§lD§6§lI§e§lA",            "§e§l[§6§lMEDIA§e§l] §r", 50),
    SUPPORTER  ("supporter",  "§b§lS§3§lU§b§lP§3§lP§b§lO§3§lR§b§lT§3§lE§b§lR", "§b§l[§3§lSUP§b§l] §r", 40);

    private final String id;
    private final String display;
    private final String tabPrefix;
    private final int weight;

    Rank(String id, String display, String tabPrefix, int weight) {
        this.id = id;
        this.display = display;
        this.tabPrefix = tabPrefix;
        this.weight = weight;
    }

    public String getId() { return id; }
    public String getDisplay() { return display; }
    public String getTabPrefix() { return tabPrefix; }
    public int getWeight() { return weight; }

    public static Rank fromId(String id) {
        if (id == null) return null;
        for (Rank r : values()) {
            if (r.id.equalsIgnoreCase(id)) return r;
        }
        return null;
    }

    public static String listAll() {
        StringBuilder sb = new StringBuilder();
        for (Rank r : values()) {
            sb.append(r.display).append("§7, ");
        }
        if (sb.length() > 2) sb.setLength(sb.length() - 2);
        return sb.toString();
    }
}
