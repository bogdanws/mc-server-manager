package ws.bogdan.mcserver.model.item;

import ws.bogdan.mcserver.model.enums.Rarity;

public class Sword extends Tool {
    private int damage;

    public Sword(String itemId, String displayName, int maxStackSize, Rarity rarity,
            int durability, String material, int damage) {
        super(itemId, displayName, maxStackSize, rarity, durability, material);
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public void use() {
        System.out.println("Swinging sword, damage=" + damage);
        durability--;
    }
}
