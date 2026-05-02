package ws.bogdan.mcserver.model.player;

import ws.bogdan.mcserver.model.Rank;
import java.time.LocalDateTime;
import java.util.UUID;

public class RegularPlayer extends Player {
    public RegularPlayer(String username, Rank rank) {
        super(username, rank);
    }

    public RegularPlayer(UUID uuid, String username, Rank rank, LocalDateTime joinDate) {
        super(uuid, username, rank, joinDate);
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }

    @Override
    public String getRoleLabel() {
        return "REGULAR";
    }
}
