package ws.bogdan.mcserver.model;

import ws.bogdan.mcserver.exception.WorldFullException;
import ws.bogdan.mcserver.model.enums.Difficulty;
import ws.bogdan.mcserver.model.enums.WorldType;
import ws.bogdan.mcserver.model.player.Player;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class World {
    private String name;
    private long seed;
    private WorldType worldType;
    private Difficulty difficulty;
    private int maxPlayers;
    private int spawnX;
    private int spawnY;
    private int spawnZ;
    private Set<Player> onlinePlayers = new HashSet<>();

    public World(String name, long seed, WorldType worldType, Difficulty difficulty,
            int maxPlayers, int spawnX, int spawnY, int spawnZ) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.seed = seed;
        this.worldType = Objects.requireNonNull(worldType, "worldType must not be null");
        this.difficulty = Objects.requireNonNull(difficulty, "difficulty must not be null");
        this.maxPlayers = maxPlayers;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.spawnZ = spawnZ;
    }

    public String getName() {
        return name;
    }

    public long getSeed() {
        return seed;
    }

    public WorldType getWorldType() {
        return worldType;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getSpawnX() {
        return spawnX;
    }

    public int getSpawnY() {
        return spawnY;
    }

    public int getSpawnZ() {
        return spawnZ;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = Objects.requireNonNull(difficulty);
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public boolean isFull() {
        return onlinePlayers.size() >= maxPlayers;
    }

    public void addPlayer(Player p) {
        if (isFull()) {
            throw new WorldFullException("World '" + name + "' is full (" + maxPlayers + "/" + maxPlayers + ")");
        }
        onlinePlayers.add(p);
    }

    public void removePlayer(Player p) {
        onlinePlayers.remove(p);
    }

    public Set<Player> getOnlinePlayers() {
        return Collections.unmodifiableSet(onlinePlayers);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof World))
            return false;
        World other = (World) o;
        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "World{name='" + name + "', type=" + worldType + ", difficulty=" + difficulty +
                ", players=" + onlinePlayers.size() + "/" + maxPlayers + "}";
    }
}
