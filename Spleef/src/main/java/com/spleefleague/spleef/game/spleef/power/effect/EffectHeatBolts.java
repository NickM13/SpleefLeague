/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.spleef.power.effect;

import com.spleefleague.core.annotation.DBField;
import com.spleefleague.core.world.projectile.FakeProjectile;
import com.spleefleague.spleef.player.SpleefPlayer;
import org.bukkit.entity.EntityType;

/**
 * @author NickM13
 */
public class EffectHeatBolts extends Effect {
    
    @DBField
    public Integer count;
    @DBField
    public Double spread;
    @DBField
    public Integer range;
    @DBField
    public Integer power;
    
    public EffectHeatBolts() {
        super();
    }
    
    public EffectHeatBolts(EffectHeatBolts o) {
        count = o.count;
        spread = o.spread;
        range = o.range;
        power = o.power;
    }
    
    @Override
    public void reset(SpleefPlayer sp) {
        
    }
    
    @Override
    public void updateEffect(SpleefPlayer sp) {
        
    }
    
    @Override
    public void activate(SpleefPlayer sp) {
        for (int i = 0; i < count; i++) {
            //sp.getBattle().getGameWorld().shootProjectile(sp.getPlayer(), new FakeProjectile(EntityType.SNOWBALL, range, 0, power, true, 0, 0, spread));
        }
    }
    
}
