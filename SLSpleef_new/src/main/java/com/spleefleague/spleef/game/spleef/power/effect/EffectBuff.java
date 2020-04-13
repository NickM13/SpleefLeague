/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.spleef.power.effect;

import com.spleefleague.core.annotation.DBField;
import com.spleefleague.spleef.player.SpleefPlayer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author NickM13
 */
public class EffectBuff extends Effect {
    
    @DBField
    public Integer amplifier;
    @DBField
    public Double duration;
    @DBField
    public String potionEffectType;
    @DBField
    public Boolean removeOnBreak = false;
    @DBField
    public Boolean removeOnMove = false;
    
    public EffectBuff() {
        super();
    }
    public EffectBuff(EffectBuff o) {
        amplifier = o.amplifier;
        duration = o.duration;
        potionEffectType = o.potionEffectType;
    }
    
    @Override
    public void reset(SpleefPlayer sp) {
        sp.getPlayer().removePotionEffect(PotionEffectType.getByName(potionEffectType));
    }
    
    @Override
    public void updateEffect(SpleefPlayer sp) {
        
    }
    
    @Override
    public void activate(SpleefPlayer sp) {
        sp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.getByName(potionEffectType), (int) (duration * 20), amplifier, false, false, true));
    }
    @Override
    public void onMove(SpleefPlayer sp) {
        if (removeOnMove) {
            sp.getPlayer().removePotionEffect(PotionEffectType.getByName(potionEffectType));
        }
    }
    @Override
    public void onBlockBreak(SpleefPlayer sp) {
        if (removeOnBreak) {
            sp.getPlayer().removePotionEffect(PotionEffectType.getByName(potionEffectType));
        }
    }
    
}
