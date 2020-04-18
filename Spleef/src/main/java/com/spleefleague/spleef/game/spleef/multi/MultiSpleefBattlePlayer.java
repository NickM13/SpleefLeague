package com.spleefleague.spleef.game.spleef.multi;

import com.spleefleague.core.database.variable.DBPlayer;
import com.spleefleague.core.game.Battle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.spleef.game.spleef.SpleefBattlePlayer;

/**
 * @author NickM
 * @since 4/15/2020
 */
public class MultiSpleefBattlePlayer extends SpleefBattlePlayer {

    public MultiSpleefBattlePlayer(CorePlayer cp, Battle<?> battle) {
        super(cp, battle);
    }
}
