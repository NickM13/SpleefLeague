package com.spleefleague.core.game.request;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.player.CorePlayer;

import javax.annotation.Nullable;

/**
 * @author NickM13
 * @since 5/1/2020
 */
public class PauseRequest extends BattleRequest {

    // Time in seconds
    private int pauseTime;

    public PauseRequest(Battle<?> battle) {
        super(battle, "pause", true, 1D);
    }

    @Override
    protected boolean attemptStartRequest(CorePlayer cp, int total, @Nullable String requestValue) {
        if (requestValue != null) {
            try {
                int pauseTime = Integer.parseInt(requestValue);
                if (pauseTime > 0 && pauseTime <= 120) {
                    chatName = "pause for " + pauseTime + " seconds";
                    scoreboardName = "Pause For " + pauseTime;
                    this.pauseTime = pauseTime;
                    return true;
                }
                battle.getPlugin().sendMessage(cp, Chat.ERROR + "Expected number from 1 to 120!");
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
        battle.pause(pauseTime);
    }

}
