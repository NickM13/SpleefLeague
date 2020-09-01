package com.spleefleague.spleef.game.battle.classic.affix.affixes;

import com.spleefleague.spleef.game.battle.classic.ClassicSpleefBattle;
import com.spleefleague.spleef.game.battle.classic.affix.ClassicSpleefAffix;

/**
 * @author NickM13
 * @since 5/15/2020
 */
public class AffixCutDown extends ClassicSpleefAffix {

    private int playTo;

    public AffixCutDown() {
        super();
        playTo = 4;
    }

    public void startBattle(ClassicSpleefBattle battle) {
        battle.setPlayTo(playTo);
        battle.getChatGroup().sendMessage("Cut Down affix has reduced the play to value to " + playTo + "!");
    }

}
