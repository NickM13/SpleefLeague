/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.battle.power.effect;

import com.spleefleague.core.database.annotation.DBField;
import com.spleefleague.core.database.variable.DBEntity;
import com.spleefleague.spleef.game.battle.power.PowerSpleefBattle;
import com.spleefleague.spleef.player.SpleefPlayer;

/**
 * @author NickM13
 */
public class Effect extends DBEntity implements Cloneable {
    
    public enum EffectType {
        ROLLER_SPADES,
        BUFF,
        HEAT_BOLTS,
        ICE_PILLARS;
    }
    
    @DBField
    protected EffectType type;

    public Effect() {
        
    }
    public Effect(Effect o) {
        this.type = o.getType();
    }
    
    public EffectType getType() {
        return type;
    }
    
    public void reset(SpleefPlayer sp) {
        
    }

    protected boolean useEffect() {
        return false;
    }
    
    public void updateEffect(SpleefPlayer sp, PowerSpleefBattle sb) {
        
    }
    public void activate(SpleefPlayer sp, PowerSpleefBattle sb) {
        
    }
    public void onMove(SpleefPlayer sp, PowerSpleefBattle sb) {

    }
    public void onBlockBreak(SpleefPlayer sp, PowerSpleefBattle sb) {

    }
    
}
