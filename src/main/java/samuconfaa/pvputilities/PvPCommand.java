// PVPUtilities by Samuconfaa

//Questo file gestisce i comandi /pvpu e le varie conseguenze
package samuconfaa.pvputilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PvPCommand implements CommandExecutor {

    private final PvPUtilities plugin;

    public PvPCommand(PvPUtilities plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("pvpu")) {
            if (!(sender instanceof Player) || !(sender.hasPermission("pvpu.pvp"))){
                sender.sendMessage("§4PvPUtilities Custum Items by Samuconfaa");
                return true;
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("pvpu.reload")) {
                    plugin.reloadConfig();
                    sender.sendMessage(ConfigurationManager.getMessage(plugin, "messages.reload"));
                } else {
                    sender.sendMessage(ConfigurationManager.getMessage(plugin, "messages.no_reload_permission"));
                }

                return true;
            } else if (args.length == 2) {
                if (!(sender instanceof Player) || !(sender.hasPermission("pvpu.pvp"))){
                    sender.sendMessage("§4PvPUtilities Custum Items by Samuconfaa");
                    return true;
                }
                if (!(sender instanceof Player) || sender.hasPermission("pvpu.pvp")) {
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
                            target.sendMessage(ConfigurationManager.getMessage(plugin, "messages.cesoie_received"));                //aggiungere al config
                        } else {
                            sender.sendMessage(ConfigurationManager.getMessage(plugin, "messages.invalid_item"));
                        }
                    } else {
                        sender.sendMessage(ConfigurationManager.getMessage(plugin, "messages.player_not_found").replace("{player}", args[1]));
                    }
                    return true;
                } else {
                    sender.sendMessage("§4PvPUtilities Custum Items by Samuconfaa");
                    return true;
                }
            } else if (sender.hasPermission("pvpu.pvp")) {
                sender.sendMessage(ConfigurationManager.getMessage(plugin, "messages.incorrect_usage"));
                return  true;
            }
        }
        return false;
    }
}
