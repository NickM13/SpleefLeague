package com.spleefleague.core.game.request;

import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.game.history.GameHistory;
import com.spleefleague.core.player.CorePlayer;

import javax.annotation.Nullable;

/**
 * @author NickM13
 * @since 5/1/2020
 */
public class EndGameRequest extends BattleRequest {
    
    public EndGameRequest(Battle<?> battle) {
        super(battle, true, "endgame");
    }
    
    @Override
    protected boolean attemptStartRequest(CorePlayer cp, int total, @Nullable String requestValue) {
        chatName = "end the game";
        scoreboardName = "End Game";
        return true;
    }
    
    /**
     * Called when enough players are requesting this
     */
    @Override
    protected void meetsRequirement() {
        battle.getGameHistory().setEndReason(GameHistory.EndReason.ENDGAME);
        battle.endBattle(null);
    }
    
}
