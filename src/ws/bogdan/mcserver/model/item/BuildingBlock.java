package ws.bogdan.mcserver.model.item;

import ws.bogdan.mcserver.model.enums.Rarity;

public class BuildingBlock extends Block {
    public BuildingBlock(String itemId, String displayName, int maxStackSize, Rarity rarity,
            int hardness, boolean stackable) {
        super(itemId, displayName, maxStackSize, rarity, hardness, stackable);
    }

    @Override
    public String describe() {
        return "BuildingBlock[" + displayName + ", hardness=" + hardness + "]";
    }
}
