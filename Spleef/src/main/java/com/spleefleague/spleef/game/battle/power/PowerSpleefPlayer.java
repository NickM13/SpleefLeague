package com.spleefleague.spleef.game.battle.power;

import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.battle.SpleefBattlePlayer;
import com.spleefleague.spleef.player.SpleefPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author NickM
 * @since 4/15/2020
 */
public class PowerSpleefPlayer extends SpleefBattlePlayer {

    List<Power> powers = new ArrayList<>(4);

    public PowerSpleefPlayer(CorePlayer cp, Battle<?> battle) {
        super(cp, battle);
        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp);
        powers.add(0, new Power(sp.getActivePower(0)));
        powers.add(1, new Power(sp.getActivePower(1)));
        powers.add(2, new Power(sp.getActivePower(2)));
        powers.add(3, new Power(sp.getActivePower(3)));
    }
}
