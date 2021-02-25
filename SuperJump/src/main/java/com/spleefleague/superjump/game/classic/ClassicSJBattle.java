/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game.classic;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.BattleUtils;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.game.battle.versus.VersusBattle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.superjump.SuperJump;
import com.spleefleague.superjump.game.SJMode;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 */
public class ClassicSJBattle extends VersusBattle<ClassicSJPlayer> {
    
    public ClassicSJBattle(UUID battleId, List<UUID> players, Arena arena) {
        super(SuperJump.getInstance(), battleId, players, arena, ClassicSJPlayer.class, SJMode.CLASSIC.getBattleMode());
        playToPoints = 1;
    }

    protected void setupScoreboard() {
        chatGroup.setScoreboardName(ChatColor.GOLD + "" + ChatColor.BOLD + this.getMode().getDisplayName());
        chatGroup.addTeam("arena", ChatColor.GREEN + "  " + arena.getName());
        chatGroup.addTeam("time", "00:00:00:000");

        for(int i = 0; i < this.sortedBattlers.size(); i++) {
            chatGroup.addTeam("p" + i, "  " + Chat.PLAYER_NAME + "" + ChatColor.BOLD + sortedBattlers.get(i).getCorePlayer().getName());
            chatGroup.addTeam("p" + i + "falls", ChatColor.RED + "Falls: ");
        }

        updateScoreboard();
    }

    public void updateScoreboard() {
        chatGroup.setTeamDisplayName("time", Chat.DEFAULT + this.getRuntimeString());
    }
    
    @Override
    protected void setupBaseSettings() {

    }

    protected void failBattler(CorePlayer cp) {
        battlers.get(cp).addFall();
        battlers.get(cp).respawn();
        for(int i = 0; i < this.sortedBattlers.size(); i++) {
            chatGroup.setTeamDisplayName("p" + i + "falls", ChatColor.RED + "Falls: " + sortedBattlers.get(i).getFalls());
        }
    }
    
    @Override
    public void reset() {

    }

}
