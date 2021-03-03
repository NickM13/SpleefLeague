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
    protected final int activateTime;
    protected double lastUpdate;
    protected boolean announced;

    public ClassicSpleefAffixFuture(ClassicSpleefAffixes.AffixType type, int activateTime) {
        super(type);
        this.activateTime = activateTime;
    }

    protected boolean isRoundActivated() {
        return lastUpdate >= activateTime;
    }

    @Override
    public void startRound() {
        lastUpdate = 0;
        announced = false;
    }

    public void activate(ClassicSpleefBattle battle) {

    }

    protected void updateActive(ClassicSpleefBattle battle) {

    }

    @Override
    public void update() {
        if (battle.isRoundStarted()) {
            if (lastUpdate < activateTime) {
                if (battle.getRoundTime() >= activateTime) {
                    activate(battle);
                } else if (!announced && Math.floor(battle.getRoundTime()) >= activateTime - 15) {
                    battle.getChatGroup().sendMessage(getType().displayName + " will activate in " + 15 + " seconds");
                    announced = true;
                }
                lastUpdate = battle.getRoundTime();
            } else {
                updateActive(battle);
            }
        }
    }

}
