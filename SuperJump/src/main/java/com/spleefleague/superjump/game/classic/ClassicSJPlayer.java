package com.spleefleague.superjump.game.classic;

import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.player.CorePlayer;

/**
 * @author NickM13
 * @since 5/4/2020
 */
public class ClassicSJPlayer extends BattlePlayer {

    private int falls = 0;

    public ClassicSJPlayer(CorePlayer cp, Battle<?> battle) {
        super(cp, battle);
    }

    public void addFall() {
        falls++;
    }

    public int getFalls() {
        return falls;
    }

}
