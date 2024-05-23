// PVPUtilities by Samuconfaa

//Questo file gestisce il file config
package samuconfaa.pvputilities;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.List;

public class ConfigurationManager {

    private final PvPUtilities plugin;
    private static FileConfiguration config;

    public ConfigurationManager(PvPUtilities plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public int getFlashJumpDistance() {
        return config.getInt("flash.jump_distance", 5);
    }

    public int getFlashEffectDuration() {
        return config.getInt("flash.effect_duration", 7);
    }
    public int getRangeSetblock() {
        return config.getInt("setblock.range", 10);
    }

    public int getFlashCooldown() {
        return config.getInt("flash.cooldown", 90);
    }

    public int getForzaCooldown() {
        return config.getInt("forza.cooldown", 90);
    }

    public int getAtomRange() {
        return config.getInt("atom.range", 5);
    }

    public int getAtomCooldown() {
        return config.getInt("atom.cooldown", 90);
    }


    public int getBoostCooldown() {
        return config.getInt("boost.cooldown", 90);
    }

    public int getBoostRange() {
        return config.getInt("boost.range", 20);
    }

    public int getCesoieRange() {
        return config.getInt("cesoie.range", 20);
    }

    public int getBoostPlayerCooldown() {
        return config.getInt("boost.playercooldown", 10);
    }
    public int getForzaPlayerCooldown() {
        return config.getInt("forza.playercooldown", 10);
    }
    public int getSquidPlayerCooldown() {
        return config.getInt("squid.playercooldown", 10);
    }

    public int getPickCooldown() {
        return config.getInt("pick.cooldown", 60);
    }

    public int getSquidCooldown() {
        return config.getInt("squid.cooldown", 60);
    }

    public int getCesoieCooldown() {
        return config.getInt("cesoie.cooldown", 60);
    }

    public static String getMessage(PvPUtilities plugin, String path) {
        FileConfiguration config = plugin.getConfig();
        if (config.contains(path)) {
            return config.getString(path);
        }
        return "Messaggio non trovato per " + path;
    }

    public static String getAtomItemName() {
        return config.getString("items.atom.name", "§6§lAtom");
    }

    public static List<String> getAtomItemLore() {
        return Collections.singletonList(config.getString("items.atom.lore", "§7Tasto Destro per usare!"));
    }


    public static String getFlashItemName() {
        return config.getString("items.flash.name", "§b§lFlash");
    }

    public static List<String> getFlashItemLore() {
        return Collections.singletonList(config.getString("items.flash.lore", "§7Tasto Destro per usare!"));
    }


    public static String getFlashCooldownMessage() {
        return ChatColor.translateAlternateColorCodes('&', config.getString("messages.flash_cooldown_message"));
    }

    public static String getCesoieCooldownMessage() {
        return ChatColor.translateAlternateColorCodes('&', config.getString("messages.cesoie_cooldown_message"));
    }

    public static String getPickCooldownMessage() {
        return ChatColor.translateAlternateColorCodes('&', config.getString("messages.pick_cooldown_message"));
    }

    public static String getAtomCooldownMessage() {
        return ChatColor.translateAlternateColorCodes('&', config.getString("messages.atom_cooldown_message"));
    }



    public static String getBoostCooldownMessage() {
        return ChatColor.translateAlternateColorCodes('&', config.getString("messages.boost_cooldown_message"));
    }





    public static String getAntiBoostItemName() {
        return config.getString("items.antiboost.name", "§2§lAnti-Boost");
    }

    public static List<String> getAntiBoostLore() {
        return Collections.singletonList(config.getString("items.antiboost.lore", "§7Tasto Destro per usare!"));
    }




    public int getPickRange()  {
        return config.getInt("pick.range", 5);
    }
    public int getSquidRange()  {
        return config.getInt("squid.range", 20);
    }





    public static List<String> getPickLore() {
        return Collections.singletonList(config.getString("items.pick.lore", "§7Tasto Destro per usare!"));
    }

    public static List<String> getSquidLore() {
        return Collections.singletonList(config.getString("items.squid.lore", "§7Tasto Destro per usare!"));
    }

    public static List<String> getForzaLore() {
        return Collections.singletonList(config.getString("items.forza.lore", "§7Tasto Destro per usare!"));
    }

    public static String getPickItemName() {
        return config.getString("items.pick.name", "§3§lPiccone");
    }

    public static String getForzaItemName() {
        return config.getString("items.forza.name", "§5§lForza");
    }
    public static String getSquidItemName() {
        return config.getString("items.squid.name", "§1§lSquid");
    }


    public static String getCesoieItemName() {                      //aggiungere al config
        return config.getString("items.cesoie.name", "§6§lCesoie");
    }

    public static List<String> getCesoieLore() {                    //aggiungere al config
        return Collections.singletonList(config.getString("items.cesoie.lore", "§7Tasto Destro per usare!"));
    }


}


// push