/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.coreapi.player.ranks;

import com.spleefleague.coreapi.chat.Chat;
import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author NickM13
 */
public class Rank extends DBEntity {

    @DBField protected String displayName = "";
    protected String chatTag = "";

    @DBField protected Integer ladder = 0;
    @DBField protected Boolean hasOp = false;
    @DBField protected ChatColor color = ChatColor.YELLOW;
    @DBField protected Integer maxFriends = 25;

    protected final Set<String> permissions = new HashSet<>();
    protected final Set<String> exclusivePermissions = new HashSet<>();
    protected final Set<String> inheritedPermissions = new HashSet<>();
    protected final Set<String> allPermissions = new HashSet<>();

    protected final List<Rank> inheritingRanks = new ArrayList<>();

    public Rank() {

    }

    @Override
    public void afterLoad() {
        if (displayName.isEmpty()) chatTag = "";
        else chatTag = Chat.TAG_BRACE + "[" + Chat.RANK + displayName + Chat.TAG_BRACE + "] ";
    }

    public String getChatTag() {
        return chatTag;
    }
    public String getDisplayName() {
        return displayName;
    }
    public String getDisplayNameUnformatted() {
        return displayName;
    }

    public void addInheritingRank(Rank rank) {
        inheritingRanks.add(rank);
    }

    public int getLadder() {
        return ladder;
    }

    public int getMaxFriends() {
        return  maxFriends;
    }

    public boolean getHasOp() {
        return hasOp;
    }

    public ChatColor getColor() {
        return color;
    }

    public void addPermission(String perm) {
        permissions.add(perm);
        allPermissions.add(perm);
        for (Rank rank : inheritingRanks) {
            rank.addInheritedPermission(perm);
        }
    }
    public void addExclusivePermission(String perm) {
        exclusivePermissions.add(perm);
        allPermissions.add(perm);
    }
    public void addInheritedPermission(String perm) {
        inheritedPermissions.add(perm);
        allPermissions.add(perm);
    }

    public Set<String> getPermissions() {
        return permissions;
    }
    public Set<String> getExclusivePermissions() {
        return exclusivePermissions;
    }


    public boolean hasPermission(Rank rank) {
        return (this == rank ||
                this.getLadder() > rank.getLadder());
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
        return allPermissions.contains(permission);
    }

    public Set<String> getAllPermissions() {
        return allPermissions;
    }

    @Override
    public String toString() {
        return "Rank{" +
                "displayName='" + displayName + '\'' +
                ", ladder=" + ladder +
                '}';
    }

}
