/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.world;

import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;

/**
 * @author NickM13
 */
public class FakeBlock {
    
    private final BlockPosition blockPosition;
    private final BlockData blockData;
    
    public FakeBlock(BlockPosition blockPosition, BlockData blockData) {
        this.blockPosition = blockPosition;
        this.blockData = blockData;
    }
    
    public BlockPosition getBlockPosition() {
        return blockPosition;
    }
    
    public BlockData getBlockData() {
        return blockData;
    }
    
    public Sound getBreakSound() {
        switch (blockData.getMaterial()) {
            case SNOW: case SNOW_BLOCK:
                return Sound.BLOCK_SNOW_BREAK;
        }
        return null;
    }
    
}
