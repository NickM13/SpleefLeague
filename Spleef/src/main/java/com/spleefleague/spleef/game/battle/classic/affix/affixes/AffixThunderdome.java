package com.spleefleague.spleef.game.battle.classic.affix.affixes;

import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.core.world.build.BuildStructures;
import com.spleefleague.spleef.game.battle.classic.ClassicSpleefBattle;
import com.spleefleague.spleef.game.battle.classic.ClassicSpleefPlayer;
import com.spleefleague.spleef.game.battle.classic.affix.ClassicSpleefAffixFuture;

/**
 * Wither Affix begins after a set time, decaying snow blocks
 *
 * @author NickM13
 * @since 5/1/2020
 */
public class AffixThunderdome extends ClassicSpleefAffixFuture {
    
    public AffixThunderdome() {
        super();
        displayName = "Thunderdome";
        this.activateTime = 180;
    }

    @Override
    public void activate(ClassicSpleefBattle battle) {
        battle.startCountdown(0);
        battle.getGameWorld().overwriteBlocks(
                FakeUtils.translateBlocks(
                        FakeUtils.rotateBlocks(
                                BuildStructures.get("ThunderDome").getFakeBlocks(),
                                (int) battle.getArena().getOrigin().getYaw()),
                battle.getArena().getOrigin().toBlockPosition()));
        for (ClassicSpleefPlayer csp : battle.getBattlers()) {
            csp.getPlayer().teleport(csp.getSpawn().clone().add(csp.getSpawn().clone().getDirection().setY(0).normalize().multiply(10)));
        }
    }

    @Override
    protected String getPreActiveMessage(int seconds) {
        return "Thunderdome will activate in " + seconds + " seconds";
    }

}
