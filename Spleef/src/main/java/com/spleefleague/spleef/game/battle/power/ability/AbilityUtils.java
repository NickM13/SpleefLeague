package com.spleefleague.spleef.game.battle.power.ability;

import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.battle.power.PowerSpleefBattle;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import org.bukkit.Bukkit;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * @author NickM13
 * @since 8/21/2020
 */
public class AbilityUtils {

    private static class PlayerFling {

        PowerSpleefPlayer psp;
        Vector direction;
        double time;

        public PlayerFling(PowerSpleefPlayer psp, Vector direction, double time) {
            this.psp = psp;
            this.direction = direction;
            this.time = time;
        }

    }

    private static Map<UUID, PlayerFling> flingMap = new HashMap<>();

    public static void init() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Spleef.getInstance(), () -> {
            Iterator<Map.Entry<UUID, PlayerFling>> fit = flingMap.entrySet().iterator();
            while (fit.hasNext()) {
                Map.Entry<UUID, PlayerFling> entry = fit.next();
                if (entry.getValue().time < 0 || !entry.getValue().psp.getBattle().isOngoing()) {
                    entry.getValue().psp.getPlayer().setGravity(true);
                    fit.remove();
                } else {
                    entry.getValue().time -= 0.1;
                    entry.getValue().psp.getPlayer().setVelocity(entry.getValue().direction.clone().multiply(1.2));
                    entry.getValue().psp.getBattle().getGameWorld().breakBlocks(entry.getValue().psp.getPlayer().getBoundingBox().expand(0.15, -0.05, 0.15, 0.15, 0.15, 0.15));
                }
            }
        }, 0L, 2L);
    }

    public static void stopFling(PowerSpleefPlayer psp) {
        flingMap.remove(psp.getPlayer().getUniqueId());
        psp.getPlayer().setGravity(true);
    }

    public static void startFling(PowerSpleefPlayer psp, Vector direction, double time) {
        flingMap.put(psp.getPlayer().getUniqueId(), new PlayerFling(psp, direction, time));
        psp.getPlayer().setGravity(true);
        psp.getPlayer().setVelocity(direction.clone().normalize().multiply(1.1));
    }

}
