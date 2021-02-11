/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.battle.multi;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.game.battle.dynamic.DynamicBattle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.purse.CoreCurrency;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefMode;
import com.spleefleague.spleef.util.SpleefUtils;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @author NickM13
 */
public class MultiSpleefBattle extends DynamicBattle<MultiSpleefPlayer> {

    public MultiSpleefBattle(UUID battleId, List<UUID> players, Arena arena) {
        super(Spleef.getInstance(), battleId, players, arena, MultiSpleefPlayer.class, SpleefMode.MULTI.getBattleMode());
    }

    /**
     * Initialize base battle settings such as GameWorld tools and Scoreboard values
     */
    @Override
    protected void setupBaseSettings() {
        SpleefUtils.setupBaseSettings(this);
        BlockPosition origin = getArena().getOrigin().toBlockPosition();
        for (BuildStructure structure : getArena().getStructures()) {
            gameWorld.setBaseBlocks(
                    FakeUtils.translateBlocks(
                            FakeUtils.rotateBlocks(structure.getFakeBlocks(), (int) getArena().getOrigin().getYaw()),
                            origin));
        }
    }

    @Override
    public void updateExperience() {

    }

    @Override
    public void fillField() {
        SpleefUtils.fillFieldFast(this);
        for (MultiSpleefPlayer msp : battlers.values()) {
            msp.respawn();
        }
    }

    @Override
    protected void applyRewards(MultiSpleefPlayer winner) {
        for (BattlePlayer bp : battlers.values()) {
            int common = 0, rare = 0, epic = 0, legendary = 0;
            int coins = getRandomCoins(bp.getCorePlayer(),
                    bp.getPlayer().equals(winner.getPlayer()),
                    0, 20);
            Battle.OreType ore = getRandomOre(bp.getCorePlayer(),
                    bp.getPlayer().equals(winner.getPlayer()),
                    0.050, 0.02, 0.01, 0.002);
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

    @Override
    public void reset() {
        fillField();
    }

    @Override
    public void startRound() {
        super.startRound();
        for (MultiSpleefPlayer msp : battlers.values()) {

        }
    }

    private final static Random random = new Random();
    private final static BlockData INDICATOR = Material.RED_CONCRETE.createBlockData();
    private final static BlockData AIR = Material.AIR.createBlockData();

    @Override
    public void updateField() {

    }

    @Override
    protected void sendEndMessage(MultiSpleefPlayer msp) {
        chatGroup.sendTitle(msp.getCorePlayer().getDisplayName() + ChatColor.GRAY + " won the game",
                "", 20, 160, 20);
    }

    @Override
    protected void failBattler(CorePlayer cp) {
        super.failBattler(cp);
        gameWorld.doFailBlast(cp);
    }
    
}
