package com.spleefleague.spleef.game.battle.classic.affix.affixes;

import com.spleefleague.spleef.game.battle.classic.ClassicSpleefBattle;
import com.spleefleague.spleef.game.battle.classic.affix.ClassicSpleefAffixFuture;

/**
 * @author NickM13
 * @since 5/15/2020
 */
public class AffixRegeneration extends ClassicSpleefAffixFuture {

    public AffixRegeneration() {
        super();
        this.activateTime = 5;
    }

    @Override
    public void activate(ClassicSpleefBattle battle) {

    }

    @Override
    protected String getPreActiveMessage(int seconds) {
        return "Regeneration will activate in " + seconds + " seconds";
    }

}
