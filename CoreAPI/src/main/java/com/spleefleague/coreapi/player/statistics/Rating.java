package com.spleefleague.coreapi.player.statistics;

import com.spleefleague.coreapi.chat.Chat;
import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NickM13
 * @since 4/28/2020
 */
public class Rating extends DBEntity {

    protected static final Map<Integer, Division> DIVISIONS = new HashMap<>();

    public enum Division {

        MASTER(    "Master I",     ChatColor.LIGHT_PURPLE + "", 2800, 30),
        DIAMOND1(  "Diamond I",    ChatColor.BLUE + "",         2600, 20),
        DIAMOND2(  "Diamond II",   ChatColor.BLUE + "",         2450, 20),
        DIAMOND3(  "Diamond III",  ChatColor.BLUE + "",         2300, 20),
        PLATINUM1( "Platinum I",   ChatColor.AQUA + "",         2150, 10),
        PLATINUM2( "Platinum II",  ChatColor.AQUA + "",         2000, 10),
        PLATINUM3( "Platinum III", ChatColor.AQUA + "",         1850, 10),
        GOLD1(     "Gold I",       ChatColor.GOLD + "",         1700, 0),
        GOLD2(     "Gold II",      ChatColor.GOLD + "",         1550, 0),
        GOLD3(     "Gold III",     ChatColor.GOLD + "",         1400, 0),
        SILVER1(   "Silver I",     ChatColor.GRAY + "",         1250, 0),
        SILVER2(   "Silver II",    ChatColor.GRAY + "",         1100, 0),
        SILVER3(   "Silver III",   ChatColor.GRAY + "",         950,  0),
        BRONZE1(   "Bronze I",     ChatColor.DARK_RED + "",     800,  0),
        BRONZE2(   "Bronze II",    ChatColor.DARK_RED + "",     650,  0),
        BRONZE3(   "Bronze III",   ChatColor.DARK_RED + "",     0,    0);

        protected String displayName;
        protected String scorePrefix;
        protected int lower, upper;
        protected int decay;

        Division(String displayName, String scorePrefix, int lower, int decay) {
            this.displayName = displayName;
            this.scorePrefix = scorePrefix;
            this.lower = lower;
            this.decay = decay;
            DIVISIONS.put(lower, this);
        }

        public String getDisplayName() {
            return scorePrefix + displayName;
        }

        public String getScorePrefix() {
            return scorePrefix;
        }

        public boolean isWithin(int elo) {
            return elo >= lower && elo <= upper;
        }

        public int getDecay() { return decay; }
    }

    protected static final int BASE_ELO = 1000;

    @DBField protected Integer elo;
    @DBField protected Division division;
    @DBField protected Integer wins;
    @DBField protected Integer losses;

    public Rating() {
        elo = BASE_ELO;
        division = Division.SILVER3;
        wins = 0;
        losses = 0;
    }

    /**
     * Called after load to further initialize anything
     */
    @Override
    public void afterLoad() {
        wins = wins == null ? 0 : wins;
        losses = losses == null ? 0 : losses;
    }

    private boolean updateDivision() {
        if (!division.isWithin(elo)) {
            for (Division div : Division.values()) {
                if (div.isWithin(elo)) {
                    division = div;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean setElo(int value) {
        boolean increase = value > elo;
        elo = value;
        updateDivision();
        return increase;
    }

    public int addElo(int value) {
        elo += value;
        boolean divisionChange = updateDivision();
        if (value > 0) {
            wins++;
        } else {
            losses++;
        }
        return elo;
    }

    public int getElo() {
        return elo;
    }

    public String getDisplayElo() {
        return Chat.BRACKET + "(" + division.getScorePrefix() + elo + Chat.BRACKET + ")";
    }

    public Division getDivision() {
        return division;
    }

    public int getWins() {
        return wins;
    }

    public void addWin() {
        this.wins++;
    }

    public int getLosses() {
        return losses;
    }

    public void addLoss() {
        this.losses++;
    }

}
