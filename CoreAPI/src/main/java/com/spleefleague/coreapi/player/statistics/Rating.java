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

    public enum Division {

        //MASTER(    "Master I",     ChatColor.LIGHT_PURPLE + "", 2000, Integer.MAX_VALUE, 30),
        DIAMOND1(  "Diamond I",    ChatColor.BLUE + "",         2600, 2800, 20),
        DIAMOND2(  "Diamond II",   ChatColor.BLUE + "",         2450, 2650, 20),
        DIAMOND3(  "Diamond III",  ChatColor.BLUE + "",         2300, 2500, 20),
        PLATINUM1( "Platinum I",   ChatColor.AQUA + "",         2150, 2350, 10),
        PLATINUM2( "Platinum II",  ChatColor.AQUA + "",         2000, 2200, 10),
        PLATINUM3( "Platinum III", ChatColor.AQUA + "",         1850, 2050, 10),
        GOLD1(     "Gold I",       ChatColor.GOLD + "",         1700, 1900, 0),
        GOLD2(     "Gold II",      ChatColor.GOLD + "",         1550, 1750, 0),
        GOLD3(     "Gold III",     ChatColor.GOLD + "",         1400, 1600, 0),
        SILVER1(   "Silver I",     ChatColor.GRAY + "",         1250, 1450, 0),
        SILVER2(   "Silver II",    ChatColor.GRAY + "",         1100, 1300, 0),
        SILVER3(   "Silver III",   ChatColor.GRAY + "",         950,  1150, 0),
        BRONZE1(   "Bronze I",     ChatColor.DARK_RED + "",     800,  1000, 0),
        BRONZE2(   "Bronze II",    ChatColor.DARK_RED + "",     650,  850,  0),
        BRONZE3(   "Bronze III",   ChatColor.DARK_RED + "",     0,    700,  0);

        protected String displayName;
        protected String scorePrefix;
        protected int lower, upper;
        protected int decay;

        Division(String displayName, String scorePrefix, int lower, int upper, int decay) {
            this.displayName = displayName;
            this.scorePrefix = scorePrefix;
            this.lower = lower;
            this.upper = upper;
            this.decay = decay;
        }

        public String getDisplayName() {
            return scorePrefix + displayName;
        }

        public String getScorePrefix() {
            return scorePrefix;
        }

        public int isWithin(int elo) {
            return (elo < lower ? -1 : (elo > upper ? 1 : 0));
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

    public int updateDivision() {
        int result = division.isWithin(elo);
        if (result != 0) {
            for (Division div : Division.values()) {
                if (div.isWithin(elo) == 0) {
                    division = div;
                    return result;
                }
            }
        }
        return 0;
    }

    /**
     * Only use this for when players enter Masters ranks and someone needs to drop
     *
     * @param division Division
     */
    public void setDivision(Division division) {
        this.division = division;
    }

    public boolean setElo(int value) {
        boolean increase = value > elo;
        elo = value;
        return increase;
    }

    public int addElo(int value) {
        elo += value;
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
