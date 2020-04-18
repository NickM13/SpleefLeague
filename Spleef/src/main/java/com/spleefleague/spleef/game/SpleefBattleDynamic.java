/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.BattlePlayer;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.player.SpleefPlayer;
import java.util.List;

/**
 * @author NickM13
 */
public abstract class SpleefBattleDynamic extends SpleefBattle {

    public SpleefBattleDynamic(List<CorePlayer> players, SpleefArena arena, Class<? extends BattlePlayer> battlePlayerClass) {
        super(players, arena, battlePlayerClass);
    }

    @Override
    protected void sendStartMessage() {
        Core.getInstance().sendMessage("A " +
                Chat.GAMEMODE + getMode().getDisplayName() + " " +
                Chat.DEFAULT + "match between " +
                "some players" +
                Chat.DEFAULT + " has begun on " +
                Chat.GAMEMAP + arena.getDisplayName());
    }

    /**
     * Applies elo change to all players in the battle
     * ELO change is 20 if players are the same rank
     * exponentially increasing/decreasing between (5, 40)
     *
     * @param winner
     */
    @Override
    protected void applyEloChange(BattlePlayer winner) {

    }

    @Override
    protected void joinBattler(CorePlayer cp) {
        chatGroup.sendMessage(cp.getDisplayName() + " has joined the match");
        Chat.sendMessageToPlayer(cp, "You have joined a " + arena.getMode().getDisplayName() + " match");
    }

    @Override
    public void surrender(CorePlayer cp) {
        if (battlers.containsKey(cp)) {
            if (battlers.size() <= 1) {
                endBattle();
            } else {
                removeBattler(cp);
                chatGroup.sendMessage(cp.getDisplayName() + " has left the match");
                Chat.sendMessageToPlayer(cp, "You have left the match");
            }
        }
    }

    @Override
    protected void resetPlayer(CorePlayer cp) {

    }

    @Override
    public void updateField() {

    }

    @Override
    public void updateExperience() {

    }

}
