package com.spleefleague.spleef.game.battle;

import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.player.CorePlayer;

/**
 * @author NickM
 * @since 4/14/2020
 */
public class SpleefBattlePlayer extends BattlePlayer {

    private int knockouts;
    private int knockoutStreak;

    public SpleefBattlePlayer(CorePlayer cp, Battle<?, ?> battle) {
        super(cp, battle);
        this.knockouts = 0;
        this.knockoutStreak = 0;
    }

    public void respawn() {
        super.respawn();
        knockoutStreak = 0;
    }

    public int getKnockouts() {
        return knockouts;
    }
    
    public void addKnockouts(int knockouts) {
        this.knockouts += knockouts;
        knockoutStreak += knockouts;
    }

    public int getKnockoutStreak() {
        return knockoutStreak;
    }

}
