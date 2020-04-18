package com.spleefleague.spleef.game.spleef.banana;

import com.spleefleague.core.game.Battle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.spleef.game.spleef.SpleefBattlePlayer;

/**
 * @author NickM
 * @since 4/14/2020
 */
public class BananaSpleefBattlePlayer extends SpleefBattlePlayer {

    BananaSpleefBattlePlayer(CorePlayer cp, Battle<?> battle) {
        super(cp, battle);
    }

    @Override
    public void respawn() {
        super.respawn();
        getPlayer().teleport(((BananaSpleefBattle) getBattle()).getRandomSpawn());
    }

}
