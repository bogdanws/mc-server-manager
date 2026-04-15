package ws.bogdan.mcserver.model.player;

import ws.bogdan.mcserver.model.Rank;

public abstract class StaffMember extends Player {
    protected String staffId;

    protected StaffMember(String username, Rank rank, String staffId) {
        super(username, rank);
        this.staffId = staffId;
    }

    public String getStaffId() {
        return staffId;
    }

    public abstract void kick(Player target, String reason);

    public abstract void mute(Player target, int minutes);
}
