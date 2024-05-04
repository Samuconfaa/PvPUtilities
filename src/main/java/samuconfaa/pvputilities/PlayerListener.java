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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;

import static samuconfaa.pvputilities.ItemManager.createCesoieItem;
import static samuconfaa.pvputilities.ItemManager.createPickItem;

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
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        String interactionKey = player.getName() + ":" + event.getAction().name() + ":" + event.getHand().name();

        // Verifica se l'interazione è già stata gestita
        if (handledInteractions.contains(interactionKey)) {
            return;
        }

        if (event.getItem() != null) {
            if (event.getItem().isSimilar(ItemManager.createFlashItem())) {
                handleFlash(event);
            } else if (event.getItem().isSimilar(ItemManager.createAtomItem())) {
                handleAtom(event);
            } else if (event.getItem().isSimilar(ItemManager.createAntiBuildItem())) {
                handleBuild(event);
            } else if (event.getItem().isSimilar(ItemManager.createAntiBoostItem())) {
                handleBoost(event);
            } else if (event.getItem().isSimilar(createPickItem())) {
                handlePick(event);
            } else if (event.getItem().isSimilar(createCesoieItem())) {
                handleCesoie(event);}
        }

        // Aggiungi l'interazione alla lista degli eventi gestiti
        handledInteractions.add(interactionKey);

        // Pulisci la lista degli eventi gestiti dopo un certo periodo di tempo
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            handledInteractions.remove(interactionKey);
        }, 5); // 5 ticks (0.25 secondi) di ritardo prima di rimuovere l'evento dalla lista
    }

    private void handleFlash(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

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

    private void handleAtom(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

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

    private void handleBuild(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        int cooldownPlayer = PvPUtilities.getInstance().getConfigManager().getBuildPlayerCooldown();
        if (CooldownManager.canUse(player, "build")) {
            int cooldown = PvPUtilities.getInstance().getConfigManager().getBuildCooldown();

            int range = PvPUtilities.getInstance().getConfigManager().getBuildRange();

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
                // Impedisce al player target di piazzare blocchi
                targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, cooldown * 20, 255));

                // Avvia il countdown del cooldown
                Player finalTargetPlayer = targetPlayer;
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    // Rimuove l'effetto di non poter piazzare blocchi
                    finalTargetPlayer.removePotionEffect(PotionEffectType.SLOW_DIGGING);
                }, cooldownPlayer * 20);

                // Mandare un messaggio al player
                targetPlayer.sendMessage(ConfigurationManager.getMessage(plugin,"messages.noblock"));

                CooldownManager.setCooldown(player, "build", cooldown);
                item.setAmount(item.getAmount() - 1);
            } else {
                // Se non ci sono player nel raggio, o se ne sto guardando due assieme, manda un messaggio al player
                player.sendMessage(ConfigurationManager.getMessage(plugin,"messages.noplayer"));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);



            }
        } else {

            long remainingCooldown = CooldownManager.getRemainingCooldown(player, "build");
            int remainingSeconds = (int) Math.ceil(remainingCooldown / 1000.0);
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            player.sendMessage(ConfigurationManager.getBuildCooldownMessage().replace("{seconds}", String.valueOf(remainingSeconds)));

        }

    }

    private void handleBoost(PlayerInteractEvent event){
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

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


    private void handlePick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // Controllo se il giocatore ha cliccato con l'oggetto giusto
        if (item != null && item.isSimilar(createPickItem())) { // Verifica se l'oggetto è simile a quello creato
            if (CooldownManager.canUse(player, "pick")) {
                int range = PvPUtilities.getInstance().getConfigManager().getPickRange();  //da aggiungere al config
                int cooldown = PvPUtilities.getInstance().getConfigManager().getPickCooldown();   //da aggiungere al config





                // Controllo se il blocco su cui ha cliccato è ossidiana
                Block clickedBlock = event.getClickedBlock();
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

    private void handleCesoie(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // Controllo se il giocatore ha cliccato con l'oggetto giusto
        if (item != null && item.isSimilar(createCesoieItem())) { // Verifica se l'oggetto è simile a quello creato
            if (CooldownManager.canUse(player, "cesoie")) {
                int range = PvPUtilities.getInstance().getConfigManager().getCesoieRange();  //da aggiungere al config
                int cooldown = PvPUtilities.getInstance().getConfigManager().getCesoieCooldown();   //da aggiungere al config




                // Controllo se il blocco su cui ha cliccato è cobweb
                Block clickedBlock = event.getClickedBlock();
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