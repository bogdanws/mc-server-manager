package ws.bogdan.mcserver.model;

import ws.bogdan.mcserver.model.player.Player;
import java.time.LocalDateTime;

public record Transaction(
        Player buyer,
        Player seller,
        ItemStack itemStack,
        double price,
        LocalDateTime timestamp) {
}
