package com.spleefleague.core.util;

/**
 * @author NickM13
 * @since 5/4/2020
 */
public enum Direction {
    NORTH(180),
    EAST(270),
    SOUTH(0),
    WEST(90);
    
    public static Direction fromYaw(int yaw) {
        yaw = (yaw % 360);
        if (yaw < 0) yaw += 360;
        
        if (yaw > 135 && yaw <= 225) {
            return NORTH;
        }
        if (yaw > 225 && yaw <= 315) {
            return EAST;
        }
        if (yaw >= 315 || yaw <= 45) {
            return SOUTH;
        }
        return WEST;
    }

    private int yaw;

    Direction(int yaw) {
        this.yaw = yaw;
    }

    public int getYaw() {
        return yaw;
    }

}
