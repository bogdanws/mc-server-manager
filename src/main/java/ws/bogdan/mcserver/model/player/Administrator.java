package ws.bogdan.mcserver.model.player;

import ws.bogdan.mcserver.model.Rank;

public class Administrator extends StaffMember {
    public Administrator(String username, Rank rank, String staffId) {
        super(username, rank, staffId);
    }

    @Override
    public void kick(Player target, String reason) {
        System.out.println("[ADMIN] " + username + " kicked " + target.getUsername() + ": " + reason);
    }

    @Override
    public void mute(Player target, int minutes) {
        System.out.println("[ADMIN] " + username + " muted " + target.getUsername() + " for " + minutes + " minutes");
    }

    public void ban(Player target, String reason) {
        System.out.println("[ADMIN] " + username + " banned " + target.getUsername() + ": " + reason);
    }

    @Override
    public int getPermissionLevel() {
        return 100;
    }

    @Override
    public String getRoleLabel() {
        return "ADMINISTRATOR";
    }
}
