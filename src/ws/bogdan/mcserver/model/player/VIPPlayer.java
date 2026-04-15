package ws.bogdan.mcserver.model.player;

import ws.bogdan.mcserver.model.Rank;

public class VIPPlayer extends Player {
    private int extraHomes;

    public VIPPlayer(String username, Rank rank, int extraHomes) {
        super(username, rank);
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
