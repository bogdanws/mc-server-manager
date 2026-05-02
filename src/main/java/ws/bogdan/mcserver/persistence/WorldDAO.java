package ws.bogdan.mcserver.persistence;

import ws.bogdan.mcserver.model.World;
import ws.bogdan.mcserver.model.enums.Difficulty;
import ws.bogdan.mcserver.model.enums.WorldType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class WorldDAO extends GenericDAO<World, String> {
    private static WorldDAO instance;

    private WorldDAO() {
        super();
    }

    public static WorldDAO getInstance() {
        if (instance == null) {
            instance = new WorldDAO();
        }
        return instance;
    }

    @Override
    public World save(World w) {
        String sql = "INSERT OR REPLACE INTO worlds"
                + "(name, seed, world_type, difficulty, max_players, spawn_x, spawn_y, spawn_z) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = prepare(sql)) {
            bind(ps, w);
            ps.executeUpdate();
            return w;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save world: " + w.getName(), e);
        }
    }

    @Override
    public Optional<World> findById(String name) {
        String sql = "SELECT * FROM worlds WHERE name = ?";
        try (PreparedStatement ps = prepare(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find world: " + name, e);
        }
    }

    @Override
    public List<World> findAll() {
        String sql = "SELECT * FROM worlds";
        List<World> result = new ArrayList<>();
        try (PreparedStatement ps = prepare(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to list worlds", e);
        }
        return result;
    }

    @Override
    public World update(World w) {
        String sql = "UPDATE worlds SET seed = ?, world_type = ?, difficulty = ?, max_players = ?, "
                + "spawn_x = ?, spawn_y = ?, spawn_z = ? WHERE name = ?";
        try (PreparedStatement ps = prepare(sql)) {
            ps.setLong(1, w.getSeed());
            ps.setString(2, w.getWorldType().name());
            ps.setString(3, w.getDifficulty().name());
            ps.setInt(4, w.getMaxPlayers());
            ps.setInt(5, w.getSpawnX());
            ps.setInt(6, w.getSpawnY());
            ps.setInt(7, w.getSpawnZ());
            ps.setString(8, w.getName());
            ps.executeUpdate();
            return w;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update world: " + w.getName(), e);
        }
    }

    @Override
    public boolean delete(String name) {
        String sql = "DELETE FROM worlds WHERE name = ?";
        try (PreparedStatement ps = prepare(sql)) {
            ps.setString(1, name);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete world: " + name, e);
        }
    }

    private void bind(PreparedStatement ps, World w) throws SQLException {
        ps.setString(1, w.getName());
        ps.setLong(2, w.getSeed());
        ps.setString(3, w.getWorldType().name());
        ps.setString(4, w.getDifficulty().name());
        ps.setInt(5, w.getMaxPlayers());
        ps.setInt(6, w.getSpawnX());
        ps.setInt(7, w.getSpawnY());
        ps.setInt(8, w.getSpawnZ());
    }

    private World mapRow(ResultSet rs) throws SQLException {
        return new World(
                rs.getString("name"),
                rs.getLong("seed"),
                WorldType.valueOf(rs.getString("world_type")),
                Difficulty.valueOf(rs.getString("difficulty")),
                rs.getInt("max_players"),
                rs.getInt("spawn_x"),
                rs.getInt("spawn_y"),
                rs.getInt("spawn_z"));
    }
}
