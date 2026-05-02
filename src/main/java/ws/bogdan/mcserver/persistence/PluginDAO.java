package ws.bogdan.mcserver.persistence;

import ws.bogdan.mcserver.model.Plugin;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class PluginDAO extends GenericDAO<Plugin, String> {
    private static PluginDAO instance;

    private PluginDAO() {
        super();
    }

    public static PluginDAO getInstance() {
        if (instance == null) {
            instance = new PluginDAO();
        }
        return instance;
    }

    @Override
    public Plugin save(Plugin p) {
        String sql = "INSERT OR REPLACE INTO plugins(name, version, author, enabled) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = prepare(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getVersion());
            ps.setString(3, p.getAuthor());
            ps.setInt(4, p.isEnabled() ? 1 : 0);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save plugin: " + p.getName(), e);
        }
        saveDependencies(p);
        return p;
    }

    private void saveDependencies(Plugin p) {
        try (PreparedStatement del = prepare("DELETE FROM plugin_dependencies WHERE plugin_name = ?")) {
            del.setString(1, p.getName());
            del.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to clear dependencies for: " + p.getName(), e);
        }
        String ins = "INSERT OR IGNORE INTO plugin_dependencies(plugin_name, depends_on) VALUES (?, ?)";
        try (PreparedStatement ps = prepare(ins)) {
            for (Plugin dep : p.getDependencies()) {
                ps.setString(1, p.getName());
                ps.setString(2, dep.getName());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save dependencies for: " + p.getName(), e);
        }
    }

    @Override
    public Optional<Plugin> findById(String name) {
        Map<String, Plugin> loaded = loadAllFlat();
        Plugin p = loaded.get(name);
        if (p == null) {
            return Optional.empty();
        }
        linkDependencies(loaded);
        return Optional.of(p);
    }

    @Override
    public List<Plugin> findAll() {
        Map<String, Plugin> loaded = loadAllFlat();
        linkDependencies(loaded);
        return new ArrayList<>(loaded.values());
    }

    private Map<String, Plugin> loadAllFlat() {
        Map<String, Plugin> result = new HashMap<>();
        String sql = "SELECT * FROM plugins";
        try (PreparedStatement ps = prepare(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Plugin p = new Plugin(rs.getString("name"), rs.getString("version"), rs.getString("author"));
                p.setEnabled(rs.getInt("enabled") == 1);
                result.put(p.getName(), p);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to list plugins", e);
        }
        return result;
    }

    private void linkDependencies(Map<String, Plugin> loaded) {
        String sql = "SELECT plugin_name, depends_on FROM plugin_dependencies";
        try (PreparedStatement ps = prepare(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Plugin owner = loaded.get(rs.getString("plugin_name"));
                Plugin dep = loaded.get(rs.getString("depends_on"));
                if (owner != null && dep != null && !owner.getDependencies().contains(dep)) {
                    owner.addDependency(dep);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load plugin dependencies", e);
        }
    }

    @Override
    public Plugin update(Plugin p) {
        String sql = "UPDATE plugins SET version = ?, author = ?, enabled = ? WHERE name = ?";
        try (PreparedStatement ps = prepare(sql)) {
            ps.setString(1, p.getVersion());
            ps.setString(2, p.getAuthor());
            ps.setInt(3, p.isEnabled() ? 1 : 0);
            ps.setString(4, p.getName());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update plugin: " + p.getName(), e);
        }
        saveDependencies(p);
        return p;
    }

    @Override
    public boolean delete(String name) {
        String sql = "DELETE FROM plugins WHERE name = ?";
        try (PreparedStatement ps = prepare(sql)) {
            ps.setString(1, name);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete plugin: " + name, e);
        }
    }
}
