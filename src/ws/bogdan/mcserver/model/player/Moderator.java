package ws.bogdan.mcserver.model.player;

import ws.bogdan.mcserver.model.Rank;

public class Moderator extends StaffMember {
    public Moderator(String username, Rank rank, String staffId) {
        super(username, rank, staffId);
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
