/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.splegg.classic;

import com.spleefleague.core.annotation.DBField;

/**
 * @author NickM13
 */
public class SpleggGunSpray extends SpleggGun {
    
    // 
    @DBField
    protected int maxAmmo;
    
    // Num of ticks after shooting that ammo starts to regenerate
    @DBField
    protected int refillDelay;
    
    public SpleggGunSpray() {
        
    }
    
}
