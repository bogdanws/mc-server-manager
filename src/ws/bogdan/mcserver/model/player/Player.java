package ws.bogdan.mcserver.model.player;

import ws.bogdan.mcserver.exception.InsufficientBalanceException;
import ws.bogdan.mcserver.model.Rank;
import ws.bogdan.mcserver.model.World;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public abstract class Player {
    protected final UUID uuid;
    protected String username;
    protected final LocalDateTime joinDate;
    protected long playtimeMinutes;
    protected long xp;
    protected World currentWorld;
    protected double balance;
    protected Rank rank;

    protected Player(String username, Rank rank) {
        this.uuid = UUID.randomUUID();
        this.username = Objects.requireNonNull(username, "username must not be null");
        this.rank = Objects.requireNonNull(rank, "rank must not be null");
        this.joinDate = LocalDateTime.now();
        this.playtimeMinutes = 0;
        this.balance = 0.0;
        this.currentWorld = null;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public long getPlaytimeMinutes() {
        return playtimeMinutes;
    }

    public long getXp() {
        return xp;
    }

    public void addXp(long amount) {
        this.xp += amount;
    }

    public World getCurrentWorld() {
        return currentWorld;
    }

    public double getBalance() {
        return balance;
    }

    public Rank getRank() {
        return rank;
    }

    public void setUsername(String username) {
        this.username = Objects.requireNonNull(username, "username must not be null");
    }

    public void setCurrentWorld(World currentWorld) {
        this.currentWorld = currentWorld;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setRank(Rank rank) {
        this.rank = Objects.requireNonNull(rank, "rank must not be null");
    }

    public void setPlaytimeMinutes(long playtimeMinutes) {
        this.playtimeMinutes = playtimeMinutes;
    }

    public void addPlaytime(long minutes) {
        this.playtimeMinutes += minutes;
    }

    public void addBalance(double amount) {
        double result = this.balance + amount;
        if (result < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance: has " + this.balance + ", needs " + (-amount));
        }
        this.balance = result;
    }

    public abstract int getPermissionLevel();

    public abstract String getRoleLabel();

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Player))
            return false;
        Player other = (Player) o;
        return uuid.equals(other.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return "Player{username='" + username + "', uuid=" + uuid +
                ", rank=" + rank.getName() + ", role=" + getRoleLabel() + "}";
    }
}
