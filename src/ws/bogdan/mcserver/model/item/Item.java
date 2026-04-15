package ws.bogdan.mcserver.model.item;

import ws.bogdan.mcserver.model.enums.Rarity;
import java.util.Objects;

public abstract class Item {
    protected final String itemId;
    protected String displayName;
    protected int maxStackSize;
    protected Rarity rarity;

    protected Item(String itemId, String displayName, int maxStackSize, Rarity rarity) {
        this.itemId = Objects.requireNonNull(itemId, "itemId must not be null");
        this.displayName = Objects.requireNonNull(displayName, "displayName must not be null");
        this.maxStackSize = maxStackSize;
        this.rarity = Objects.requireNonNull(rarity, "rarity must not be null");
    }

    public String getItemId() {
        return itemId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public void setDisplayName(String displayName) {
        this.displayName = Objects.requireNonNull(displayName, "displayName must not be null");
    }

    public abstract String describe();

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Item))
            return false;
        Item other = (Item) o;
        return itemId.equals(other.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId);
    }

    @Override
    public String toString() {
        return "Item{itemId='" + itemId + "', displayName='" + displayName +
                "', maxStackSize=" + maxStackSize + ", rarity=" + rarity + "}";
    }
}
