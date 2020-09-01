package com.spleefleague.spleef.game.battle.classic.affix.affixes;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.spleef.game.battle.classic.ClassicSpleefBattle;
import com.spleefleague.spleef.game.battle.classic.ClassicSpleefPlayer;
import com.spleefleague.spleef.game.battle.classic.affix.ClassicSpleefAffix;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;

/**
 * @author NickM13
 * @since 5/15/2020
 */
public class AffixHotStreak extends ClassicSpleefAffix {

    private double streakDelay;

    public AffixHotStreak() {
        super();
        this.streakDelay = 10;
    }

    /**
     * Called at the start of a round
     *
     * @param battle
     */
    @Override
    public void startRound(ClassicSpleefBattle battle) {
        for (ClassicSpleefPlayer csp : battle.getBattlers()) {
            csp.getAffixValueMap().put("LastBreak", 0D);
        }
    }

    @Override
    public void onBlockBreak(ClassicSpleefPlayer csp) {
        csp.getAffixValueMap().put("LastBreak", csp.getBattle().getRoundTime());
        csp.getPlayer().removePotionEffect(PotionEffectType.SLOW);
    }

    /**
     * Called every 2 ticks (1/10 of a second)
     *
     * @param battle Classic Spleef Battle
     */
    @Override
    public void update(ClassicSpleefBattle battle) {
        for (ClassicSpleefPlayer csp : battle.getBattlers()) {
            double time = battle.getRoundTime() - (double) csp.getAffixValueMap().get("LastBreak");
            if (battle.isRoundStarted()) {
                if (time > streakDelay) {
                    csp.getPlayer().sendExperienceChange(0, 0);
                    csp.getPlayer().addPotionEffect(PotionEffectType.SLOW.createEffect(5, 0));
                    Set<BlockPosition> positions = new HashSet<>();
                    positions.add(new BlockPosition(
                            (int) Math.floor(csp.getPlayer().getBoundingBox().getMinX()),
                            (int) Math.floor(csp.getPlayer().getBoundingBox().getMinY() - 0.5),
                            (int) Math.floor(csp.getPlayer().getBoundingBox().getMinZ())));
                    positions.add(new BlockPosition(
                            (int) Math.floor(csp.getPlayer().getBoundingBox().getMinX()),
                            (int) Math.floor(csp.getPlayer().getBoundingBox().getMinY() - 0.5),
                            (int) Math.floor(csp.getPlayer().getBoundingBox().getMaxZ())));
                    positions.add(new BlockPosition(
                            (int) Math.floor(csp.getPlayer().getBoundingBox().getMaxX()),
                            (int) Math.floor(csp.getPlayer().getBoundingBox().getMinY() - 0.5),
                            (int) Math.floor(csp.getPlayer().getBoundingBox().getMaxZ())));
                    positions.add(new BlockPosition(
                            (int) Math.floor(csp.getPlayer().getBoundingBox().getMaxX()),
                            (int) Math.floor(csp.getPlayer().getBoundingBox().getMinY() - 0.5),
                            (int) Math.floor(csp.getPlayer().getBoundingBox().getMinZ())));
                    for (BlockPosition pos : positions) {
                        battle.getGameWorld().setBlockDelayed(pos, Material.AIR.createBlockData(), 10L);
                    }
                } else {
                    csp.getPlayer().sendExperienceChange((float) (time / streakDelay), 0);
                }
            } else {
                csp.getPlayer().sendExperienceChange(0, 0);
            }
        }
    }

}
