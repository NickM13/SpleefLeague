package com.spleefleague.spleef.game.battle.classic.affix.affixes;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.core.world.build.BuildStructures;
import com.spleefleague.spleef.game.battle.classic.ClassicSpleefBattle;
import com.spleefleague.spleef.game.battle.classic.ClassicSpleefPlayer;
import com.spleefleague.spleef.game.battle.classic.affix.ClassicSpleefAffixFuture;
import com.spleefleague.spleef.game.battle.classic.affix.ClassicSpleefAffixes;
import org.bukkit.Location;
import org.bukkit.Sound;

/**
 * Wither Affix begins after a set time, decaying snow blocks
 *
 * @author NickM13
 * @since 5/1/2020
 */
public class AffixThunderdome extends ClassicSpleefAffixFuture {
    
    public AffixThunderdome() {
        super(ClassicSpleefAffixes.AffixType.THUNDERDOME, 180);
    }

    @Override
    public void activate(ClassicSpleefBattle battle) {
        battle.getGameWorld().overwriteBlocks(
                FakeUtils.translateBlocks(
                        FakeUtils.rotateBlocks(
                                BuildStructures.get("ThunderDome").getFakeBlocks(),
                                (int) battle.getArena().getOrigin().getYaw()),
                battle.getArena().getOrigin().toBlockPosition()));
        double x = 0, y = 0, z = 0;
        for (ClassicSpleefPlayer csp : battle.getBattlers()) {
            csp.getPlayer().teleport(csp.getSpawn().clone().add(csp.getSpawn().clone().getDirection().setY(0).normalize().multiply(10)));
            x += csp.getSpawn().getX();
            y += csp.getSpawn().getY();
            z += csp.getSpawn().getZ();
        }
        battle.getGameWorld().playSound(new Location(battle.getGameWorld().getWorld(), x, y, z), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 1);
    }

}
