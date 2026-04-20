package ws.bogdan.mcserver.model.item;

import ws.bogdan.mcserver.model.enums.Rarity;

public class Potion extends Consumable {
    private int durationSeconds;

    public Potion(String itemId, String displayName, int maxStackSize, Rarity rarity,
            int hungerRestored, String effect, int durationSeconds) {
        super(itemId, displayName, maxStackSize, rarity, hungerRestored, effect);
        this.durationSeconds = durationSeconds;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    @Override
    public void consume() {
        System.out.println("Drank potion, effect=" + effect + " for " + durationSeconds + "s");
    }
}
