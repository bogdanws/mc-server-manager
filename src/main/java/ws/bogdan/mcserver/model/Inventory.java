package ws.bogdan.mcserver.model;

import ws.bogdan.mcserver.model.player.Player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Inventory {
    private final Player owner;
    private final List<ItemStack> stacks = new ArrayList<>();
    private final int maxSlots;

    public Inventory(Player owner, int maxSlots) {
        this.owner = owner;
        this.maxSlots = maxSlots;
    }

    public Player getOwner() {
        return owner;
    }

    public int getMaxSlots() {
        return maxSlots;
    }

    public void addStack(ItemStack stack) {
        if (stacks.size() >= maxSlots) {
            throw new IllegalStateException("Inventory is full (" + maxSlots + " slots)");
        }
        stacks.add(stack);
    }

    public boolean removeStack(ItemStack stack) {
        return stacks.remove(stack);
    }

    public List<ItemStack> getStacks() {
        return Collections.unmodifiableList(stacks);
    }

    public int totalItems() {
        return stacks.stream().mapToInt(ItemStack::count).sum();
    }
}
