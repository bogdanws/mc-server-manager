package ws.bogdan.mcserver.model.player;

import ws.bogdan.mcserver.model.Rank;
import java.time.LocalDateTime;
import java.util.UUID;

public class VIPPlayer extends Player {
    private int extraHomes;

    public VIPPlayer(String username, Rank rank, int extraHomes) {
        super(username, rank);
        this.extraHomes = extraHomes;
    }

    public VIPPlayer(UUID uuid, String username, Rank rank, LocalDateTime joinDate, int extraHomes) {
        super(uuid, username, rank, joinDate);
        this.extraHomes = extraHomes;
    }

    public int getExtraHomes() {
        return extraHomes;
    }

    @Override
    public void addBalance(double amount) {
        if (amount > 0) {
            super.addBalance(amount * 1.10);
        } else {
            super.addBalance(amount);
        }
    }

    @Override
    public int getPermissionLevel() {
        return 10;
    }

    @Override
    public String getRoleLabel() {
        return "VIP";
    }
}
