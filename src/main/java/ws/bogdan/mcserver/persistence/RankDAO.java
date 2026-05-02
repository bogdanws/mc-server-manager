package ws.bogdan.mcserver.persistence;

import ws.bogdan.mcserver.model.Rank;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class RankDAO extends GenericDAO<Rank, String> {
    private static RankDAO instance;

    private RankDAO() {
        super();
    }

    public static RankDAO getInstance() {
        if (instance == null) {
            instance = new RankDAO();
        }
        return instance;
    }

    @Override
    public Rank save(Rank rank) {
        String sql = "INSERT OR REPLACE INTO ranks(name, prefix, color, permissions, weight) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = prepare(sql)) {
            ps.setString(1, rank.getName());
            ps.setString(2, rank.getPrefix());
            ps.setString(3, rank.getColor());
            ps.setString(4, String.join(";", rank.getPermissions()));
            ps.setInt(5, rank.getWeight());
            ps.executeUpdate();
            return rank;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save rank: " + rank.getName(), e);
        }
    }

    @Override
    public Optional<Rank> findById(String name) {
        String sql = "SELECT * FROM ranks WHERE name = ?";
        try (PreparedStatement ps = prepare(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find rank: " + name, e);
        }
    }

    @Override
    public List<Rank> findAll() {
        String sql = "SELECT * FROM ranks ORDER BY weight DESC";
        List<Rank> result = new ArrayList<>();
        try (PreparedStatement ps = prepare(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to list ranks", e);
        }
        return result;
    }

    @Override
    public Rank update(Rank rank) {
        String sql = "UPDATE ranks SET prefix = ?, color = ?, permissions = ?, weight = ? WHERE name = ?";
        try (PreparedStatement ps = prepare(sql)) {
            ps.setString(1, rank.getPrefix());
            ps.setString(2, rank.getColor());
            ps.setString(3, String.join(";", rank.getPermissions()));
            ps.setInt(4, rank.getWeight());
            ps.setString(5, rank.getName());
            ps.executeUpdate();
            return rank;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update rank: " + rank.getName(), e);
        }
    }

    @Override
    public boolean delete(String name) {
        String sql = "DELETE FROM ranks WHERE name = ?";
        try (PreparedStatement ps = prepare(sql)) {
            ps.setString(1, name);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete rank: " + name, e);
        }
    }

    private Rank mapRow(ResultSet rs) throws SQLException {
        String permsRaw = rs.getString("permissions");
        Set<String> permissions = new LinkedHashSet<>();
        if (permsRaw != null && !permsRaw.isEmpty()) {
            for (String perm : permsRaw.split(";")) {
                if (!perm.isEmpty()) {
                    permissions.add(perm);
                }
            }
        }
        return new Rank(
                rs.getString("name"),
                rs.getString("prefix"),
                rs.getString("color"),
                new HashSet<>(permissions),
                rs.getInt("weight"));
    }
}
