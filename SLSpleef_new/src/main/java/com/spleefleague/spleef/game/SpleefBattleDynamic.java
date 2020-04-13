/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.util.database.DBPlayer;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.player.SpleefPlayer;
import java.util.List;

/**
 * @author NickM13
 */
public class SpleefBattleDynamic extends SpleefBattle {
    
    public SpleefBattleDynamic(List<DBPlayer> players, SpleefArena arena) {
        super(players, arena);
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
    
    @Override
    public void addBattler(DBPlayer dbp) {
        chatGroup.sendMessage(dbp.getDisplayName() + " has joined the match");
        Chat.sendMessageToPlayer(dbp, "You have joined a " + arena.getMode().getDisplayName() + " match");
        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(dbp);
        players.add(sp);
        addBattler(sp, 0);
    }
    
    @Override
    public boolean surrender(SpleefPlayer sp) {
        if (battlers.containsKey(sp)) {
            if (battlers.size() <= 1) {
                endBattle();
                return true;
            } else {
                removeBattler(sp);
                chatGroup.sendMessage(sp.getDisplayName() + " has left the match");
                Chat.sendMessageToPlayer(sp, "You have left the match");
            }
            return true;
        }
        return false;
    }
    
}
