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
        meta.setDisplayName(ConfigurationManager.getFlashItemName("items.flash.name"));
        meta.setLore(ConfigurationManager.getFlashItemLore("items.flash.lore"));
        item.setItemMeta(meta);


        return item;
    }

    public static ItemStack createAtomItem() {
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ConfigurationManager.getAtomItemName("items.atom.name"));
        meta.setLore(ConfigurationManager.getAtomItemLore("items.atom.lore"));
        item.setItemMeta(meta);

        return item;
    }



    public static ItemStack createAntiBoostItem() {
        ItemStack item = new ItemStack(Material.STICK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ConfigurationManager.getAntiBoostItemName("items.antiboost.name"));
        meta.setLore(ConfigurationManager.getAntiBoostLore("items.antiboost.lore"));
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack createPickItem() {
        ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ConfigurationManager.getPickItemName("items.pick.name"));
        meta.setLore(ConfigurationManager.getPickLore("items.pick.lore"));
        item.setItemMeta(meta);

        return item;
    }


    public static ItemStack createCesoieItem() {
        ItemStack item = new ItemStack(Material.SHEARS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ConfigurationManager.getCesoieItemName("items.cesoie.name"));
        meta.setLore(ConfigurationManager.getCesoieLore("items.cesoie.lore"));
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack createSquidItem() {
        ItemStack item = new ItemStack(Material.INK_SACK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ConfigurationManager.getSquidItemName("items.squid.name"));
        meta.setLore(ConfigurationManager.getSquidLore("items.squid.lore"));
        item.setItemMeta(meta);

        return item;
    }



}
