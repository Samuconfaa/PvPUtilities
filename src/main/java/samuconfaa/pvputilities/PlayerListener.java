// PVPUtilities by Samuconfaa

//Questo file gestisce gli item custum e le azioni che fanno
package samuconfaa.pvputilities;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.Sound;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static samuconfaa.pvputilities.ItemManager.*;

public class PlayerListener implements Listener {

    private PvPUtilities plugin;
    private Set<String> handledInteractions;

    public PlayerListener(PvPUtilities plugin) {
        this.plugin = plugin;
        // Registra questa classe come listener nel plugin
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
        this.handledInteractions = new HashSet<>();
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR) return;
        Player p = e.getPlayer();
        ItemStack i = p.getItemInHand();
        if (Objects.equals(i.getItemMeta().getDisplayName(), ConfigurationManager.getAtomItemName())) handleAtom(p, i);
        if (Objects.equals(i.getItemMeta().getDisplayName(), ConfigurationManager.getFlashItemName())) handleFlash(p, i);
        if (Objects.equals(i.getItemMeta().getDisplayName(), ConfigurationManager.getForzaItemName())) handleForza(p, i);
        if (Objects.equals(i.getItemMeta().getDisplayName(), ConfigurationManager.getPickItemName())) handlePick(p, i, e.getClickedBlock());
        if (Objects.equals(i.getItemMeta().getDisplayName(), ConfigurationManager.getAntiBoostItemName())) handleBoost(p, i);
        if (Objects.equals(i.getItemMeta().getDisplayName(), ConfigurationManager.getCesoieItemName())) handleCesoie(p, i, e.getClickedBlock());
        if (Objects.equals(i.getItemMeta().getDisplayName(), ConfigurationManager.getSquidItemName())) handleSquid(p, i);

    }

    private void handleFlash(Player player, ItemStack item) {

        if (CooldownManager.canUse(player, "flash")) {
            int jumpDistance = PvPUtilities.getInstance().getConfigManager().getFlashJumpDistance();
            int effectDuration = PvPUtilities.getInstance().getConfigManager().getFlashEffectDuration();
            int cooldown = PvPUtilities.getInstance().getConfigManager().getFlashCooldown();

            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, effectDuration * 20, 5));
            player.setVelocity(player.getLocation().getDirection().multiply(jumpDistance));

            CooldownManager.setCooldown(player, "flash", cooldown);
            item.setAmount(item.getAmount() - 1);
        } else {
            long remainingCooldown = CooldownManager.getRemainingCooldown(player, "flash");
            int remainingSeconds = (int) Math.ceil(remainingCooldown / 1000.0);
            player.sendMessage(ConfigurationManager.getFlashCooldownMessage().replace("{seconds}", String.valueOf(remainingSeconds)));
        }
    }

    private void handleAtom(Player player, ItemStack item) {

        if (CooldownManager.canUse(player, "atom")) {
            int range = PvPUtilities.getInstance().getConfigManager().getAtomRange();
            int cooldown = PvPUtilities.getInstance().getConfigManager().getAtomCooldown();

            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);

            player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, player.getLocation(), 100, 0.5, 0.5, 0.5, 1);


            for (int x = -range; x <= range; x++) {
                for (int y = -range; y <= range; y++) {
                    for (int z = -range; z <= range; z++) {
                        Block block = player.getWorld().getBlockAt(player.getLocation().getBlockX() + x, player.getLocation().getBlockY() + y, player.getLocation().getBlockZ() + z);
                        if (block.getType() == Material.OBSIDIAN || block.getType() == Material.WEB) {
                            block.setType(Material.AIR);
                        }
                    }
                }
            }

            for (Entity entity : player.getNearbyEntities(range, range, range)) {
                if (entity instanceof Player) {
                    ((Player) entity).setVelocity(player.getLocation().getDirection().multiply(-1).normalize().multiply(2));
                }

            }

            CooldownManager.setCooldown(player, "atom", cooldown);
            item.setAmount(item.getAmount() - 1);
        } else {
            long remainingCooldown = CooldownManager.getRemainingCooldown(player, "atom");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            int remainingSeconds = (int) Math.ceil(remainingCooldown / 1000.0);
            player.sendMessage(ConfigurationManager.getAtomCooldownMessage().replace("{seconds}", String.valueOf(remainingSeconds)));
        }
    }


    private void handleBoost(Player player, ItemStack item){


        int cooldownPlayer = PvPUtilities.getInstance().getConfigManager().getBoostPlayerCooldown();
        if (CooldownManager.canUse(player, "boost")) {
            int cooldown = PvPUtilities.getInstance().getConfigManager().getBoostCooldown();

            int range = PvPUtilities.getInstance().getConfigManager().getBoostRange();

            boolean playerFound = false;
            Player targetPlayer = null;

            for (Entity entity : player.getNearbyEntities(range, range, range)) {
                if (entity instanceof Player && !entity.equals(player)) {
                    playerFound = true;
                    targetPlayer = (Player) entity;
                    break;
                }
            }

            if (playerFound) {
                // Impedisce al player target di usare l'arco per il tempo specificato
                targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, cooldown * 20, 255));

                // Avvia il countdown del cooldown
                Player finalTargetPlayer = targetPlayer;
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    // Rimuove l'effetto di non poter usare l'arco
                    finalTargetPlayer.removePotionEffect(PotionEffectType.SLOW);
                }, cooldownPlayer * 20);

                // Mandare un messaggio al player
                targetPlayer.sendMessage(ConfigurationManager.getMessage(plugin,"messages.noBow"));

                CooldownManager.setCooldown(player, "boost", cooldown);
                item.setAmount(item.getAmount() - 1);
            } else {
                // Se non ci sono player nel raggio, o se ne sto guardando due assieme, manda un messaggio al player
                player.sendMessage(ConfigurationManager.getMessage(plugin,"messages.noplayer"));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
        } else {
            long remainingCooldown = CooldownManager.getRemainingCooldown(player, "boost");
            int remainingSeconds = (int) Math.ceil(remainingCooldown / 1000.0);
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            player.sendMessage(ConfigurationManager.getBoostCooldownMessage().replace("{seconds}", String.valueOf(remainingSeconds)));
        }
    }
    private void handleForza(Player player, ItemStack item){


        int cooldownPlayer = PvPUtilities.getInstance().getConfigManager().getForzaPlayerCooldown();
        if (CooldownManager.canUse(player, "forza")) {
            int cooldown = PvPUtilities.getInstance().getConfigManager().getForzaCooldown();


            // Effetto dato al giocatore che esegue l'azione
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, cooldown * 20, 10));

            // Avvia il countdown del cooldown
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                // Rimuove l'effetto
                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
            }, cooldownPlayer * 20);

            // Mandare un messaggio al player
            player.sendMessage(ConfigurationManager.getMessage(plugin,"messages.forzaricevuta"));

            CooldownManager.setCooldown(player, "forza", cooldown);
            item.setAmount(item.getAmount() - 1);
        } else {
            long remainingCooldown = CooldownManager.getRemainingCooldown(player, "forza");
            int remainingSeconds = (int) Math.ceil(remainingCooldown / 1000.0);
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            player.sendMessage(ConfigurationManager.getBoostCooldownMessage().replace("{seconds}", String.valueOf(remainingSeconds)));
        }
    }


    private void handleSquid(Player player, ItemStack item){


        int cooldownPlayer = PvPUtilities.getInstance().getConfigManager().getSquidPlayerCooldown();
        if (CooldownManager.canUse(player, "squid")) {
            int cooldown = PvPUtilities.getInstance().getConfigManager().getSquidCooldown();

            int range = PvPUtilities.getInstance().getConfigManager().getSquidRange();

            boolean playerFound = false;
            Player targetPlayer = null;

            for (Entity entity : player.getNearbyEntities(range, range, range)) {
                if (entity instanceof Player && !entity.equals(player)) {
                    playerFound = true;
                    targetPlayer = (Player) entity;
                    break;
                }
            }

            if (playerFound) {
                // Impedisce al player target di usare l'arco per il tempo specificato
                targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, cooldown * 20, 255));

                // Avvia il countdown del cooldown
                Player finalTargetPlayer = targetPlayer;
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    // Rimuove l'effetto di non poter usare l'arco
                    finalTargetPlayer.removePotionEffect(PotionEffectType.BLINDNESS);
                }, cooldownPlayer * 20);

                // Mandare un messaggio al player
                targetPlayer.sendMessage(ConfigurationManager.getMessage(plugin,"messages.cecità"));

                CooldownManager.setCooldown(player, "squid", cooldown);
                item.setAmount(item.getAmount() - 1);
            } else {
                // Se non ci sono player nel raggio, o se ne sto guardando due assieme, manda un messaggio al player
                player.sendMessage(ConfigurationManager.getMessage(plugin,"messages.noplayer"));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
        } else {
            long remainingCooldown = CooldownManager.getRemainingCooldown(player, "squid");
            int remainingSeconds = (int) Math.ceil(remainingCooldown / 1000.0);
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            player.sendMessage(ConfigurationManager.getBoostCooldownMessage().replace("{seconds}", String.valueOf(remainingSeconds)));
        }



    }


    private void handlePick(Player player, ItemStack item, Block clickedBlock) {

        // Controllo se il giocatore ha cliccato con l'oggetto giusto
        if (item != null && item.isSimilar(createPickItem())) { // Verifica se l'oggetto è simile a quello creato
            if (CooldownManager.canUse(player, "pick")) {
                int range = PvPUtilities.getInstance().getConfigManager().getPickRange();  //da aggiungere al config
                int cooldown = PvPUtilities.getInstance().getConfigManager().getPickCooldown();   //da aggiungere al config





                // Controllo se il blocco su cui ha cliccato è ossidiana

                if (clickedBlock != null && clickedBlock.getType() == Material.OBSIDIAN) {
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1.0f, 1.0f);
                    clickedBlock.setType(Material.AIR);
                    for (int x = -range; x <= range; x++) {
                        for (int y = -range; y <= range; y++) {
                            for (int z = -range; z <= range; z++) {
                                Block block = player.getWorld().getBlockAt(player.getLocation().getBlockX() + x, player.getLocation().getBlockY() + y, player.getLocation().getBlockZ() + z);
                                if (block.getType() == Material.OBSIDIAN) {
                                    block.setType(Material.AIR);
                                }
                            }
                        }
                    }
                    CooldownManager.setCooldown(player, "pick", cooldown);
                    item.setAmount(item.getAmount() - 1);
                } else {
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    player.sendMessage(ConfigurationManager.getMessage(plugin, "messages.noObsidian"));
                }




            } else {
                long remainingCooldown = CooldownManager.getRemainingCooldown(player, "pick");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                int remainingSeconds = (int) Math.ceil(remainingCooldown / 1000.0);
                player.sendMessage(ConfigurationManager.getPickCooldownMessage().replace("{seconds}", String.valueOf(remainingSeconds)));
            }

        }
    }

    private void handleCesoie(Player player, ItemStack item, Block clickedBlock) {


        // Controllo se il giocatore ha cliccato con l'oggetto giusto
        if (item != null && item.isSimilar(createCesoieItem())) { // Verifica se l'oggetto è simile a quello creato
            if (CooldownManager.canUse(player, "cesoie")) {
                int range = PvPUtilities.getInstance().getConfigManager().getCesoieRange();  //da aggiungere al config
                int cooldown = PvPUtilities.getInstance().getConfigManager().getCesoieCooldown();   //da aggiungere al config




                // Controllo se il blocco su cui ha cliccato è cobweb

                if (clickedBlock != null && clickedBlock.getType() == Material.WEB) {
                    clickedBlock.setType(Material.AIR);
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1.0f, 1.0f);

                    for (int x = -range; x <= range; x++) {
                        for (int y = -range; y <= range; y++) {
                            for (int z = -range; z <= range; z++) {
                                Block block = player.getWorld().getBlockAt(player.getLocation().getBlockX() + x, player.getLocation().getBlockY() + y, player.getLocation().getBlockZ() + z);
                                if (block.getType() == Material.WEB) {
                                    block.setType(Material.AIR);
                                }
                            }
                        }
                    }
                    CooldownManager.setCooldown(player, "cesoie", cooldown);
                    item.setAmount(item.getAmount() - 1);
                } else {
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    player.sendMessage(ConfigurationManager.getMessage(plugin, "messages.noCobweb"));
                }




            } else {
                long remainingCooldown = CooldownManager.getRemainingCooldown(player, "cesoie");
                int remainingSeconds = (int) Math.ceil(remainingCooldown / 1000.0);
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                player.sendMessage(ConfigurationManager.getCesoieCooldownMessage().replace("{seconds}", String.valueOf(remainingSeconds)));
            }

        }
    }





}