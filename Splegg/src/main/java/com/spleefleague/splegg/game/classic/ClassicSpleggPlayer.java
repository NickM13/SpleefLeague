package com.spleefleague.splegg.game.classic;

import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.splegg.game.SpleggBattlePlayer;
import com.spleefleague.splegg.game.SpleggGun;

/**
 * @author NickM13
 * @since 4/25/2020
 */
public class ClassicSpleggPlayer extends SpleggBattlePlayer {
    
    public ClassicSpleggPlayer(CorePlayer cp, Battle<?> battle) {
        super(cp, battle);
        this.spleggGun1 = cp.getCollectibles().getActiveOrDefault(SpleggGun.class, "s1", SpleggGun.getDefault());
        this.spleggGun2 = cp.getCollectibles().getActiveOrDefault(SpleggGun.class, "s2", SpleggGun.getDefault());
    }
    
}
