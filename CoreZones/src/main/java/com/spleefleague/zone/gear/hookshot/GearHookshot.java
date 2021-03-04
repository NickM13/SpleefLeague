package com.spleefleague.zone.gear.hookshot;

import com.google.common.collect.Lists;
import com.spleefleague.core.Core;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.core.world.game.projectile.ProjectileStats;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.zone.gear.Gear;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author NickM13
 */
public class GearHookshot extends Gear {

    private static final Map<CorePlayer, HookshotPlayer> playerFiredMap = new HashMap<>();

    @DBField protected int customModelData = 0;
    @DBField protected double fireRange;

    protected static final ProjectileStats projectileStats = new ProjectileStats();

    static {
        projectileStats.entityClass = HookshotProjectile.class;
        projectileStats.customModelDatas = Lists.newArrayList(29);
        projectileStats.gravity = true;
        projectileStats.lifeTicks = 7;
        projectileStats.fireRange = 12D;
        projectileStats.collidable = false;
        projectileStats.size = 0.5;
        projectileStats.noClip = true;
        projectileStats.bounces = 1;
    }

    public static void init() {
        Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            for (HookshotPlayer hookshotPlayer : playerFiredMap.values()) {
                hookshotPlayer.update();
            }
        }, 2L, 2L);
    }

    public static void close() {

    }

    public static void onPlayerJoin(UUID uuid) {
        CorePlayer corePlayer = Core.getInstance().getPlayers().get(uuid);
        playerFiredMap.put(corePlayer, new HookshotPlayer(corePlayer));
    }

    public static HookshotPlayer getHookshotPlayer(CorePlayer shooter) {
        return playerFiredMap.get(shooter);
    }

    public static void onPlayerSneak(CorePlayer shooter) {
        if (playerFiredMap.containsKey(shooter)) {
            HookshotPlayer hookshotPlayer = playerFiredMap.get(shooter);
            if (hookshotPlayer.isFired() && hookshotPlayer.getProjectile().isHooked()) {
                hookshotPlayer.getProjectile().killEntity();
            }
        }
    }

    public GearHookshot() {
        super(GearType.HOOKSHOT);
    }

    public GearHookshot(String identifier, String name) {
        super(GearType.HOOKSHOT, identifier, name);
    }

    @Override
    public boolean onActivate(CorePlayer corePlayer) {
        if (!playerFiredMap.containsKey(corePlayer)) {
            playerFiredMap.put(corePlayer, new HookshotPlayer(corePlayer));
        }
        HookshotPlayer hookshotPlayer = playerFiredMap.get(corePlayer);
        if (!hookshotPlayer.isFired()) {
            HookshotProjectile projectile = (HookshotProjectile) corePlayer.getGlobalWorld().shootProjectile(corePlayer, projectileStats).get(0);
            hookshotPlayer.setProjectile(projectile);
            return true;
        } else {
            if (hookshotPlayer.getProjectile().isHooked()) {
                hookshotPlayer.getProjectile().attemptKill();
            }
        }
        return false;
    }

    @Override
    public boolean isAvailable(CorePlayer corePlayer) {
        return super.isAvailable(corePlayer) || (!playerFiredMap.containsKey(corePlayer) || !playerFiredMap.get(corePlayer).isFired());
    }

    @Override
    public ItemStack getGearItem(CorePlayer corePlayer) {
        if (!playerFiredMap.containsKey(corePlayer) || !playerFiredMap.get(corePlayer).isFired()) {
            return available;
        }
        return unavailable;
    }

}
