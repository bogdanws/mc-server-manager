package ws.bogdan.mcserver.service;

import ws.bogdan.mcserver.audit.AuditService;
import ws.bogdan.mcserver.exception.InsufficientBalanceException;
import ws.bogdan.mcserver.model.Inventory;
import ws.bogdan.mcserver.model.ItemStack;
import ws.bogdan.mcserver.model.Transaction;
import ws.bogdan.mcserver.model.player.Player;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EconomyService {
    private final ServerState state;

    public EconomyService(ServerState state) {
        this.state = state;
    }

    public Transaction executeTransaction(Player buyer, Player seller, ItemStack stack, double price) {
        AuditService.getInstance().logAction("EXECUTE_TRANSACTION",
                "buyer=" + buyer.getUsername() + ";seller=" + seller.getUsername()
                + ";item=" + stack.item().getDisplayName() + ";qty=" + stack.count() + ";price=" + price);
        if (buyer.getBalance() < price) {
            throw new InsufficientBalanceException(
                    buyer.getUsername() + " has insufficient balance: " + buyer.getBalance() + " < " + price);
        }
        Inventory sellerInventory = state.getInventories().get(seller);
        if (sellerInventory == null || !sellerInventory.getStacks().contains(stack)) {
            throw new IllegalStateException(
                    seller.getUsername() + " does not have the item: " + stack.item().getDisplayName());
        }
        buyer.addBalance(-price);
        seller.addBalance(price);
        sellerInventory.removeStack(stack);
        Inventory buyerInventory = state.getInventories().get(buyer);
        if (buyerInventory != null) {
            buyerInventory.addStack(stack);
        }
        Transaction transaction = new Transaction(buyer, seller, stack, price, LocalDateTime.now());
        state.getTransactionHistory().add(transaction);
        return transaction;
    }

    public List<Player> topRichestPlayers(int n) {
        AuditService.getInstance().logAction("TOP_RICHEST", "n=" + n);
        List<Player> sorted = new ArrayList<>(state.getPlayers().values());
        sorted.sort(Comparator.comparingDouble(Player::getBalance).reversed());
        return sorted.subList(0, Math.min(n, sorted.size()));
    }

    public List<Transaction> mostExpensiveTransactions(int n) {
        AuditService.getInstance().logAction("MOST_EXPENSIVE_TRANSACTIONS", "n=" + n);
        List<Transaction> sorted = new ArrayList<>(state.getTransactionHistory());
        sorted.sort(Comparator.comparingDouble(Transaction::price).reversed());
        return sorted.subList(0, Math.min(n, sorted.size()));
    }
}
