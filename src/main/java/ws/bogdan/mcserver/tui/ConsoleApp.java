package ws.bogdan.mcserver.tui;

import ws.bogdan.mcserver.exception.PermissionDeniedException;
import ws.bogdan.mcserver.exception.PluginDependencyException;
import ws.bogdan.mcserver.exception.WorldFullException;
import ws.bogdan.mcserver.model.Achievement;
import ws.bogdan.mcserver.model.Plugin;
import ws.bogdan.mcserver.model.Rank;
import ws.bogdan.mcserver.model.World;
import ws.bogdan.mcserver.model.enums.Difficulty;
import ws.bogdan.mcserver.model.enums.WorldType;
import ws.bogdan.mcserver.model.player.Administrator;
import ws.bogdan.mcserver.model.player.Moderator;
import ws.bogdan.mcserver.model.player.Player;
import ws.bogdan.mcserver.model.player.RegularPlayer;
import ws.bogdan.mcserver.model.player.StaffMember;
import ws.bogdan.mcserver.model.player.VIPPlayer;
import ws.bogdan.mcserver.persistence.AchievementDAO;
import ws.bogdan.mcserver.persistence.PlayerDAO;
import ws.bogdan.mcserver.persistence.PluginDAO;
import ws.bogdan.mcserver.persistence.RankDAO;
import ws.bogdan.mcserver.persistence.WorldDAO;
import ws.bogdan.mcserver.service.AchievementService;
import ws.bogdan.mcserver.service.EconomyService;
import ws.bogdan.mcserver.service.PlayerService;
import ws.bogdan.mcserver.service.PluginService;
import ws.bogdan.mcserver.service.ServerState;
import ws.bogdan.mcserver.service.SessionService;
import ws.bogdan.mcserver.service.WorldService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class ConsoleApp {

    private final ServerState state;
    private final PlayerService playerService;
    private final WorldService worldService;
    private final PluginService pluginService;
    private final SessionService sessionService;
    private final EconomyService economyService;
    private final AchievementService achievementService;

    private final PlayerDAO playerDAO;
    private final WorldDAO worldDAO;
    private final PluginDAO pluginDAO;
    private final RankDAO rankDAO;
    private final AchievementDAO achievementDAO;

    private final Scanner scanner = new Scanner(System.in);

    public ConsoleApp(ServerState state,
                      PlayerService playerService,
                      WorldService worldService,
                      PluginService pluginService,
                      SessionService sessionService,
                      EconomyService economyService,
                      AchievementService achievementService,
                      PlayerDAO playerDAO,
                      WorldDAO worldDAO,
                      PluginDAO pluginDAO,
                      RankDAO rankDAO,
                      AchievementDAO achievementDAO) {
        this.state = state;
        this.playerService = playerService;
        this.worldService = worldService;
        this.pluginService = pluginService;
        this.sessionService = sessionService;
        this.economyService = economyService;
        this.achievementService = achievementService;
        this.playerDAO = playerDAO;
        this.worldDAO = worldDAO;
        this.pluginDAO = pluginDAO;
        this.rankDAO = rankDAO;
        this.achievementDAO = achievementDAO;
    }

    public void run() {
        System.out.println("\n========================================");
        System.out.println("       MC Server Manager");
        System.out.println("========================================");
        System.out.printf("Loaded from database: %d player(s), %d world(s), %d plugin(s)%n",
                state.getPlayers().size(), state.getWorlds().size(), state.getPlugins().size());

        boolean running = true;
        while (running) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Players      (" + state.getPlayers().size() + " registered)");
            System.out.println("2. Worlds       (" + state.getWorlds().size() + " loaded)");
            System.out.println("3. Plugins      (" + state.getPlugins().size() + " installed)");
            System.out.println("4. Achievements (" + state.getAchievements().size() + " in catalog)");
            System.out.println("5. Reports");
            System.out.println("0. Exit");
            switch (readInt("> ")) {
                case 1 -> playersMenu();
                case 2 -> worldsMenu();
                case 3 -> pluginsMenu();
                case 4 -> achievementsMenu();
                case 5 -> reportsMenu();
                case 0 -> running = false;
                default -> System.out.println("Unknown option.");
            }
        }

        System.out.println("Toate schimbarile au fost salvate.");
    }

    // players

    private void playersMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Players ---");
            System.out.println("1. List all players");
            System.out.println("2. Add player");
            System.out.println("3. Add playtime");
            System.out.println("4. Adjust balance");
            System.out.println("5. Login player to world");
            System.out.println("6. Logout player");
            System.out.println("7. Promote player (staff only)");
            System.out.println("0. Back");
            switch (readInt("> ")) {
                case 1 -> listPlayers();
                case 2 -> addPlayer();
                case 3 -> addPlaytime();
                case 4 -> adjustBalance();
                case 5 -> loginPlayer();
                case 6 -> logoutPlayer();
                case 7 -> promotePlayer();
                case 0 -> back = true;
                default -> System.out.println("Unknown option.");
            }
        }
    }

    private void listPlayers() {
        if (state.getPlayers().isEmpty()) {
            System.out.println("No players registered.");
            return;
        }
        System.out.printf("%n%-20s %-13s %-8s %10s %9s  %s%n",
                "Username", "Role", "Rank", "Balance", "Playtime", "World");
        System.out.println("-".repeat(75));
        List<Player> sorted = new ArrayList<>(state.getPlayers().values());
        sorted.sort(Comparator.comparing(Player::getUsername, String.CASE_INSENSITIVE_ORDER));
        for (Player p : sorted) {
            System.out.printf("%-20s %-13s %-8s %10.2f %8dm  %s%n",
                    p.getUsername(),
                    p.getRoleLabel(),
                    p.getRank().getName(),
                    p.getBalance(),
                    p.getPlaytimeMinutes(),
                    p.getCurrentWorld() != null ? p.getCurrentWorld().getName() : "-");
        }
    }

    private void addPlayer() {
        String name = readLine("Username: ");
        if (name.isEmpty())
            return;

        boolean exists = state.getPlayers().values().stream()
                .anyMatch(p -> p.getUsername().equalsIgnoreCase(name));
        if (exists) {
            System.out.println("A player with that name already exists.");
            return;
        }

        System.out.println("Type:  1=Regular  2=VIP  3=Moderator  4=Administrator");
        int type = readInt("> ");
        if (type < 1 || type > 4) {
            System.out.println("Invalid type.");
            return;
        }

        Rank rank = pickRank("Select rank:");
        if (rank == null)
            return;

        Player player;
        switch (type) {
            case 2 -> {
                int homes = readInt("Extra homes: ");
                player = new VIPPlayer(name, rank, homes);
            }
            case 3 -> {
                String staffId = readLine("Staff ID: ");
                player = new Moderator(name, rank, staffId);
            }
            case 4 -> {
                String staffId = readLine("Staff ID: ");
                player = new Administrator(name, rank, staffId);
            }
            default -> player = new RegularPlayer(name, rank);
        }

        playerService.addPlayer(player);
        playerDAO.save(player);
        System.out.println(
                "[saved] Added: " + player.getUsername() + " [" + player.getRoleLabel() + "] rank=" + rank.getName());
    }

    private void addPlaytime() {
        Player player = pickPlayer("Select player:");
        if (player == null)
            return;

        int minutes = readInt("Minutes to add: ");
        if (minutes <= 0) {
            System.out.println("Must be a positive number.");
            return;
        }

        playerService.addPlaytime(player, minutes);
        playerDAO.update(player);
        System.out.printf("[saved] %s now has %d min playtime.%n", player.getUsername(), player.getPlaytimeMinutes());
    }

    private void adjustBalance() {
        Player player = pickPlayer("Select player:");
        if (player == null)
            return;

        System.out.printf("Current balance: %.2f coins%n", player.getBalance());
        String input = readLine("Amount to add (use negative to subtract): ");
        double amount;
        try {
            amount = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
            return;
        }

        try {
            player.addBalance(amount);
            playerDAO.update(player);
            System.out.printf("[saved] %s balance is now %.2f coins.%n", player.getUsername(), player.getBalance());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void loginPlayer() {
        List<Player> offline = state.getPlayers().values().stream()
                .filter(p -> p.getCurrentWorld() == null)
                .sorted(Comparator.comparing(Player::getUsername, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

        if (offline.isEmpty()) {
            System.out.println("All players are already logged in.");
            return;
        }

        System.out.println("Select player to login:");
        for (int i = 0; i < offline.size(); i++) {
            System.out.printf("  %d. %s [%s]%n", i + 1, offline.get(i).getUsername(),
                    offline.get(i).getRank().getName());
        }
        int idx = readInt("> ") - 1;
        if (idx < 0 || idx >= offline.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        Player player = offline.get(idx);

        World world = pickWorld("Select world:");
        if (world == null)
            return;

        try {
            sessionService.login(player, world);
            playerDAO.update(player);
            System.out.printf("[saved] %s logged into %s.%n", player.getUsername(), world.getName());
        } catch (WorldFullException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void logoutPlayer() {
        List<Player> online = state.getPlayers().values().stream()
                .filter(p -> p.getCurrentWorld() != null)
                .sorted(Comparator.comparing(Player::getUsername, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

        if (online.isEmpty()) {
            System.out.println("No players are currently logged in.");
            return;
        }

        System.out.println("Select player to logout:");
        for (int i = 0; i < online.size(); i++) {
            System.out.printf("  %d. %s (in %s)%n", i + 1,
                    online.get(i).getUsername(), online.get(i).getCurrentWorld().getName());
        }
        int idx = readInt("> ") - 1;
        if (idx < 0 || idx >= online.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        Player player = online.get(idx);

        sessionService.logout(player);
        playerDAO.update(player);
        System.out.printf("[saved] %s logged out. Total playtime: %d min.%n",
                player.getUsername(), player.getPlaytimeMinutes());
    }

    private void promotePlayer() {
        List<Player> staff = state.getPlayers().values().stream()
                .filter(p -> p instanceof StaffMember)
                .sorted(Comparator.comparing(Player::getUsername, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

        if (staff.isEmpty()) {
            System.out.println("No staff members available. Add a Moderator or Administrator first.");
            return;
        }

        System.out.println("Select acting staff member:");
        for (int i = 0; i < staff.size(); i++) {
            System.out.printf("  %d. %s [%s]%n", i + 1, staff.get(i).getUsername(), staff.get(i).getRoleLabel());
        }
        int actorIdx = readInt("> ") - 1;
        if (actorIdx < 0 || actorIdx >= staff.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        StaffMember actor = (StaffMember) staff.get(actorIdx);

        Player target = pickPlayer("Select player to promote:");
        if (target == null)
            return;

        Rank newRank = pickRank("Select new rank:");
        if (newRank == null)
            return;

        try {
            playerService.promoteTo(actor, target, newRank);
            playerDAO.update(target);
            System.out.printf("[saved] %s promoted to %s.%n", target.getUsername(), newRank.getName());
        } catch (PermissionDeniedException e) {
            System.out.println("Permission denied: " + e.getMessage());
        }
    }

    // worlds

    private void worldsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Worlds ---");
            System.out.println("1. List all worlds");
            System.out.println("2. Add world");
            System.out.println("3. Delete world");
            System.out.println("0. Back");
            switch (readInt("> ")) {
                case 1 -> listWorlds();
                case 2 -> addWorld();
                case 3 -> deleteWorld();
                case 0 -> back = true;
                default -> System.out.println("Unknown option.");
            }
        }
    }

    private void listWorlds() {
        if (state.getWorlds().isEmpty()) {
            System.out.println("No worlds loaded.");
            return;
        }
        System.out.printf("%n%-20s %-10s %-10s %10s %7s%n", "Name", "Type", "Difficulty", "MaxPlayers", "Online");
        System.out.println("-".repeat(62));
        List<World> sorted = new ArrayList<>(state.getWorlds().values());
        sorted.sort(Comparator.comparing(World::getName));
        for (World w : sorted) {
            System.out.printf("%-20s %-10s %-10s %10d %7d%n",
                    w.getName(),
                    w.getWorldType(),
                    w.getDifficulty(),
                    w.getMaxPlayers(),
                    w.getOnlinePlayers().size());
        }
    }

    private void addWorld() {
        String name = readLine("World name: ");
        if (name.isEmpty())
            return;

        if (state.getWorlds().containsKey(name)) {
            System.out.println("A world with that name already exists.");
            return;
        }

        long seed;
        try {
            seed = Long.parseLong(readLine("Seed (integer): "));
        } catch (NumberFormatException e) {
            System.out.println("Invalid seed.");
            return;
        }

        System.out.println("Type:  1=SURVIVAL  2=CREATIVE  3=ADVENTURE");
        WorldType worldType = switch (readInt("> ")) {
            case 1 -> WorldType.SURVIVAL;
            case 2 -> WorldType.CREATIVE;
            case 3 -> WorldType.ADVENTURE;
            default -> null;
        };
        if (worldType == null) {
            System.out.println("Invalid type.");
            return;
        }

        System.out.println("Difficulty:  1=PEACEFUL  2=EASY  3=NORMAL  4=HARD");
        Difficulty difficulty = switch (readInt("> ")) {
            case 1 -> Difficulty.PEACEFUL;
            case 2 -> Difficulty.EASY;
            case 3 -> Difficulty.NORMAL;
            case 4 -> Difficulty.HARD;
            default -> null;
        };
        if (difficulty == null) {
            System.out.println("Invalid difficulty.");
            return;
        }

        int maxPlayers = readInt("Max players: ");
        if (maxPlayers <= 0) {
            System.out.println("Must be a positive number.");
            return;
        }

        World world = new World(name, seed, worldType, difficulty, maxPlayers, 0, 64, 0);
        worldService.createWorld(world);
        worldDAO.save(world);
        System.out.println("[saved] World created: " + name + " (" + worldType + ", " + difficulty + ")");
    }

    private void deleteWorld() {
        World world = pickWorld("Select world to delete:");
        if (world == null)
            return;

        if (!world.getOnlinePlayers().isEmpty()) {
            System.out.println("Cannot delete: " + world.getOnlinePlayers().size()
                    + " player(s) are online in this world. Log them out first.");
            return;
        }

        state.getWorlds().remove(world.getName());
        worldDAO.delete(world.getName());
        System.out.println("[saved] World deleted: " + world.getName());
    }

    // plugins

    private void pluginsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Plugins ---");
            System.out.println("1. List all plugins");
            System.out.println("2. Install plugin");
            System.out.println("3. Enable / Disable plugin");
            System.out.println("4. Uninstall plugin");
            System.out.println("0. Back");
            switch (readInt("> ")) {
                case 1 -> listPlugins();
                case 2 -> installPlugin();
                case 3 -> togglePlugin();
                case 4 -> uninstallPlugin();
                case 0 -> back = true;
                default -> System.out.println("Unknown option.");
            }
        }
    }

    private void listPlugins() {
        if (state.getPlugins().isEmpty()) {
            System.out.println("No plugins installed.");
            return;
        }
        System.out.printf("%n%-25s %-10s %-15s %-8s  %s%n", "Name", "Version", "Author", "Enabled", "Dependencies");
        System.out.println("-".repeat(80));
        List<Plugin> sorted = new ArrayList<>(state.getPlugins());
        sorted.sort(Comparator.comparing(Plugin::getName));
        for (Plugin p : sorted) {
            String deps = p.getDependencies().isEmpty()
                    ? "-"
                    : p.getDependencies().stream().map(Plugin::getName).collect(Collectors.joining(", "));
            System.out.printf("%-25s %-10s %-15s %-8s  %s%n",
                    p.getName(), p.getVersion(), p.getAuthor(), p.isEnabled() ? "yes" : "no", deps);
        }
    }

    private void installPlugin() {
        String name = readLine("Plugin name: ");
        if (name.isEmpty())
            return;

        boolean exists = state.getPlugins().stream().anyMatch(p -> p.getName().equalsIgnoreCase(name));
        if (exists) {
            System.out.println("A plugin with that name is already installed.");
            return;
        }

        String version = readLine("Version: ");
        String author = readLine("Author: ");

        Plugin plugin = new Plugin(name, version, author);

        List<Plugin> enabled = state.getPlugins().stream()
                .filter(Plugin::isEnabled)
                .sorted(Comparator.comparing(Plugin::getName))
                .collect(Collectors.toList());

        if (!enabled.isEmpty()) {
            System.out.print("Select dependencies (comma-separated numbers, blank for none): ");
            for (int i = 0; i < enabled.size(); i++) {
                System.out.printf("%n  %d. %s v%s", i + 1, enabled.get(i).getName(), enabled.get(i).getVersion());
            }
            System.out.println();
            String input = readLine("> ");
            if (!input.isBlank()) {
                for (String part : input.split(",")) {
                    try {
                        int idx = Integer.parseInt(part.trim()) - 1;
                        if (idx >= 0 && idx < enabled.size()) {
                            plugin.addDependency(enabled.get(idx));
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }

        try {
            pluginService.installPlugin(plugin);
            pluginDAO.save(plugin);
            System.out.println("[saved] Plugin installed: " + plugin.getName() + " v" + plugin.getVersion());
        } catch (PluginDependencyException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void togglePlugin() {
        Plugin plugin = pickPlugin("Select plugin to enable/disable:");
        if (plugin == null)
            return;

        if (plugin.isEnabled()) {
            try {
                pluginService.disablePlugin(plugin.getName());
                pluginDAO.update(plugin);
                System.out.println("[saved] Plugin disabled: " + plugin.getName());
            } catch (PluginDependencyException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            plugin.setEnabled(true);
            pluginDAO.update(plugin);
            System.out.println("[saved] Plugin enabled: " + plugin.getName());
        }
    }

    private void uninstallPlugin() {
        Plugin plugin = pickPlugin("Select plugin to uninstall:");
        if (plugin == null)
            return;

        boolean hasDependents = state.getPlugins().stream()
                .filter(p -> !p.equals(plugin))
                .anyMatch(p -> p.getDependencies().contains(plugin));

        if (hasDependents) {
            System.out.println("Cannot uninstall: other installed plugins depend on " + plugin.getName() + ".");
            return;
        }

        state.getPlugins().remove(plugin);
        pluginDAO.delete(plugin.getName());
        System.out.println("[saved] Plugin uninstalled: " + plugin.getName());
    }

    // achievements

    private void achievementsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Achievements ---");
            System.out.println("1. List all achievements");
            System.out.println("2. Grant achievement to player");
            System.out.println("3. View player's achievements");
            System.out.println("4. Register new achievement");
            System.out.println("0. Back");
            switch (readInt("> ")) {
                case 1 -> listAchievements();
                case 2 -> grantAchievement();
                case 3 -> viewPlayerAchievements();
                case 4 -> registerAchievement();
                case 0 -> back = true;
                default -> System.out.println("Unknown option.");
            }
        }
    }

    private void listAchievements() {
        if (state.getAchievements().isEmpty()) {
            System.out.println("No achievements in catalog.");
            return;
        }
        System.out.printf("%n%-20s %-25s %6s  %s%n", "ID", "Title", "XP", "Requires");
        System.out.println("-".repeat(70));
        state.getAchievements().values().stream()
                .sorted(Comparator.comparing(Achievement::getId))
                .forEach(a -> System.out.printf("%-20s %-25s %6d  %s%n",
                        a.getId(),
                        a.getTitle(),
                        a.getXpReward(),
                        a.getParentAchievement() != null ? a.getParentAchievement().getTitle() : "-"));
    }

    private void grantAchievement() {
        Player player = pickPlayer("Select player:");
        if (player == null) return;

        Set<Achievement> alreadyHas = state.getPlayerAchievements()
                .getOrDefault(player, java.util.Collections.emptySet());

        List<Achievement> available = state.getAchievements().values().stream()
                .filter(a -> !alreadyHas.contains(a))
                .sorted(Comparator.comparing(Achievement::getId))
                .collect(Collectors.toList());

        if (available.isEmpty()) {
            System.out.println(player.getUsername() + " has already unlocked every achievement.");
            return;
        }

        System.out.println("Available achievements:");
        for (int i = 0; i < available.size(); i++) {
            Achievement a = available.get(i);
            String req = a.getParentAchievement() != null
                    ? " (requires: " + a.getParentAchievement().getTitle() + ")"
                    : "";
            System.out.printf("  %d. %-25s +%d XP%s%n", i + 1, a.getTitle(), a.getXpReward(), req);
        }
        int idx = readInt("> ") - 1;
        if (idx < 0 || idx >= available.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        Achievement achievement = available.get(idx);

        try {
            achievementService.grantAchievement(player, achievement);
            achievementDAO.saveGrant(player.getUuid(), achievement.getId());
            playerDAO.update(player);   // XP changed
            System.out.printf("[saved] %s unlocked \"%s\" (+%d XP, total XP: %d).%n",
                    player.getUsername(), achievement.getTitle(), achievement.getXpReward(), player.getXp());
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewPlayerAchievements() {
        Player player = pickPlayer("Select player:");
        if (player == null) return;

        Set<Achievement> unlocked = state.getPlayerAchievements().get(player);
        System.out.println("\n" + player.getUsername() + " - XP: " + player.getXp());
        if (unlocked == null || unlocked.isEmpty()) {
            System.out.println("  No achievements unlocked yet.");
            return;
        }
        unlocked.stream()
                .sorted(Comparator.comparing(Achievement::getId))
                .forEach(a -> System.out.printf("  [x] %-25s +%d XP  - %s%n",
                        a.getTitle(), a.getXpReward(), a.getDescription()));
    }

    private void registerAchievement() {
        String id = readLine("Achievement ID (e.g. first_win): ");
        if (id.isEmpty()) return;

        if (state.getAchievements().containsKey(id)) {
            System.out.println("An achievement with that ID already exists.");
            return;
        }

        String title       = readLine("Title: ");
        String description = readLine("Description: ");
        int xp             = readInt("XP reward: ");
        if (xp < 0) {
            System.out.println("XP must be non-negative.");
            return;
        }

        Achievement parent = null;
        if (!state.getAchievements().isEmpty()) {
            System.out.println("Requires a parent achievement? (y/n)");
            if (readLine("> ").equalsIgnoreCase("y")) {
                parent = pickAchievement("Select parent achievement:");
            }
        }

        Achievement achievement = new Achievement(id, title, description, xp, parent);
        achievementService.registerAchievement(achievement);
        achievementDAO.save(achievement);
        System.out.printf("[saved] Achievement registered: \"%s\" (+%d XP).%n", title, xp);
    }

    // reports

    private void reportsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Reports ---");
            System.out.println("1. Top N by playtime");
            System.out.println("2. Top N richest players");
            System.out.println("3. Players by rank");
            System.out.println("4. World stats");
            System.out.println("0. Back");
            switch (readInt("> ")) {
                case 1 -> reportTopPlaytime();
                case 2 -> reportRichest();
                case 3 -> reportByRank();
                case 4 -> reportWorldStats();
                case 0 -> back = true;
                default -> System.out.println("Unknown option.");
            }
        }
    }

    private void reportTopPlaytime() {
        int n = readInt("How many players? ");
        if (n <= 0)
            return;
        List<Player> top = playerService.topNByPlaytime(n);
        if (top.isEmpty()) {
            System.out.println("No players.");
            return;
        }
        System.out.println("\n--- Top " + n + " by Playtime ---");
        for (int i = 0; i < top.size(); i++) {
            Player p = top.get(i);
            System.out.printf("  %2d. %-20s %d min%n", i + 1, p.getUsername(), p.getPlaytimeMinutes());
        }
    }

    private void reportRichest() {
        int n = readInt("How many players? ");
        if (n <= 0)
            return;
        List<Player> rich = economyService.topRichestPlayers(n);
        System.out.println("\n--- Top " + n + " Richest ---");
        for (int i = 0; i < rich.size(); i++) {
            Player p = rich.get(i);
            System.out.printf("  %2d. %-20s %.2f coins%n", i + 1, p.getUsername(), p.getBalance());
        }
    }

    private void reportByRank() {
        System.out.println("\n--- Players by Rank ---");
        if (state.getPlayersByRank().isEmpty()) {
            System.out.println("No players.");
            return;
        }
        for (Map.Entry<Rank, Set<Player>> entry : state.getPlayersByRank().entrySet()) {
            if (entry.getValue().isEmpty())
                continue;
            String names = entry.getValue().stream()
                    .map(Player::getUsername)
                    .sorted()
                    .collect(Collectors.joining(", "));
            System.out.printf("  %-8s %s %s%n", entry.getKey().getName(), entry.getKey().getPrefix(), names);
        }
    }

    private void reportWorldStats() {
        System.out.println("\n--- World Stats ---");
        if (state.getWorlds().isEmpty()) {
            System.out.println("No worlds.");
            return;
        }
        worldService.worldStats().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> System.out.printf("  %-20s %d online%n", e.getKey(), e.getValue()));
    }

    // pickers (shared helpers)

    private Player pickPlayer(String prompt) {
        if (state.getPlayers().isEmpty()) {
            System.out.println("No players registered.");
            return null;
        }
        List<Player> list = new ArrayList<>(state.getPlayers().values());
        list.sort(Comparator.comparing(Player::getUsername, String.CASE_INSENSITIVE_ORDER));
        System.out.println(prompt);
        for (int i = 0; i < list.size(); i++) {
            System.out.printf("  %d. %-20s [%s]%n", i + 1, list.get(i).getUsername(), list.get(i).getRank().getName());
        }
        int idx = readInt("> ") - 1;
        if (idx < 0 || idx >= list.size()) {
            System.out.println("Invalid selection.");
            return null;
        }
        return list.get(idx);
    }

    private World pickWorld(String prompt) {
        if (state.getWorlds().isEmpty()) {
            System.out.println("No worlds available.");
            return null;
        }
        List<World> list = new ArrayList<>(state.getWorlds().values());
        list.sort(Comparator.comparing(World::getName));
        System.out.println(prompt);
        for (int i = 0; i < list.size(); i++) {
            System.out.printf("  %d. %-20s (%s, %s, %d/%d online)%n",
                    i + 1, list.get(i).getName(), list.get(i).getWorldType(),
                    list.get(i).getDifficulty(), list.get(i).getOnlinePlayers().size(), list.get(i).getMaxPlayers());
        }
        int idx = readInt("> ") - 1;
        if (idx < 0 || idx >= list.size()) {
            System.out.println("Invalid selection.");
            return null;
        }
        return list.get(idx);
    }

    private Rank pickRank(String prompt) {
        List<Rank> ranks = rankDAO.findAll();
        if (ranks.isEmpty()) {
            System.out.println("No ranks available.");
            return null;
        }
        System.out.println(prompt);
        for (int i = 0; i < ranks.size(); i++) {
            System.out.printf("  %d. %-8s (weight=%d)%n", i + 1, ranks.get(i).getName(), ranks.get(i).getWeight());
        }
        int idx = readInt("> ") - 1;
        if (idx < 0 || idx >= ranks.size()) {
            System.out.println("Invalid selection.");
            return null;
        }
        return ranks.get(idx);
    }

    private Achievement pickAchievement(String prompt) {
        if (state.getAchievements().isEmpty()) {
            System.out.println("No achievements in catalog.");
            return null;
        }
        List<Achievement> list = state.getAchievements().values().stream()
                .sorted(Comparator.comparing(Achievement::getId))
                .collect(Collectors.toList());
        System.out.println(prompt);
        for (int i = 0; i < list.size(); i++) {
            System.out.printf("  %d. %-20s (%s)%n", i + 1, list.get(i).getTitle(), list.get(i).getId());
        }
        int idx = readInt("> ") - 1;
        if (idx < 0 || idx >= list.size()) {
            System.out.println("Invalid selection.");
            return null;
        }
        return list.get(idx);
    }

    private Plugin pickPlugin(String prompt) {
        if (state.getPlugins().isEmpty()) {
            System.out.println("No plugins installed.");
            return null;
        }
        List<Plugin> list = new ArrayList<>(state.getPlugins());
        list.sort(Comparator.comparing(Plugin::getName));
        System.out.println(prompt);
        for (int i = 0; i < list.size(); i++) {
            System.out.printf("  %d. %-25s v%-10s [%s]%n",
                    i + 1, list.get(i).getName(), list.get(i).getVersion(),
                    list.get(i).isEnabled() ? "enabled" : "disabled");
        }
        int idx = readInt("> ") - 1;
        if (idx < 0 || idx >= list.size()) {
            System.out.println("Invalid selection.");
            return null;
        }
        return list.get(idx);
    }

    // i/o helpers

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int readInt(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
