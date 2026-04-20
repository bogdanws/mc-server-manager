package ws.bogdan.mcserver.model.player;

import ws.bogdan.mcserver.model.Rank;

public class RegularPlayer extends Player {
    public RegularPlayer(String username, Rank rank) {
        super(username, rank);
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
