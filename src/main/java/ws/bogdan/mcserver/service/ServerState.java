package ws.bogdan.mcserver.service;

import ws.bogdan.mcserver.model.Achievement;
import ws.bogdan.mcserver.model.Inventory;
import ws.bogdan.mcserver.model.Plugin;
import ws.bogdan.mcserver.model.Rank;
import ws.bogdan.mcserver.model.Session;
import ws.bogdan.mcserver.model.Transaction;
import ws.bogdan.mcserver.model.World;
import ws.bogdan.mcserver.model.player.Player;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

public class ServerState {
    private final Map<UUID, Player> players = new HashMap<>();
    private final TreeSet<Player> leaderboard = new TreeSet<>(
            Comparator.comparingLong(Player::getPlaytimeMinutes).reversed()
                    .thenComparing(Player::getUsername));
    private final Map<String, World> worlds = new HashMap<>();
    private final List<Plugin> plugins = new ArrayList<>();
    private final Map<Player, Inventory> inventories = new HashMap<>();
    private final TreeMap<Rank, Set<Player>> playersByRank = new TreeMap<>();
    private final Map<String, Achievement> achievements = new HashMap<>();
    private final Map<Player, Set<Achievement>> playerAchievements = new HashMap<>();
    private final List<Transaction> transactionHistory = new ArrayList<>();
    private final List<Session> activeSessions = new ArrayList<>();

    public Map<UUID, Player> getPlayers() {
        return players;
    }

    public TreeSet<Player> getLeaderboard() {
        return leaderboard;
    }

    public Map<String, World> getWorlds() {
        return worlds;
    }

    public List<Plugin> getPlugins() {
        return plugins;
    }

    public Map<Player, Inventory> getInventories() {
        return inventories;
    }

    public TreeMap<Rank, Set<Player>> getPlayersByRank() {
        return playersByRank;
    }

    public Map<String, Achievement> getAchievements() {
        return achievements;
    }

    public Map<Player, Set<Achievement>> getPlayerAchievements() {
        return playerAchievements;
    }

    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    public List<Session> getActiveSessions() {
        return activeSessions;
    }
}
