// PVPUtilities by Samuconfaa

//Questo file gestisce gli item custum
package samuconfaa.pvputilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class ItemManager {

    public static ItemStack createFlashItem() {
        ItemStack item = new ItemStack(Material.FEATHER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ConfigurationManager.getFlashItemName());
        meta.setLore(Collections.singletonList(ConfigurationManager.getFlashItemLore()));
        item.setItemMeta(meta);


        return item;
    }

    public static ItemStack createAtomItem() {
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ConfigurationManager.getAtomItemName());
        meta.setLore(Collections.singletonList(ConfigurationManager.getAtomItemLore()));
        item.setItemMeta(meta);

        return item;
    }




    public static ItemStack createAntiBoostItem() {
        ItemStack item = new ItemStack(Material.STICK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ConfigurationManager.getAntiBoostItemName());
        meta.setLore(Collections.singletonList(ConfigurationManager.getAntiBoostLore()));
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack createPickItem() {
        ItemStack item = new ItemStack(Material.TNT);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ConfigurationManager.getPickItemName());
        meta.setLore(Collections.singletonList(ConfigurationManager.getPickLore()));
        item.setItemMeta(meta);

        return item;
    }


    public static ItemStack createCesoieItem() {
        ItemStack item = new ItemStack(Material.GLOWSTONE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ConfigurationManager.getCesoieItemName());
        meta.setLore(Collections.singletonList(ConfigurationManager.getCesoieLore()));
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack createSquidItem() {
        ItemStack item = new ItemStack(Material.INK_SACK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ConfigurationManager.getSquidItemName());
        meta.setLore(Collections.singletonList(ConfigurationManager.getSquidLore()));
        item.setItemMeta(meta);

        return item;
    }


    public static ItemStack createForzaItem() {
        ItemStack item = new ItemStack(Material.IRON_INGOT);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ConfigurationManager.getForzaItemName());
        meta.setLore(Collections.singletonList(ConfigurationManager.getForzaLore()));
        item.setItemMeta(meta);

        return item;
    }



}