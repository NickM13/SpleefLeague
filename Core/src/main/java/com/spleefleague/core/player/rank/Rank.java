/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.player.rank;

import com.spleefleague.core.chat.Chat;

import java.util.*;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;
import org.bukkit.ChatColor;

/**
 * @author NickM13
 */
public class Rank extends DBEntity {

    // Some common ranks
    public static Rank  DEFAULT,
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

    @DBField private String displayName = "";
    private String formattedName = "";
    
    @DBField private Integer ladder = 0;
    @DBField private Boolean hasOp = false;
    @DBField private ChatColor color = ChatColor.YELLOW;
    @DBField private Integer maxFriends = 25;
    
    private final Set<String> permissions = new HashSet<>();
    private final Set<String> exclusivePermissions = new HashSet<>();

    private String priority = "000";
    
    public Rank() {
        
    }

    public Rank(String identifier, int ladder, ChatColor chatColor) {
        this.identifier = identifier;
        this.ladder = ladder;
        this.color = chatColor;
        setDisplayName(identifier);
    }

    @Override
    public void afterLoad() {
        setDisplayName(displayName);
    }

    public void setPriority(int priority) {
        if (priority < 0 || priority > 100) return;
        this.priority = String.format("%03d", priority);
    }

    public String getIdentifierShort() {
        return priority + identifier.substring(0, Math.min(identifier.length(), 13));
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        this.formattedName = Chat.colorize(this.displayName);
    }
    public String getChatTag() {
        if (formattedName.isEmpty()) return "";
        return Chat.TAG_BRACE + "[" + Chat.RANK + formattedName + Chat.TAG_BRACE + "] ";
    }
    public String getDisplayName() {
        return formattedName;
    }
    public String getDisplayNameUnformatted() {
        return formattedName;
    }

    public void setLadder(int ladder) {
        this.ladder = ladder;
    }
    public int getLadder() {
        return ladder;
    }

    public void setMaxFriends(int maxFriends) {
        this.maxFriends = maxFriends;
    }
    public int getMaxFriends() {
        return  maxFriends;
    }

    public void setHasOp(boolean hasOp) {
        this.hasOp = hasOp;
    }
    public boolean getHasOp() {
        return hasOp;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }
    public ChatColor getColor() {
        return color;
    }
    
    public void addPermission(String perm) {
        permissions.add(perm);
        exclusivePermissions.add(perm);
    }
    public void addExclusivePermission(String perm) {
        exclusivePermissions.add(perm);
    }
    
    public Set<String> getPermissions() {
        return permissions;
    }
    public Set<String> getExclusivePermissions() {
        return exclusivePermissions;
    }
    

    public boolean hasPermission(Rank rank) {
        return (this == rank ||
                this.getLadder() >= rank.getLadder());
    }
    public boolean hasPermission(Rank rank, List<Rank> additional) {
        if (hasPermission(rank)) {
            return true;
        } else {
            for (Rank r : additional) {
                if (this == r) {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean hasAdditionalRank(String ranks) {
        String[] rankArray = ranks.split(",");
        for (String rank : rankArray) {
            if (getIdentifier().equalsIgnoreCase(rank)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasExclusivePermission(String permission) {
        return exclusivePermissions.contains(permission);
    }

    public boolean hasPermission(String permission) {
        return Ranks.hasPermission(this, permission);
    }

    public Set<String> getAllPermissions() {
        return Ranks.getAllPermissions(this);
    }

    @Override
    public String toString() {
        return "Rank{" +
                "displayName='" + displayName + '\'' +
                ", ladder=" + ladder +
                '}';
    }

}
