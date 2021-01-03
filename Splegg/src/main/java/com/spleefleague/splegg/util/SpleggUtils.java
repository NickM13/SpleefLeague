package com.spleefleague.splegg.util;

import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.core.world.game.GameWorld;
import com.spleefleague.splegg.game.SpleggArena;
import org.bukkit.GameMode;
import org.bukkit.Material;

/**
 * @author NickM13
 * @since 4/25/2020
 */
public class SpleggUtils {
    
    /**
     * Initialize base battle settings such as GameWorld tools and Scoreboard values
     */
    public static void setupBaseSettings(Battle<?> battle) {
        battle.setGameMode(GameMode.ADVENTURE);
        battle.getGameWorld().addBreakableBlock(Material.SNOW_BLOCK);
    }
    
    /**
     * Fill the field without any delay
     *
     * @param battle Battle
     */
    public static void fillFieldFast(Battle<?> battle) {
        if (battle == null || battle.getGameWorld() == null) return;
        GameWorld gameWorld = battle.getGameWorld();
        gameWorld.clear();
        for (BuildStructure structure : battle.getArena().getStructures()) {
            gameWorld.overwriteBlocks(
                    FakeUtils.translateBlocks(
                            FakeUtils.rotateBlocks(structure.getFakeBlocks(), (int) battle.getArena().getOrigin().getYaw()),
                            battle.getArena().getOrigin().toBlockPosition()));
        }
    }

}
