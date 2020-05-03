package com.spleefleague.spleef.game.battle.classic.affix;

import com.spleefleague.spleef.game.battle.classic.ClassicSpleefAffix;
import com.spleefleague.spleef.game.battle.classic.ClassicSpleefBattle;

/**
 * Wither Affix begins after a set time, decaying snow blocks
 *
 * @author NickM13
 * @since 5/1/2020
 */
public class AffixWither extends ClassicSpleefAffix {
    
    private static double START_DELAY = 60;
    
    private static long SPACING = 5000L;
    private static int BLOCKS_PER_DECAY = 3;
    
    private int lastDecay = 0;
    
    public AffixWither(ClassicSpleefBattle battle) {
        super(battle);
    }
    
    public void start() {
    
    }
    
    public void update() {
        if (battle.getRoundTime() > START_DELAY
                && System.currentTimeMillis() - lastDecay > SPACING) {
            
        }
    }
    
}
