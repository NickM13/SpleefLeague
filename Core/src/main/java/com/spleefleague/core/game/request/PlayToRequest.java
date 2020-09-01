package com.spleefleague.core.game.request;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.player.CorePlayer;

import javax.annotation.Nullable;

/**
 * @author NickM13
 * @since 4/27/2020
 */
public class PlayToRequest extends BattleRequest {
    
    private int playToValue;
    
    public PlayToRequest(Battle<?> battle) {
        super(battle, true, "playto");
    }
    
    @Override
    protected boolean attemptStartRequest(CorePlayer cp, int total, @Nullable String requestValue) {
        if (requestValue != null) {
            try {
                int playToVal = Integer.parseInt(requestValue);
                if (playToVal > 0 && playToVal <= 100) {
                    chatName = "play to " + playToVal;
                    scoreboardName = "Play To " + playToVal;
                    this.playToValue = playToVal;
                    return true;
                }
                battle.getPlugin().sendMessage(cp, Chat.ERROR + "Expected number from 1 to 100!");
                return false;
            } catch (NumberFormatException exception) {
                battle.getPlugin().sendMessage(cp, Chat.ERROR + "That's not a number!");
                return false;
            }
        }
        battle.getPlugin().sendMessage(cp, Chat.ERROR + "That's not a number!");
        return false;
    }
    
    /**
     * Called when enough players are requesting this
     */
    @Override
    protected void meetsRequirement() {
        battle.setPlayTo(playToValue);
    }
    
}
