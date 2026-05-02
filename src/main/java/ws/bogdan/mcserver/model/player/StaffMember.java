package ws.bogdan.mcserver.model.player;

import ws.bogdan.mcserver.model.Rank;
import java.time.LocalDateTime;
import java.util.UUID;

public abstract class StaffMember extends Player {
    protected String staffId;

    protected StaffMember(String username, Rank rank, String staffId) {
        super(username, rank);
        this.staffId = staffId;
    }

    protected StaffMember(UUID uuid, String username, Rank rank, LocalDateTime joinDate, String staffId) {
        super(uuid, username, rank, joinDate);
        this.staffId = staffId;
    }

    public String getStaffId() {
        return staffId;
    }

    public abstract void kick(Player target, String reason);

    public abstract void mute(Player target, int minutes);
}
