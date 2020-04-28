/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.battle.power.effect;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.database.annotation.DBField;
import com.spleefleague.spleef.game.battle.power.PowerSpleefBattle;
import com.spleefleague.spleef.player.SpleefPlayer;

/**
 * @author NickM13
 */
public class EffectRollerSpades extends Effect {
    
    @DBField
    private Double duration;
    
    private long expireTime = 0;
    private BlockPosition prevBlock;
    
    public EffectRollerSpades() {
        super();
    }
    public EffectRollerSpades(EffectRollerSpades o) {
        super(o);
        this.duration = o.getDuration();
    }
    
    public double getDuration() {
        return duration;
    }
    
    @Override
    public void reset(SpleefPlayer sp) {
        expireTime = 0;
    }
    
    @Override
    public void updateEffect(SpleefPlayer sp, PowerSpleefBattle sb) {
        if (System.currentTimeMillis() < expireTime) {
            BlockPosition pos = new BlockPosition(sp.getPlayer().getLocation().clone().add(0, -0.05, 0).toVector());
            if (!prevBlock.equals(pos)) {
                //sb.chipBlock(prevBlock, 2);
                prevBlock = pos;
            }
        }
    }
    
    @Override
    public void activate(SpleefPlayer sp, PowerSpleefBattle sb) {
        prevBlock = new BlockPosition(sp.getPlayer().getLocation().clone().add(0, -0.05, 0).toVector());
        expireTime = System.currentTimeMillis() + (long) (duration * 1000);
    }
    
}
