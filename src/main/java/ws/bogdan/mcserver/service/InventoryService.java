package ws.bogdan.mcserver.service;

import ws.bogdan.mcserver.audit.AuditService;
import ws.bogdan.mcserver.model.Inventory;
import ws.bogdan.mcserver.model.ItemStack;
import ws.bogdan.mcserver.model.item.Item;
import ws.bogdan.mcserver.model.enums.Rarity;
import ws.bogdan.mcserver.model.player.Player;
import java.util.ArrayList;
import java.util.List;

public class InventoryService {
    private final ServerState state;

    public InventoryService(ServerState state) {
        this.state = state;
    }

    public void addItem(Player p, Item item, int count) {
        AuditService.getInstance().logAction("ADD_ITEM");
        Inventory inventory = state.getInventories().get(p);
        if (inventory == null) {
            throw new IllegalStateException("No inventory found for player: " + p.getUsername());
        }
        inventory.addStack(new ItemStack(item, count));
    }

    public List<Item> searchByRarity(Rarity r) {
        AuditService.getInstance().logAction("SEARCH_BY_RARITY");
        List<Item> result = new ArrayList<>();
        for (Inventory inventory : state.getInventories().values()) {
            for (ItemStack stack : inventory.getStacks()) {
                if (stack.item().getRarity() == r) {
                    result.add(stack.item());
                }
            }
        }
        return result;
    }

    public <T extends Item> List<T> searchByType(Class<T> type) {
        AuditService.getInstance().logAction("SEARCH_BY_TYPE");
        List<T> result = new ArrayList<>();
        for (Inventory inventory : state.getInventories().values()) {
            for (ItemStack stack : inventory.getStacks()) {
                if (type.isInstance(stack.item())) {
                    result.add(type.cast(stack.item()));
                }
            }
        }
        return result;
    }
}
