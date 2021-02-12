/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.battle.team;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.BattleUtils;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.game.battle.team.TeamBattle;
import com.spleefleague.core.game.battle.team.TeamBattleTeam;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.purse.CoreCurrency;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefMode;
import com.spleefleague.spleef.game.battle.power.ability.Ability;
import com.spleefleague.spleef.util.SpleefUtils;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @author NickM13
 */
public class TeamSpleefBattle extends TeamBattle<TeamSpleefPlayer> {
    
    public TeamSpleefBattle(UUID battleId, List<UUID> players, Arena arena) {
        super(Spleef.getInstance(), battleId, players, arena, TeamSpleefPlayer.class, SpleefMode.TEAM.getBattleMode());
    }

    /**
     * Initialize base battle settings such as GameWorld tools and Scoreboard values
     */
    @Override
    protected void setupBaseSettings() {
        SpleefUtils.setupBaseSettings(this);
        for (BuildStructure structure : arena.getStructures()) {
            gameWorld.setBaseBlocks(
                    FakeUtils.translateBlocks(
                            FakeUtils.rotateBlocks(structure.getFakeBlocks(), (int) getArena().getOrigin().getYaw()),
                            getArena().getOrigin().toBlockPosition()));
        }
        playToPoints = 5;
    }

    @Override
    protected void setupScoreboard() {
        chatGroup.setScoreboardName(ChatColor.GOLD + "" + ChatColor.BOLD + getMode().getDisplayName());
        chatGroup.addTeam("time", "  00:00:00");
        for (int i = 0; i < sortedTeams.size(); i++) {
            TeamBattleTeam<TeamSpleefPlayer> team = sortedTeams.get(i);
            chatGroup.addTeam("t" + i, "  " + Chat.PLAYER_NAME + ChatColor.BOLD + team.getTeamInfo().getName());
            chatGroup.addTeam("tscore" + i, "");
            if (i < sortedTeams.size() - 1) {
                chatGroup.addTeam("l" + i, "");
            }
        }
    }

    @Override
    public void updateScoreboard() {
        chatGroup.setTeamDisplayName("time", "  " + Chat.DEFAULT + getRuntimeStringNoMillis());
        for (int i = 0; i < sortedTeams.size(); i++) {
            TeamBattleTeam<TeamSpleefPlayer> team = sortedTeams.get(i);
            chatGroup.setTeamDisplayName("tscore" + i, BattleUtils.toScoreSquares(team, playToPoints));
        }
    }

    @Override
    protected void sendEndMessage(TeamBattleTeam<TeamSpleefPlayer> teamBattleTeam) {

    }
    
    @Override
    protected void saveBattlerStats(TeamSpleefPlayer teamSpleefPlayer) {

    }

    @Override
    public void fillField() {
        SpleefUtils.fillFieldFast(this);
    }

    @Override
    protected void applyRewards(TeamBattleTeam<TeamSpleefPlayer> winner) {
        if (winner.getRoundWins() < 5) {
            // No rewards for less than 5 round games
            return;
        }
        for (TeamSpleefPlayer bp : battlers.values()) {
            int coins;
            int common = 0, rare = 0, epic = 0, legendary = 0;
            Battle.OreType ore;
            coins = getRandomCoins(bp.getCorePlayer(),
                    winner.getPlayers().contains(bp),
                    0, 15);
            ore = getRandomOre(bp.getCorePlayer(),
                    winner.getPlayers().contains(bp),
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
    
}
