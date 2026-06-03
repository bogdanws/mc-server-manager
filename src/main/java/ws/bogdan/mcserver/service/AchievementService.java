package ws.bogdan.mcserver.service;

import ws.bogdan.mcserver.audit.AuditService;
import ws.bogdan.mcserver.model.Achievement;
import ws.bogdan.mcserver.model.player.Player;
import java.util.HashSet;
import java.util.Set;

public class AchievementService {
    private final ServerState state;

    public AchievementService(ServerState state) {
        this.state = state;
    }

    public void registerAchievement(Achievement a) {
        AuditService.getInstance().logAction("REGISTER_ACHIEVEMENT");
        state.getAchievements().put(a.getId(), a);
    }

    public void grantAchievement(Player p, Achievement a) {
        AuditService.getInstance().logAction("GRANT_ACHIEVEMENT");
        if (a.getParentAchievement() != null) {
            Set<Achievement> playerAchievements = state.getPlayerAchievements().get(p);
            if (playerAchievements == null || !playerAchievements.contains(a.getParentAchievement())) {
                throw new IllegalStateException(
                        "Parent achievement '" + a.getParentAchievement().getTitle() + "' not yet unlocked");
            }
        }
        state.getPlayerAchievements()
                .computeIfAbsent(p, k -> new HashSet<>())
                .add(a);
        p.addXp(a.getXpReward());
        System.out
                .println(p.getUsername() + " unlocked achievement: " + a.getTitle() + " (+" + a.getXpReward() + " XP)");
    }
}
