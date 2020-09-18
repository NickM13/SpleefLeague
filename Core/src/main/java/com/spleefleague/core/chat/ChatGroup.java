/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.chat;

import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.scoreboard.PersonalScoreboard;

import java.util.*;

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
    private final String chatTag;
    
    private String scoreboardName = "empty";
    private final Map<String, SimpleScore> scores = new HashMap<>();
    private final List<String> sortedScores = new ArrayList<>();
    private int rightSideBuffer = 0;
    
    public ChatGroup(String chatTag) {
        //addTeam("lastslotempty", Strings.repeat(' ', 30));
        this.chatTag = chatTag;
    }
    
    /**
     * Sets the name displayed at the top of the scoreboard
     *
     * @param name Scoreboard Name
     */
    public void setScoreboardName(String name) {
        scoreboardName = name;
        for (CorePlayer cp : players) {
            PersonalScoreboard ps = PersonalScoreboard.getScoreboard(cp.getUniqueId());
            ps.getSideBar().setDisplayName(name);
        }
    }
    
    /**
     * Sets the number of pixels to the right second text is shown
     *
     * @param buffer Pixel Count
     */
    public void setRightSideBuffer(int buffer) {
        rightSideBuffer = buffer;
    }
    
    /**
     * Scoreboard identifier names ChatColor strings to prevent them
     * from showing up in the scoreboards
     * 
     * <Name, Colorized>
     */
    private int nextNameIndex = 0;
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
        String str = idToStr(nextNameIndex + 16);
        nextNameIndex++;
        nameIdHash.put(name, str);
        SimpleScore ss = new SimpleScore(str, 0);
        ss.displayName = Chat.SCOREBOARD_DEFAULT + displayName;
        scores.put(name, ss);
        sortedScores.add(name);
        
        int i = sortedScores.size() - 1;
        for (String sscore : sortedScores) {
            scores.get(sscore).score = i;
            i--;
        }
        
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
            for (String sscore : sortedScores) {
                ps.getSideBar().getScore(scores.get(sscore).name).setScore(scores.get(sscore).score);
            }
        }
    }
    
    public void removeTeam(String name) {
        String str = nameIdHash.remove(name);
        if (str == null) return;
        scores.remove(name);
    
        int i = scores.size() - 1;
        Iterator<String> ssit = sortedScores.iterator();
        while (ssit.hasNext()) {
            String next = ssit.next();
            if (!scores.containsKey(next)) {
                ssit.remove();
            } else {
                scores.get(next).score = i;
            }
            i--;
        }
        
        for (CorePlayer cp : players) {
            PersonalScoreboard ps = PersonalScoreboard.getScoreboard(cp.getUniqueId());
            Team team = ps.getScoreboard().getTeam(str);
            if (team != null) {
                team.unregister();
                ps.getScoreboard().resetScores(str);
            }
            for (String sscore : sortedScores) {
                ps.getSideBar().getScore(scores.get(sscore).name).setScore(scores.get(sscore).score);
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
            if (team == null || ss.displayName.length() > 63) continue;
            team.setPrefix(ss.displayName);
        }
    }
    
    /**
     * Sets the displayed name of a team, sent to all
     * players in the ChatGroup
     *
     * @param name Name
     * @param displayLeft Shown text on Left
     * @param displayRight Shown text on Right
     */
    public void setTeamDisplayName(String name, String displayLeft, String displayRight) {
        SimpleScore ss = scores.get(name);
        StringBuilder displayName = new StringBuilder(Chat.SCOREBOARD_DEFAULT + displayLeft);
        ChatUtils.appendSpacesTo(displayName, rightSideBuffer);
        displayName.append(displayRight);
        ss.displayName = displayName.toString();
        for (CorePlayer cp : players) {
            PersonalScoreboard ps = PersonalScoreboard.getScoreboard(cp.getUniqueId());
            
            Team team = cp.getPlayer().getScoreboard().getTeam(ss.name);
            if (team == null || ss.displayName.length() > 63) continue;
            team.setPrefix(ss.displayName);
        }
    }
    
    /**
     * Does not store name locally, only sent to specified player
     * 
     * @param cp CorePlayer
     * @param name Identifier
     * @param displayName Display Name
     */
    public void setTeamDisplayNamePersonal(CorePlayer cp, String name, String displayName) {
        Team team = PersonalScoreboard.getScoreboard(cp.getUniqueId()).getScoreboard().getTeam(scores.get(name).name);
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
        for (CorePlayer dbp : players) {
            PersonalScoreboard ps = PersonalScoreboard.getScoreboard(dbp.getUniqueId());
            
            ps.getSideBar().getScore(ss.name).setScore(ss.score);
        }
    }
    
    /**
     * Sets the experience level and progress of every player in the ChatGroup
     * 
     * @param progress Progress 0 to 1
     * @param level Level
     */
    @Deprecated
    public void setExperience(float progress, int level) {
        for (CorePlayer cp : players) {
            cp.getPlayer().sendExperienceChange(progress, level);
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
        ps.getSideBar().setDisplayName(scoreboardName);
        for (SimpleScore ss : scores.values()) {
            Team team = cp.getPlayer().getScoreboard().getTeam(ss.name);
            if (team == null) {
                team = ps.getScoreboard().registerNewTeam(ss.name);
                //TODO: Is this necessary?
                team.addEntry(ss.name);
            }
            if (ss.displayName.length() <= 63) {
                team.setDisplayName(ss.displayName);
                team.setPrefix(ss.displayName);
            }
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            ps.getSideBar().getScore(ss.name).setScore(ss.score);
            int i = sortedScores.size() - 1;
            for (String sscore : sortedScores) {
                scores.get(sscore).score = i;
                ps.getSideBar().getScore(scores.get(sscore).name).setScore(i);
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
        //cp.getPlayer().sendExperienceChange(0, 0);
        if (cp != null) {
            if (cp.getPlayer() != null) {
                cp.getPlayer().setTotalExperience(0);
            }
            players.remove(cp);
            PersonalScoreboard.getScoreboard(cp.getUniqueId()).resetObjective();
        }
    }
    
    /**
     * Sends a chat message to all players in the ChatGroup
     * 
     * @param msg Message
     */
    public void sendMessage(String msg) {
        for (CorePlayer cp : players) {
            Chat.sendMessageToPlayer(cp, chatTag + msg);
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
        for (CorePlayer cp : players) {
            Bukkit.getPlayer(cp.getUniqueId()).sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }
    }
    
}
