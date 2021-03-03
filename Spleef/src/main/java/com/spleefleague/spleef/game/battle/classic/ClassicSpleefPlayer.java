package com.spleefleague.spleef.game.battle.classic;

import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.spleef.game.battle.SpleefBattlePlayer;
import com.spleefleague.spleef.game.battle.classic.affix.ClassicSpleefAffixes;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NickM
 * @since 4/15/2020
 */
public class ClassicSpleefPlayer extends SpleefBattlePlayer {

    private Map<String, Object> affixValueMap = new HashMap<>();

    public ClassicSpleefPlayer(CorePlayer cp, Battle<?> battle) {
        super(cp, battle);
    }

    public Map<String, Object> getAffixValueMap() {
        return affixValueMap;
    }

    @Override
    public void onBlockBreak() {
        super.onBlockBreak();
        getBattle().onBlockBreak(getCorePlayer());
    }

    @Override
    public void onRightClick() {
        super.onRightClick();
        getBattle().onRightClick(getCorePlayer());
    }

}
