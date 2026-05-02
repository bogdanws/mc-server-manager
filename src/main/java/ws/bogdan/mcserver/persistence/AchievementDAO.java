package ws.bogdan.mcserver.persistence;

import ws.bogdan.mcserver.model.Achievement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class AchievementDAO extends GenericDAO<Achievement, String> {
    private static AchievementDAO instance;

    private AchievementDAO() {
        super();
    }

    public static AchievementDAO getInstance() {
        if (instance == null) {
            instance = new AchievementDAO();
        }
        return instance;
    }

    @Override
    public Achievement save(Achievement a) {
        String sql = "INSERT OR REPLACE INTO achievements(id, title, description, xp_reward, parent_id) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = prepare(sql)) {
            ps.setString(1, a.getId());
            ps.setString(2, a.getTitle());
            ps.setString(3, a.getDescription());
            ps.setInt(4, a.getXpReward());
            ps.setString(5, a.getParentAchievement() != null ? a.getParentAchievement().getId() : null);
            ps.executeUpdate();
            return a;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save achievement: " + a.getId(), e);
        }
    }

    @Override
    public Optional<Achievement> findById(String id) {
        Map<String, Row> rows = loadRows();
        if (!rows.containsKey(id)) {
            return Optional.empty();
        }
        Map<String, Achievement> resolved = new HashMap<>();
        return Optional.of(resolve(id, rows, resolved));
    }

    @Override
    public List<Achievement> findAll() {
        Map<String, Row> rows = loadRows();
        Map<String, Achievement> resolved = new HashMap<>();
        List<Achievement> result = new ArrayList<>();
        for (String id : rows.keySet()) {
            result.add(resolve(id, rows, resolved));
        }
        return result;
    }

    @Override
    public Achievement update(Achievement a) {
        String sql = "UPDATE achievements SET title = ?, description = ?, xp_reward = ?, parent_id = ? WHERE id = ?";
        try (PreparedStatement ps = prepare(sql)) {
            ps.setString(1, a.getTitle());
            ps.setString(2, a.getDescription());
            ps.setInt(3, a.getXpReward());
            ps.setString(4, a.getParentAchievement() != null ? a.getParentAchievement().getId() : null);
            ps.setString(5, a.getId());
            ps.executeUpdate();
            return a;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update achievement: " + a.getId(), e);
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM achievements WHERE id = ?";
        try (PreparedStatement ps = prepare(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete achievement: " + id, e);
        }
    }

    private Map<String, Row> loadRows() {
        Map<String, Row> rows = new HashMap<>();
        String sql = "SELECT * FROM achievements";
        try (PreparedStatement ps = prepare(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.put(rs.getString("id"), new Row(
                        rs.getString("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("xp_reward"),
                        rs.getString("parent_id")));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to list achievements", e);
        }
        return rows;
    }

    // reconstructie recursiva care rezolva intai parintele
    private Achievement resolve(String id, Map<String, Row> rows, Map<String, Achievement> resolved) {
        if (resolved.containsKey(id)) {
            return resolved.get(id);
        }
        Row row = rows.get(id);
        Achievement parent = null;
        if (row.parentId != null && rows.containsKey(row.parentId)) {
            parent = resolve(row.parentId, rows, resolved);
        }
        Achievement a = new Achievement(row.id, row.title, row.description, row.xpReward, parent);
        resolved.put(id, a);
        return a;
    }

    private static final class Row {
        final String id;
        final String title;
        final String description;
        final int xpReward;
        final String parentId;

        Row(String id, String title, String description, int xpReward, String parentId) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.xpReward = xpReward;
            this.parentId = parentId;
        }
    }
}
