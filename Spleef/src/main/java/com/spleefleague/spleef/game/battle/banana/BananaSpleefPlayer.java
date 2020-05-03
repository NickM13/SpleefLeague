package com.spleefleague.spleef.game.battle.banana;

import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.spleef.game.battle.SpleefBattlePlayer;

/**
 * @author NickM
 * @since 4/14/2020
 */
public class BananaSpleefPlayer extends SpleefBattlePlayer {

    BananaSpleefPlayer(CorePlayer cp, Battle<?> battle) {
        super(cp, battle);
    }

    @Override
    public void respawn() {
        super.respawn();
        getPlayer().teleport(((BananaSpleefBattle) getBattle()).getRandomSpawn());
    }

}
