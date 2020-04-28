package com.spleefleague.spleef.game.battle.banana;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author NickM
 * @since 4/14/2020
 */
public class KnockoutStreak {

    protected static TreeMap<Integer, KnockoutStreak> KNOCKOUT_STREAKS;
    static {
        KNOCKOUT_STREAKS = new TreeMap<>(Collections.reverseOrder());
        KNOCKOUT_STREAKS.put(3, new KnockoutStreak("Killing Spleef!", "Shutdown 1", 3));
        KNOCKOUT_STREAKS.put(5, new KnockoutStreak("is Unspleefable!", "Shutdown 2", 10));
        KNOCKOUT_STREAKS.put(7, new KnockoutStreak("might actually be SaberTTiger!", "Shutdown 3", 20));
    }

    public static KnockoutStreak getStreak(int knockouts) {
        return KNOCKOUT_STREAKS.get(knockouts);
    }
    public static KnockoutStreak getStreakMin(int knockouts) {
        for (Map.Entry<Integer, KnockoutStreak> ks : KNOCKOUT_STREAKS.entrySet()) {
            if (knockouts >= ks.getKey()) {
                return ks.getValue();
            }
        }
        return null;
    }

    String reachMessage, endedMessage;
    int bounty;

    public KnockoutStreak(String reachMessage, String endedMessage, int bounty) {
        this.reachMessage = reachMessage;
        this.endedMessage = endedMessage;
        this.bounty = bounty;
    }

    public String getReachMessage() {
        return reachMessage;
    }

    public String getEndedMessage() {
        return endedMessage;
    }

    public int getBounty() {
        return bounty;
    }

}
