package ws.bogdan.mcserver.model.item;

import ws.bogdan.mcserver.model.enums.Rarity;

public abstract class Tool extends Item {
    protected int durability;
    protected String material;

    protected Tool(String itemId, String displayName, int maxStackSize, Rarity rarity,
            int durability, String material) {
        super(itemId, displayName, maxStackSize, rarity);
        this.durability = durability;
        this.material = material;
    }

    public int getDurability() {
        return durability;
    }

    public String getMaterial() {
        return material;
    }

    public abstract void use();

    @Override
    public String describe() {
        return "Tool[" + material + ", durability=" + durability + "]";
    }
}
