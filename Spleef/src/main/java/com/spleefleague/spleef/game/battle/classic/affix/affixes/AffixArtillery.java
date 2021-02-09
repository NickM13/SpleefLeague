package com.spleefleague.spleef.game.battle.classic.affix.affixes;

import com.spleefleague.core.world.game.projectile.ProjectileStats;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.spleef.game.battle.classic.ClassicSpleefBattle;
import com.spleefleague.spleef.game.battle.classic.ClassicSpleefPlayer;
import com.spleefleague.spleef.game.battle.classic.affix.ClassicSpleefAffix;

/**
 * @author NickM13
 * @since 5/15/2020
 */
public class AffixArtillery extends ClassicSpleefAffix {

    private static final ProjectileStats projectileStats = new ProjectileStats();
    private int blocks = 4;

    public AffixArtillery() {
        super();
        displayName = "Artillery";
    }

    /**
     * Called at the start of a round
     *
     * @param battle Classic Spleef Battle
     */
    @Override
    public void startRound(ClassicSpleefBattle battle) {
        for (ClassicSpleefPlayer csp : battle.getBattlers()) {
            csp.getAffixValueMap().put("Artillery", 0);
        }
    }

    private void updateArtilleryDisplay(ClassicSpleefPlayer csp) {
        int val = (int) csp.getAffixValueMap().getOrDefault("Artillery", 0);
        csp.getPlayer().sendExperienceChange((val % blocks) / (float) blocks, (int) Math.floor(val / (float) blocks));
    }

    @Override
    public void onBlockBreak(ClassicSpleefPlayer csp) {
        csp.getAffixValueMap().put("Artillery", (int) csp.getAffixValueMap().getOrDefault("Artillery", 0) + 1);
        updateArtilleryDisplay(csp);
    }

    @Override
    public void onRightClick(ClassicSpleefPlayer csp) {
        if ((int) csp.getAffixValueMap().getOrDefault("Artillery", 0) >= blocks) {
            csp.getBattle().getGameWorld().shootProjectile(csp.getCorePlayer(), projectileStats);
            csp.getAffixValueMap().put("Artillery", (int) csp.getAffixValueMap().get("Artillery") - blocks);
            updateArtilleryDisplay(csp);
        }
    }

}
