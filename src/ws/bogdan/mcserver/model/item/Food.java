package ws.bogdan.mcserver.model.item;

import ws.bogdan.mcserver.model.enums.Rarity;

public class Food extends Consumable {
    public Food(String itemId, String displayName, int maxStackSize, Rarity rarity,
            int hungerRestored, String effect) {
        super(itemId, displayName, maxStackSize, rarity, hungerRestored, effect);
    }

    @Override
    public void consume() {
        System.out.println("Ate " + displayName + ", +" + hungerRestored + " hunger");
    }
}
