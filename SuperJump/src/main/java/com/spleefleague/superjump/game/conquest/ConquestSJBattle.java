/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game.conquest;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.util.database.DBPlayer;
import com.spleefleague.superjump.game.SJBattle;
import com.spleefleague.superjump.player.SuperJumpPlayer;
import java.util.List;

/**
 * @author NickM13
 */
public class ConquestSJBattle extends SJBattle<ConquestSJArena> {

    public ConquestSJBattle(List<DBPlayer> players, ConquestSJArena arena) {
        super(players, arena);
    }
    
    @Override
    protected void winPlayer(SuperJumpPlayer sjp) {
        double time = getRoundTime();
        switch (sjp.getConquestStats().tryNewTime(arena, time)) {
            case NEW:
                Core.sendMessageToPlayer(sjp, "You've set a new score for "
                        + Chat.GAMEMAP + arena.getDisplayName()
                        + Chat.DEFAULT + "! ("
                        + Chat.TIME + time
                        + Chat.DEFAULT + ")");
                break;
            case BEAT:
                Core.sendMessageToPlayer(sjp, "You beat your previous score on "
                        + Chat.GAMEMAP + arena.getDisplayName()
                        + Chat.DEFAULT + "! ("
                        + Chat.TIME + time
                        + Chat.DEFAULT + ")");
                break;
            case NONE:
                Core.sendMessageToPlayer(sjp, "You did not set any records on "
                        + Chat.GAMEMAP + arena.getDisplayName()
                        + Chat.DEFAULT + "! ("
                        + Chat.TIME + time
                        + Chat.DEFAULT + ") better luck next time :(");
                break;
        }
        endBattle();
    }

}
