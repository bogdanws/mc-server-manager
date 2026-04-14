package ws.bogdan.mcserver.model.enums;

public enum Rarity {
    COMMON(1),
    UNCOMMON(2),
    RARE(3),
    EPIC(4),
    LEGENDARY(5);

    private final int weight;

    Rarity(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}
