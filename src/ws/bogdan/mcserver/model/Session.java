package ws.bogdan.mcserver.model;

import ws.bogdan.mcserver.model.player.Player;
import java.time.Duration;
import java.time.LocalDateTime;

public class Session {
    private final Player player;
    private final LocalDateTime loginTime;
    private LocalDateTime logoutTime;
    private World worldVisited;
    private int deaths;
    private int mobsKilled;

    public Session(Player player, World world) {
        this.player = player;
        this.worldVisited = world;
        this.loginTime = LocalDateTime.now();
        this.deaths = 0;
        this.mobsKilled = 0;
    }

    public Player getPlayer() {
        return player;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public LocalDateTime getLogoutTime() {
        return logoutTime;
    }

    public World getWorldVisited() {
        return worldVisited;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getMobsKilled() {
        return mobsKilled;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void setMobsKilled(int mobsKilled) {
        this.mobsKilled = mobsKilled;
    }

    public void close() {
        this.logoutTime = LocalDateTime.now();
    }

    public boolean isActive() {
        return logoutTime == null;
    }

    public long durationMinutes() {
        LocalDateTime end = (logoutTime != null) ? logoutTime : LocalDateTime.now();
        return Duration.between(loginTime, end).toMinutes();
    }
}
