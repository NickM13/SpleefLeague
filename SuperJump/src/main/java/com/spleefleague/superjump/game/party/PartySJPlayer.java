package com.spleefleague.superjump.game.party;

import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.superjump.game.SJBattlePlayer;

/**
 * @author NickM13
 * @since 5/4/2020
 */
public class PartySJPlayer extends SJBattlePlayer {
    
    public PartySJPlayer(CorePlayer cp, Battle<?> battle) {
        super(cp, battle);
    }
    
}
