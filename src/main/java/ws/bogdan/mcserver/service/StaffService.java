package ws.bogdan.mcserver.service;

import ws.bogdan.mcserver.audit.AuditService;
import ws.bogdan.mcserver.model.player.Administrator;
import ws.bogdan.mcserver.model.player.Player;
import ws.bogdan.mcserver.model.player.StaffMember;
import java.util.Set;

public class StaffService {
    private final ServerState state;

    public StaffService(ServerState state) {
        this.state = state;
    }

    public void kickPlayer(StaffMember actor, Player target, String reason) {
        AuditService.getInstance().logAction("KICK_PLAYER");
        actor.kick(target, reason);
        System.out.println("[STAFF] " + target.getUsername() + " was kicked from the server");
    }

    public void banPlayer(Administrator actor, Player target, String reason) {
        AuditService.getInstance().logAction("BAN_PLAYER");
        actor.ban(target, reason);
        state.getPlayers().remove(target.getUuid());
        state.getLeaderboard().remove(target);
        Set<ws.bogdan.mcserver.model.player.Player> bucket = state.getPlayersByRank().get(target.getRank());
        if (bucket != null) {
            bucket.remove(target);
        }
        System.out.println("[STAFF] " + target.getUsername() + " was banned and removed from the server");
    }
}
