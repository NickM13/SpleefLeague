/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.player;

import com.spleefleague.core.Core;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.spleef.game.battle.power.ability.Abilities;
import com.spleefleague.spleef.game.battle.power.ability.Ability;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityMobility;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityOffensive;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityUtility;
import com.spleefleague.spleef.game.battle.power.training.PowerTrainingArena;
import com.spleefleague.spleef.game.battle.power.training.PowerTrainingBattle;

/**
 * @author NickM13
 */
public class SpleefPlayer extends DBPlayer {

    @DBField protected String activeUtility = "";
    @DBField protected String activeOffensive = "";
    @DBField protected String activeMobility = "";
    
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

    public void setActiveUtility(String powerName) {
        activeUtility = powerName;
        Battle<?> battle = Core.getInstance().getPlayers().get(this).getBattle();
        if (battle instanceof PowerTrainingBattle) {
            ((PowerTrainingBattle) battle).updatePowers();
        }
    }

    public void setActiveOffensive(String powerName) {
        activeOffensive = powerName;
        Battle<?> battle = Core.getInstance().getPlayers().get(this).getBattle();
        if (battle instanceof PowerTrainingBattle) {
            ((PowerTrainingBattle) battle).updatePowers();
        }
    }

    public void setActiveMobility(String powerName) {
        activeMobility = powerName;
        Battle<?> battle = Core.getInstance().getPlayers().get(this).getBattle();
        if (battle instanceof PowerTrainingBattle) {
            ((PowerTrainingBattle) battle).updatePowers();
        }
    }

    public AbilityUtility getActiveUtility() {
        return (AbilityUtility) Abilities.getAbility(Ability.Type.UTILITY, activeUtility);
    }

    public AbilityOffensive getActiveOffensive() {
        return (AbilityOffensive) Abilities.getAbility(Ability.Type.OFFENSIVE, activeOffensive);
    }

    public AbilityMobility getActiveMobility() {
        return (AbilityMobility) Abilities.getAbility(Ability.Type.MOBILITY, activeMobility);
    }
    
}
