package ws.bogdan.mcserver.service;

import ws.bogdan.mcserver.audit.AuditService;
import ws.bogdan.mcserver.exception.PlayerNotFoundException;
import ws.bogdan.mcserver.model.World;
import ws.bogdan.mcserver.model.player.Player;
import java.util.HashMap;
import java.util.Map;

public class WorldService {
    private final ServerState state;

    public WorldService(ServerState state) {
        this.state = state;
    }

    public World createWorld(World w) {
        AuditService.getInstance().logAction("CREATE_WORLD");
        state.getWorlds().put(w.getName(), w);
        return w;
    }

    public void teleportPlayer(Player p, String worldName) {
        AuditService.getInstance().logAction("TELEPORT_PLAYER");
        World destination = state.getWorlds().get(worldName);
        if (destination == null) {
            throw new PlayerNotFoundException("World not found: " + worldName);
        }
        World current = p.getCurrentWorld();
        if (current != null) {
            current.removePlayer(p);
        }
        destination.addPlayer(p);
        p.setCurrentWorld(destination);
    }

    public Map<String, Integer> worldStats() {
        AuditService.getInstance().logAction("WORLD_STATS");
        Map<String, Integer> stats = new HashMap<>();
        for (World w : state.getWorlds().values()) {
            stats.put(w.getName(), w.getOnlinePlayers().size());
        }
        return stats;
    }
}
