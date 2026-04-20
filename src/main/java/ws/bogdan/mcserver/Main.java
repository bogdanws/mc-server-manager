package ws.bogdan.mcserver;

import ws.bogdan.mcserver.model.Achievement;
import ws.bogdan.mcserver.model.ItemStack;
import ws.bogdan.mcserver.model.Plugin;
import ws.bogdan.mcserver.model.Rank;
import ws.bogdan.mcserver.model.Transaction;
import ws.bogdan.mcserver.model.World;
import ws.bogdan.mcserver.model.enums.Difficulty;
import ws.bogdan.mcserver.model.enums.Rarity;
import ws.bogdan.mcserver.model.enums.WorldType;
import ws.bogdan.mcserver.model.item.BuildingBlock;
import ws.bogdan.mcserver.model.item.Food;
import ws.bogdan.mcserver.model.item.Item;
import ws.bogdan.mcserver.model.item.OreBlock;
import ws.bogdan.mcserver.model.item.Pickaxe;
import ws.bogdan.mcserver.model.item.Potion;
import ws.bogdan.mcserver.model.item.Sword;
import ws.bogdan.mcserver.model.item.Tool;
import ws.bogdan.mcserver.model.player.Administrator;
import ws.bogdan.mcserver.model.player.Moderator;
import ws.bogdan.mcserver.model.player.Player;
import ws.bogdan.mcserver.model.player.RegularPlayer;
import ws.bogdan.mcserver.model.player.VIPPlayer;
import ws.bogdan.mcserver.exception.PluginDependencyException;
import ws.bogdan.mcserver.service.AchievementService;
import ws.bogdan.mcserver.service.EconomyService;
import ws.bogdan.mcserver.service.InventoryService;
import ws.bogdan.mcserver.service.PlayerService;
import ws.bogdan.mcserver.service.PluginService;
import ws.bogdan.mcserver.service.ServerState;
import ws.bogdan.mcserver.service.SessionService;
import ws.bogdan.mcserver.service.StaffService;
import ws.bogdan.mcserver.service.WorldService;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {

    static ServerState state;
    static PlayerService playerService;
    static WorldService worldService;
    static InventoryService inventoryService;
    static EconomyService economyService;
    static AchievementService achievementService;
    static PluginService pluginService;
    static SessionService sessionService;
    static StaffService staffService;

    // Players
    static Player alice, bob, charlie, diana, mod, admin;

    // Worlds
    static World survival, creative, minigames;

    // Items
    static Pickaxe diamondPickaxe;
    static Sword ironSword;
    static Food bread;
    static Potion healingPotion;
    static BuildingBlock stone;
    static OreBlock diamondOre;

    // Plugins
    static Plugin corePlugin, economyPlugin, shopPlugin;

    // Achievements
    static Achievement firstLogin, firstKill, legendary;

    public static void main(String[] args) {
        state = new ServerState();
        playerService = new PlayerService(state);
        worldService = new WorldService(state);
        inventoryService = new InventoryService(state);
        economyService = new EconomyService(state);
        achievementService = new AchievementService(state);
        pluginService = new PluginService(state);
        sessionService = new SessionService(state, playerService);
        staffService = new StaffService(state);

        System.out.println("\n=== SETUP RANKS ===");
        setupRanks();

        System.out.println("\n=== SETUP WORLDS ===");
        setupWorlds();

        System.out.println("\n=== SETUP PLAYERS ===");
        setupPlayers();

        System.out.println("\n=== LOGIN PLAYERS ===");
        loginPlayers();

        System.out.println("\n=== POPULATE INVENTORIES ===");
        populateInventories();

        System.out.println("\n=== ADD PLAYTIME ===");
        addPlaytime();

        System.out.println("\n=== DEMO ECONOMY ===");
        demoEconomy();

        System.out.println("\n=== DEMO ACHIEVEMENTS ===");
        demoAchievements();

        System.out.println("\n=== DEMO PLUGINS ===");
        demoPlugins();

        System.out.println("\n=== DEMO STAFF ===");
        demoStaff();

        System.out.println("\n=== REPORTS ===");
        printReports();

        System.out.println("\n=== LOGOUT PLAYERS ===");
        logoutPlayers();
    }

    private static Rank guestRank, vipRank, modRank, adminRank;

    private static void setupRanks() {
        guestRank = new Rank("GUEST", "[G]", "&7", new HashSet<>(), 1);
        vipRank = new Rank("VIP", "[V]", "&6", new HashSet<>(Set.of("vip.homes", "vip.fly")), 10);
        modRank = new Rank("MOD", "[M]", "&b", new HashSet<>(Set.of("mod.kick", "mod.mute")), 50);
        adminRank = new Rank("ADMIN", "[A]", "&c", new HashSet<>(Set.of("admin.ban", "admin.op")), 100);
        System.out.println("Created ranks: GUEST, VIP, MOD, ADMIN");
    }

    private static void setupWorlds() {
        survival = worldService
                .createWorld(new World("survival_main", 12345L, WorldType.SURVIVAL, Difficulty.NORMAL, 50, 0, 64, 0));
        creative = worldService
                .createWorld(new World("creative_hub", 67890L, WorldType.CREATIVE, Difficulty.PEACEFUL, 20, 0, 64, 0));
        minigames = worldService
                .createWorld(new World("minigames", 11111L, WorldType.ADVENTURE, Difficulty.EASY, 100, 0, 64, 0));
        System.out.println("Created worlds: survival_main, creative_hub, minigames");
    }

    private static void setupPlayers() {
        alice = playerService.addPlayer(new RegularPlayer("Alice", guestRank));
        bob = playerService.addPlayer(new RegularPlayer("Bob", guestRank));
        charlie = playerService.addPlayer(new RegularPlayer("Charlie", guestRank));
        diana = playerService.addPlayer(new VIPPlayer("Diana", vipRank, 3));
        mod = playerService.addPlayer(new Moderator("ModSteve", modRank, "STAFF-001"));
        admin = playerService.addPlayer(new Administrator("AdminJoe", adminRank, "STAFF-002"));
        System.out.println(
                "Registered 6 players: Alice, Bob, Charlie (GUEST), Diana (VIP), ModSteve (MOD), AdminJoe (ADMIN)");
    }

    private static void loginPlayers() {
        sessionService.login(alice, survival);
        sessionService.login(bob, survival);
        sessionService.login(charlie, survival);
        sessionService.login(diana, survival);
        sessionService.login(mod, creative);
        sessionService.login(admin, creative);
        System.out.println("Alice, Bob, Charlie, Diana logged into survival_main");
        System.out.println("ModSteve, AdminJoe logged into creative_hub");
    }

    private static void populateInventories() {
        diamondPickaxe = new Pickaxe("minecraft:diamond_pickaxe", "Diamond Pickaxe", 1, Rarity.RARE, 1561, "DIAMOND");
        ironSword = new Sword("minecraft:iron_sword", "Iron Sword", 1, Rarity.UNCOMMON, 250, "IRON", 6);
        bread = new Food("minecraft:bread", "Bread", 64, Rarity.COMMON, 5, null);
        healingPotion = new Potion("minecraft:potion_healing", "Potion of Healing", 1, Rarity.UNCOMMON, 0,
                "REGENERATION", 30);
        stone = new BuildingBlock("minecraft:stone", "Stone", 64, Rarity.COMMON, 1, true);
        diamondOre = new OreBlock("minecraft:diamond_ore", "Diamond Ore", 64, Rarity.EPIC, 3, false, "DIAMOND", 7);

        inventoryService.addItem(alice, diamondPickaxe, 1);
        inventoryService.addItem(alice, bread, 32);
        inventoryService.addItem(bob, ironSword, 1);
        inventoryService.addItem(bob, stone, 64);
        inventoryService.addItem(charlie, healingPotion, 1);
        inventoryService.addItem(charlie, bread, 16);
        inventoryService.addItem(diana, diamondOre, 8);
        inventoryService.addItem(diana, ironSword, 1);

        System.out.println("Inventories populated");

        // demonstrate item usage
        diamondPickaxe.use();
        ironSword.use();
        bread.consume();
        healingPotion.consume();
        diamondOre.mine();
    }

    private static void addPlaytime() {
        playerService.addPlaytime(alice, 240);
        playerService.addPlaytime(bob, 180);
        playerService.addPlaytime(charlie, 300);
        playerService.addPlaytime(diana, 420);
        playerService.addPlaytime(mod, 150);
        playerService.addPlaytime(admin, 90);
        System.out.println("Playtime added to all players");
    }

    private static void demoEconomy() {
        alice.addBalance(500.0);
        diana.addBalance(1000.0);
        bob.addBalance(200.0);

        System.out.println("Alice balance: " + alice.getBalance());
        System.out.println("Bob balance (before sale): " + bob.getBalance());

        ItemStack swordStack = state.getInventories().get(bob).getStacks().get(0);
        Transaction t = economyService.executeTransaction(alice, bob, swordStack, 150.0);
        System.out.println("Transaction: " + t.buyer().getUsername() + " bought "
                + t.itemStack().item().getDisplayName() + " from " + t.seller().getUsername()
                + " for " + t.price() + " coins");
        System.out.println("Alice balance after: " + alice.getBalance());
        System.out.println("Bob balance after: " + bob.getBalance());
    }

    private static void demoAchievements() {
        firstLogin = new Achievement("first_login", "First Login", "Log in for the first time", 10, null);
        firstKill = new Achievement("first_kill", "First Blood", "Kill your first mob", 25, firstLogin);
        legendary = new Achievement("legendary", "Legendary", "Reach legendary status", 100, null);

        achievementService.registerAchievement(firstLogin);
        achievementService.registerAchievement(firstKill);
        achievementService.registerAchievement(legendary);

        achievementService.grantAchievement(alice, firstLogin);
        achievementService.grantAchievement(alice, firstKill);
        achievementService.grantAchievement(diana, firstLogin);
        achievementService.grantAchievement(diana, legendary);

        try {
            achievementService.grantAchievement(bob, firstKill);
        } catch (IllegalStateException e) {
            System.out.println("Expected error: " + e.getMessage());
        }
    }

    private static void demoPlugins() {
        corePlugin = new Plugin("CorePlugin", "1.0.0", "DevTeam");
        economyPlugin = new Plugin("EconomyPlugin", "2.1.0", "DevTeam");
        shopPlugin = new Plugin("ShopPlugin", "1.5.0", "DevTeam");

        economyPlugin.addDependency(corePlugin);
        shopPlugin.addDependency(corePlugin);
        shopPlugin.addDependency(economyPlugin);

        pluginService.installPlugin(corePlugin);
        pluginService.installPlugin(economyPlugin);
        pluginService.installPlugin(shopPlugin);

        try {
            pluginService.disablePlugin("CorePlugin");
        } catch (PluginDependencyException e) {
            System.out.println("Expected error: " + e.getMessage());
        }

        pluginService.disablePlugin("ShopPlugin");
        pluginService.disablePlugin("EconomyPlugin");
        pluginService.disablePlugin("CorePlugin");
    }

    private static void demoStaff() {
        Moderator modSteve = (Moderator) mod;
        Administrator adminJoe = (Administrator) admin;

        staffService.kickPlayer(modSteve, charlie, "Spamming in chat");
        playerService.promoteTo(adminJoe, bob, vipRank);
        System.out.println("Bob promoted to: " + bob.getRank().getName());
        staffService.banPlayer(adminJoe, charlie, "Repeated offenses");
        System.out.println("Players remaining: " + state.getPlayers().size());
    }

    private static void printReports() {
        System.out.println("\n-- Top 5 by Playtime --");
        List<Player> top5 = playerService.topNByPlaytime(5);
        for (int i = 0; i < top5.size(); i++) {
            Player p = top5.get(i);
            System.out.println((i + 1) + ". " + p.getUsername() + " - " + p.getPlaytimeMinutes() + " min");
        }

        System.out.println("\n-- Players by Rank --");
        for (Map.Entry<Rank, Set<Player>> entry : state.getPlayersByRank().entrySet()) {
            System.out.print(entry.getKey().getName() + ": ");
            entry.getValue().forEach(p -> System.out.print(p.getUsername() + " "));
            System.out.println();
        }

        System.out.println("\n-- Top 3 Richest --");
        List<Player> richest = economyService.topRichestPlayers(3);
        for (int i = 0; i < richest.size(); i++) {
            Player p = richest.get(i);
            System.out.println((i + 1) + ". " + p.getUsername() + " - " + p.getBalance() + " coins");
        }

        System.out.println("\n-- World Stats --");
        worldService.worldStats().forEach((name, count) -> System.out.println(name + ": " + count + " players online"));

        System.out.println("\n-- Rare Items --");
        List<Item> rareItems = inventoryService.searchByRarity(Rarity.RARE);
        rareItems.forEach(item -> System.out.println(item.getDisplayName() + " [" + item.getRarity() + "]"));

        System.out.println("\n-- All Tools --");
        List<Tool> tools = inventoryService.searchByType(Tool.class);
        tools.forEach(t -> System.out.println(t.getDisplayName() + " - " + t.describe()));
    }

    private static void logoutPlayers() {
        for (Player p : List.of(alice, bob, diana, mod, admin)) {
            if (p.getCurrentWorld() != null) {
                sessionService.logout(p);
                System.out.println(p.getUsername() + " logged out");
            }
        }
    }
}
