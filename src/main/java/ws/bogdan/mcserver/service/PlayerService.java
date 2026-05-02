package ws.bogdan.mcserver.service;

import ws.bogdan.mcserver.audit.AuditService;
import ws.bogdan.mcserver.exception.PermissionDeniedException;
import ws.bogdan.mcserver.exception.PlayerNotFoundException;
import ws.bogdan.mcserver.model.Inventory;
import ws.bogdan.mcserver.model.Rank;
import ws.bogdan.mcserver.model.player.Player;
import ws.bogdan.mcserver.model.player.StaffMember;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PlayerService {
    private final ServerState state;

    public PlayerService(ServerState state) {
        this.state = state;
    }

    public Player addPlayer(Player p) {
        AuditService.getInstance().logAction("ADD_PLAYER");
        state.getPlayers().put(p.getUuid(), p);
        state.getLeaderboard().add(p);
        state.getPlayersByRank()
                .computeIfAbsent(p.getRank(), k -> new HashSet<>())
                .add(p);
        state.getInventories().put(p, new Inventory(p, 36));
        return p;
    }

    public Player findByUuid(UUID uuid) {
        Player p = state.getPlayers().get(uuid);
        if (p == null) {
            throw new PlayerNotFoundException("No player with UUID: " + uuid);
        }
        return p;
    }

    public Player findByUsername(String name) {
        return state.getPlayers().values().stream()
                .filter(p -> p.getUsername().equals(name))
                .findFirst()
                .orElseThrow(() -> new PlayerNotFoundException("No player with username: " + name));
    }

    public void addPlaytime(Player p, long minutes) {
        AuditService.getInstance().logAction("ADD_PLAYTIME");
        state.getLeaderboard().remove(p);
        p.addPlaytime(minutes);
        state.getLeaderboard().add(p);
    }

    public void promoteTo(StaffMember actor, Player target, Rank newRank) {
        AuditService.getInstance().logAction("PROMOTE_PLAYER");
        if (actor.getPermissionLevel() < 50) {
            throw new PermissionDeniedException(
                    actor.getUsername() + " does not have permission to promote players");
        }
        Rank oldRank = target.getRank();
        Set<Player> oldBucket = state.getPlayersByRank().get(oldRank);
        if (oldBucket != null) {
            oldBucket.remove(target);
        }
        target.setRank(newRank);
        state.getPlayersByRank()
                .computeIfAbsent(newRank, k -> new HashSet<>())
                .add(target);
    }

    public List<Player> topNByPlaytime(int n) {
        AuditService.getInstance().logAction("TOP_N_PLAYTIME");
        List<Player> result = new ArrayList<>();
        for (Player p : state.getLeaderboard()) {
            if (result.size() >= n)
                break;
            result.add(p);
        }
        return result;
    }
}
