package com.spleefleague.superjump.util;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.google.common.collect.Lists;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.util.Direction;

import java.util.List;
import java.util.Random;

/**
 * @author NickM13
 * @since 5/3/2020
 */
public class SJUtils {
    
    public static void init() {
        Jumps.init();
    }
    
    public static List<BlockPosition> generateJumpsFrom(Battle<?> battle,
                                                        int jumpCount,
                                                        BlockPosition start,
                                                        int minRight,
                                                        int maxRight,
                                                        int minUp,
                                                        int maxUp,
                                                        Direction direction,
                                                        Double difficulty,
                                                        Random random) {
        BlockPosition current = new BlockPosition(start.getX(), start.getY(), start.getZ());
        int currRight = 0, currUp = 0;
        List<BlockPosition> blockPositions = Lists.newArrayList(current);
        for (int i = 0; i < jumpCount; i++) {
            Jump jump = Jumps.getNextJump(
                    minRight - currRight,
                    maxRight - currRight,
                    minUp - currUp,
                    maxUp - currUp,
                    difficulty,
                    random);
            switch (direction) {
                case SOUTH: current = current.add(new BlockPosition(-jump.right, jump.up, jump.forward));   break;
                case EAST:  current = current.add(new BlockPosition(jump.forward, jump.up, jump.right));    break;
                case NORTH: current = current.add(new BlockPosition(jump.right, jump.up, -jump.forward));   break;
                case WEST:  current = current.add(new BlockPosition(-jump.forward, jump.up, -jump.right));  break;
            }
            blockPositions.add(current);
            currRight += jump.right;
            currUp += jump.up;
        }
        
        return blockPositions;
    }
    
}
