package com.spleefleague.spleef.game.battle.power.ability.abilities.offensive;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityOffensive;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class OffensiveIntoTheShadows extends AbilityOffensive {

    public static AbilityStats init() {
        return init(OffensiveIntoTheShadows.class)
                .setCustomModelData(5)
                .setName("Into the Shadows")
                .setDescription("Turn invisible for %DURATION% seconds. Holds up to %charges% charges.")
                .setUsage(3, 5, 2);
    }

    private static final double DURATION = 1.25;
    private static final Material INDICATOR = Material.ENDER_EYE;

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     */
    @Override
    public boolean onUse() {
        getPlayer().addPotionEffect(PotionEffectType.INVISIBILITY.createEffect((int) (DURATION * 20), 0));
        getPlayer().addPotionEffect(PotionEffectType.GLOWING.createEffect((int) (DURATION * 20), 0));
        getUser().getBattle().getBattlers().forEach(bp -> {
            if (!bp.getCorePlayer().equals(getUser().getCorePlayer()))
                bp.getCorePlayer().getPlayer().hidePlayer(Spleef.getInstance(), getPlayer());
        });
        getPlayer().getInventory().setItem(149, new ItemStack(INDICATOR));
        getUser().getBattle().getGameWorld().runTask(Bukkit.getScheduler().runTaskLater(Spleef.getInstance(), () -> {
            getPlayer().getInventory().setItem(149, null);
            getUser().getBattle().getBattlers().forEach(bp -> {
                if (!bp.getCorePlayer().equals(getUser().getCorePlayer()))
                    bp.getCorePlayer().getPlayer().showPlayer(Spleef.getInstance(), getPlayer());
            });
        }, (int) (DURATION * 20) + 5));
        getUser().getBattle().getGameWorld().playSound(getPlayer().getLocation(), Sound.UI_TOAST_IN, 1, 1);
        getUser().getBattle().getGameWorld().spawnParticles(Particle.REDSTONE,
                getPlayer().getBoundingBox().getCenterX(),
                getPlayer().getBoundingBox().getCenterY(),
                getPlayer().getBoundingBox().getCenterZ(),
                20, 0.2, 0.9, 0.2, 0D,
                Type.OFFENSIVE.getDustMedium());
        return true;
    }

    /**
     * Called at the start of a round
     */
    @Override
    public void reset() {
        getUser().getBattle().getBattlers().forEach(bp -> {
            if (bp.getCorePlayer().equals(getUser().getCorePlayer()))
                bp.getCorePlayer().getPlayer().showPlayer(Spleef.getInstance(), getPlayer());
        });
    }

}
