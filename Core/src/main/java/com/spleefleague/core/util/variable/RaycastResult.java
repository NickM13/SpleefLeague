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

    private Double distance;
    private Vector intersection;

    RaycastResult(Double distance, Vector intersection) {
        this.distance = distance;
        this.intersection = intersection;
    }

    public Double getDistance() {
        return distance;
    }

    public Vector getIntersection() {
        return intersection;
    }

}
