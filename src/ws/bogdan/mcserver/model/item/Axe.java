package ws.bogdan.mcserver.model.item;

import ws.bogdan.mcserver.model.enums.Rarity;

public class Axe extends Tool {
    public Axe(String itemId, String displayName, int maxStackSize, Rarity rarity,
            int durability, String material) {
        super(itemId, displayName, maxStackSize, rarity, durability, material);
    }

    @Override
    public void use() {
        System.out.println("Chopping with " + material + " axe");
        durability--;
    }
}
