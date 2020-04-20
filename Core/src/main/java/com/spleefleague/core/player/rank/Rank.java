/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.player.rank;

import com.mongodb.client.MongoCursor;
import com.spleefleague.core.Core;
import com.spleefleague.core.database.annotation.DBField;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.database.variable.DBEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

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
    
    @DBField
    private String name;
    @DBField
    private String displayName;
    
    @DBField
    private Integer ladder;
    @DBField
    private Boolean hasOp;
    @DBField
    private ChatColor color;
    
    private final Set<String> permissions = new HashSet<>();
    private final Set<String> exclusivePermissions = new HashSet<>();
    
    private static final Map<String, Rank> ranks = new HashMap<>();
    
    private Rank() {
        
    }
    
    public String getName() {
        return name;
    }
    public String getNameShort() {
        return name.substring(0, Math.min(name.length(), 16));
    }
    public String getDisplayName() {
        return Chat.RANK + displayName + Chat.DEFAULT;
    }
    public String getDisplayNameUnformatted() {
        return displayName;
    }
    
    public int getLadder() {
        return ladder;
    }
    public boolean getHasOp() {
        return hasOp;
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
    public boolean hasAdditionalRank(String ranks) {
        String[] rankArray = ranks.split(",");
        for (String r : rankArray) {
            if (this.getName().equalsIgnoreCase(r)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasExclusivePermission(String permission) {
        return exclusivePermissions.contains(permission);
    }

    public boolean hasPermission(String permission) {
        for (String perm : exclusivePermissions) {
            if (perm.equals(permission)) {
                return true;
            }
        }
        for (Rank rank : ranks.values()) {
            if (rank.getLadder() < this.getLadder()) {
                for (String perm : rank.permissions) {
                    if (perm.equals(permission)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Set<String> getAllPermissions() {
        Set<String> perms = new HashSet<>(exclusivePermissions);
        perms.addAll(this.permissions);
        for (Rank rank : ranks.values()) {
            if (rank.getLadder() < this.getLadder()) {
                perms.addAll(rank.permissions);
            }
        }
        return perms;
    }

    public static Rank getDefaultRank() {
        return ranks.get("DEFAULT");
    }
    
    public static Rank getRank(String name) {
        if (name == null || !ranks.containsKey(name.toUpperCase())) return null;
        return ranks.get(name.toUpperCase());
    }
    
    public static Set<String> getRankNames() {
        return ranks.keySet();
    }

    public static void init() {
        MongoCursor<Document> dbc = Core.getInstance().getPluginDB().getCollection("Ranks").find().iterator();
        while (dbc.hasNext()) {
            Rank rank = new Rank();
            rank.load(dbc.next());
            ranks.put(rank.getName().toUpperCase(), rank);
        }
        
        Scoreboard mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Set<Team> teams = mainScoreboard.getTeams();
        teams.forEach(Team::unregister);
        initScoreboard(mainScoreboard);
        
        // Probably redo this at some point, just here to
        // make setting up command ranks easier
        DEFAULT = ranks.get("DEFAULT");
        ADMIN = ranks.get("ADMIN");
        DEVELOPER = ranks.get("DEVELOPER");
        MODERATOR = ranks.get("MODERATOR");
        SENIOR_MODERATOR = ranks.get("SENIOR_MODERATOR");
        BUILDER = ranks.get("BUILDER");
        ORGANIZER = ranks.get("ORGANIZER");
        VIP = ranks.get("VIP");
        DONOR_1 = ranks.get("$");
        DONOR_2 = ranks.get("$$");
        DONOR_3 = ranks.get("$$$");
    }
    
    public static void initScoreboard(Scoreboard scoreboard) {
        for (Rank rank : ranks.values()) {
            Team team = scoreboard.registerNewTeam(rank.getNameShort());
            team.setColor(rank.getColor());
            if (rank.getDisplayNameUnformatted().length() > 0)
                team.setPrefix("[" + rank.getDisplayNameUnformatted() + "] ");
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            team.setAllowFriendlyFire(true);
        }
    }
    
}
