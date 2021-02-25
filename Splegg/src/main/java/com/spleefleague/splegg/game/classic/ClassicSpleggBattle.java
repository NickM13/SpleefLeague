/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.splegg.game.classic;

import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.battle.versus.VersusBattle;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.splegg.Splegg;
import com.spleefleague.splegg.game.SpleggMode;
import com.spleefleague.splegg.util.SpleggUtils;

import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 */
public class ClassicSpleggBattle extends VersusBattle<ClassicSpleggPlayer> {
    
    public ClassicSpleggBattle(UUID battleId, List<UUID> players, Arena arena) {
        super(Splegg.getInstance(), battleId, players, arena, ClassicSpleggPlayer.class, SpleggMode.VERSUS.getBattleMode());
    }

    @Override
    protected void setupBattlers() {
        super.setupBattlers();
        for (ClassicSpleggPlayer battler : battlers.values()) {
            gameHistory.addPlayerAdditional(battler.getCorePlayer().getUniqueId(), "splegg:gun1", battler.getGun1().getIdentifier());
            gameHistory.addPlayerAdditional(battler.getCorePlayer().getUniqueId(), "splegg:gun2", battler.getGun2().getIdentifier());
        }
    }
    
    /**
     * Initialize base battle settings such as GameWorld tools and Scoreboard values
     */
    @Override
    protected void setupBaseSettings() {
        SpleggUtils.setupBaseSettings(this);
        for (BuildStructure structure : getArena().getStructures()) {
            gameWorld.setBaseBlocks(
                    FakeUtils.translateBlocks(
                            FakeUtils.rotateBlocks(structure.getFakeBlocks(), (int) getArena().getOrigin().getYaw()),
                            getArena().getOrigin().toBlockPosition()));
        }
    }
    
    @Override
    public void fillField() {
        SpleggUtils.fillFieldFast(this);
        for (ClassicSpleggPlayer csp : battlers.values()) {
            csp.respawn();
        }
    }
    
    @Override
    public void reset() {
        fillField();
    }

    @Override
    public void startRound() {
        super.startRound();
        for (ClassicSpleggPlayer csp : battlers.values()) {
            csp.resetAbilities();
        }
    }

    @Override
    public void updateField() {
        for (ClassicSpleggPlayer csp : battlers.values()) {
            csp.updateAbilities();
        }
        gameWorld.performBaseBreakRegen();
    }

}
