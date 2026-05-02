package ws.bogdan.mcserver.model.player;

import ws.bogdan.mcserver.model.Rank;
import java.time.LocalDateTime;
import java.util.UUID;

public class Moderator extends StaffMember {
    public Moderator(String username, Rank rank, String staffId) {
        super(username, rank, staffId);
    }

    public Moderator(UUID uuid, String username, Rank rank, LocalDateTime joinDate, String staffId) {
        super(uuid, username, rank, joinDate, staffId);
    }

    @Override
    public void kick(Player target, String reason) {
        System.out.println("[MOD] " + username + " kicked " + target.getUsername() + ": " + reason);
    }

    @Override
    public void mute(Player target, int minutes) {
        System.out.println("[MOD] " + username + " muted " + target.getUsername() + " for " + minutes + " minutes");
    }

    @Override
    public int getPermissionLevel() {
        return 50;
    }

    @Override
    public String getRoleLabel() {
        return "MODERATOR";
    }
}
