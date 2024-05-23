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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

import static samuconfaa.pvputilities.ItemManager.*;

public class PlayerListener implements Listener {

    private PvPUtilities plugin;
    private Set<String> handledInteractions;
    private final long rightClickDelay = 250;
    private final Map<String, Long> lastRightClickTimes = new HashMap<>();

    public PlayerListener(PvPUtilities plugin) {
        this.plugin = plugin;
        // Registra questa classe come listener nel plugin
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
        this.handledInteractions = new HashSet<>();
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR) return;
        Player player = e.getPlayer();
        String playerName = player.getName();

        // Controllo del cooldown per il tasto destro
        if (lastRightClickTimes.containsKey(playerName)) {
            long lastRightClickTime = lastRightClickTimes.get(playerName);
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastRightClickTime < rightClickDelay) {
                // Se è passato meno del tempo di ritardo, non fare nulla
                return;
            }
        }


        ItemStack item = player.getItemInHand();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        String displayName = meta.getDisplayName();
        if (displayName == null) return; // Verifica se il nome visualizzato non è nullo

        if (Objects.equals(displayName, ConfigurationManager.getAtomItemName())) handleAtom(player, item);
        if (Objects.equals(displayName, ConfigurationManager.getFlashItemName())) handleFlash(player, item);
        if (Objects.equals(displayName, ConfigurationManager.getForzaItemName())) handleForza(player, item);
        if (Objects.equals(displayName, ConfigurationManager.getPickItemName()))
            handlePick(player, item, e.getClickedBlock());
        if (Objects.equals(displayName, ConfigurationManager.getAntiBoostItemName())) handleBoost(player, item);
        if (Objects.equals(displayName, ConfigurationManager.getCesoieItemName()))
            handleCesoie(player, item, e.getClickedBlock());
        if (Objects.equals(displayName, ConfigurationManager.getSquidItemName())) handleSquid(player, item);

        // Aggiorna il tempo dell'ultimo clic destro
        lastRightClickTimes.put(playerName, System.currentTimeMillis());
    }

    @EventHandler
    public void onLeftClick(PlayerInteractEvent e){
        if (e.getAction() != Action.LEFT_CLICK_BLOCK && e.getAction() != Action.LEFT_CLICK_AIR) return;
        Player player = e.getPlayer();
        String playerName = player.getName();

        // Controllo del cooldown per il tasto destro
        if (lastRightClickTimes.containsKey(playerName)) {
            long lastRightClickTime = lastRightClickTimes.get(playerName);
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastRightClickTime < rightClickDelay) {
                // Se è passato meno del tempo di ritardo, non fare nulla
                return;
            }
        }

        // Se il giocatore può fare un altro clic destro, esegui le azioni associate
        ItemStack item = player.getItemInHand();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return; // Verifica se l'oggetto ha un meta associato
        String displayName = meta.getDisplayName();
        if (displayName == null) return; // Verifica se il nome visualizzato non è nullo

        if (Objects.equals(displayName, ConfigurationManager.getAtomItemName())) handleAtom(player, item);
        if (Objects.equals(displayName, ConfigurationManager.getFlashItemName())) handleFlash(player, item);
        if (Objects.equals(displayName, ConfigurationManager.getForzaItemName())) handleForza(player, item);
        if (Objects.equals(displayName, ConfigurationManager.getPickItemName()))
            handlePick(player, item, e.getClickedBlock());
        if (Objects.equals(displayName, ConfigurationManager.getAntiBoostItemName())) handleBoost(player, item);
        if (Objects.equals(displayName, ConfigurationManager.getCesoieItemName()))
            handleCesoie(player, item, e.getClickedBlock());
        if (Objects.equals(displayName, ConfigurationManager.getSquidItemName())) handleSquid(player, item);

        // Aggiorna il tempo dell'ultimo clic destro
        lastRightClickTimes.put(playerName, System.currentTimeMillis());
    }

    private boolean removeItemFromPlayer(Player player, ItemStack item) {
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack slotItem = player.getInventory().getItem(i);
            if (slotItem != null && slotItem.isSimilar(item)) {
                int amountToRemove = 1; // Puoi modificare la quantità da rimuovere se necessario
                if (slotItem.getAmount() > amountToRemove) {
                    slotItem.setAmount(slotItem.getAmount() - amountToRemove);
                    return true; // Rimozione riuscita
                } else {
                    player.getInventory().clear(i); // Rimuovi completamente l'oggetto dallo slot
                    return true; // Rimozione riuscita
                }
            }
        }
        return false; // L'oggetto non è stato trovato nell'inventario del giocatore
    }


    private void handleFlash(Player player, ItemStack item) {
        if (CooldownManager.canUse(player, "flash")) {
            int jumpDistance = PvPUtilities.getInstance().getConfigManager().getFlashJumpDistance();
            int effectDuration = PvPUtilities.getInstance().getConfigManager().getFlashEffectDuration();
            int cooldown = PvPUtilities.getInstance().getConfigManager().getFlashCooldown();
            int increments = 10; // Numero di incrementi per raggiungere la velocità massima
            double maxVelocity = 3.0; // Velocità massima sicura

            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, effectDuration * 20, 5));
            Vector direction = player.getLocation().getDirection().normalize();

            new BukkitRunnable() {
                int currentIncrement = 0;

                @Override
                public void run() {
                    if (currentIncrement >= increments) {
                        // Impedisce al giocatore di prendere danni da caduta per 10 secondi dopo l'utilizzo
                        player.setNoDamageTicks(10 * 20); // 10 secondi in ticks
                        this.cancel();
                    } else {
                        // Calcola il fattore di moltiplicazione per l'incremento attuale
                        double factor = (double) (currentIncrement + 1) / increments;
                        Vector incrementalVelocity = direction.clone().multiply(jumpDistance * factor);
                        // Verifica che la velocità non superi il massimo consentito
                        if (incrementalVelocity.length() > maxVelocity) {
                            incrementalVelocity.normalize().multiply(maxVelocity);
                        }
                        player.setVelocity(incrementalVelocity);
                        currentIncrement++;
                    }
                }
            }.runTaskTimer(plugin, 0L, 2L); // Esegui ogni 2 tick per un aumento più fluido

            CooldownManager.setCooldown(player, "flash", cooldown);
            removeItemFromPlayer(player, item);
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

            List<String> savedObsidian = PvPUtilities.getInstance().getConfig().getStringList("saved_blocks.obsidian");
            List<String> savedCobweb = PvPUtilities.getInstance().getConfig().getStringList("saved_blocks.cobweb");

            for (int x = -range; x <= range; x++) {
                for (int y = -range; y <= range; y++) {
                    for (int z = -range; z <= range; z++) {
                        Block block = player.getWorld().getBlockAt(player.getLocation().getBlockX() + x, player.getLocation().getBlockY() + y, player.getLocation().getBlockZ() + z);
                        String locationString = block.getX() + "_" + block.getY() + "_" + block.getZ();
                        if (!savedObsidian.contains(locationString) && !savedCobweb.contains(locationString)) {
                            // Rimuovi solo i blocchi che non sono stati salvati
                            if (block.getType() == Material.OBSIDIAN || block.getType() == Material.WEB) {
                                block.setType(Material.AIR);
                            }
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
            removeItemFromPlayer(player, item);
        } else {
            long remainingCooldown = CooldownManager.getRemainingCooldown(player, "atom");
            int remainingSeconds = (int) Math.ceil(remainingCooldown / 1000.0);
            player.sendMessage(ConfigurationManager.getAtomCooldownMessage().replace("{seconds}", String.valueOf(remainingSeconds)));
        }
    }


    private void handleBoost(Player player, ItemStack item) {


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
                targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, cooldownPlayer * 20, 255));

                // Avvia il countdown del cooldown
                Player finalTargetPlayer = targetPlayer;
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    // Rimuove l'effetto di non poter usare l'arco
                    finalTargetPlayer.removePotionEffect(PotionEffectType.SLOW);
                }, cooldownPlayer * 20);

                // Mandare un messaggio al player
                targetPlayer.sendMessage(ConfigurationManager.getMessage(plugin, "messages.noBow"));

                CooldownManager.setCooldown(player, "boost", cooldown);
                removeItemFromPlayer(player, item);
            } else {
                // Se non ci sono player nel raggio, o se ne sto guardando due assieme, manda un messaggio al player
                player.sendMessage(ConfigurationManager.getMessage(plugin, "messages.noplayer"));

            }
        } else {
            long remainingCooldown = CooldownManager.getRemainingCooldown(player, "boost");
            int remainingSeconds = (int) Math.ceil(remainingCooldown / 1000.0);

            player.sendMessage(ConfigurationManager.getBoostCooldownMessage().replace("{seconds}", String.valueOf(remainingSeconds)));
        }
    }

    private void handleForza(Player player, ItemStack item) {


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
            player.sendMessage(ConfigurationManager.getMessage(plugin, "messages.forzaricevuta"));

            CooldownManager.setCooldown(player, "forza", cooldown);
            removeItemFromPlayer(player, item);
        } else {
            long remainingCooldown = CooldownManager.getRemainingCooldown(player, "forza");
            int remainingSeconds = (int) Math.ceil(remainingCooldown / 1000.0);

            player.sendMessage(ConfigurationManager.getBoostCooldownMessage().replace("{seconds}", String.valueOf(remainingSeconds)));
        }
    }


    private void handleSquid(Player player, ItemStack item) {


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
                targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, cooldownPlayer * 20, 255));

                // Avvia il countdown del cooldown
                Player finalTargetPlayer = targetPlayer;
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    // Rimuove l'effetto di non poter usare l'arco
                    finalTargetPlayer.removePotionEffect(PotionEffectType.BLINDNESS);
                }, cooldownPlayer * 20);

                // Mandare un messaggio al player
                targetPlayer.sendMessage(ConfigurationManager.getMessage(plugin, "messages.cecità"));

                CooldownManager.setCooldown(player, "squid", cooldown);
                removeItemFromPlayer(player, item);
            } else {
                // Se non ci sono player nel raggio, o se ne sto guardando due assieme, manda un messaggio al player
                player.sendMessage(ConfigurationManager.getMessage(plugin, "messages.noplayer"));

            }
        } else {
            long remainingCooldown = CooldownManager.getRemainingCooldown(player, "squid");
            int remainingSeconds = (int) Math.ceil(remainingCooldown / 1000.0);

            player.sendMessage(ConfigurationManager.getBoostCooldownMessage().replace("{seconds}", String.valueOf(remainingSeconds)));
        }


    }


    private void handlePick(Player player, ItemStack item, Block clickedBlock) {
        if (item != null && item.isSimilar(createPickItem())) { // Verifica se l'oggetto è simile a quello creato
            if (CooldownManager.canUse(player, "pick")) {
                int range = PvPUtilities.getInstance().getConfigManager().getPickRange();
                int cooldown = PvPUtilities.getInstance().getConfigManager().getPickCooldown();

                List<String> savedObsidian = PvPUtilities.getInstance().getConfig().getStringList("saved_blocks.obsidian");

                if (clickedBlock != null && clickedBlock.getType() == Material.OBSIDIAN) {
                    for (int x = -range; x <= range; x++) {
                        for (int y = -range; y <= range; y++) {
                            for (int z = -range; z <= range; z++) {
                                Block block = player.getWorld().getBlockAt(player.getLocation().getBlockX() + x, player.getLocation().getBlockY() + y, player.getLocation().getBlockZ() + z);
                                String locationString = block.getX() + "_" + block.getY() + "_" + block.getZ();
                                if (!savedObsidian.contains(locationString)) {
                                    // Rimuovi solo i blocchi che non sono stati salvati
                                    if (block.getType() == Material.OBSIDIAN) {
                                        block.setType(Material.AIR);
                                    }
                                }
                            }
                        }
                    }

                    CooldownManager.setCooldown(player, "pick", cooldown);
                    removeItemFromPlayer(player, item);
                } else {
                    player.sendMessage(ConfigurationManager.getMessage(plugin, "messages.noObsidian"));
                }
            } else {
                long remainingCooldown = CooldownManager.getRemainingCooldown(player, "pick");
                int remainingSeconds = (int) Math.ceil(remainingCooldown / 1000.0);
                player.sendMessage(ConfigurationManager.getPickCooldownMessage().replace("{seconds}", String.valueOf(remainingSeconds)));
            }
        }

    }

    private void handleCesoie(Player player, ItemStack item, Block clickedBlock) {
        // Controllo se il giocatore ha cliccato con l'oggetto giusto
        if (item != null && item.isSimilar(createCesoieItem())) { // Verifica se l'oggetto è simile a quello creato
            if (CooldownManager.canUse(player, "cesoie")) {
                int range = PvPUtilities.getInstance().getConfigManager().getCesoieRange();
                int cooldown = PvPUtilities.getInstance().getConfigManager().getCesoieCooldown();


                List<String> savedCobweb = PvPUtilities.getInstance().getConfig().getStringList("saved_blocks.cobweb");

                if (clickedBlock != null && clickedBlock.getType() == Material.WEB) {
                    for (int x = -range; x <= range; x++) {
                        for (int y = -range; y <= range; y++) {
                            for (int z = -range; z <= range; z++) {
                                Block block = player.getWorld().getBlockAt(player.getLocation().getBlockX() + x, player.getLocation().getBlockY() + y, player.getLocation().getBlockZ() + z);
                                String locationString = block.getX() + "_" + block.getY() + "_" + block.getZ();
                                if (!savedCobweb.contains(locationString)) {
                                    // Rimuovi solo i blocchi che non sono stati salvati
                                    if (block.getType() == Material.WEB) {
                                        block.setType(Material.AIR);
                                    }
                                }
                            }
                        }
                    }

                    CooldownManager.setCooldown(player, "cesoie", cooldown);
                    removeItemFromPlayer(player, item);
                }else {
                    player.sendMessage(ConfigurationManager.getMessage(plugin, "messages.noCobweb"));
                }

            } else {
                long remainingCooldown = CooldownManager.getRemainingCooldown(player, "cesoie");
                int remainingSeconds = (int) Math.ceil(remainingCooldown / 1000.0);
                player.sendMessage(ConfigurationManager.getCesoieCooldownMessage().replace("{seconds}", String.valueOf(remainingSeconds)));
            }
        }
    }

}
