/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.battle.power.effect;

import com.spleefleague.core.database.annotation.DBField;
import com.spleefleague.spleef.game.battle.power.PowerSpleefBattle;
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

    @Override
    public void reset(SpleefPlayer sp) {
        PotionEffectType pet = PotionEffectType.getByName(potionEffectType);
        if (pet != null)
            sp.getPlayer().removePotionEffect(pet);
    }
    
    @Override
    public void updateEffect(SpleefPlayer sp, PowerSpleefBattle sb) {
        
    }
    
    @Override
    public void activate(SpleefPlayer sp, PowerSpleefBattle sb) {
        PotionEffectType pet = PotionEffectType.getByName(potionEffectType);
        if (pet != null)
            sp.getPlayer().addPotionEffect(new PotionEffect(pet, (int) (duration * 20), amplifier, false, false, true));
    }
    @Override
    public void onMove(SpleefPlayer sp, PowerSpleefBattle sb) {
        if (removeOnMove) {
            PotionEffectType pet = PotionEffectType.getByName(potionEffectType);
            if (pet != null)
                sp.getPlayer().removePotionEffect(pet);
        }
    }
    @Override
    public void onBlockBreak(SpleefPlayer sp, PowerSpleefBattle sb) {
        if (removeOnBreak) {
            PotionEffectType pet = PotionEffectType.getByName(potionEffectType);
            if (pet != null)
                sp.getPlayer().removePotionEffect(pet);
        }
    }
    
}
