package ws.bogdan.mcserver.persistence;

import ws.bogdan.mcserver.model.enums.Rarity;
import ws.bogdan.mcserver.model.item.Axe;
import ws.bogdan.mcserver.model.item.BuildingBlock;
import ws.bogdan.mcserver.model.item.Food;
import ws.bogdan.mcserver.model.item.Item;
import ws.bogdan.mcserver.model.item.OreBlock;
import ws.bogdan.mcserver.model.item.Pickaxe;
import ws.bogdan.mcserver.model.item.Potion;
import ws.bogdan.mcserver.model.item.Sword;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ItemDAO extends GenericDAO<Item, String> {
    private static ItemDAO instance;

    private ItemDAO() {
        super();
    }

    public static ItemDAO getInstance() {
        if (instance == null) {
            instance = new ItemDAO();
        }
        return instance;
    }

    @Override
    public Item save(Item item) {
        String sql = "INSERT OR REPLACE INTO items"
                + "(item_id, display_name, max_stack_size, rarity, item_type, durability, material, "
                + "damage, hunger_restored, effect, duration_seconds, hardness, stackable, mineral_type, xp_drop) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = prepare(sql)) {
            ps.setString(1, item.getItemId());
            ps.setString(2, item.getDisplayName());
            ps.setInt(3, item.getMaxStackSize());
            ps.setString(4, item.getRarity().name());
            ps.setString(5, itemType(item));

            setNullableInt(ps, 6, item instanceof ws.bogdan.mcserver.model.item.Tool t ? t.getDurability() : null);
            ps.setString(7, item instanceof ws.bogdan.mcserver.model.item.Tool t ? t.getMaterial() : null);
            setNullableInt(ps, 8, item instanceof Sword s ? s.getDamage() : null);
            setNullableInt(ps, 9,
                    item instanceof ws.bogdan.mcserver.model.item.Consumable c ? c.getHungerRestored() : null);
            ps.setString(10, item instanceof ws.bogdan.mcserver.model.item.Consumable c ? c.getEffect() : null);
            setNullableInt(ps, 11, item instanceof Potion p ? p.getDurationSeconds() : null);
            setNullableInt(ps, 12, item instanceof ws.bogdan.mcserver.model.item.Block b ? b.getHardness() : null);
            if (item instanceof ws.bogdan.mcserver.model.item.Block b) {
                ps.setInt(13, b.isStackable() ? 1 : 0);
            } else {
                ps.setNull(13, java.sql.Types.INTEGER);
            }
            ps.setString(14, item instanceof OreBlock o ? o.getMineralType() : null);
            setNullableInt(ps, 15, item instanceof OreBlock o ? o.getXpDrop() : null);

            ps.executeUpdate();
            return item;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save item: " + item.getItemId(), e);
        }
    }

    @Override
    public Optional<Item> findById(String itemId) {
        String sql = "SELECT * FROM items WHERE item_id = ?";
        try (PreparedStatement ps = prepare(sql)) {
            ps.setString(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find item: " + itemId, e);
        }
    }

    @Override
    public List<Item> findAll() {
        String sql = "SELECT * FROM items";
        List<Item> result = new ArrayList<>();
        try (PreparedStatement ps = prepare(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to list items", e);
        }
        return result;
    }

    @Override
    public Item update(Item item) {
        return save(item);
    }

    @Override
    public boolean delete(String itemId) {
        String sql = "DELETE FROM items WHERE item_id = ?";
        try (PreparedStatement ps = prepare(sql)) {
            ps.setString(1, itemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete item: " + itemId, e);
        }
    }

    private String itemType(Item item) {
        if (item instanceof Pickaxe) {
            return "PICKAXE";
        }
        if (item instanceof Sword) {
            return "SWORD";
        }
        if (item instanceof Axe) {
            return "AXE";
        }
        if (item instanceof Food) {
            return "FOOD";
        }
        if (item instanceof Potion) {
            return "POTION";
        }
        if (item instanceof BuildingBlock) {
            return "BUILDING_BLOCK";
        }
        if (item instanceof OreBlock) {
            return "ORE_BLOCK";
        }
        throw new IllegalArgumentException("Unknown item subtype: " + item.getClass().getName());
    }

    private Item mapRow(ResultSet rs) throws SQLException {
        String itemId = rs.getString("item_id");
        String displayName = rs.getString("display_name");
        int maxStack = rs.getInt("max_stack_size");
        Rarity rarity = Rarity.valueOf(rs.getString("rarity"));
        String type = rs.getString("item_type");

        return switch (type) {
            case "PICKAXE" -> new Pickaxe(itemId, displayName, maxStack, rarity,
                    rs.getInt("durability"), rs.getString("material"));
            case "SWORD" -> new Sword(itemId, displayName, maxStack, rarity,
                    rs.getInt("durability"), rs.getString("material"), rs.getInt("damage"));
            case "AXE" -> new Axe(itemId, displayName, maxStack, rarity,
                    rs.getInt("durability"), rs.getString("material"));
            case "FOOD" -> new Food(itemId, displayName, maxStack, rarity,
                    rs.getInt("hunger_restored"), rs.getString("effect"));
            case "POTION" -> new Potion(itemId, displayName, maxStack, rarity,
                    rs.getInt("hunger_restored"), rs.getString("effect"), rs.getInt("duration_seconds"));
            case "BUILDING_BLOCK" -> new BuildingBlock(itemId, displayName, maxStack, rarity,
                    rs.getInt("hardness"), rs.getInt("stackable") == 1);
            case "ORE_BLOCK" -> new OreBlock(itemId, displayName, maxStack, rarity,
                    rs.getInt("hardness"), rs.getInt("stackable") == 1,
                    rs.getString("mineral_type"), rs.getInt("xp_drop"));
            default -> throw new IllegalStateException("Unknown item_type in DB: " + type);
        };
    }

    private void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(index, java.sql.Types.INTEGER);
        } else {
            ps.setInt(index, value);
        }
    }
}
