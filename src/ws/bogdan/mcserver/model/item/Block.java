package ws.bogdan.mcserver.model.item;

import ws.bogdan.mcserver.model.enums.Rarity;

public abstract class Block extends Item {
    protected int hardness;
    protected boolean stackable;

    protected Block(String itemId, String displayName, int maxStackSize, Rarity rarity,
            int hardness, boolean stackable) {
        super(itemId, displayName, maxStackSize, rarity);
        this.hardness = hardness;
        this.stackable = stackable;
    }

    public int getHardness() {
        return hardness;
    }

    public boolean isStackable() {
        return stackable;
    }
}
