package ws.bogdan.mcserver.model.item;

import ws.bogdan.mcserver.model.enums.Rarity;

public abstract class Consumable extends Item {
    protected int hungerRestored;
    protected String effect;

    protected Consumable(String itemId, String displayName, int maxStackSize, Rarity rarity,
            int hungerRestored, String effect) {
        super(itemId, displayName, maxStackSize, rarity);
        this.hungerRestored = hungerRestored;
        this.effect = effect;
    }

    public int getHungerRestored() {
        return hungerRestored;
    }

    public String getEffect() {
        return effect;
    }

    public abstract void consume();

    @Override
    public String describe() {
        return "Consumable[" + displayName + ", hungerRestored=" + hungerRestored + "]";
    }
}
