/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.chat;

import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.scoreboard.PersonalScoreboard;
import com.spleefleague.core.database.variable.DBPlayer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import joptsimple.internal.Strings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

/**
 * @author NickM13
 */
public class ChatGroup {
    
    private static class SimpleScore {
        public String name, displayName;
        public int score;
        
        public SimpleScore(String name, int score) {
            this.name = name;
            this.displayName = "";
            this.score = score;
        }
    }
    
    private final Set<CorePlayer> players = new HashSet<>();
    
    private String scoreboardName = "empty";
    private final Map<String, SimpleScore> scores = new HashMap<>();
    private final List<String> sortedScores = new ArrayList<>();
    
    public ChatGroup() {
        addTeam("lastslotempty", Strings.repeat(' ', 30));
    }
    
    public void setScoreboardName(String name) {
        scoreboardName = name;
        for (CorePlayer cp : players) {
            PersonalScoreboard ps = PersonalScoreboard.getScoreboard(cp.getUniqueId());
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
        StringBuilder str = new StringBuilder();
        while (id >= 0) {
            switch (id % 16) {
                case 0: str.append(ChatColor.AQUA);          break;
                case 1: str.append(ChatColor.BLACK);         break;
                case 2: str.append(ChatColor.BLUE);          break;
                case 3: str.append(ChatColor.DARK_AQUA);     break;
                case 4: str.append(ChatColor.DARK_BLUE);     break;
                case 5: str.append(ChatColor.DARK_GRAY);     break;
                case 6: str.append(ChatColor.DARK_GREEN);    break;
                case 7: str.append(ChatColor.DARK_PURPLE);   break;
                case 8: str.append(ChatColor.DARK_RED);      break;
                case 9: str.append(ChatColor.GOLD);          break;
                case 10: str.append(ChatColor.GRAY);         break;
                case 11: str.append(ChatColor.GREEN);        break;
                case 12: str.append(ChatColor.LIGHT_PURPLE); break;
                case 13: str.append(ChatColor.RED);          break;
                case 14: str.append(ChatColor.WHITE);        break;
                case 15: str.append(ChatColor.YELLOW);       break;
            }
            id -= 16;
        }
        return str.toString();
    }
    /**
     * Adds a line at the bottom of the scoreboard, name is used to refer
     * to the team but is converted to an unreadable string of ChatColors
     * to prevent it from showing up on the board
     * 
     * @param name Name
     * @param displayName Display Name
     */
    public void addTeam(String name, String displayName) {
        String str = idToStr(nameIdHash.size() + 16);
        nameIdHash.put(name, str);
        SimpleScore ss = new SimpleScore(str, 0);
        ss.displayName = Chat.SCOREBOARD_DEFAULT + displayName;
        scores.put(name, ss);
        sortedScores.add(name);
        for (CorePlayer cp : players) {
            PersonalScoreboard ps = PersonalScoreboard.getScoreboard(cp.getUniqueId());
            Team team = ps.getScoreboard().getTeam(ss.name);
            if (team == null) {
                team = ps.getScoreboard().registerNewTeam(ss.name);
                // TODO: Is this necessary?
                team.addEntry(ss.name);
            }
            team.setDisplayName(ss.displayName);
            team.setPrefix(ss.displayName);
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
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
     * @param name Name
     * @param displayName Display Name
     */
    public void setTeamDisplayName(String name, String displayName) {
        SimpleScore ss = scores.get(name);
        ss.displayName = Chat.SCOREBOARD_DEFAULT + displayName;
        for (CorePlayer cp : players) {
            PersonalScoreboard ps = PersonalScoreboard.getScoreboard(cp.getUniqueId());
            
            Team team = cp.getPlayer().getScoreboard().getTeam(ss.name);
            if (team == null) continue;
            team.setPrefix(ss.displayName);
        }
    }
    
    /**
     * Does not store name locally, only sent to specified player
     * 
     * @param dbp DBPlayer
     * @param name Identifier
     * @param displayName Display Name
     */
    public void setTeamDisplayNamePersonal(DBPlayer dbp, String name, String displayName) {
        Team team = PersonalScoreboard.getScoreboard(dbp.getUniqueId()).getScoreboard().getTeam(scores.get(name).name);
        if (team != null)
            team.setPrefix(displayName);
    }
    
    /**
     * Moving away from using team scores for points,
     * instead use a display name with the score included
     * because scores are used for sorting lines
     * 
     * @param name Identifier
     * @param score Score
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
     * @param progress Progress 0 to 1
     * @param level Level
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
     * @param cp Core Player
     */
    public void addPlayer(CorePlayer cp) {
        players.add(cp);
        
        PersonalScoreboard ps = PersonalScoreboard.getScoreboard(cp.getUniqueId());
        ps.getObjective().setDisplayName(scoreboardName);
        for (SimpleScore ss : scores.values()) {
            Team team = cp.getPlayer().getScoreboard().getTeam(ss.name);
            if (team == null) {
                team = ps.getScoreboard().registerNewTeam(ss.name);
                //TODO: Is this necessary?
                team.addEntry(ss.name);
            }
            team.setDisplayName(ss.displayName);
            team.setPrefix(ss.displayName);
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
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
     * @param cp Core Player
     */
    public void removePlayer(CorePlayer cp) {
        cp.getPlayer().sendExperienceChange(0, 0);
        players.remove(cp);
        PersonalScoreboard.getScoreboard(cp.getUniqueId()).resetObjective();
    }
    
    /**
     * Sends a chat message to all players in the ChatGroup
     * 
     * @param msg Message
     */
    public void sendMessage(String msg) {
        for (CorePlayer cp : players) {
            Chat.sendMessageToPlayer(cp, msg);
        }
    }
    
    /**
     * Sends a title to all players in the ChatGroup
     * 
     * @param title Title
     * @param subtitle Sub Title
     * @param fadeIn Ticks
     * @param stay Ticks
     * @param fadeOut Ticks
     */
    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        for (DBPlayer dbp : players) {
            dbp.getPlayer().sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }
    }
    
}
