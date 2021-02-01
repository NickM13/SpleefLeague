/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.player.rank;

import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.player.ranks.Rank;

/**
 * @author NickM13
 */
public class CoreRank extends Rank {

    // Some common ranks
    public static CoreRank DEFAULT,
            DEVELOPER,
            ADMIN,
            MODERATOR,
            SENIOR_MODERATOR,
            BUILDER,
            ORGANIZER,
            VIP,
            DONOR_1,
            DONOR_2,
            DONOR_3;
    
    private String priority = "000";
    
    public CoreRank() {

    }

    public CoreRank(String rankName, int ladder, ChatColor color) {
        this.displayName = rankName;
        this.ladder = ladder;
        this.color = color;
        afterLoad();
    }

    public void setPriority(int priority) {
        if (priority < 0 || priority > 100) return;
        this.priority = String.format("%03d", priority);
    }

    public String getPriority() {
        return priority;
    }

    public String getIdentifierShort() {
        return priority + identifier.substring(0, Math.min(identifier.length(), 13));
    }

    @Override
    public String toString() {
        return "Rank{" +
                "displayName='" + displayName + '\'' +
                ", ladder=" + ladder +
                '}';
    }

}
