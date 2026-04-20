package ws.bogdan.mcserver.model.item;

import ws.bogdan.mcserver.model.enums.Rarity;

public class OreBlock extends Block {
    private String mineralType;
    private int xpDrop;

    public OreBlock(String itemId, String displayName, int maxStackSize, Rarity rarity,
            int hardness, boolean stackable, String mineralType, int xpDrop) {
        super(itemId, displayName, maxStackSize, rarity, hardness, stackable);
        this.mineralType = mineralType;
        this.xpDrop = xpDrop;
    }

    public String getMineralType() {
        return mineralType;
    }

    public int getXpDrop() {
        return xpDrop;
    }

    public void mine() {
        System.out.println("Mined " + mineralType + ", +" + xpDrop + " XP");
    }

    @Override
    public String describe() {
        return "OreBlock[" + mineralType + ", xpDrop=" + xpDrop + "]";
    }
}
