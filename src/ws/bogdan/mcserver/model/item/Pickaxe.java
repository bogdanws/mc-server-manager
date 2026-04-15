package ws.bogdan.mcserver.model.item;

import ws.bogdan.mcserver.model.enums.Rarity;

public class Pickaxe extends Tool {
    public Pickaxe(String itemId, String displayName, int maxStackSize, Rarity rarity,
            int durability, String material) {
        super(itemId, displayName, maxStackSize, rarity, durability, material);
    }

    @Override
    public void use() {
        durability--;
        System.out.println("Mining with " + material + " pickaxe");
    }
}
