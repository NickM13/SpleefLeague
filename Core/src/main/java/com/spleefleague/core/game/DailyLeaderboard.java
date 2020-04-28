package com.spleefleague.core.game;

import com.spleefleague.core.util.variable.Day;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 * @since 4/27/2020
 */
public class DailyLeaderboard extends Leaderboard {
    
    public DailyLeaderboard(String name, String displayName, ItemStack displayItem, String description) {
        super(name, displayName, displayItem, description);
    }
    
    
    @Override
    public void checkResetDay() {
        if (createDay + 1 <= Day.getCurrentDay()) {
            reset();
        }
    }
    
}
