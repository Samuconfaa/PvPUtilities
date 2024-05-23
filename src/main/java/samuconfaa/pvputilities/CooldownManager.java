// PVPUtilities by Samuconfaa

//Questo file gestisce il cooldown
package samuconfaa.pvputilities;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CooldownManager {

    private static final Map<String, Map<String, Long>> cooldowns = new HashMap<>();

    public static boolean canUse(Player player, String ability) {
        if (!cooldowns.containsKey(player.getName())) {
            cooldowns.put(player.getName(), new HashMap<>());
        }

        Map<String, Long> playerCooldowns = cooldowns.get(player.getName());

        if (!playerCooldowns.containsKey(ability)) {
            return true;
        }

        long cooldownTime = playerCooldowns.get(ability);
        long currentTime = System.currentTimeMillis();
        long remainingTime = cooldownTime - currentTime;

        return remainingTime <= 0;
    }


    public static void setCooldown(Player player, String ability, int seconds) {
        if (!cooldowns.containsKey(player.getName())) {
            cooldowns.put(player.getName(), new HashMap<>());
        }

        long cooldownTime = System.currentTimeMillis() + (seconds * 1000);
        cooldowns.get(player.getName()).put(ability, cooldownTime);
    }


    public static long getRemainingCooldown(Player player, String ability) {
        if (!cooldowns.containsKey(player.getName())) {
            return 0;
        }

        Map<String, Long> playerCooldowns = cooldowns.get(player.getName());

        if (!playerCooldowns.containsKey(ability)) {
            return 0;
        }


        long cooldownTime = playerCooldowns.get(ability);
        long currentTime = System.currentTimeMillis();
        long remainingTime = cooldownTime - currentTime;

        return Math.max(0, remainingTime);
    }
}