/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.chat;

import com.spleefleague.core.scoreboard.PersonalScoreboard;
import com.spleefleague.core.util.database.DBPlayer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import joptsimple.internal.Strings;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

/**
 * @author NickM13
 */
public class ChatGroup {
    
    private class SimpleScore {
        public String name, displayName;
        public int score;
        
        public SimpleScore(String name, int score) {
            this.name = name;
            this.displayName = "";
            this.score = score;
        }
    }
    
    private final Set<DBPlayer> players = new HashSet<>();
    
    private String scoreboardName = "empty";
    private final Map<String, SimpleScore> scores = new HashMap<>();
    private final List<String> sortedScores = new ArrayList<>();
    
    public ChatGroup() {
        addTeam("lastslotempty", Strings.repeat(' ', 30));
    }
    
    public void setScoreboardName(String name) {
        scoreboardName = name;
        for (DBPlayer dbp : players) {
            PersonalScoreboard ps = PersonalScoreboard.getScoreboard(dbp.getUniqueId());
            ps.getObjective().setDisplayName(name);
        }
    }
    
    /**
     * Scoreboard identifier names ChatColor strings to prevent them
     * from showing up in the scoreboards
     * 
     * <Name, Colorized>
     */
    private final Map<String, String> nameIdHash = new HashMap<>();
    private String idToStr(int id) {
        String str = "";
        while (id >= 0) {
            switch (id % 16) {
                case 0: str += ChatColor.AQUA;          break;
                case 1: str += ChatColor.BLACK;         break;
                case 2: str += ChatColor.BLUE;          break;
                case 3: str += ChatColor.DARK_AQUA;     break;
                case 4: str += ChatColor.DARK_BLUE;     break;
                case 5: str += ChatColor.DARK_GRAY;     break;
                case 6: str += ChatColor.DARK_GREEN;    break;
                case 7: str += ChatColor.DARK_PURPLE;   break;
                case 8: str += ChatColor.DARK_RED;      break;
                case 9: str += ChatColor.GOLD;          break;
                case 10: str += ChatColor.GRAY;         break;
                case 11: str += ChatColor.GREEN;        break;
                case 12: str += ChatColor.LIGHT_PURPLE; break;
                case 13: str += ChatColor.RED;          break;
                case 14: str += ChatColor.WHITE;        break;
                case 15: str += ChatColor.YELLOW;       break;
            }
            id -= 16;
        }
        return str;
    }
    /**
     * Adds a line at the bottom of the scoreboard, name is used to refer
     * to the team but is converted to an unreadable string of ChatColors
     * to prevent it from showing up on the board
     * 
     * @param name
     * @param displayName 
     */
    public void addTeam(String name, String displayName) {
        String str = idToStr(nameIdHash.size() + 16);
        nameIdHash.put(name, str);
        SimpleScore ss = new SimpleScore(str, 0);
        ss.displayName = displayName;
        scores.put(name, ss);
        sortedScores.add(name);
        for (DBPlayer dbp : players) {
            PersonalScoreboard ps = PersonalScoreboard.getScoreboard(dbp.getUniqueId());
            
            if (ps.getScoreboard().getTeam(ss.name) == null)
                ps.getScoreboard().registerNewTeam(ss.name).addEntry(ss.name);
            ps.getScoreboard().getTeam(ss.name).setDisplayName(ss.displayName);
            ps.getScoreboard().getTeam(ss.name).setPrefix(ss.displayName);
            ps.getScoreboard().getTeam(ss.name).setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            int i = sortedScores.size() - 1;
            for (String sscore : sortedScores) {
                scores.get(sscore).score = i;
                ps.getObjective().getScore(scores.get(sscore).name).setScore(i);
                i--;
            }
        }
    }
    
    /**
     * Sets the displayed name of a team, sent to all
     * players in the ChatGroup
     * 
     * @param name
     * @param displayName 
     */
    public void setTeamDisplayName(String name, String displayName) {
        SimpleScore ss = scores.get(name);
        ss.displayName = displayName;
        for (DBPlayer dbp : players) {
            PersonalScoreboard ps = PersonalScoreboard.getScoreboard(dbp.getUniqueId());
            
            dbp.getPlayer().getScoreboard().getTeam(ss.name).setPrefix(ss.displayName);
        }
    }
    
    /**
     * Does not store name locally, only sent to specified player
     * 
     * @param dbp
     * @param name
     * @param displayName 
     */
    public void setTeamDisplayNamePersonal(DBPlayer dbp, String name, String displayName) {
        PersonalScoreboard.getScoreboard(dbp.getUniqueId()).getScoreboard().getTeam(scores.get(name).name).setPrefix(displayName);
    }
    
    /**
     * Moving away from using team scores for points,
     * instead use a display name with the score included
     * because scores are used for sorting lines
     * 
     * @param name
     * @param score
     * @deprecated
     */
    @Deprecated
    public void setTeamScore(String name, int score) {
        SimpleScore ss = scores.get(name);
        ss.score = score;
        for (DBPlayer dbp : players) {
            PersonalScoreboard ps = PersonalScoreboard.getScoreboard(dbp.getUniqueId());
            
            ps.getObjective().getScore(ss.name).setScore(ss.score);
        }
    }
    
    /**
     * Sets the experience level and progress of every player in the ChatGroup
     * 
     * @param progress
     * @param level 
     */
    public void setExperience(float progress, int level) {
        for (DBPlayer dbp : players) {
            dbp.getPlayer().sendExperienceChange(progress, level);
        }
    }
    
    /**
     * Adds a player to the ChatGroup and updates their scoreboard to match
     * the default version (no personal scores filled)
     * 
     * @param dbp 
     */
    public void addPlayer(DBPlayer dbp) {
        players.add(dbp);
        
        PersonalScoreboard ps = PersonalScoreboard.getScoreboard(dbp.getUniqueId());
        ps.getObjective().setDisplayName(scoreboardName);
        for (SimpleScore ss : scores.values()) {
            if (ps.getScoreboard().getTeam(ss.name) == null)
                ps.getScoreboard().registerNewTeam(ss.name).addEntry(ss.name);
            ps.getScoreboard().getTeam(ss.name).setDisplayName(ss.displayName);
            ps.getScoreboard().getTeam(ss.name).setPrefix(ss.displayName);
            ps.getScoreboard().getTeam(ss.name).setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            ps.getObjective().getScore(ss.name).setScore(ss.score);
            int i = sortedScores.size() - 1;
            for (String sscore : sortedScores) {
                scores.get(sscore).score = i;
                ps.getObjective().getScore(scores.get(sscore).name).setScore(i);
                i--;
            }
        }
    }
    
    /**
     * Removes a player from the ChatGroup and resets their
     * SideBar scoreboard
     * 
     * @param dbp 
     */
    public void removePlayer(DBPlayer dbp) {
        dbp.getPlayer().sendExperienceChange(0, 0);
        players.remove(dbp);
        PersonalScoreboard.getScoreboard(dbp.getUniqueId()).resetObjective();
    }
    
    /**
     * Sends a chat message to all players in the ChatGroup
     * 
     * @param msg 
     */
    public void sendMessage(String msg) {
        for (DBPlayer dbp : players) {
            Chat.sendMessageToPlayer(dbp, msg);
        }
    }
    
    /**
     * Sends a title to all players in the ChatGroup
     * 
     * @param title
     * @param subtitle
     * @param fadeIn
     * @param stay
     * @param fadeOut 
     */
    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        for (DBPlayer dbp : players) {
            dbp.getPlayer().sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }
    }
    
}
