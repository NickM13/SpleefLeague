/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.spleef.power.effect;

import com.spleefleague.core.annotation.DBField;
import com.spleefleague.core.util.database.DBEntity;
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
    
    public void updateEffect(SpleefPlayer sp) {
        
    }

    public void activate(SpleefPlayer sp) {
        
    }
    public void onMove(SpleefPlayer sp) {

    }
    public void onBlockBreak(SpleefPlayer sp) {

    }
    
}
