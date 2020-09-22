package com.spleefleague.splegg.game.multi;

import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.splegg.game.SpleggBattlePlayer;
import com.spleefleague.splegg.game.SpleggGun;

/**
 * @author NickM13
 * @since 4/25/2020
 */
public class MultiSpleggPlayer extends SpleggBattlePlayer {

    public MultiSpleggPlayer(CorePlayer cp, Battle<?> battle) {
        super(cp, battle, cp.getCollectibles().getActive(SpleggGun.class, "m1"), cp.getCollectibles().getActive(SpleggGun.class, "m2"));
    }

}
