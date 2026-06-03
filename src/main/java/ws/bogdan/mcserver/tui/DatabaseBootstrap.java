package ws.bogdan.mcserver.tui;

import ws.bogdan.mcserver.model.Achievement;
import ws.bogdan.mcserver.model.Inventory;
import ws.bogdan.mcserver.model.Plugin;
import ws.bogdan.mcserver.model.Rank;
import ws.bogdan.mcserver.model.World;
import ws.bogdan.mcserver.model.enums.Difficulty;
import ws.bogdan.mcserver.model.enums.Rarity;
import ws.bogdan.mcserver.model.enums.WorldType;
import ws.bogdan.mcserver.model.item.BuildingBlock;
import ws.bogdan.mcserver.model.item.Food;
import ws.bogdan.mcserver.model.item.OreBlock;
import ws.bogdan.mcserver.model.item.Pickaxe;
import ws.bogdan.mcserver.model.item.Potion;
import ws.bogdan.mcserver.model.item.Sword;
import ws.bogdan.mcserver.model.player.Administrator;
import ws.bogdan.mcserver.model.player.Moderator;
import ws.bogdan.mcserver.model.player.Player;
import ws.bogdan.mcserver.model.player.RegularPlayer;
import ws.bogdan.mcserver.model.player.VIPPlayer;
import ws.bogdan.mcserver.persistence.AchievementDAO;
import ws.bogdan.mcserver.persistence.ItemDAO;
import ws.bogdan.mcserver.persistence.PlayerDAO;
import ws.bogdan.mcserver.persistence.PluginDAO;
import ws.bogdan.mcserver.persistence.RankDAO;
import ws.bogdan.mcserver.persistence.WorldDAO;
import ws.bogdan.mcserver.service.ServerState;

import java.util.HashSet;
import java.util.Set;

public class DatabaseBootstrap {

    private final RankDAO rankDAO;
    private final WorldDAO worldDAO;
    private final PlayerDAO playerDAO;
    private final PluginDAO pluginDAO;
    private final ItemDAO itemDAO;
    private final AchievementDAO achievementDAO;

    public DatabaseBootstrap(RankDAO rankDAO, WorldDAO worldDAO, PlayerDAO playerDAO,
            PluginDAO pluginDAO, ItemDAO itemDAO, AchievementDAO achievementDAO) {
        this.rankDAO = rankDAO;
        this.worldDAO = worldDAO;
        this.playerDAO = playerDAO;
        this.pluginDAO = pluginDAO;
        this.itemDAO = itemDAO;
        this.achievementDAO = achievementDAO;
    }

