/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.util;

import java.util.ArrayList;
import org.bukkit.Location;

/**
 * @author NickM13
 */
public class Jumps_old {
    
    /* 
     * Contains list of all possible jumps, and their associated skill values.
     * 
     * Frequency = 100 - (DifficultyOffset * 60) - (5 * JumpOffset)
     * DifficultyOffset: Jumps from other difficulties can (intentionally)
     * appear in more than one list - this minimizes the occurrences.
     */
    
    public static ArrayList<Jump> getJumpsByDifficultyB(float difficulty) {
        ArrayList<Jump> possibleJumps = new ArrayList<>();
        if(difficulty < 0) difficulty = 0;
        if(difficulty > 4) difficulty = 4;
        for(Jump j : POSSIBLE_JUMPS_BB) {
            if(j.getFrequency(difficulty) > 0) {
                possibleJumps.add(j);
            }
        }
        for(Jump j : POSSIBLE_JUMPS_BF) {
            if(j.getFrequency(difficulty) > 0) {
                possibleJumps.add(j);
            }
        }
        return possibleJumps;
    }
    
    public static ArrayList<Jump> getJumpsByDifficultyF(float difficulty) {
        ArrayList<Jump> possibleJumps = new ArrayList<>();
        if(difficulty < 0) difficulty = 0;
        if(difficulty > 4) difficulty = 4;
        for(Jump j : POSSIBLE_JUMPS_FB) {
            if(j.getFrequency(difficulty) > 0) {
                possibleJumps.add(j);
            }
        }
        for(Jump j : POSSIBLE_JUMPS_FF) {
            if(j.getFrequency(difficulty) > 0) {
                possibleJumps.add(j);
            }
        }
        return possibleJumps;
    }
    
    public static ArrayList<Jump> getJumpsByDifficultyI(float difficulty) {
        ArrayList<Jump> possibleJumps = new ArrayList<>();
        if(difficulty < 0) difficulty = 0;
        if(difficulty > 4) difficulty = 4;
        return possibleJumps;
    }
    
    public static class Jump {

        private final int x, y, z;
        private final float difficulty;
        private final char from, to;

        public Jump(int x, int y, int z, float difficulty, char from, char to) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.difficulty = difficulty;
            this.from = from;
            this.to = to;
        }

        public int getFrequency(float difficulty) {
            return Math.max((int)(100.f - (Math.abs(this.difficulty - difficulty) * 45.f)/* - ((Math.abs(this.y) + Math.abs(this.z)) * 5.f)*/), -1);
        }

        public Location apply(Location loc, boolean back, boolean reverseY) {
            return loc.clone().add(back ? -x : x, reverseY ? -y : y, z);
        }
        
        public char getFrom() {
            return from;
        }
        
