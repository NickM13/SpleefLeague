/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.pet;

import net.minecraft.server.v1_15_R1.EntityChicken;
import net.minecraft.server.v1_15_R1.EntityLiving;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.EntityZombie;
import net.minecraft.server.v1_15_R1.IRangedEntity;
import net.minecraft.server.v1_15_R1.World;

/**
 * @author NickM13
 */
public class PetMonkey extends EntityChicken {

    public PetMonkey(EntityTypes<? extends EntityChicken> entitytypes, World world) {
        super(entitytypes, world);
    }
    
}
