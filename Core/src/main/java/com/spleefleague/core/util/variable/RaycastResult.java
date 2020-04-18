/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.util.variable;

import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 */
public class RaycastResult {
    
    public BlockPosition blockPos;
    public BlockPosition relative;
    public Double distance;
    public Vector intersection;
    public Integer axis;
    public BlockFace face;

    RaycastResult(BlockPosition blockPos, Double distance, Vector intersection, Integer axis, BlockFace face) {
        this.blockPos = blockPos;
        this.distance = distance;
        this.intersection = intersection;
        this.axis = axis;
        this.face = face;
        switch (face) {
            case UP: this.relative = blockPos.add(new BlockPosition(0, 1, 0)); break;
            case DOWN: this.relative = blockPos.add(new BlockPosition(0, -1, 0)); break;
            case EAST: this.relative = blockPos.add(new BlockPosition(1, 0, 0)); break;
            case WEST: this.relative = blockPos.add(new BlockPosition(-1, 0, 0)); break;
            case NORTH: this.relative = blockPos.add(new BlockPosition(0, 0, 1)); break;
            case SOUTH: this.relative = blockPos.add(new BlockPosition(0, 0, -1)); break;
        }
    }
    
    @Override
    public String toString() {
        return blockPos.toString() + ", " + distance + ", " + intersection.toString() + ", " + axis;
    }
    
}
