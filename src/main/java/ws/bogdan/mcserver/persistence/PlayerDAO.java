package ws.bogdan.mcserver.persistence;

import ws.bogdan.mcserver.model.Rank;
import ws.bogdan.mcserver.model.World;
import ws.bogdan.mcserver.model.player.Administrator;
import ws.bogdan.mcserver.model.player.Moderator;
import ws.bogdan.mcserver.model.player.Player;
import ws.bogdan.mcserver.model.player.RegularPlayer;
import ws.bogdan.mcserver.model.player.StaffMember;
import ws.bogdan.mcserver.model.player.VIPPlayer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class PlayerDAO extends GenericDAO<Player, UUID> {
    private static PlayerDAO instance;

    private PlayerDAO() {
        super();
    }

    public static PlayerDAO getInstance() {
        if (instance == null) {
            instance = new PlayerDAO();
        }
        return instance;
    }

    @Override
    public Player save(Player p) {
        String sql = "INSERT OR REPLACE INTO players"
                + "(uuid, username, join_date, playtime_minutes, xp, balance, player_type, "
                + "rank_name, current_world, extra_homes, staff_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = prepare(sql)) {
            ps.setString(1, p.getUuid().toString());
            ps.setString(2, p.getUsername());
            ps.setString(3, p.getJoinDate().toString());
            ps.setLong(4, p.getPlaytimeMinutes());
            ps.setLong(5, p.getXp());
            ps.setDouble(6, p.getBalance());
            ps.setString(7, playerType(p));
            ps.setString(8, p.getRank() != null ? p.getRank().getName() : null);
            ps.setString(9, p.getCurrentWorld() != null ? p.getCurrentWorld().getName() : null);
            if (p instanceof VIPPlayer vip) {
                ps.setInt(10, vip.getExtraHomes());
            } else {
                ps.setNull(10, java.sql.Types.INTEGER);
            }
            if (p instanceof StaffMember staff) {
                ps.setString(11, staff.getStaffId());
            } else {
                ps.setNull(11, java.sql.Types.VARCHAR);
            }
            ps.executeUpdate();
            return p;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save player: " + p.getUsername(), e);
        }
    }

    @Override
    public Optional<Player> findById(UUID uuid) {
        String sql = "SELECT * FROM players WHERE uuid = ?";
        try (PreparedStatement ps = prepare(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find player: " + uuid, e);
        }
    }

    @Override
    public List<Player> findAll() {
        String sql = "SELECT * FROM players";
        List<Player> result = new ArrayList<>();
        try (PreparedStatement ps = prepare(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to list players", e);
        }
        return result;
    }

    @Override
    public Player update(Player p) {
        String sql = "UPDATE players SET username = ?, playtime_minutes = ?, xp = ?, balance = ?, "
                + "player_type = ?, rank_name = ?, current_world = ?, extra_homes = ?, staff_id = ? "
                + "WHERE uuid = ?";
        try (PreparedStatement ps = prepare(sql)) {
            ps.setString(1, p.getUsername());
            ps.setLong(2, p.getPlaytimeMinutes());
            ps.setLong(3, p.getXp());
            ps.setDouble(4, p.getBalance());
            ps.setString(5, playerType(p));
            ps.setString(6, p.getRank() != null ? p.getRank().getName() : null);
            ps.setString(7, p.getCurrentWorld() != null ? p.getCurrentWorld().getName() : null);
            if (p instanceof VIPPlayer vip) {
                ps.setInt(8, vip.getExtraHomes());
            } else {
                ps.setNull(8, java.sql.Types.INTEGER);
            }
            if (p instanceof StaffMember staff) {
                ps.setString(9, staff.getStaffId());
            } else {
                ps.setNull(9, java.sql.Types.VARCHAR);
            }
            ps.setString(10, p.getUuid().toString());
            ps.executeUpdate();
            return p;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update player: " + p.getUsername(), e);
        }
    }

    @Override
    public boolean delete(UUID uuid) {
        String sql = "DELETE FROM players WHERE uuid = ?";
        try (PreparedStatement ps = prepare(sql)) {
            ps.setString(1, uuid.toString());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete player: " + uuid, e);
        }
    }

    private String playerType(Player p) {
        if (p instanceof Administrator) {
            return "ADMIN";
        }
        if (p instanceof Moderator) {
            return "MOD";
        }
        if (p instanceof VIPPlayer) {
            return "VIP";
        }
        return "REGULAR";
    }

    private Player mapRow(ResultSet rs) throws SQLException {
        UUID uuid = UUID.fromString(rs.getString("uuid"));
        String username = rs.getString("username");
        LocalDateTime joinDate = LocalDateTime.parse(rs.getString("join_date"));
        String type = rs.getString("player_type");

        String rankName = rs.getString("rank_name");
        Rank rank = rankName != null
                ? RankDAO.getInstance().findById(rankName).orElse(null)
                : null;

        Player player;
        switch (type) {
            case "ADMIN" -> player = new Administrator(uuid, username, rank, joinDate, rs.getString("staff_id"));
            case "MOD" -> player = new Moderator(uuid, username, rank, joinDate, rs.getString("staff_id"));
            case "VIP" -> player = new VIPPlayer(uuid, username, rank, joinDate, rs.getInt("extra_homes"));
            default -> player = new RegularPlayer(uuid, username, rank, joinDate);
        }

        player.setPlaytimeMinutes(rs.getLong("playtime_minutes"));
        player.setBalance(rs.getDouble("balance"));
        player.addXp(rs.getLong("xp"));

        String currentWorld = rs.getString("current_world");
        if (currentWorld != null) {
            World w = WorldDAO.getInstance().findById(currentWorld).orElse(null);
            player.setCurrentWorld(w);
        }
        return player;
    }
}
