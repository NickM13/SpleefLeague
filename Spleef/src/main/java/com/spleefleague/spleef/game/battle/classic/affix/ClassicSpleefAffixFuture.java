package com.spleefleague.spleef.game.battle.classic.affix;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.spleef.game.battle.classic.ClassicSpleefBattle;

/**
 * @author NickM13
 * @since 5/15/2020
 */
public abstract class ClassicSpleefAffixFuture extends ClassicSpleefAffix {

    /**
     * Time into the round that this affix will activate, in seconds
     */
    protected Integer activateTime;
    protected double lastUpdate;

    public ClassicSpleefAffixFuture() {
        super();
    }

    public void setActiveTime(int activeTime) {
        this.activateTime = activeTime;
    }

    protected boolean isRoundActivated() {
        return lastUpdate >= activateTime;
    }

    @Override
    public void startRound(ClassicSpleefBattle battle) {
        lastUpdate = 0;
    }

    public void activate(ClassicSpleefBattle battle) {

    }

    protected void updateActive(ClassicSpleefBattle battle) {

    }

    protected abstract String getPreActiveMessage(int seconds);

    @Override
    public void update(ClassicSpleefBattle battle) {
        if (battle.isRoundStarted() && activateTime > 0) {
            if (lastUpdate < activateTime) {
                if (battle.getRoundTime() > activateTime) {
                    activate(battle);
                } else if (Math.floor(lastUpdate) < Math.floor(battle.getRoundTime())) {
                    if (Math.floor(battle.getRoundTime()) == activateTime - 3) {
                        battle.getChatGroup().sendMessage(getPreActiveMessage(3));
                    }
                }
                lastUpdate = battle.getRoundTime();
            } else {
                updateActive(battle);
            }
        }
    }

}
