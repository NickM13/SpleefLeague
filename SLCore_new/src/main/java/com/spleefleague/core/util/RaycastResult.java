/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.util;

import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 */
public class RaycastResult {
    
    public BlockPosition blockPos;
    public Double distance;
    public Vector intersection;
    public Integer axis;

    RaycastResult(BlockPosition blockPos, Double distance, Vector intersection, Integer axis) {
        this.blockPos = blockPos;
        this.distance = distance;
        this.intersection = intersection;
        this.axis = axis;
    }
    
    @Override
    public String toString() {
        return blockPos.toString() + ", " + distance + ", " + intersection.toString() + ", " + axis;
    }
    
}
