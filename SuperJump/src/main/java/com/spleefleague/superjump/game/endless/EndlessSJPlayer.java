package com.spleefleague.superjump.game.endless;

import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.superjump.game.SJBattlePlayer;

/**
 * @author NickM13
 * @since 5/4/2020
 */
public class EndlessSJPlayer extends SJBattlePlayer {
    
    public EndlessSJPlayer(CorePlayer cp, Battle<?> battle) {
        super(cp, battle);
    }
    
    @Override
    public void addRoundWin() {
        super.addRoundWin();
        getCorePlayer().getStatistics().get("superjump").add("endless:level", 1);
    }
    
    public long getLevel() {
        return getCorePlayer().getStatistics().get("superjump").get("endless:level");
    }
    
}
