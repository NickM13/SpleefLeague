package com.spleefleague.spleef.game.battle.classic.affix.affixes;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.FakeWorld;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.battle.classic.ClassicSpleefPlayer;
import com.spleefleague.spleef.game.battle.classic.affix.ClassicSpleefAffix;
import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author NickM13
 * @since 5/15/2020
 */
public class AffixSlowFall extends ClassicSpleefAffix {

    public AffixSlowFall() {
        super();
        displayName = "Slow Fall";
    }

    @Override
    public void onBlockBreak(ClassicSpleefPlayer csp) {
        if (!FakeUtils.isOnGround(csp.getCorePlayer())) {
            Bukkit.getScheduler().runTask(Spleef.getInstance(), () -> csp.getPlayer().addPotionEffect(PotionEffectType.SLOW_FALLING.createEffect(10, 0)));
        }
    }
}
