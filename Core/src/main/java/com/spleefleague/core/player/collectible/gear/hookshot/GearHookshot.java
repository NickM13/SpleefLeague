package com.spleefleague.core.player.collectible.gear.hookshot;

import com.google.common.collect.Lists;
import com.spleefleague.core.Core;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.gear.Gear;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.core.world.game.projectile.ProjectileStats;
import com.spleefleague.core.world.global.GlobalWorld;
import com.spleefleague.core.world.global.GlobalWorldPlayer;
import com.spleefleague.coreapi.database.annotation.DBField;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NickM13
 */
public class GearHookshot extends Gear {

    private static final ItemStack HOOKSHOT_UNFIRED = InventoryMenuUtils.createCustomItem(Material.BREWING_STAND, 1);
    private static final ItemStack HOOKSHOT_FIRED = InventoryMenuUtils.createCustomItem(Material.BREWING_STAND, 2);

    private static final Map<CorePlayer, HookshotPlayer> playerFiredMap = new HashMap<>();

    @DBField
    protected int customModelData = 0;
    @DBField
    protected double fireRange;

    protected static final ProjectileStats projectileStats = new ProjectileStats();

    static {
        projectileStats.entityClass = HookshotProjectile.class;
        projectileStats.customModelDatas = Lists.newArrayList(29);
        projectileStats.gravity = false;
        projectileStats.lifeTicks = 20 * 10;
        projectileStats.fireRange = 10D;
        projectileStats.collidable = false;
        projectileStats.size = 0.5;
        projectileStats.noClip = true;
        projectileStats.bounces = 1;
    }

    public static void init() {
        Vendorable.registerExactType(GearHookshot.class);

        Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            for (HookshotPlayer hookshotPlayer : playerFiredMap.values()) {
                hookshotPlayer.update();
            }
        }, 2L, 2L);
    }

    public static void close() {

    }

    public static HookshotPlayer getHookshotPlayer(CorePlayer shooter) {
        return playerFiredMap.get(shooter);
    }

    public GearHookshot() {
        super(GearType.HOOKSHOT);
    }

    public GearHookshot(String identifier, String name) {
        super(GearType.HOOKSHOT, identifier, name);
        this.material = Material.BREWING_STAND;
        this.customModelData = 1;
    }

    @Override
    public void onRightClick(CorePlayer cp) {
        if (!playerFiredMap.containsKey(cp)) {
            playerFiredMap.put(cp, new HookshotPlayer(cp));
        }
        HookshotPlayer hookshotPlayer = playerFiredMap.get(cp);
        if (!hookshotPlayer.isFired()) {
            hookshotPlayer.setProjectile((HookshotProjectile) GlobalWorld.getGlobalFakeWorld().shootProjectile(cp, projectileStats).get(0));
        }
    }

    @Override
    public ItemStack getGearItem(CorePlayer cp) {
        if (!playerFiredMap.containsKey(cp) || !playerFiredMap.get(cp).isFired()) {
            return HOOKSHOT_UNFIRED;
        }
        return HOOKSHOT_FIRED;
    }

}
