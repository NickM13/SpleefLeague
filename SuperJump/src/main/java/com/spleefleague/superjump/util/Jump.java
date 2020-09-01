package com.spleefleague.superjump.util;

/**
 * @author NickM13
 * @since 5/4/2020
 */
public class Jump {
    
    public int forward, up, right;
    public double difficulty;
    
    public Jump(int forward, int up, int right) {
        this.forward = forward;
        this.up = up;
        this.right = right;
        this.difficulty = (Math.sqrt(Math.pow(forward, 2) + Math.pow(right, 2))) / 1.3D + (up < 0 ? -1 : up) - 1;
    }
    
    public double getDifficulty() {
        return difficulty;
    }
    
}
