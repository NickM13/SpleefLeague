package com.spleefleague.spleef.util;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.util.variable.Dimension;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.core.world.game.GameWorld;
import com.spleefleague.spleef.game.SpleefArena;
import org.bukkit.GameMode;
import org.bukkit.Material;

/**
 * @author NickM13
 * @since 4/24/2020
 */
public class SpleefUtils {
    
    /**
     * Initialize base battle settings such as GameWorld tools and Scoreboard values
     */
    public static void setupBaseSettings(Battle<?, ?> battle) {
        battle.setGameMode(GameMode.ADVENTURE);
        battle.getGameWorld().addBreakableBlock(Material.SNOW_BLOCK);
        battle.getGameWorld().addBreakTool(Material.DIAMOND_SHOVEL);
    }
    
    /**
     * Fill the field without any delay
     *
     * @param battle Battle
     */
    public static void fillFieldFast(Battle<? extends SpleefArena, ?> battle) {
        if (battle == null || battle.getGameWorld() == null) return;
        GameWorld gameWorld = battle.getGameWorld();
        gameWorld.clear();
        for (BuildStructure structure : battle.getArena().getFields()) {
            gameWorld.setBlocks(structure.getOriginPos(), structure.getFakeBlocks());
        }
    }
    
}
