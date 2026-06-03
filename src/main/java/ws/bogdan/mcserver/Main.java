package ws.bogdan.mcserver;

import ws.bogdan.mcserver.persistence.AchievementDAO;
import ws.bogdan.mcserver.persistence.DatabaseConnection;
import ws.bogdan.mcserver.persistence.ItemDAO;
import ws.bogdan.mcserver.persistence.PlayerDAO;
import ws.bogdan.mcserver.persistence.PluginDAO;
import ws.bogdan.mcserver.persistence.RankDAO;
import ws.bogdan.mcserver.persistence.WorldDAO;
import ws.bogdan.mcserver.service.AchievementService;
import ws.bogdan.mcserver.service.EconomyService;
import ws.bogdan.mcserver.service.InventoryService;
import ws.bogdan.mcserver.service.PlayerService;
import ws.bogdan.mcserver.service.PluginService;
import ws.bogdan.mcserver.service.ServerState;
import ws.bogdan.mcserver.service.SessionService;
import ws.bogdan.mcserver.service.StaffService;
import ws.bogdan.mcserver.service.WorldService;
import ws.bogdan.mcserver.tui.ConsoleApp;
import ws.bogdan.mcserver.tui.DatabaseBootstrap;

public class Main {

    public static void main(String[] args) {
        // conectare la baza de date
        DatabaseConnection db = DatabaseConnection.getInstance();
        db.runSchema();

        // DAO
        RankDAO rankDAO = RankDAO.getInstance();
        WorldDAO worldDAO = WorldDAO.getInstance();
        PlayerDAO playerDAO = PlayerDAO.getInstance();
        ItemDAO itemDAO = ItemDAO.getInstance();
        PluginDAO pluginDAO = PluginDAO.getInstance();
        AchievementDAO achDAO = AchievementDAO.getInstance();

        // scriere date default daca baza de date e goala
        DatabaseBootstrap bootstrap = new DatabaseBootstrap(
                rankDAO, worldDAO, playerDAO, pluginDAO, itemDAO, achDAO);
        bootstrap.seedIfEmpty();

        ServerState state = new ServerState();
        bootstrap.loadIntoState(state);

        // servicii
        PlayerService playerService = new PlayerService(state);
        WorldService worldService = new WorldService(state);
        InventoryService inventoryService = new InventoryService(state);
        EconomyService economyService = new EconomyService(state);
        AchievementService achievementService = new AchievementService(state);
        PluginService pluginService = new PluginService(state);
        SessionService sessionService = new SessionService(state, playerService);
        StaffService staffService = new StaffService(state);

        // TUI
        ConsoleApp app = new ConsoleApp(
                state,
                playerService, worldService, pluginService, sessionService, economyService,
                achievementService,
                playerDAO, worldDAO, pluginDAO, rankDAO, achDAO);
        app.run();

        // inchidere conexiune
        db.close();
    }
}
