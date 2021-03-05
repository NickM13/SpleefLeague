/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.battle.power.team;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.BattleUtils;
import com.spleefleague.core.game.battle.team.TeamBattle;
import com.spleefleague.core.game.battle.team.TeamBattleTeam;
import com.spleefleague.core.game.history.GameHistory;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.Shovel;
import com.spleefleague.spleef.game.SpleefMode;
import com.spleefleague.spleef.game.battle.SpleefBattlePlayer;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.util.SpleefUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author NickM13
 */
public class PowerSpleefTeamBattle extends TeamBattle<PowerSpleefPlayer> {

    private BuildStructure randomField;

    public PowerSpleefTeamBattle(UUID battleId, List<UUID> players, Arena arena) {
        super(Spleef.getInstance(), battleId, players, arena, PowerSpleefPlayer.class, SpleefMode.TEAM.getBattleMode());
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
        playToPoints = 5;
    }

    @Override
    protected void setupScoreboard() {
        chatGroup.setScoreboardName(ChatColor.GOLD + "" + ChatColor.BOLD + getMode().getDisplayName());
        chatGroup.addTeam("time", "  00:00:00");
        for (int i = 0; i < sortedTeams.size(); i++) {
            TeamBattleTeam<PowerSpleefPlayer> team = sortedTeams.get(i);
            chatGroup.addTeam("t" + i, "  " + Chat.PLAYER_NAME + ChatColor.BOLD + team.getTeamInfo().getName());
            chatGroup.addTeam("tscore" + i, "");
            if (i < sortedTeams.size() - 1) {
                chatGroup.addTeam("l" + i, "");
            }
        }
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
        for (TeamBattleTeam<PowerSpleefPlayer> team : teams) {
            for (PowerSpleefPlayer psp : team.getPlayers()) {
                for (TeamBattleTeam<PowerSpleefPlayer> team2 : teams) {
                    if (!team2.equals(team)) {
                        for (PowerSpleefPlayer psp2 : team2.getPlayers()) {
                            psp.addOpponent(psp2);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void updateScoreboard() {
        chatGroup.setTeamDisplayName("time", "  " + Chat.DEFAULT + getRuntimeStringNoMillis());
        for (int i = 0; i < sortedTeams.size(); i++) {
            TeamBattleTeam<PowerSpleefPlayer> team = sortedTeams.get(i);
            chatGroup.setTeamDisplayName("tscore" + i, BattleUtils.toScoreSquares(team, playToPoints));
        }
    }

    @Override
    protected void sendEndMessage(TeamBattleTeam<PowerSpleefPlayer> teamBattleTeam) {

    }

    @Override
    public void updateField() {
        for (PowerSpleefPlayer psp : battlers.values()) {
            psp.updateAbilities();
        }
        gameWorld.performBaseBreakRegen();
    }

    @Override
    public void fillField() {
        for (Map.Entry<BlockPosition, FakeBlock> baseBlock : gameWorld.getBaseBlocks().entrySet()) {
            FakeBlock fakeBlock = gameWorld.getFakeBlock(baseBlock.getKey());
            if (fakeBlock == null || fakeBlock.getBlockData().getMaterial() != baseBlock.getValue().getBlockData().getMaterial()) {
                gameWorld.setBlockDelayed(baseBlock.getKey(), baseBlock.getValue(), (int) (Math.random() * 40));
            }
        }
    }

    @Override
    protected void saveBattlerStats(PowerSpleefPlayer powerSpleefPlayer) {

    }

    @Override
    public void startCountdown() {
        super.startCountdown();
        countdown = 5;
    }

    @Override
    public void reset() {
        fillField();
    }

    @Override
    public void endBattleTeam(TeamBattleTeam<PowerSpleefPlayer> winner) {
        for (PowerSpleefPlayer psp : battlers.values()) {
            Bukkit.getScheduler().runTaskLater(Spleef.getInstance(), psp::resetCooldowns, 2L);
        }
        super.endBattleTeam(winner);
    }

    @Override
    protected void failBattler(CorePlayer cp) {
        super.failBattler(cp);

        gameWorld.doFailBlast(cp);
    }
    
}
