package com.spleefleague.spleef.game.battle.bonanza;

import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.spleef.game.battle.SpleefBattlePlayer;

/**
 * @author NickM
 * @since 4/14/2020
 */
public class BonanzaSpleefPlayer extends SpleefBattlePlayer {

    private BattleState enteredState;

    BonanzaSpleefPlayer(CorePlayer cp, Battle<?> battle) {
        super(cp, battle);
    }

    public void setEnteredState(BattleState state) {
        enteredState = state;
    }

    public BattleState getEnteredState() {
        return enteredState;
    }

    @Override
    public void respawn() {
        super.respawn();
        getPlayer().teleport(((BonanzaSpleefBattle) getBattle()).getRandomSpawn());
    }

}
