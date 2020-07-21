package com.spleefleague.spleef.game.battle.power.ability.abilities.offensive;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityOffensive;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffectType;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class OffensiveIntoTheShadows extends AbilityOffensive {

    private static final double DURATION = 1;

    public OffensiveIntoTheShadows() {
        super(3, 3, 5D, 2D);
    }

    @Override
    public String getDisplayName() {
        return "Into the Shadows";
    }

    @Override
    public String getDescription() {
        return Chat.DESCRIPTION + "Turn invisible for " +
                Chat.STAT + DURATION +
                Chat.DESCRIPTION + " second" + (DURATION > 1 ? "s" : "") + ". Holds up to " +
                Chat.STAT + this.charges +
                Chat.DESCRIPTION + " charges.";
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     *
     * @param psp Casting Player
     */
    @Override
    public boolean onUse(PowerSpleefPlayer psp) {
        psp.getPlayer().addPotionEffect(PotionEffectType.INVISIBILITY.createEffect((int) (DURATION * 20), 0));
        psp.getPlayer().addPotionEffect(PotionEffectType.GLOWING.createEffect((int) (DURATION * 20), 0));
        psp.getBattle().getBattlers().forEach(bp -> bp.getCorePlayer().getPlayer().hidePlayer(Spleef.getInstance(), psp.getPlayer()));
        psp.getBattle().getGameWorld().runTask(Bukkit.getScheduler().runTaskLater(Spleef.getInstance(), () -> {
            psp.getBattle().getBattlers().forEach(bp -> bp.getCorePlayer().getPlayer().showPlayer(Spleef.getInstance(), psp.getPlayer()));
        }, (int) (DURATION * 20) + 5));
        psp.getBattle().getGameWorld().playSound(psp.getPlayer().getLocation(), Sound.UI_TOAST_IN, 1, 1);
        psp.getBattle().getGameWorld().spawnParticles(Particle.REDSTONE,
                psp.getPlayer().getBoundingBox().getCenterX(),
                psp.getPlayer().getBoundingBox().getCenterY(),
                psp.getPlayer().getBoundingBox().getCenterZ(),
                15, 0.2, 0.9, 0.2, 0D,
                Type.OFFENSIVE.getDustMedium());
        return true;
    }

    /**
     * Called at the start of a round
     *
     * @param psp
     */
    @Override
    public void reset(PowerSpleefPlayer psp) {
        psp.getBattle().getBattlers().forEach(bp -> bp.getCorePlayer().getPlayer().showPlayer(Spleef.getInstance(), psp.getPlayer()));
    }

}