    public void seedIfEmpty() {
        if (rankDAO.findAll().isEmpty()) {
            rankDAO.save(new Rank("GUEST", "[G]", "&7", new HashSet<>(), 1));
            rankDAO.save(new Rank("VIP", "[V]", "&6", new HashSet<>(Set.of("vip.homes", "vip.fly")), 10));
            rankDAO.save(new Rank("MOD", "[M]", "&b", new HashSet<>(Set.of("mod.kick", "mod.mute")), 50));
            rankDAO.save(new Rank("ADMIN", "[A]", "&c", new HashSet<>(Set.of("admin.ban", "admin.op")), 100));
            System.out.println("Seeded default ranks.");
        }
        if (worldDAO.findAll().isEmpty()) {
            worldDAO.save(new World("survival_main", 12345L, WorldType.SURVIVAL, Difficulty.NORMAL, 50, 0, 64, 0));
            worldDAO.save(new World("creative_hub", 67890L, WorldType.CREATIVE, Difficulty.PEACEFUL, 20, 0, 64, 0));
            worldDAO.save(new World("minigames", 11111L, WorldType.ADVENTURE, Difficulty.EASY, 100, 0, 64, 0));
            System.out.println("Seeded default worlds.");
        }
        if (itemDAO.findAll().isEmpty()) {
            itemDAO.save(new Pickaxe("minecraft:diamond_pickaxe", "Diamond Pickaxe", 1, Rarity.RARE, 1561, "DIAMOND"));
            itemDAO.save(new Sword("minecraft:iron_sword", "Iron Sword", 1, Rarity.UNCOMMON, 250, "IRON", 6));
            itemDAO.save(new Food("minecraft:bread", "Bread", 64, Rarity.COMMON, 5, null));
            itemDAO.save(new Potion("minecraft:potion_healing", "Potion of Healing", 1, Rarity.UNCOMMON, 0,
                    "REGENERATION", 30));
            itemDAO.save(new BuildingBlock("minecraft:stone", "Stone", 64, Rarity.COMMON, 1, true));
            itemDAO.save(new OreBlock("minecraft:diamond_ore", "Diamond Ore", 64, Rarity.EPIC, 3, false, "DIAMOND", 7));
            System.out.println("Seeded default items.");
        }
        if (playerDAO.findAll().isEmpty()) {
            Rank guest = rankDAO.findById("GUEST").orElseThrow();
            Rank vip   = rankDAO.findById("VIP").orElseThrow();
            Rank mod   = rankDAO.findById("MOD").orElseThrow();
            Rank admin = rankDAO.findById("ADMIN").orElseThrow();
            playerDAO.save(new RegularPlayer("Alice",   guest));
            playerDAO.save(new RegularPlayer("Bob",     guest));
            playerDAO.save(new RegularPlayer("Charlie", guest));
            playerDAO.save(new VIPPlayer("Diana",       vip,   3));
            playerDAO.save(new Moderator("ModSteve",    mod,   "STAFF-001"));
            playerDAO.save(new Administrator("AdminJoe", admin, "STAFF-002"));
            System.out.println("Seeded default players.");
        }
        if (pluginDAO.findAll().isEmpty()) {
            Plugin core    = new Plugin("CorePlugin",    "1.0.0", "DevTeam");
            Plugin economy = new Plugin("EconomyPlugin", "2.1.0", "DevTeam");
            Plugin shop    = new Plugin("ShopPlugin",    "1.5.0", "DevTeam");
            economy.addDependency(core);
            shop.addDependency(core);
            shop.addDependency(economy);
            core.setEnabled(true);
            economy.setEnabled(true);
            shop.setEnabled(true);
            pluginDAO.save(core);
            pluginDAO.save(economy);
            pluginDAO.save(shop);
            System.out.println("Seeded default plugins.");
        }
        if (achievementDAO.findAll().isEmpty()) {
            Achievement firstLogin = new Achievement("first_login", "First Login", "Log in for the first time", 10,
                    null);
            Achievement firstKill = new Achievement("first_kill", "First Blood", "Kill your first mob", 25, firstLogin);
            Achievement legendary = new Achievement("legendary", "Legendary", "Reach legendary status", 100, null);
            achievementDAO.save(firstLogin);
            achievementDAO.save(firstKill);
            achievementDAO.save(legendary);
            System.out.println("Seeded default achievements.");
        }
    }

    // incarcare date in ServerState
    public void loadIntoState(ServerState state) {
        for (World w : worldDAO.findAll()) {
            state.getWorlds().put(w.getName(), w);
        }

        for (Player p : playerDAO.findAll()) {
            p.setCurrentWorld(null);
            state.getPlayers().put(p.getUuid(), p);
            state.getLeaderboard().add(p);
            state.getPlayersByRank()
                    .computeIfAbsent(p.getRank(), k -> new HashSet<>())
                    .add(p);
            state.getInventories().put(p, new Inventory(p, 36));
        }

        for (Plugin pl : pluginDAO.findAll()) {
            state.getPlugins().add(pl);
        }

        // achievement-urile trebuie incarcate inainte de grants
        for (Achievement a : achievementDAO.findAll()) {
            state.getAchievements().put(a.getId(), a);
        }

        // restaurare achievement-uri
        for (var entry : achievementDAO.loadAllGrants().entrySet()) {
            Player p = state.getPlayers().get(entry.getKey());
            if (p == null) {
                continue;
            }
            Set<Achievement> unlocked = state.getPlayerAchievements()
                    .computeIfAbsent(p, k -> new HashSet<>());
            for (String id : entry.getValue()) {
                Achievement a = state.getAchievements().get(id);
                if (a != null) {
                    unlocked.add(a);
                }
            }
        }
    }
}
