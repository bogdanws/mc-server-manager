package ws.bogdan.mcserver.model;

import ws.bogdan.mcserver.model.item.Item;

public record ItemStack(Item item, int count) {
    public ItemStack {
        if (item == null)
            throw new IllegalArgumentException("item must not be null");
        if (count <= 0 || count > item.getMaxStackSize())
            throw new IllegalArgumentException("invalid count: " + count + " for item " + item.getItemId());
    }
}