        public char getTo() {
            return to;
        }
    }
    
    public static class JumpBB extends Jump {
        
        public JumpBB(int x, int y, int z, float difficulty) {
            super(x, y, z, difficulty, 'b', 'b');
        }
    }
    
    public static class JumpBF extends Jump {
        
        public JumpBF(int x, int y, int z, float difficulty) {
            super(x, y, z, difficulty, 'b', 'f');
        }
    }
    
    public static class JumpBI extends Jump {
        
        public JumpBI(int x, int y, int z, float difficulty) {
            super(x, y, z, difficulty, 'b', 'i');
        }
    }
    
    public static class JumpFB extends Jump {
        
        public JumpFB(int x, int y, int z, float difficulty) {
            super(x, y, z, difficulty, 'f', 'b');
        }
    }
    
    public static class JumpFF extends Jump {
        
        public JumpFF(int x, int y, int z, float difficulty) {
            super(x, y, z, difficulty, 'f', 'f');
        }
    }
    
    public static class JumpFI extends Jump {
        
        public JumpFI(int x, int y, int z, float difficulty) {
            super(x, y, z, difficulty, 'f', 'i');
        }
    }
    
    public static class JumpIB extends Jump {
        
        public JumpIB(int x, int y, int z, float difficulty) {
            super(x, y, z, difficulty, 'i', 'b');
        }
    }
    
    public static class JumpIF extends Jump {
        
        public JumpIF(int x, int y, int z, float difficulty) {
            super(x, y, z, difficulty, 'i', 'f');
        }
    }
    
    public static class JumpII extends Jump {
        
        public JumpII(int x, int y, int z, float difficulty) {
            super(x, y, z, difficulty, 'i', 'i');
        }
    }
    
    private final static Jump[] POSSIBLE_JUMPS_BB = new Jump[]{
        /*
        // x 0
        // y -1
        new Jump(0, -1, -5, 2), new Jump(0, -1,  5, 2),
        new Jump(0, -1, -4, 1), new Jump(0, -1,  4, 1),
        new Jump(0, -1, -3, 1), new Jump(0, -1,  3, 1),
        new Jump(0, -1, -2, 1), new Jump(0, -1,  2, 1),
        // y 0
        new Jump(0,  0, -5, 3), new Jump(0,  0,  5, 3),
        new Jump(0,  0, -4, 2), new Jump(0,  0,  4, 2),
        new Jump(0,  0, -3, 1), new Jump(0,  0,  3, 1),
        new Jump(0,  0, -2, 1), new Jump(0,  0,  2, 1),
        // y 1
        new Jump(0,  1, -4, 3), new Jump(0,  1,  4, 3),
        new Jump(0,  1, -3, 2), new Jump(0,  1,  3, 2),
        new Jump(0,  1, -2, 1), new Jump(0,  1,  2, 1),
        */
        /*
        // x 1
        // y -1
        new Jump(1, -1, -5, 2), new Jump(1, -1,  5, 2),
        new Jump(1, -1, -4, 1), new Jump(1, -1,  4, 1),
        new Jump(1, -1, -3, 1), new Jump(1, -1,  3, 1),
        new Jump(1, -1, -2, 1), new Jump(1, -1,  2, 1),
        // y 0
        new Jump(1,  0, -5, 3), new Jump(1,  0,  5, 3),
        new Jump(1,  0, -4, 2), new Jump(1,  0,  4, 2),
        new Jump(1,  0, -3, 1), new Jump(1,  0,  3, 1),
        new Jump(1,  0, -2, 1), new Jump(1,  0,  2, 1),
        // y 1
        new Jump(1,  1, -4, 3), new Jump(1,  1,  4, 3),
        new Jump(1,  1, -3, 2), new Jump(1,  1,  3, 2),
        new Jump(1,  1, -2, 1), new Jump(1,  1,  2, 1),
        new Jump(1,  1, -1, 1), new Jump(1,  1,  1, 1),
        */
        // x 2
        // y -1
        new JumpBB(2, -1, -5, 3), new JumpBB(2, -1,  5, 3),
        new JumpBB(2, -1, -4, 2), new JumpBB(2, -1,  4, 2),
        new JumpBB(2, -1, -3, 1), new JumpBB(2, -1,  3, 1),
        new JumpBB(2, -1, -2, 1), new JumpBB(2, -1,  2, 1),
        new JumpBB(2, -1, -1, 1), new JumpBB(2, -1,  1, 1),
        new JumpBB(2, -1,  0, 1),
        // y 0
        new JumpBB(2,  0, -5, 3), new JumpBB(2,  0,  5, 3),
        new JumpBB(2,  0, -4, 2), new JumpBB(2,  0,  4, 2),
        new JumpBB(2,  0, -3, 1), new JumpBB(2,  0,  3, 1),
        new JumpBB(2,  0, -2, 1), new JumpBB(2,  0,  2, 1),
        new JumpBB(2,  0, -1, 1), new JumpBB(2,  0,  1, 1),
        new JumpBB(2,  0,  0, 1),
        // y 1
        new JumpBB(2,  1, -4, 3), new JumpBB(2,  1,  4, 3),
        new JumpBB(2,  1, -3, 2), new JumpBB(2,  1,  3, 2),
        new JumpBB(2,  1, -2, 1), new JumpBB(2,  1,  2, 1),
        new JumpBB(2,  1, -1, 1), new JumpBB(2,  1,  1, 1),
        new JumpBB(2,  1,  0, 1),
        
        // x 3
        // y -1
        new JumpBB(3, -1, -5, 3), new JumpBB(3, -1,  5, 3),
        new JumpBB(3, -1, -4, 2), new JumpBB(3, -1,  4, 2),
        new JumpBB(3, -1, -3, 1), new JumpBB(3, -1,  3, 1),
        new JumpBB(3, -1, -2, 1), new JumpBB(3, -1,  2, 1),
        new JumpBB(3, -1, -1, 1), new JumpBB(3, -1,  1, 1),
        new JumpBB(3, -1,  0, 1),
        // y 0
        new JumpBB(3,  0, -4, 2), new JumpBB(3,  0,  4, 2),
        new JumpBB(3,  0, -3, 2), new JumpBB(3,  0,  3, 2),
        new JumpBB(3,  0, -2, 1), new JumpBB(3,  0,  2, 1),
        new JumpBB(3,  0, -1, 1), new JumpBB(3,  0,  1, 1),
        new JumpBB(3,  0,  0, 1),
        // y 1
        new JumpBB(3,  1, -3, 3), new JumpBB(3,  1,  3, 3),
        new JumpBB(3,  1, -2, 2), new JumpBB(3,  1,  2, 2),
        new JumpBB(3,  1, -1, 2), new JumpBB(3,  1,  1, 2),
        new JumpBB(3,  1,  0, 2),
       
        // x 4
        // y -1
        new JumpBB(4, -1, -4, 3), new JumpBB(4, -1,  4, 3),
        new JumpBB(4, -1, -3, 2), new JumpBB(4, -1,  3, 2),
        new JumpBB(4, -1, -2, 2), new JumpBB(4, -1,  2, 2),
        new JumpBB(4, -1, -1, 1), new JumpBB(4, -1,  1, 1),
        new JumpBB(4, -1,  0, 1),
        // y 0
        new JumpBB(4,  0, -4, 3), new JumpBB(4,  0,  4, 3),
        new JumpBB(4,  0, -3, 2), new JumpBB(4,  0,  3, 2),
        new JumpBB(4,  0, -2, 2), new JumpBB(4,  0,  2, 2),
        new JumpBB(4,  0, -1, 2), new JumpBB(4,  0,  1, 2),
        new JumpBB(4,  0,  0, 2),
        // y 1
        new JumpBB(4,  1, -2, 3), new JumpBB(4,  1,  2, 3),
        new JumpBB(4,  1, -1, 3), new JumpBB(4,  1,  1, 3),
        new JumpBB(4,  1,  0, 3),
        
        // x 5
        // y -1
        new JumpBB(5, -1, -3, 3), new JumpBB(5, -1,  3, 3),
        new JumpBB(5, -1, -2, 3), new JumpBB(5, -1,  2, 3),
        new JumpBB(5, -1, -1, 2), new JumpBB(5, -1,  1, 2),
        new JumpBB(5, -1,  0, 2),
        // y 0
        new JumpBB(5,  0, -2, 3), new JumpBB(5,  0,  2, 3),
        new JumpBB(5,  0, -1, 3), new JumpBB(5,  0,  1, 3),
        new JumpBB(5,  0, 0, 3)
        // y 1
    };
    
    private final static Jump[] POSSIBLE_JUMPS_BF = new Jump[]{
        /*
        // x 0
        // y -1
        new Jump(0, -1, -5, 2), new Jump(0, -1,  5, 2),
        new Jump(0, -1, -4, 1), new Jump(0, -1,  4, 1),
        new Jump(0, -1, -3, 1), new Jump(0, -1,  3, 1),
        new Jump(0, -1, -2, 1), new Jump(0, -1,  2, 1),
        // y 0
        new Jump(0,  0, -5, 3), new Jump(0,  0,  5, 3),
        new Jump(0,  0, -4, 2), new Jump(0,  0,  4, 2),
        new Jump(0,  0, -3, 1), new Jump(0,  0,  3, 1),
        new Jump(0,  0, -2, 1), new Jump(0,  0,  2, 1),
        // y 1
        new Jump(0,  1, -4, 3), new Jump(0,  1,  4, 3),
        new Jump(0,  1, -3, 2), new Jump(0,  1,  3, 2),
        new Jump(0,  1, -2, 1), new Jump(0,  1,  2, 1),
        */
        
        // x 1
        // y -1
        new JumpBF(1, -1, -5, 3), new JumpBF(1, -1,  5, 3),
        new JumpBF(1, -1, -4, 2), new JumpBF(1, -1,  4, 2),
        new JumpBF(1, -1, -3, 1), new JumpBF(1, -1,  3, 1),
        new JumpBF(1, -1, -2, 1), new JumpBF(1, -1,  2, 1),
        // y 0
        new JumpBF(1,  0, -4, 3), new JumpBF(1,  0,  4, 3),
        new JumpBF(1,  0, -3, 2), new JumpBF(1,  0,  3, 2),
        new JumpBF(1,  0, -2, 1), new JumpBF(1,  0,  2, 1),
        
        // x 2
        // y -1
        new JumpBF(2, -1, -4, 2), new JumpBF(2, -1,  4, 2),
        new JumpBF(2, -1, -3, 1), new JumpBF(2, -1,  3, 1),
        new JumpBF(2, -1, -2, 1), new JumpBF(2, -1,  2, 1),
        new JumpBF(2, -1, -1, 1), new JumpBF(2, -1,  1, 1),
        new JumpBF(2, -1,  0, 1),
        // y 0
        new JumpBF(2,  0, -4, 3), new JumpBF(2,  0,  4, 3),
        new JumpBF(2,  0, -3, 2), new JumpBF(2,  0,  3, 2),
        new JumpBF(2,  0, -2, 1), new JumpBF(2,  0,  2, 1),
        new JumpBF(2,  0, -1, 1), new JumpBF(2,  0,  1, 1),
        new JumpBF(2,  0,  0, 1),
        
        // x 3
        // y -1
        new JumpBF(3, -1, -3, 2), new JumpBF(3, -1,  3, 2),
        new JumpBF(3, -1, -2, 1), new JumpBF(3, -1,  2, 1),
        new JumpBF(3, -1, -1, 1), new JumpBF(3, -1,  1, 1),
        new JumpBF(3, -1,  0, 1),
        // y 0
        new JumpBF(3,  0, -3, 3), new JumpBF(3,  0,  3, 3),
        new JumpBF(3,  0, -2, 2), new JumpBF(3,  0,  2, 2),
        new JumpBF(3,  0, -1, 2), new JumpBF(3,  0,  1, 2),
        new JumpBF(3,  0,  0, 2),
       
        // x 4
        // y -1
        new JumpBF(4, -1, -2, 2), new JumpBF(4, -1,  2, 2),
        new JumpBF(4, -1, -1, 2), new JumpBF(4, -1,  1, 2),
        new JumpBF(4, -1,  0, 2),
        // y 0
        new JumpBF(4,  0, -2, 3), new JumpBF(4,  0,  2, 3),
        new JumpBF(4,  0, -1, 2), new JumpBF(4,  0,  1, 2),
        new JumpBF(4,  0,  0, 2),
        
        // x 5
        // y -1
        new JumpBF(5, -1, -1, 3), new JumpBF(5, -1,  1, 3),
        new JumpBF(5, -1,  0, 3),
        // y 0
    };
    
    private final static Jump[] POSSIBLE_JUMPS_BI = new Jump[]{
        /*
        // x 0
        // y -1
        new Jump(0, -1, -5, 2), new Jump(0, -1,  5, 2),
        new Jump(0, -1, -4, 1), new Jump(0, -1,  4, 1),
        new Jump(0, -1, -3, 1), new Jump(0, -1,  3, 1),
        new Jump(0, -1, -2, 1), new Jump(0, -1,  2, 1),
        // y 0
        new Jump(0,  0, -5, 3), new Jump(0,  0,  5, 3),
        new Jump(0,  0, -4, 2), new Jump(0,  0,  4, 2),
        new Jump(0,  0, -3, 1), new Jump(0,  0,  3, 1),
        new Jump(0,  0, -2, 1), new Jump(0,  0,  2, 1),
        // y 1
        new Jump(0,  1, -4, 3), new Jump(0,  1,  4, 3),
        new Jump(0,  1, -3, 2), new Jump(0,  1,  3, 2),
        new Jump(0,  1, -2, 1), new Jump(0,  1,  2, 1),
        */
        /*
        // x 1
        // y -1
        new Jump(1, -1, -5, 2), new Jump(1, -1,  5, 2),
        new Jump(1, -1, -4, 1), new Jump(1, -1,  4, 1),
        new Jump(1, -1, -3, 1), new Jump(1, -1,  3, 1),
        new Jump(1, -1, -2, 1), new Jump(1, -1,  2, 1),
        // y 0
        new Jump(1,  0, -5, 3), new Jump(1,  0,  5, 3),
        new Jump(1,  0, -4, 2), new Jump(1,  0,  4, 2),
        new Jump(1,  0, -3, 1), new Jump(1,  0,  3, 1),
        new Jump(1,  0, -2, 1), new Jump(1,  0,  2, 1),
        // y 1
        new Jump(1,  1, -4, 3), new Jump(1,  1,  4, 3),
        new Jump(1,  1, -3, 2), new Jump(1,  1,  3, 2),
        new Jump(1,  1, -2, 1), new Jump(1,  1,  2, 1),
        new Jump(1,  1, -1, 1), new Jump(1,  1,  1, 1),
        */
        // x 2
        // y -1
        new JumpBI(2, -1, -5, 3), new JumpBI(2, -1,  5, 3),
        new JumpBI(2, -1, -4, 2), new JumpBI(2, -1,  4, 2),
        new JumpBI(2, -1, -3, 1), new JumpBI(2, -1,  3, 1),
        new JumpBI(2, -1, -2, 1), new JumpBI(2, -1,  2, 1),
        new JumpBI(2, -1, -1, 1), new JumpBI(2, -1,  1, 1),
        new JumpBI(2, -1,  0, 1),
        // y 0
        new JumpBI(2,  0, -5, 3), new JumpBI(2,  0,  5, 3),
        new JumpBI(2,  0, -4, 2), new JumpBI(2,  0,  4, 2),
        new JumpBI(2,  0, -3, 1), new JumpBI(2,  0,  3, 1),
        new JumpBI(2,  0, -2, 1), new JumpBI(2,  0,  2, 1),
        new JumpBI(2,  0, -1, 1), new JumpBI(2,  0,  1, 1),
        new JumpBI(2,  0,  0, 1),
        // y 1
        new JumpBI(2,  1, -4, 3), new JumpBI(2,  1,  4, 3),
        new JumpBI(2,  1, -3, 2), new JumpBI(2,  1,  3, 2),
        new JumpBI(2,  1, -2, 1), new JumpBI(2,  1,  2, 1),
        new JumpBI(2,  1, -1, 1), new JumpBI(2,  1,  1, 1),
        new JumpBI(2,  1,  0, 1),
        
        // x 3
        // y -1
        new JumpBI(3, -1, -5, 3), new JumpBI(3, -1,  5, 3),
        new JumpBI(3, -1, -4, 2), new JumpBI(3, -1,  4, 2),
        new JumpBI(3, -1, -3, 1), new JumpBI(3, -1,  3, 1),
        new JumpBI(3, -1, -2, 1), new JumpBI(3, -1,  2, 1),
        new JumpBI(3, -1, -1, 1), new JumpBI(3, -1,  1, 1),
        new JumpBI(3, -1,  0, 1),
        // y 0
        new JumpBI(3,  0, -4, 2), new JumpBI(3,  0,  4, 2),
        new JumpBI(3,  0, -3, 2), new JumpBI(3,  0,  3, 2),
        new JumpBI(3,  0, -2, 1), new JumpBI(3,  0,  2, 1),
        new JumpBI(3,  0, -1, 1), new JumpBI(3,  0,  1, 1),
        new JumpBI(3,  0,  0, 1),
        // y 1
        new JumpBI(3,  1, -3, 3), new JumpBI(3,  1,  3, 3),
        new JumpBI(3,  1, -2, 2), new JumpBI(3,  1,  2, 2),
        new JumpBI(3,  1, -1, 2), new JumpBI(3,  1,  1, 2),
        new JumpBI(3,  1,  0, 2),
       
        // x 4
        // y -1
        new JumpBI(4, -1, -4, 3), new JumpBI(4, -1,  4, 3),
        new JumpBI(4, -1, -3, 2), new JumpBI(4, -1,  3, 2),
        new JumpBI(4, -1, -2, 2), new JumpBI(4, -1,  2, 2),
        new JumpBI(4, -1, -1, 1), new JumpBI(4, -1,  1, 1),
        new JumpBI(4, -1,  0, 1),
        // y 0
        new JumpBI(4,  0, -4, 3), new JumpBI(4,  0,  4, 3),
        new JumpBI(4,  0, -3, 2), new JumpBI(4,  0,  3, 2),
        new JumpBI(4,  0, -2, 2), new JumpBI(4,  0,  2, 2),
        new JumpBI(4,  0, -1, 2), new JumpBI(4,  0,  1, 2),
        new JumpBI(4,  0,  0, 2),
        // y 1
        new JumpBI(4,  1, -2, 3), new JumpBI(4,  1,  2, 3),
        new JumpBI(4,  1, -1, 3), new JumpBI(4,  1,  1, 3),
        new JumpBI(4,  1,  0, 3),
        
        // x 5
        // y -1
        new JumpBI(5, -1, -3, 3), new JumpBI(5, -1,  3, 3),
        new JumpBI(5, -1, -2, 3), new JumpBI(5, -1,  2, 3),
        new JumpBI(5, -1, -1, 2), new JumpBI(5, -1,  1, 2),
        new JumpBI(5, -1,  0, 2),
        // y 0
        new JumpBI(5,  0, -2, 3), new JumpBI(5,  0,  2, 3),
        new JumpBI(5,  0, -1, 3), new JumpBI(5,  0,  1, 3),
        new JumpBI(5,  0, 0, 3)
        // y 1
    };
    
    private final static Jump[] POSSIBLE_JUMPS_FB = new Jump[]{
        /*
        // x 0
        // y -1
        new Jump(0, -1, -5, 2), new Jump(0, -1,  5, 2),
        new Jump(0, -1, -4, 1), new Jump(0, -1,  4, 1),
        new Jump(0, -1, -3, 1), new Jump(0, -1,  3, 1),
        new Jump(0, -1, -2, 1), new Jump(0, -1,  2, 1),
        // y 0
        new Jump(0,  0, -5, 3), new Jump(0,  0,  5, 3),
        new Jump(0,  0, -4, 2), new Jump(0,  0,  4, 2),
        new Jump(0,  0, -3, 1), new Jump(0,  0,  3, 1),
        new Jump(0,  0, -2, 1), new Jump(0,  0,  2, 1),
        // y 1
        new Jump(0,  1, -4, 3), new Jump(0,  1,  4, 3),
        new Jump(0,  1, -3, 2), new Jump(0,  1,  3, 2),
        new Jump(0,  1, -2, 1), new Jump(0,  1,  2, 1),
        */
        
        // x 1
        // y -1
        new JumpFB(1, -1, -5, 2), new JumpFB(1, -1,  5, 2),
        new JumpFB(1, -1, -4, 2), new JumpFB(1, -1,  4, 2),
        new JumpFB(1, -1, -3, 1), new JumpFB(1, -1,  3, 1),
        new JumpFB(1, -1, -2, 1), new JumpFB(1, -1,  2, 1),
        // y 0
        new JumpFB(1,  0, -5, 3), new JumpFB(1,  0,  5, 3),
        new JumpFB(1,  0, -4, 2), new JumpFB(1,  0,  4, 2),
        new JumpFB(1,  0, -3, 1), new JumpFB(1,  0,  3, 1),
        new JumpFB(1,  0, -2, 1), new JumpFB(1,  0,  2, 1),
        // y 1
        new JumpFB(1,  1, -4, 2), new JumpFB(1,  1,  4, 2),
        new JumpFB(1,  1, -3, 2), new JumpFB(1,  1,  3, 2),
        new JumpFB(1,  1, -2, 1), new JumpFB(1,  1,  2, 1),
        new JumpFB(1,  1, -1, 1), new JumpFB(1,  1,  1, 1),
        
        // x 2
        // y -1
        new JumpFB(2, -1, -5, 2), new JumpFB(2, -1,  5, 2),
        new JumpFB(2, -1, -4, 2), new JumpFB(2, -1,  4, 2),
        new JumpFB(2, -1, -3, 1), new JumpFB(2, -1,  3, 1),
        new JumpFB(2, -1, -2, 1), new JumpFB(2, -1,  2, 1),
        new JumpFB(2, -1, -1, 1), new JumpFB(2, -1,  1, 1),
        new JumpFB(2, -1,  0, 1),
        // y 0
        new JumpFB(2,  0, -4, 2), new JumpFB(2,  0,  4, 2),
        new JumpFB(2,  0, -3, 1), new JumpFB(2,  0,  3, 1),
        new JumpFB(2,  0, -2, 1), new JumpFB(2,  0,  2, 1),
        new JumpFB(2,  0, -1, 1), new JumpFB(2,  0,  1, 1),
        new JumpFB(2,  0,  0, 1),
        // y 1
        new JumpFB(2,  1, -4, 3), new JumpFB(2,  1,  4, 3),
        new JumpFB(2,  1, -3, 2), new JumpFB(2,  1,  3, 2),
        new JumpFB(2,  1, -2, 1), new JumpFB(2,  1,  2, 1),
        new JumpFB(2,  1, -1, 1), new JumpFB(2,  1,  1, 1),
        new JumpFB(2,  1,  0, 1),
        
        // x 3
        // y -1
        new JumpFB(3, -1, -5, 3), new JumpFB(3, -1,  5, 3),
        new JumpFB(3, -1, -4, 2), new JumpFB(3, -1,  4, 2),
        new JumpFB(3, -1, -3, 1), new JumpFB(3, -1,  3, 1),
        new JumpFB(3, -1, -2, 1), new JumpFB(3, -1,  2, 1),
        new JumpFB(3, -1, -1, 1), new JumpFB(3, -1,  1, 1),
        new JumpFB(3, -1,  0, 1),
        // y 0
        new JumpFB(3,  0, -4, 2), new JumpFB(3,  0,  4, 2),
        new JumpFB(3,  0, -3, 2), new JumpFB(3,  0,  3, 2),
        new JumpFB(3,  0, -2, 1), new JumpFB(3,  0,  2, 1),
        new JumpFB(3,  0, -1, 1), new JumpFB(3,  0,  1, 1),
        new JumpFB(3,  0,  0, 1),
        // y 1
        new JumpFB(3,  1, -3, 2), new JumpFB(3,  1,  3, 2),
        new JumpFB(3,  1, -2, 2), new JumpFB(3,  1,  2, 2),
        new JumpFB(3,  1, -1, 2), new JumpFB(3,  1,  1, 2),
        new JumpFB(3,  1,  0, 2),
       
        // x 4
        // y -1
        new JumpFB(4, -1, -4, 2), new JumpFB(4, -1,  4, 2),
        new JumpFB(4, -1, -3, 2), new JumpFB(4, -1,  3, 2),
        new JumpFB(4, -1, -2, 2), new JumpFB(4, -1,  2, 2),
        new JumpFB(4, -1, -1, 1), new JumpFB(4, -1,  1, 1),
        new JumpFB(4, -1,  0, 1),
        // y 0
        new JumpFB(4,  0, -3, 2), new JumpFB(4,  0,  3, 2),
        new JumpFB(4,  0, -2, 2), new JumpFB(4,  0,  2, 2),
        new JumpFB(4,  0, -1, 2), new JumpFB(4,  0,  1, 2),
        new JumpFB(4,  0,  0, 2),
        // y 1
        new JumpFB(4,  1, -2, 3), new JumpFB(4,  1,  2, 3),
        new JumpFB(4,  1, -1, 2), new JumpFB(4,  1,  1, 2),
        new JumpFB(4,  1,  0, 2),
        
        // x 5
        // y -1
        new JumpFB(5, -1, -3, 3), new JumpFB(5, -1,  3, 3),
        new JumpFB(5, -1, -2, 2), new JumpFB(5, -1,  2, 2),
        new JumpFB(5, -1, -1, 2), new JumpFB(5, -1,  1, 2),
        new JumpFB(5, -1,  0, 2),
        // y 0
        new JumpFB(5,  0, -1, 3), new JumpFB(5,  0,  1, 3),
        new JumpFB(5,  0, 0, 3)
        // y 1
    };
    
    private final static Jump[] POSSIBLE_JUMPS_FF = new Jump[]{
        /*
        // x 0
        // y -1
        new Jump(0, -1, -5, 2), new Jump(0, -1,  5, 2),
        new Jump(0, -1, -4, 1), new Jump(0, -1,  4, 1),
        new Jump(0, -1, -3, 1), new Jump(0, -1,  3, 1),
        new Jump(0, -1, -2, 1), new Jump(0, -1,  2, 1),
        // y 0
        new Jump(0,  0, -5, 3), new Jump(0,  0,  5, 3),
        new Jump(0,  0, -4, 2), new Jump(0,  0,  4, 2),
        new Jump(0,  0, -3, 1), new Jump(0,  0,  3, 1),
        new Jump(0,  0, -2, 1), new Jump(0,  0,  2, 1),
        // y 1
        new Jump(0,  1, -4, 3), new Jump(0,  1,  4, 3),
        new Jump(0,  1, -3, 2), new Jump(0,  1,  3, 2),
        new Jump(0,  1, -2, 1), new Jump(0,  1,  2, 1),
        */
        
        // x 1
        // y -1
        new JumpFF(1, -1, -4, 2), new JumpFF(1, -1,  4, 2),
        new JumpFF(1, -1, -3, 1), new JumpFF(1, -1,  3, 1),
        new JumpFF(1, -1, -2, 1), new JumpFF(1, -1,  2, 1),
        // y 0
        new JumpFF(1,  0, -4, 2), new JumpFF(1,  0,  4, 2),
        new JumpFF(1,  0, -3, 2), new JumpFF(1,  0,  3, 2),
        new JumpFF(1,  0, -2, 1), new JumpFF(1,  0,  2, 1),
        // y 1
        new JumpFF(1,  1, -3, 2), new JumpFF(1,  1,  3, 2),
        new JumpFF(1,  1, -2, 1), new JumpFF(1,  1,  2, 1),
        
        // x 2
        // y -1
        new JumpFF(2, -1, -4, 2), new JumpFF(2, -1,  4, 2),
        new JumpFF(2, -1, -3, 2), new JumpFF(2, -1,  3, 2),
        new JumpFF(2, -1, -2, 1), new JumpFF(2, -1,  2, 1),
        new JumpFF(2, -1, -1, 1), new JumpFF(2, -1,  1, 1),
        new JumpFF(2, -1,  0, 1),
        // y 0
        new JumpFF(2,  0, -4, 3), new JumpFF(2,  0,  4, 3),
        new JumpFF(2,  0, -3, 2), new JumpFF(2,  0,  3, 2),
        new JumpFF(2,  0, -2, 1), new JumpFF(2,  0,  2, 1),
        new JumpFF(2,  0, -1, 1), new JumpFF(2,  0,  1, 1),
        new JumpFF(2,  0,  0, 1),
        // y 1
        new JumpFF(2,  1, -3, 3), new JumpFF(2,  1,  3, 3),
        new JumpFF(2,  1, -2, 2), new JumpFF(2,  1,  2, 2),
        new JumpFF(2,  1, -1, 1), new JumpFF(2,  1,  1, 1),
        new JumpFF(2,  1,  0, 1),
        
        // x 3
        // y -1
        new JumpFF(3, -1, -4, 3), new JumpFF(3, -1,  4, 3),
        new JumpFF(3, -1, -3, 2), new JumpFF(3, -1,  3, 2),
        new JumpFF(3, -1, -2, 2), new JumpFF(3, -1,  2, 2),
        new JumpFF(3, -1, -1, 1), new JumpFF(3, -1,  1, 1),
        new JumpFF(3, -1,  0, 1),
        // y 0
        new JumpFF(3,  0, -3, 2), new JumpFF(3,  0,  3, 2),
        new JumpFF(3,  0, -2, 2), new JumpFF(3,  0,  2, 2),
        new JumpFF(3,  0, -1, 2), new JumpFF(3,  0,  1, 2),
        new JumpFF(3,  0,  0, 2),
        // y 1
        new JumpFF(3,  1, -2, 2), new JumpFF(3,  1,  2, 2),
        new JumpFF(3,  1, -1, 2), new JumpFF(3,  1,  1, 2),
        new JumpFF(3,  1,  0, 2),
       
        // x 4
        // y -1
        new JumpFF(4, -1, -3, 3), new JumpFF(4, -1,  3, 3),
        new JumpFF(4, -1, -2, 2), new JumpFF(4, -1,  2, 2),
        new JumpFF(4, -1, -1, 2), new JumpFF(4, -1,  1, 2),
        new JumpFF(4, -1,  0, 2),
        // y 0
        new JumpFF(4,  0, -2, 3), new JumpFF(4,  0,  2, 3),
        new JumpFF(4,  0, -1, 2), new JumpFF(4,  0,  1, 2),
        new JumpFF(4,  0,  0, 2),
        // y 1
        
        // x 5
        // y -1
        // y 0
        // y 1
    };
    
    private final static Jump[] POSSIBLE_JUMPS_FI = new Jump[]{
        /*
        // x 0
        // y -1
        new Jump(0, -1, -5, 2), new Jump(0, -1,  5, 2),
        new Jump(0, -1, -4, 1), new Jump(0, -1,  4, 1),
        new Jump(0, -1, -3, 1), new Jump(0, -1,  3, 1),
        new Jump(0, -1, -2, 1), new Jump(0, -1,  2, 1),
        // y 0
        new Jump(0,  0, -5, 3), new Jump(0,  0,  5, 3),
        new Jump(0,  0, -4, 2), new Jump(0,  0,  4, 2),
        new Jump(0,  0, -3, 1), new Jump(0,  0,  3, 1),
        new Jump(0,  0, -2, 1), new Jump(0,  0,  2, 1),
        // y 1
        new Jump(0,  1, -4, 3), new Jump(0,  1,  4, 3),
        new Jump(0,  1, -3, 2), new Jump(0,  1,  3, 2),
        new Jump(0,  1, -2, 1), new Jump(0,  1,  2, 1),
        */
        /*
        // x 1
        // y -1
        new Jump(1, -1, -5, 2), new Jump(1, -1,  5, 2),
        new Jump(1, -1, -4, 1), new Jump(1, -1,  4, 1),
        new Jump(1, -1, -3, 1), new Jump(1, -1,  3, 1),
        new Jump(1, -1, -2, 1), new Jump(1, -1,  2, 1),
        // y 0
        new Jump(1,  0, -5, 3), new Jump(1,  0,  5, 3),
        new Jump(1,  0, -4, 2), new Jump(1,  0,  4, 2),
        new Jump(1,  0, -3, 1), new Jump(1,  0,  3, 1),
        new Jump(1,  0, -2, 1), new Jump(1,  0,  2, 1),
        // y 1
        new Jump(1,  1, -4, 3), new Jump(1,  1,  4, 3),
        new Jump(1,  1, -3, 2), new Jump(1,  1,  3, 2),
        new Jump(1,  1, -2, 1), new Jump(1,  1,  2, 1),
        new Jump(1,  1, -1, 1), new Jump(1,  1,  1, 1),
        */
        // x 2
        // y -1
        new JumpFI(2, -1, -5, 3), new JumpFI(2, -1,  5, 3),
        new JumpFI(2, -1, -4, 2), new JumpFI(2, -1,  4, 2),
        new JumpFI(2, -1, -3, 1), new JumpFI(2, -1,  3, 1),
        new JumpFI(2, -1, -2, 1), new JumpFI(2, -1,  2, 1),
        new JumpFI(2, -1, -1, 1), new JumpFI(2, -1,  1, 1),
        new JumpFI(2, -1,  0, 1),
        // y 0
        new JumpFI(2,  0, -5, 3), new JumpFI(2,  0,  5, 3),
        new JumpFI(2,  0, -4, 2), new JumpFI(2,  0,  4, 2),
        new JumpFI(2,  0, -3, 1), new JumpFI(2,  0,  3, 1),
        new JumpFI(2,  0, -2, 1), new JumpFI(2,  0,  2, 1),
        new JumpFI(2,  0, -1, 1), new JumpFI(2,  0,  1, 1),
        new JumpFI(2,  0,  0, 1),
        // y 1
        new JumpFI(2,  1, -4, 3), new JumpFI(2,  1,  4, 3),
        new JumpFI(2,  1, -3, 2), new JumpFI(2,  1,  3, 2),
        new JumpFI(2,  1, -2, 1), new JumpFI(2,  1,  2, 1),
        new JumpFI(2,  1, -1, 1), new JumpFI(2,  1,  1, 1),
        new JumpFI(2,  1,  0, 1),
        
        // x 3
        // y -1
        new JumpFI(3, -1, -5, 3), new JumpFI(3, -1,  5, 3),
        new JumpFI(3, -1, -4, 2), new JumpFI(3, -1,  4, 2),
        new JumpFI(3, -1, -3, 1), new JumpFI(3, -1,  3, 1),
        new JumpFI(3, -1, -2, 1), new JumpFI(3, -1,  2, 1),
        new JumpFI(3, -1, -1, 1), new JumpFI(3, -1,  1, 1),
        new JumpFI(3, -1,  0, 1),
        // y 0
        new JumpFI(3,  0, -4, 2), new JumpFI(3,  0,  4, 2),
        new JumpFI(3,  0, -3, 2), new JumpFI(3,  0,  3, 2),
        new JumpFI(3,  0, -2, 1), new JumpFI(3,  0,  2, 1),
        new JumpFI(3,  0, -1, 1), new JumpFI(3,  0,  1, 1),
        new JumpFI(3,  0,  0, 1),
        // y 1
        new JumpFI(3,  1, -3, 3), new JumpFI(3,  1,  3, 3),
        new JumpFI(3,  1, -2, 2), new JumpFI(3,  1,  2, 2),
        new JumpFI(3,  1, -1, 2), new JumpFI(3,  1,  1, 2),
        new JumpFI(3,  1,  0, 2),
       
        // x 4
        // y -1
        new JumpFI(4, -1, -4, 3), new JumpFI(4, -1,  4, 3),
        new JumpFI(4, -1, -3, 2), new JumpFI(4, -1,  3, 2),
        new JumpFI(4, -1, -2, 2), new JumpFI(4, -1,  2, 2),
        new JumpFI(4, -1, -1, 1), new JumpFI(4, -1,  1, 1),
        new JumpFI(4, -1,  0, 1),
        // y 0
        new JumpFI(4,  0, -4, 3), new JumpFI(4,  0,  4, 3),
        new JumpFI(4,  0, -3, 2), new JumpFI(4,  0,  3, 2),
        new JumpFI(4,  0, -2, 2), new JumpFI(4,  0,  2, 2),
        new JumpFI(4,  0, -1, 2), new JumpFI(4,  0,  1, 2),
        new JumpFI(4,  0,  0, 2),
        // y 1
        new JumpFI(4,  1, -2, 3), new JumpFI(4,  1,  2, 3),
        new JumpFI(4,  1, -1, 3), new JumpFI(4,  1,  1, 3),
        new JumpFI(4,  1,  0, 3),
        
        // x 5
        // y -1
        new JumpFI(5, -1, -3, 3), new JumpFI(5, -1,  3, 3),
        new JumpFI(5, -1, -2, 3), new JumpFI(5, -1,  2, 3),
        new JumpFI(5, -1, -1, 2), new JumpFI(5, -1,  1, 2),
        new JumpFI(5, -1,  0, 2),
        // y 0
        new JumpFI(5,  0, -2, 3), new JumpFI(5,  0,  2, 3),
        new JumpFI(5,  0, -1, 3), new JumpFI(5,  0,  1, 3),
        new JumpFI(5,  0, 0, 3)
        // y 1
    };
    
    private final static Jump[] POSSIBLE_JUMPS_IB = new Jump[]{
        /*
        // x 0
        // y -1
        new Jump(0, -1, -5, 2), new Jump(0, -1,  5, 2),
        new Jump(0, -1, -4, 1), new Jump(0, -1,  4, 1),
        new Jump(0, -1, -3, 1), new Jump(0, -1,  3, 1),
        new Jump(0, -1, -2, 1), new Jump(0, -1,  2, 1),
        // y 0
        new Jump(0,  0, -5, 3), new Jump(0,  0,  5, 3),
        new Jump(0,  0, -4, 2), new Jump(0,  0,  4, 2),
        new Jump(0,  0, -3, 1), new Jump(0,  0,  3, 1),
        new Jump(0,  0, -2, 1), new Jump(0,  0,  2, 1),
        // y 1
        new Jump(0,  1, -4, 3), new Jump(0,  1,  4, 3),
        new Jump(0,  1, -3, 2), new Jump(0,  1,  3, 2),
        new Jump(0,  1, -2, 1), new Jump(0,  1,  2, 1),
        */
        /*
        // x 1
        // y -1
        new Jump(1, -1, -5, 2), new Jump(1, -1,  5, 2),
        new Jump(1, -1, -4, 1), new Jump(1, -1,  4, 1),
        new Jump(1, -1, -3, 1), new Jump(1, -1,  3, 1),
        new Jump(1, -1, -2, 1), new Jump(1, -1,  2, 1),
        // y 0
        new Jump(1,  0, -5, 3), new Jump(1,  0,  5, 3),
        new Jump(1,  0, -4, 2), new Jump(1,  0,  4, 2),
        new Jump(1,  0, -3, 1), new Jump(1,  0,  3, 1),
        new Jump(1,  0, -2, 1), new Jump(1,  0,  2, 1),
        // y 1
        new Jump(1,  1, -4, 3), new Jump(1,  1,  4, 3),
        new Jump(1,  1, -3, 2), new Jump(1,  1,  3, 2),
        new Jump(1,  1, -2, 1), new Jump(1,  1,  2, 1),
        new Jump(1,  1, -1, 1), new Jump(1,  1,  1, 1),
        */
        // x 2
        // y -1
        new JumpIB(2, -1, -5, 3), new JumpIB(2, -1,  5, 3),
        new JumpIB(2, -1, -4, 2), new JumpIB(2, -1,  4, 2),
        new JumpIB(2, -1, -3, 1), new JumpIB(2, -1,  3, 1),
        new JumpIB(2, -1, -2, 1), new JumpIB(2, -1,  2, 1),
        new JumpIB(2, -1, -1, 1), new JumpIB(2, -1,  1, 1),
        new JumpIB(2, -1,  0, 1),
        // y 0
        new JumpIB(2,  0, -5, 3), new JumpIB(2,  0,  5, 3),
        new JumpIB(2,  0, -4, 2), new JumpIB(2,  0,  4, 2),
        new JumpIB(2,  0, -3, 1), new JumpIB(2,  0,  3, 1),
        new JumpIB(2,  0, -2, 1), new JumpIB(2,  0,  2, 1),
        new JumpIB(2,  0, -1, 1), new JumpIB(2,  0,  1, 1),
        new JumpIB(2,  0,  0, 1),
        // y 1
        new JumpIB(2,  1, -4, 3), new JumpIB(2,  1,  4, 3),
        new JumpIB(2,  1, -3, 2), new JumpIB(2,  1,  3, 2),
        new JumpIB(2,  1, -2, 1), new JumpIB(2,  1,  2, 1),
        new JumpIB(2,  1, -1, 1), new JumpIB(2,  1,  1, 1),
        new JumpIB(2,  1,  0, 1),
        
        // x 3
        // y -1
        new JumpIB(3, -1, -5, 3), new JumpIB(3, -1,  5, 3),
        new JumpIB(3, -1, -4, 2), new JumpIB(3, -1,  4, 2),
        new JumpIB(3, -1, -3, 1), new JumpIB(3, -1,  3, 1),
        new JumpIB(3, -1, -2, 1), new JumpIB(3, -1,  2, 1),
        new JumpIB(3, -1, -1, 1), new JumpIB(3, -1,  1, 1),
        new JumpIB(3, -1,  0, 1),
        // y 0
        new JumpIB(3,  0, -4, 2), new JumpIB(3,  0,  4, 2),
        new JumpIB(3,  0, -3, 2), new JumpIB(3,  0,  3, 2),
        new JumpIB(3,  0, -2, 1), new JumpIB(3,  0,  2, 1),
        new JumpIB(3,  0, -1, 1), new JumpIB(3,  0,  1, 1),
        new JumpIB(3,  0,  0, 1),
        // y 1
        new JumpIB(3,  1, -3, 3), new JumpIB(3,  1,  3, 3),
        new JumpIB(3,  1, -2, 2), new JumpIB(3,  1,  2, 2),
        new JumpIB(3,  1, -1, 2), new JumpIB(3,  1,  1, 2),
        new JumpIB(3,  1,  0, 2),
       
        // x 4
        // y -1
        new JumpIB(4, -1, -4, 3), new JumpIB(4, -1,  4, 3),
        new JumpIB(4, -1, -3, 2), new JumpIB(4, -1,  3, 2),
        new JumpIB(4, -1, -2, 2), new JumpIB(4, -1,  2, 2),
        new JumpIB(4, -1, -1, 1), new JumpIB(4, -1,  1, 1),
        new JumpIB(4, -1,  0, 1),
        // y 0
        new JumpIB(4,  0, -4, 3), new JumpIB(4,  0,  4, 3),
        new JumpIB(4,  0, -3, 2), new JumpIB(4,  0,  3, 2),
        new JumpIB(4,  0, -2, 2), new JumpIB(4,  0,  2, 2),
        new JumpIB(4,  0, -1, 2), new JumpIB(4,  0,  1, 2),
        new JumpIB(4,  0,  0, 2),
        // y 1
        new JumpIB(4,  1, -2, 3), new JumpIB(4,  1,  2, 3),
        new JumpIB(4,  1, -1, 3), new JumpIB(4,  1,  1, 3),
        new JumpIB(4,  1,  0, 3),
        
        // x 5
        // y -1
        new JumpIB(5, -1, -3, 3), new JumpIB(5, -1,  3, 3),
        new JumpIB(5, -1, -2, 3), new JumpIB(5, -1,  2, 3),
        new JumpIB(5, -1, -1, 2), new JumpIB(5, -1,  1, 2),
        new JumpIB(5, -1,  0, 2),
        // y 0
        new JumpIB(5,  0, -2, 3), new JumpIB(5,  0,  2, 3),
        new JumpIB(5,  0, -1, 3), new JumpIB(5,  0,  1, 3),
        new JumpIB(5,  0, 0, 3)
        // y 1
    };
    
    private final static Jump[] POSSIBLE_JUMPS_IF = new Jump[]{
        /*
        // x 0
        // y -1
        new Jump(0, -1, -5, 2), new Jump(0, -1,  5, 2),
        new Jump(0, -1, -4, 1), new Jump(0, -1,  4, 1),
        new Jump(0, -1, -3, 1), new Jump(0, -1,  3, 1),
        new Jump(0, -1, -2, 1), new Jump(0, -1,  2, 1),
        // y 0
        new Jump(0,  0, -5, 3), new Jump(0,  0,  5, 3),
        new Jump(0,  0, -4, 2), new Jump(0,  0,  4, 2),
        new Jump(0,  0, -3, 1), new Jump(0,  0,  3, 1),
        new Jump(0,  0, -2, 1), new Jump(0,  0,  2, 1),
        // y 1
        new Jump(0,  1, -4, 3), new Jump(0,  1,  4, 3),
        new Jump(0,  1, -3, 2), new Jump(0,  1,  3, 2),
        new Jump(0,  1, -2, 1), new Jump(0,  1,  2, 1),
        */
        /*
        // x 1
        // y -1
        new Jump(1, -1, -5, 2), new Jump(1, -1,  5, 2),
        new Jump(1, -1, -4, 1), new Jump(1, -1,  4, 1),
        new Jump(1, -1, -3, 1), new Jump(1, -1,  3, 1),
        new Jump(1, -1, -2, 1), new Jump(1, -1,  2, 1),
        // y 0
        new Jump(1,  0, -5, 3), new Jump(1,  0,  5, 3),
        new Jump(1,  0, -4, 2), new Jump(1,  0,  4, 2),
        new Jump(1,  0, -3, 1), new Jump(1,  0,  3, 1),
        new Jump(1,  0, -2, 1), new Jump(1,  0,  2, 1),
        // y 1
        new Jump(1,  1, -4, 3), new Jump(1,  1,  4, 3),
        new Jump(1,  1, -3, 2), new Jump(1,  1,  3, 2),
        new Jump(1,  1, -2, 1), new Jump(1,  1,  2, 1),
        new Jump(1,  1, -1, 1), new Jump(1,  1,  1, 1),
        */
        // x 2
        // y -1
        new JumpIF(2, -1, -5, 3), new JumpIF(2, -1,  5, 3),
        new JumpIF(2, -1, -4, 2), new JumpIF(2, -1,  4, 2),
        new JumpIF(2, -1, -3, 1), new JumpIF(2, -1,  3, 1),
        new JumpIF(2, -1, -2, 1), new JumpIF(2, -1,  2, 1),
        new JumpIF(2, -1, -1, 1), new JumpIF(2, -1,  1, 1),
        new JumpIF(2, -1,  0, 1),
        // y 0
        new JumpIF(2,  0, -5, 3), new JumpIF(2,  0,  5, 3),
        new JumpIF(2,  0, -4, 2), new JumpIF(2,  0,  4, 2),
        new JumpIF(2,  0, -3, 1), new JumpIF(2,  0,  3, 1),
        new JumpIF(2,  0, -2, 1), new JumpIF(2,  0,  2, 1),
        new JumpIF(2,  0, -1, 1), new JumpIF(2,  0,  1, 1),
        new JumpIF(2,  0,  0, 1),
        // y 1
        new JumpIF(2,  1, -4, 3), new JumpIF(2,  1,  4, 3),
        new JumpIF(2,  1, -3, 2), new JumpIF(2,  1,  3, 2),
        new JumpIF(2,  1, -2, 1), new JumpIF(2,  1,  2, 1),
        new JumpIF(2,  1, -1, 1), new JumpIF(2,  1,  1, 1),
        new JumpIF(2,  1,  0, 1),
        
        // x 3
        // y -1
        new JumpIF(3, -1, -5, 3), new JumpIF(3, -1,  5, 3),
        new JumpIF(3, -1, -4, 2), new JumpIF(3, -1,  4, 2),
        new JumpIF(3, -1, -3, 1), new JumpIF(3, -1,  3, 1),
        new JumpIF(3, -1, -2, 1), new JumpIF(3, -1,  2, 1),
        new JumpIF(3, -1, -1, 1), new JumpIF(3, -1,  1, 1),
        new JumpIF(3, -1,  0, 1),
        // y 0
        new JumpIF(3,  0, -4, 2), new JumpIF(3,  0,  4, 2),
        new JumpIF(3,  0, -3, 2), new JumpIF(3,  0,  3, 2),
        new JumpIF(3,  0, -2, 1), new JumpIF(3,  0,  2, 1),
        new JumpIF(3,  0, -1, 1), new JumpIF(3,  0,  1, 1),
        new JumpIF(3,  0,  0, 1),
        // y 1
        new JumpIF(3,  1, -3, 3), new JumpIF(3,  1,  3, 3),
        new JumpIF(3,  1, -2, 2), new JumpIF(3,  1,  2, 2),
        new JumpIF(3,  1, -1, 2), new JumpIF(3,  1,  1, 2),
        new JumpIF(3,  1,  0, 2),
       
        // x 4
        // y -1
        new JumpIF(4, -1, -4, 3), new JumpIF(4, -1,  4, 3),
        new JumpIF(4, -1, -3, 2), new JumpIF(4, -1,  3, 2),
        new JumpIF(4, -1, -2, 2), new JumpIF(4, -1,  2, 2),
        new JumpIF(4, -1, -1, 1), new JumpIF(4, -1,  1, 1),
        new JumpIF(4, -1,  0, 1),
        // y 0
        new JumpIF(4,  0, -4, 3), new JumpIF(4,  0,  4, 3),
        new JumpIF(4,  0, -3, 2), new JumpIF(4,  0,  3, 2),
        new JumpIF(4,  0, -2, 2), new JumpIF(4,  0,  2, 2),
        new JumpIF(4,  0, -1, 2), new JumpIF(4,  0,  1, 2),
        new JumpIF(4,  0,  0, 2),
        // y 1
        new JumpIF(4,  1, -2, 3), new JumpIF(4,  1,  2, 3),
        new JumpIF(4,  1, -1, 3), new JumpIF(4,  1,  1, 3),
        new JumpIF(4,  1,  0, 3),
        
        // x 5
        // y -1
        new JumpIF(5, -1, -3, 3), new JumpIF(5, -1,  3, 3),
        new JumpIF(5, -1, -2, 3), new JumpIF(5, -1,  2, 3),
        new JumpIF(5, -1, -1, 2), new JumpIF(5, -1,  1, 2),
        new JumpIF(5, -1,  0, 2),
        // y 0
        new JumpIF(5,  0, -2, 3), new JumpIF(5,  0,  2, 3),
        new JumpIF(5,  0, -1, 3), new JumpIF(5,  0,  1, 3),
        new JumpIF(5,  0, 0, 3)
        // y 1
    };
    
    private final static Jump[] POSSIBLE_JUMPS_II = new Jump[]{
        /*
        // x 0
        // y -1
        new Jump(0, -1, -5, 2), new Jump(0, -1,  5, 2),
        new Jump(0, -1, -4, 1), new Jump(0, -1,  4, 1),
        new Jump(0, -1, -3, 1), new Jump(0, -1,  3, 1),
        new Jump(0, -1, -2, 1), new Jump(0, -1,  2, 1),
        // y 0
        new Jump(0,  0, -5, 3), new Jump(0,  0,  5, 3),
        new Jump(0,  0, -4, 2), new Jump(0,  0,  4, 2),
        new Jump(0,  0, -3, 1), new Jump(0,  0,  3, 1),
        new Jump(0,  0, -2, 1), new Jump(0,  0,  2, 1),
        // y 1
        new Jump(0,  1, -4, 3), new Jump(0,  1,  4, 3),
        new Jump(0,  1, -3, 2), new Jump(0,  1,  3, 2),
        new Jump(0,  1, -2, 1), new Jump(0,  1,  2, 1),
        */
        /*
        // x 1
        // y -1
        new Jump(1, -1, -5, 2), new Jump(1, -1,  5, 2),
        new Jump(1, -1, -4, 1), new Jump(1, -1,  4, 1),
        new Jump(1, -1, -3, 1), new Jump(1, -1,  3, 1),
        new Jump(1, -1, -2, 1), new Jump(1, -1,  2, 1),
        // y 0
        new Jump(1,  0, -5, 3), new Jump(1,  0,  5, 3),
        new Jump(1,  0, -4, 2), new Jump(1,  0,  4, 2),
        new Jump(1,  0, -3, 1), new Jump(1,  0,  3, 1),
        new Jump(1,  0, -2, 1), new Jump(1,  0,  2, 1),
        // y 1
        new Jump(1,  1, -4, 3), new Jump(1,  1,  4, 3),
        new Jump(1,  1, -3, 2), new Jump(1,  1,  3, 2),
        new Jump(1,  1, -2, 1), new Jump(1,  1,  2, 1),
        new Jump(1,  1, -1, 1), new Jump(1,  1,  1, 1),
        */
        // x 2
        // y -1
        new JumpII(2, -1, -5, 3), new JumpII(2, -1,  5, 3),
        new JumpII(2, -1, -4, 2), new JumpII(2, -1,  4, 2),
        new JumpII(2, -1, -3, 1), new JumpII(2, -1,  3, 1),
        new JumpII(2, -1, -2, 1), new JumpII(2, -1,  2, 1),
        new JumpII(2, -1, -1, 1), new JumpII(2, -1,  1, 1),
        new JumpII(2, -1,  0, 1),
        // y 0
        new JumpII(2,  0, -5, 3), new JumpII(2,  0,  5, 3),
        new JumpII(2,  0, -4, 2), new JumpII(2,  0,  4, 2),
        new JumpII(2,  0, -3, 1), new JumpII(2,  0,  3, 1),
        new JumpII(2,  0, -2, 1), new JumpII(2,  0,  2, 1),
        new JumpII(2,  0, -1, 1), new JumpII(2,  0,  1, 1),
        new JumpII(2,  0,  0, 1),
        // y 1
        new JumpII(2,  1, -4, 3), new JumpII(2,  1,  4, 3),
        new JumpII(2,  1, -3, 2), new JumpII(2,  1,  3, 2),
        new JumpII(2,  1, -2, 1), new JumpII(2,  1,  2, 1),
        new JumpII(2,  1, -1, 1), new JumpII(2,  1,  1, 1),
        new JumpII(2,  1,  0, 1),
        
        // x 3
        // y -1
        new JumpII(3, -1, -5, 3), new JumpII(3, -1,  5, 3),
        new JumpII(3, -1, -4, 2), new JumpII(3, -1,  4, 2),
        new JumpII(3, -1, -3, 1), new JumpII(3, -1,  3, 1),
        new JumpII(3, -1, -2, 1), new JumpII(3, -1,  2, 1),
        new JumpII(3, -1, -1, 1), new JumpII(3, -1,  1, 1),
        new JumpII(3, -1,  0, 1),
        // y 0
        new JumpII(3,  0, -4, 2), new JumpII(3,  0,  4, 2),
        new JumpII(3,  0, -3, 2), new JumpII(3,  0,  3, 2),
        new JumpII(3,  0, -2, 1), new JumpII(3,  0,  2, 1),
        new JumpII(3,  0, -1, 1), new JumpII(3,  0,  1, 1),
        new JumpII(3,  0,  0, 1),
        // y 1
        new JumpII(3,  1, -3, 3), new JumpII(3,  1,  3, 3),
        new JumpII(3,  1, -2, 2), new JumpII(3,  1,  2, 2),
        new JumpII(3,  1, -1, 2), new JumpII(3,  1,  1, 2),
        new JumpII(3,  1,  0, 2),
       
        // x 4
        // y -1
        new JumpII(4, -1, -4, 3), new JumpII(4, -1,  4, 3),
        new JumpII(4, -1, -3, 2), new JumpII(4, -1,  3, 2),
        new JumpII(4, -1, -2, 2), new JumpII(4, -1,  2, 2),
        new JumpII(4, -1, -1, 1), new JumpII(4, -1,  1, 1),
        new JumpII(4, -1,  0, 1),
        // y 0
        new JumpII(4,  0, -4, 3), new JumpII(4,  0,  4, 3),
        new JumpII(4,  0, -3, 2), new JumpII(4,  0,  3, 2),
        new JumpII(4,  0, -2, 2), new JumpII(4,  0,  2, 2),
        new JumpII(4,  0, -1, 2), new JumpII(4,  0,  1, 2),
        new JumpII(4,  0,  0, 2),
        // y 1
        new JumpII(4,  1, -2, 3), new JumpII(4,  1,  2, 3),
        new JumpII(4,  1, -1, 3), new JumpII(4,  1,  1, 3),
        new JumpII(4,  1,  0, 3),
        
        // x 5
        // y -1
        new JumpII(5, -1, -3, 3), new JumpII(5, -1,  3, 3),
        new JumpII(5, -1, -2, 3), new JumpII(5, -1,  2, 3),
        new JumpII(5, -1, -1, 2), new JumpII(5, -1,  1, 2),
        new JumpII(5, -1,  0, 2),
        // y 0
        new JumpII(5,  0, -2, 3), new JumpII(5,  0,  2, 3),
        new JumpII(5,  0, -1, 3), new JumpII(5,  0,  1, 3),
        new JumpII(5,  0, 0, 3)
        // y 1
    };
}
