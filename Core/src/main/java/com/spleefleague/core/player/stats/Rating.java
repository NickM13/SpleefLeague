package com.spleefleague.core.player.stats;

import com.spleefleague.core.database.annotation.DBField;
import com.spleefleague.core.database.variable.DBEntity;
import org.bukkit.ChatColor;

/**
 * @author NickM13
 * @since 4/28/2020
 */
public class Rating extends DBEntity {
    
    public static enum Division {
        BRONZE("Bronze", ChatColor.DARK_RED + "", 0, 400),
        SILVER("Silver", ChatColor.GRAY + "", 400, 800),
        GOLD("Gold", ChatColor.YELLOW + "", 800, 1200),
        PLATINUM("Platinum", ChatColor.AQUA + "", 1200, 1600),
        DIAMOND("Diamond", ChatColor.BLUE + "", 1600, 2000),
        MASTER("Master", ChatColor.LIGHT_PURPLE + "", 2000, 2500);
        
        protected String displayName;
        protected String scorePrefix;
        protected int lower, upper;
        
        Division(String displayName, String scorePrefix, int lower, int upper) {
            this.displayName = displayName;
            this.scorePrefix = scorePrefix;
            this.lower = lower;
            this.upper = upper;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getScorePrefix() {
            return scorePrefix;
        }
        
        public boolean isWithin(int elo) {
            return elo >= lower && elo <= upper;
        }
    }
    
    protected static final int BASE_ELO = 1000;
    
    @DBField
    protected int elo;
    @DBField
    protected Division division;
    
    public Rating() {
        elo = BASE_ELO;
        this.division = Division.GOLD;
    }
    
    private void updateDivision() {
        for (Division div : Division.values()) {
            if (div.isWithin(elo)) {
                if (division != div) {
                    division = div;
                }
                break;
            }
        }
    }
    
    public void addElo(int value) {
        elo += value;
    }
    
}
