package com.spleefleague.spleef.game.battle;

import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.player.CorePlayer;

/**
 * @author NickM
 * @since 4/14/2020
 */
public class SpleefBattlePlayer extends BattlePlayer {

    protected int knockouts;
    protected int knockoutStreak;
    protected int blocksBrokenRound;
    protected int blocksBrokenTotal;

    public SpleefBattlePlayer(CorePlayer cp, Battle<?> battle) {
        super(cp, battle);
        this.knockouts = 0;
        this.knockoutStreak = 0;
        this.blocksBrokenTotal = 0;
    }

    @Override
    public void respawn() {
        super.respawn();
        knockoutStreak = 0;
        blocksBrokenRound = 0;
    }

    public int getBlocksBrokenRound() {
        return blocksBrokenRound;
    }

    public int getBlocksBrokenTotal() {
        return blocksBrokenTotal;
    }

    @Override
    public void onBlockBreak() {
        blocksBrokenTotal++;
        blocksBrokenRound++;
    }

    public int getKnockouts() {
        return knockouts;
    }
    
    public int getKnockoutStreak() {
        return knockoutStreak;
    }
    
    public void addKnockouts(int knockouts) {
        this.knockouts += knockouts;
        knockoutStreak += knockouts;
    }

}
