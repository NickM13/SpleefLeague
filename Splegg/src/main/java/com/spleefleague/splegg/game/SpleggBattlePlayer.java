package com.spleefleague.splegg.game;

import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.player.CorePlayer;

/**
 * @author NickM13
 * @since 4/25/2020
 */
public class SpleggBattlePlayer extends BattlePlayer {
    
    private int knockouts;
    private int knockoutStreak;
    
    public SpleggBattlePlayer(CorePlayer cp, Battle<?> battle) {
        super(cp, battle);
        this.knockouts = 0;
        this.knockoutStreak = 0;
    }
    
    @Override
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
