/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.util.database.DBPlayer;
import com.spleefleague.spleef.player.SpleefPlayer;
import java.util.List;

/**
 * @author NickM13
 */
public class SpleefBattleStatic extends SpleefBattle {

    public SpleefBattleStatic(List<DBPlayer> players, SpleefArena arena) {
        super(players, arena);
    }
    
    @Override
    protected void sendStartMessage() {
        Core.getInstance().sendMessage("A " +
                Chat.GAMEMODE + getMode().getDisplayName() + " " +
                Chat.DEFAULT + "match between " +
                playersFormatted +
                Chat.DEFAULT + " has begun on " +
                Chat.GAMEMAP + arena.getDisplayName());
    }
    
    @Override
    protected void startBattle() {
        chatGroup.addTeam("PlayTo", Chat.SCORE + "PlayTo: " + playToPoints);
        //chatGroup.setTeamDisplayName("PlayTo", "PlayTo: " + playToPoints);
        super.startBattle();
    }
    
    @Override
    public boolean surrender(SpleefPlayer sp) {
        if (battlers.containsKey(sp)) {
            if (battlers.size() <= 1) {
                Core.getInstance().sendMessage(Chat.PLAYER_NAME + sp.getDisplayName() +
                        Chat.DEFAULT + " has surrendered");
                endBattle();
                return true;
            } else {
                BattlePlayer winner = (BattlePlayer) (battlers.keySet().toArray()[0].equals(sp) ? battlers.values().toArray()[1] : battlers.values().toArray()[0]);
                endBattle(winner);
                Core.getInstance().sendMessage(Chat.PLAYER_NAME + sp.getDisplayName() +
                        Chat.DEFAULT + " has surrendered to " +
                        Chat.PLAYER_NAME + winner.player.getDisplayName());
            }
            return true;
        }
        return false;
    }

}
