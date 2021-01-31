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

    @DBField protected String activeUtility = null;
    @DBField protected String activeOffensive = null;
    @DBField protected String activeMobility = null;
    
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

    public void setActive(Ability.Type type, String powerName) {
        switch (type) {
            case UTILITY:
                activeUtility = powerName;
                break;
            case MOBILITY:
                activeMobility = powerName;
                break;
            case OFFENSIVE:
                activeOffensive = powerName;
                break;
        }
        CorePlayer cp = Core.getInstance().getPlayers().get(getUniqueId());
        Battle<?> battle = cp.getBattle();
        if (battle instanceof PowerTrainingBattle) {
            switch (type) {
                case UTILITY:
                    ((PowerTrainingBattle) battle).getBattler(cp).chooseUtility();
                    break;
                case MOBILITY:
                    ((PowerTrainingBattle) battle).getBattler(cp).chooseMobililty();
                    break;
                case OFFENSIVE:
                    ((PowerTrainingBattle) battle).getBattler(cp).chooseOffensive();
                    break;
            }
            cp.refreshHotbar();
        }
    }

    public AbilityStats getActive(Ability.Type type) {
        switch (type) {
            case MOBILITY:
                return Abilities.getAbility(type, activeMobility);
            case UTILITY:
                return Abilities.getAbility(type, activeUtility);
            case OFFENSIVE:
                return Abilities.getAbility(type, activeOffensive);
        }
        return null;
    }

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

    public AbilityStats getActiveUtility() {
        return Abilities.getAbility(Ability.Type.UTILITY, activeUtility);
    }

    public AbilityStats getActiveOffensive() {
        return Abilities.getAbility(Ability.Type.OFFENSIVE, activeOffensive);
    }

    public AbilityStats getActiveMobility() {
        return Abilities.getAbility(Ability.Type.MOBILITY, activeMobility);
    }
    
}
