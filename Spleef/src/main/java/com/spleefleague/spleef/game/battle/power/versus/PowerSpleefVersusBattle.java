/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.battle.power.versus;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.BattleUtils;
import com.spleefleague.core.game.battle.versus.VersusBattle;
import com.spleefleague.core.game.history.GameHistory;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.Shovel;
import com.spleefleague.spleef.game.SpleefMode;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.ability.Ability;
import com.spleefleague.spleef.util.SpleefUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author NickM13
 */
public class PowerSpleefVersusBattle extends VersusBattle<PowerSpleefPlayer> {

    private BuildStructure randomField;

    public PowerSpleefVersusBattle(UUID battleId, List<UUID> players, Arena arena) {
        super(Spleef.getInstance(), battleId, players, arena, PowerSpleefPlayer.class, SpleefMode.POWER.getBattleMode());
    }
    
    /**
     * Initialize base battle settings such as GameWorld tools and Scoreboard values
     */
    @Override
    protected void setupBaseSettings() {
        SpleefUtils.setupBaseSettings(this);
        randomField = arena.getRandomStructure("spleef:power");
        if (randomField != null) {
            gameWorld.setBaseBlocks(
                    FakeUtils.translateBlocks(
                            FakeUtils.rotateBlocks(randomField.getFakeBlocks(), (int) getArena().getOrigin().getYaw()),
                            getArena().getOrigin().toBlockPosition()));
        }
        gameWorld.setRegenSpeed(0.5);
    }

    private static final String LB = ChatColor.GRAY + "" + ChatColor.BOLD + "[";
    private static final String RB = ChatColor.GRAY + "" + ChatColor.BOLD + "]";

    @Override
    protected void setupScoreboard() {
        chatGroup.setScoreboardName(ChatColor.GOLD + "" + ChatColor.BOLD + getMode().getDisplayName());
        chatGroup.addTeam("arena", ChatColor.GREEN + "  " + arena.getName());
        chatGroup.addTeam("time", "  00:00:00");
        chatGroup.addTeam("p1", "  " + Chat.PLAYER_NAME + ChatColor.BOLD + sortedBattlers.get(0).getCorePlayer().getName());
        chatGroup.addTeam("p1score", "");
        chatGroup.addTeam("p1o", "");
        chatGroup.addTeam("p1u", "");
        chatGroup.addTeam("p1m", "");
        chatGroup.addTeam("l1", "");
        chatGroup.addTeam("p2", "  " + Chat.PLAYER_NAME + ChatColor.BOLD + sortedBattlers.get(1).getCorePlayer().getName());
        chatGroup.addTeam("p2score", "");
        chatGroup.addTeam("p2o", "");
        chatGroup.addTeam("p2u", "");
        chatGroup.addTeam("p2m", "");
    }

    @Override
    protected void setupBattlers() {
        super.setupBattlers();
        for (PowerSpleefPlayer psp : battlers.values()) {
            gameHistory.addPlayerAdditional(psp.getCorePlayer().getUniqueId(), "shovel", psp.getCorePlayer().getCollectibles().getActive(Shovel.class).getIdentifier());
            gameHistory.addPlayerAdditional(psp.getCorePlayer().getUniqueId(), "power:offensive", psp.getOffensiveName());
            gameHistory.addPlayerAdditional(psp.getCorePlayer().getUniqueId(), "power:utility", psp.getUtilityName());
            gameHistory.addPlayerAdditional(psp.getCorePlayer().getUniqueId(), "power:mobility", psp.getMobilityName());
        }
        sortedBattlers.get(0).setOpponents(sortedBattlers.get(1));
        sortedBattlers.get(1).setOpponents(sortedBattlers.get(0));
    }

    @Override
    public void updateScoreboard() {
        chatGroup.setTeamDisplayName("time", "  " + Chat.DEFAULT + getRuntimeStringNoMillis());
        chatGroup.setTeamDisplayName("p1score", BattleUtils.toScoreSquares(sortedBattlers.get(0), playToPoints));
        chatGroup.setTeamDisplayName("p1o", LB + Ability.Type.OFFENSIVE.getColor() + sortedBattlers.get(0).getOffensiveName() + RB);
        chatGroup.setTeamDisplayName("p1u", LB + Ability.Type.UTILITY.getColor() + sortedBattlers.get(0).getUtilityName() + RB);
        chatGroup.setTeamDisplayName("p1m", LB + Ability.Type.MOBILITY.getColor() + sortedBattlers.get(0).getMobilityName() + RB);
        chatGroup.setTeamDisplayName("p2score", BattleUtils.toScoreSquares(sortedBattlers.get(1), playToPoints));
        chatGroup.setTeamDisplayName("p2o", LB + Ability.Type.OFFENSIVE.getColor() + sortedBattlers.get(1).getOffensiveName() + RB);
        chatGroup.setTeamDisplayName("p2u", LB + Ability.Type.UTILITY.getColor() + sortedBattlers.get(1).getUtilityName() + RB);
        chatGroup.setTeamDisplayName("p2m", LB + Ability.Type.MOBILITY.getColor() + sortedBattlers.get(1).getMobilityName() + RB);
    }

    @Override
    public void fillField() {
        SpleefUtils.fillFieldFast(this, randomField);
    }
    
    @Override
    public void reset() {
        fillField();
    }
    
    @Override
    public void updateField() {
        for (PowerSpleefPlayer psp : battlers.values()) {
            psp.updateAbilities();
        }
        gameWorld.performBaseBreakRegen();
    }

    @Override
    public void startCountdown() {
        super.startCountdown();
        countdown = 5;
    }

    @Override
    public void endBattle(PowerSpleefPlayer winner) {
        for (PowerSpleefPlayer psp : battlers.values()) {
            Bukkit.getScheduler().runTaskLater(Spleef.getInstance(), psp::resetCooldowns, 2L);
        }
        super.endBattle(winner);
    }

    @Override
    protected void failBattler(CorePlayer cp) {
        for (PowerSpleefPlayer psp : sortedBattlers) {
            if (!psp.getCorePlayer().equals(cp)) {
                psp.addRoundWin();
                if (psp.getRoundWins() >= playToPoints) {
                    gameHistory.setEndReason(GameHistory.EndReason.NORMAL);
                    endBattle(psp);
                    return;
                } else if (psp.getRoundWins() == playToPoints - 1) {
                    chatGroup.sendTitle(ChatColor.GOLD + "Match Point: " + psp.getCorePlayer().getName(), "", 5, 20, 5);
                }
            }
        }

        gameWorld.doFailBlast(cp);
        battlers.get(cp).respawn();

        for (Map.Entry<BlockPosition, FakeBlock> baseBlock : gameWorld.getBaseBlocks().entrySet()) {
            FakeBlock fakeBlock = gameWorld.getFakeBlock(baseBlock.getKey());
            if (fakeBlock == null || fakeBlock.getBlockData().getMaterial() != baseBlock.getValue().getBlockData().getMaterial()) {
                gameWorld.setBlockDelayed(baseBlock.getKey(), baseBlock.getValue(), (int) (Math.random() * 40));
            }
        }
    }

}
