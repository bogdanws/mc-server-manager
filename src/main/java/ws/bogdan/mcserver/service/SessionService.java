package ws.bogdan.mcserver.service;

import ws.bogdan.mcserver.audit.AuditService;
import ws.bogdan.mcserver.model.Session;
import ws.bogdan.mcserver.model.World;
import ws.bogdan.mcserver.model.player.Player;
import java.util.Iterator;

public class SessionService {
    private final ServerState state;
    private final PlayerService playerService;

    public SessionService(ServerState state, PlayerService playerService) {
        this.state = state;
        this.playerService = playerService;
    }

    public Session login(Player p, World w) {
        AuditService.getInstance().logAction("LOGIN_PLAYER",
                "player=" + p.getUsername() + ";uuid=" + p.getUuid() + ";world=" + w.getName());
        w.addPlayer(p);
        p.setCurrentWorld(w);
        Session session = new Session(p, w);
        state.getActiveSessions().add(session);
        return session;
    }

    public void logout(Player p) {
        AuditService.getInstance().logAction("LOGOUT_PLAYER",
                "player=" + p.getUsername() + ";uuid=" + p.getUuid());
        Iterator<Session> it = state.getActiveSessions().iterator();
        while (it.hasNext()) {
            Session session = it.next();
            if (session.getPlayer().equals(p) && session.isActive()) {
                session.close();
                playerService.addPlaytime(p, session.durationMinutes());
                World w = p.getCurrentWorld();
                if (w != null) {
                    w.removePlayer(p);
                    p.setCurrentWorld(null);
                }
                it.remove();
                return;
            }
        }
    }
}
