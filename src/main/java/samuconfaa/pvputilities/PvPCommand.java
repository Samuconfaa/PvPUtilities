package samuconfaa.pvputilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PvPCommand implements CommandExecutor {

    private final PvPUtilities plugin;

    public PvPCommand(PvPUtilities plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("pvpu")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("pvpu.reload")) {
                    plugin.reloadConfig();
                    sender.sendMessage(ConfigurationManager.getMessage(plugin, "messages.reload"));
                } else {
                    sender.sendMessage(ConfigurationManager.getMessage(plugin, "messages.no_reload_permission"));
                }
                return true;
            } else if (args.length == 1 && args[0].equalsIgnoreCase("setblock")) {
                if (sender.hasPermission("pvpu.setblock")) {
                    saveBlocks(sender);
                } else {
                    sender.sendMessage(ConfigurationManager.getMessage(plugin, "messages.no_setblock_permission"));
                }
                return true;
            } else if (args.length == 2 && sender.hasPermission("pvpu.pvp")) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    if (args[0].equalsIgnoreCase("flash")) {
                        target.getInventory().addItem(ItemManager.createFlashItem());
                        sender.sendMessage(ConfigurationManager.getMessage(plugin, "messages.flash_given").replace("{player}", target.getName()));
                        target.sendMessage(ConfigurationManager.getMessage(plugin, "messages.flash_received"));
                    } else if (args[0].equalsIgnoreCase("atom")) {
                        target.getInventory().addItem(ItemManager.createAtomItem());
                        sender.sendMessage(ConfigurationManager.getMessage(plugin, "messages.atom_given").replace("{player}", target.getName()));
                        target.sendMessage(ConfigurationManager.getMessage(plugin, "messages.atom_received"));
                    } else if(args[0].equalsIgnoreCase("boost"))    {
                        target.getInventory().addItem(ItemManager.createAntiBoostItem());
                        sender.sendMessage(ConfigurationManager.getMessage(plugin, "messages.boost_given").replace("{player}", target.getName()));
                        target.sendMessage(ConfigurationManager.getMessage(plugin, "messages.boost_received"));
                    } else if(args[0].equalsIgnoreCase("pick"))    {
                        target.getInventory().addItem(ItemManager.createPickItem());
                        sender.sendMessage(ConfigurationManager.getMessage(plugin, "messages.pick_given").replace("{player}", target.getName()));
                        target.sendMessage(ConfigurationManager.getMessage(plugin, "messages.pick_received"));
                    } else if (args[0].equalsIgnoreCase("cesoie")) {
                        target.getInventory().addItem(ItemManager.createCesoieItem());
                        sender.sendMessage(ConfigurationManager.getMessage(plugin, "messages.cesoie_given").replace("{player}", target.getName()));     //aggiungere al config
                        target.sendMessage(ConfigurationManager.getMessage(plugin, "messages.cesoie_received"));
                    } else if (args[0].equalsIgnoreCase("squid")) {
                        target.getInventory().addItem(ItemManager.createSquidItem());
                        sender.sendMessage(ConfigurationManager.getMessage(plugin, "messages.squid_given").replace("{player}", target.getName()));
                        target.sendMessage(ConfigurationManager.getMessage(plugin, "messages.squid_received"));
                    } else if (args[0].equalsIgnoreCase("forza")) {
                        target.getInventory().addItem(ItemManager.createForzaItem());
                        sender.sendMessage(ConfigurationManager.getMessage(plugin, "messages.forza_given").replace("{player}", target.getName()));
                        target.sendMessage(ConfigurationManager.getMessage(plugin, "messages.forza_received"));
                    } else {
                        sender.sendMessage(ConfigurationManager.getMessage(plugin, "messages.invalid_item"));
                    }
                } else {
                    sender.sendMessage(ConfigurationManager.getMessage(plugin, "messages.player_not_found").replace("{player}", args[1]));
                }
                return true;
            } else if (sender instanceof Player && !sender.hasPermission("pvpu.pvp")) {
                sender.sendMessage("§4PvPUtilities Custom Items by Samuconfaa");
                return true;
            } else {
                sender.sendMessage("§4PvPUtilities Custom Items by Samuconfaa");
                return true;
            }
        }
        return false;
    }

    private void saveBlocks(CommandSender sender) {
        Map<String, Integer> obsidianLocations = new HashMap<>();
        Map<String, Integer> cobwebLocations = new HashMap<>();

        Player player = (Player) sender;
        int radius = PvPUtilities.getInstance().getConfigManager().getRangeSetblock(); // Raggio di ricerca dei blocchi

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block block = player.getWorld().getBlockAt(player.getLocation().getBlockX() + x, player.getLocation().getBlockY() + y, player.getLocation().getBlockZ() + z);
                    if (block.getType() == Material.OBSIDIAN) {
                        String locationString = block.getX() + "_" + block.getY() + "_" + block.getZ();
                        obsidianLocations.put(locationString, 1);
                    } else if (block.getType() == Material.WEB) {
                        String locationString = block.getX() + "_" + block.getY() + "_" + block.getZ();
                        cobwebLocations.put(locationString, 1);
                    }
                }
            }
        }

        // Salva i blocchi nel file di configurazione
        plugin.getConfig().set("saved_blocks.obsidian", new ArrayList<>(obsidianLocations.keySet()));
        plugin.getConfig().set("saved_blocks.cobweb", new ArrayList<>(cobwebLocations.keySet()));
        plugin.saveConfig();

        sender.sendMessage(ConfigurationManager.getMessage(plugin, "messages.blocks_saved"));
    }
}
