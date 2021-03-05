/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.battle.team;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.BattleUtils;
import com.spleefleague.core.game.battle.team.TeamBattle;
import com.spleefleague.core.game.battle.team.TeamBattleTeam;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefMode;
import com.spleefleague.spleef.util.SpleefUtils;
import org.bukkit.ChatColor;

import java.util.List;
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
    public void reset() {
        fillField();
    }
    
}
