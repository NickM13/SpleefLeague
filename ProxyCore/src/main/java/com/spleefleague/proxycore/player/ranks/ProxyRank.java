package com.spleefleague.proxycore.player.ranks;

import com.spleefleague.coreapi.chat.Chat;
import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.player.ranks.Rank;

/**
 * @author NickM13
 */
public class ProxyRank extends Rank {

    // Some common ranks
    public static ProxyRank DEFAULT,
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

    public ProxyRank() {

    }

    public ProxyRank(String identifier, int ladder, ChatColor chatColor) {
        this.identifier = identifier;
        this.ladder = ladder;
        this.color = chatColor;
        setDisplayName(identifier);
    }

    @Override
    public void afterLoad() {
        super.afterLoad();
        setDisplayName(displayName);
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        if (displayName.isEmpty()) chatTag = "";
        else chatTag = Chat.TAG_BRACE + "[" + Chat.RANK + displayName + Chat.TAG_BRACE + "] ";
    }

    public void setLadder(int ladder) {
        this.ladder = ladder;
    }

    public void setMaxFriends(int maxFriends) {
        this.maxFriends = maxFriends;
    }

    public void setHasOp(boolean hasOp) {
        this.hasOp = hasOp;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Rank{" +
                "displayName='" + displayName + '\'' +
                ", ladder=" + ladder +
                '}';
    }

}
