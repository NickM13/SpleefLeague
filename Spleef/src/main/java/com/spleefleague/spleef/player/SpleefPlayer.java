/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.player;

import com.spleefleague.core.Core;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.spleef.game.battle.power.ability.Abilities;
import com.spleefleague.spleef.game.battle.power.ability.Ability;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityMobility;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityOffensive;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityUtility;
import com.spleefleague.spleef.game.battle.power.training.PowerTrainingArena;
import com.spleefleague.spleef.game.battle.power.training.PowerTrainingBattle;

/**
 * @author NickM13
 */
public class SpleefPlayer extends DBPlayer {
    
    public SpleefPlayer() {
        super();
    }

    @Override
    public void init() {

    }
    
    @Override
    public void initOffline() {

    }
    
    @Override
    public void close() { }
    
}
