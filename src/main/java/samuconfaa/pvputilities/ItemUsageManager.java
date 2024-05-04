package samuconfaa.pvputilities;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemUsageManager {

    private static final Map<String, Integer> itemMaxUses = new HashMap<>();

    public static void setMaxUses(String itemName, int maxUses) {
        itemMaxUses.put(itemName, maxUses);
    }

    public static int getMaxUses(String itemName) {
        return itemMaxUses.getOrDefault(itemName, 0);
    }

    public static void useItem(Player player, ItemStack item) {
        String itemName = getItemName(item);
        int maxUses = getMaxUses(itemName);

        int currentUses = player.getInventory().contains(item.getType()) ? player.getInventory().getItem(player.getInventory().first(item.getType())).getAmount() : 0;

        if (currentUses >= maxUses) {
            // Rimuovi l'oggetto una volta raggiunto il numero massimo di utilizzi
            player.getInventory().removeItem(item);
        } else {
            // Incrementa il conteggio degli utilizzi
            player.getInventory().getItem(player.getInventory().first(item.getType())).setAmount(currentUses + 1);
        }
    }

    private static String getItemName(ItemStack item) {
        // Implementa la logica per ottenere il nome dell'oggetto dall'ItemStack
        // Questo potrebbe coinvolgere l'accesso ai metadati dell'oggetto
        // o altri attributi personalizzati.
        // In questa implementazione di esempio, assumiamo che il nome dell'oggetto sia l'etichetta dell'ItemMeta.
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return item.getItemMeta().getDisplayName();
        } else {
            return item.getType().name();
        }
    }
}
