package com.spleefleague.superjump.game;

import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.player.CorePlayer;
import org.bukkit.Location;

/**
 * @author NickM
 * @since 4/15/2020
 */
public class SJBattlePlayer extends BattlePlayer {

    protected int falls;
    
    public SJBattlePlayer(CorePlayer cp, Battle<?> battle) {
        super(cp, battle);
        falls = 0;
    }
    
    public void addFall() {
        falls++;
    }
    
    public int getFalls() {
        return falls;
    }
    
}
