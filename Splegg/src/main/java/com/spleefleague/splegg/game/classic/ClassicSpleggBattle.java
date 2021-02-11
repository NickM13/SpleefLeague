/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.splegg.game.classic;

import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.game.battle.versus.VersusBattle;
import com.spleefleague.core.player.purse.CoreCurrency;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.splegg.Splegg;
import com.spleefleague.splegg.game.SpleggMode;
import com.spleefleague.splegg.util.SpleggUtils;

import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @author NickM13
 */
public class ClassicSpleggBattle extends VersusBattle<ClassicSpleggPlayer> {
    
    public ClassicSpleggBattle(UUID battleId, List<UUID> players, Arena arena) {
        super(Splegg.getInstance(), battleId, players, arena, ClassicSpleggPlayer.class, SpleggMode.VERSUS.getBattleMode());
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

    @Override
    protected void applyRewards(ClassicSpleggPlayer winner) {
        for (BattlePlayer bp : battlers.values()) {
            int common = 0, rare = 0, epic = 0, legendary = 0;
            int coins = getRandomCoins(bp.getCorePlayer(),
                    bp.getPlayer().equals(winner.getPlayer()),
                    0, 10);
            Battle.OreType ore = getRandomOre(bp.getCorePlayer(),
                    bp.getPlayer().equals(winner.getPlayer()),
                    0.025, 0.01, 0.005, 0.001);
            switch (ore) {
                case COMMON: common++; break;
                case RARE: rare++; break;
                case EPIC: epic++; break;
                case LEGENDARY: legendary++; break;
            }
            if (coins > 0) bp.getCorePlayer().getPurse().addCurrency(CoreCurrency.COIN, coins);
            if (common > 0) bp.getCorePlayer().getPurse().addCurrency(CoreCurrency.ORE_COMMON, common);
            if (rare > 0) bp.getCorePlayer().getPurse().addCurrency(CoreCurrency.ORE_RARE, rare);
            if (epic > 0) bp.getCorePlayer().getPurse().addCurrency(CoreCurrency.ORE_EPIC, epic);
            if (legendary > 0) bp.getCorePlayer().getPurse().addCurrency(CoreCurrency.ORE_LEGENDARY, legendary);
        }
    }

}
