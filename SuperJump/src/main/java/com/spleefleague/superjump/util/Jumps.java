package com.spleefleague.superjump.util;

import com.mongodb.client.MongoCollection;
import com.spleefleague.superjump.SuperJump;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author NickM13
 * @since 5/4/2020
 */
public class Jumps {
    
    private static Set<Jump> jumpSet = new HashSet<>();
    // [Up][Right][MaxForward]
    private static List<List<Integer>> jumpPositionLists = new ArrayList<>();
    
    public static void init() {
        addJumps(5, 0, 0);
        addJumps(5, 0, 1);
        addJumps(5, 0, 2);
        addJumps(4, 0, 3);
        addJumps(3, 0, 4);
        addJumps(2, 0, 5);
    
        addJumps(6, -1, 0);
        addJumps(6, -1, 1);
        addJumps(5, -1, 2);
        addJumps(5, -1, 3);
        addJumps(4, -1, 4);
        addJumps(3, -1, 5);
    
        addJumps(4, 1, 0);
        addJumps(4, 1, 1);
        addJumps(3, 1, 2);
        addJumps(2, 1, 3);
    }
    
    protected static void addJumps(int maxForward, int up, int right) {
        for (int i = 2; i <= maxForward; i++) {
            Jump jump = new Jump(i, up, right);
            jumpSet.add(jump);
        }
        up = -up + 1;
        while (jumpPositionLists.size() < up + 1) {
            jumpPositionLists.add(new ArrayList<>());
        }
        while (jumpPositionLists.get(up).size() < right + 1) {
            jumpPositionLists.get(up).add(0);
        }
        jumpPositionLists.get(up).set(right, maxForward);
    }
    
    public static List<Jump> getPossibleJumps(int minRight, int maxRight, int minUp, int maxUp) {
        List<Jump> possibleJumps = new ArrayList<>();
        int upIndex;
        for (int up = Math.max(-1, minUp); up <= maxUp && (upIndex = -up + 1) >= 0; up++) {
            for (int right = 0; right <= maxRight && right < jumpPositionLists.get(upIndex).size(); right++) {
                for (int forward = 2; forward <= jumpPositionLists.get(upIndex).get(right); forward++) {
                    possibleJumps.add(new Jump(forward, up, right));
                }
            }
            for (int right = 0; right <= -minRight && right < jumpPositionLists.get(upIndex).size(); right++) {
                for (int forward = 2; forward <= jumpPositionLists.get(upIndex).get(right); forward++) {
                    possibleJumps.add(new Jump(forward, up, -right));
                }
            }
        }
        return possibleJumps;
    }

    private static class PossibleJump {
        Jump jump;
        double frequency;

        public PossibleJump(Jump jump, double frequency) {
            this.jump = jump;
            this.frequency = frequency;
        }
    }

    public static Jump getNextJump(int minRight, int maxRight, int minUp, int maxUp, double difficulty, Random random) {
        difficulty = Math.min(3, Math.max(0, difficulty));
        List<PossibleJump> possibleJumps = new ArrayList<>();
        int upIndex;
        for (int up = Math.max(-1, minUp); up <= maxUp && (upIndex = -up + 1) >= 0; up++) {
            for (int right = 0; right <= maxRight && right < jumpPositionLists.get(upIndex).size(); right++) {
                for (int forward = 2; forward <= jumpPositionLists.get(upIndex).get(right); forward++) {
                    Jump jump = new Jump(forward, up, right);
                    double frequency = 1.3D - Math.abs(difficulty - jump.getDifficulty());
                    if (frequency > 0) {
                        possibleJumps.add(new PossibleJump(jump, frequency));
                    }
                }
            }
            for (int right = 0; right <= -minRight && right < jumpPositionLists.get(upIndex).size(); right++) {
                for (int forward = 2; forward <= jumpPositionLists.get(upIndex).get(right); forward++) {
                    Jump jump = new Jump(forward, up, -right);
                    double frequency = 1.3D - Math.abs(difficulty - jump.getDifficulty());
                    if (frequency > 0) {
                        possibleJumps.add(new PossibleJump(jump, frequency));
                    }
                }
            }
        }
        double breakFreq = 1;
        while (possibleJumps.size() > 0) {
            if (possibleJumps.size() == 1) {
                return possibleJumps.get(0).jump;
            }
            int index = random.nextInt(possibleJumps.size());
            PossibleJump possibleJump = possibleJumps.get(index);
            breakFreq -= possibleJump.frequency;
            if (breakFreq <= 0) {
                return possibleJump.jump;
            }
            possibleJumps.remove(index);
        }
        return null;
    }

}
