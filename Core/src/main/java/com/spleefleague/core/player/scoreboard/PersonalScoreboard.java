/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.player.scoreboard;

import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * @author NickM13
 */
public class PersonalScoreboard {
    
    protected static Map<UUID, PersonalScoreboard> scoreboards = new HashMap<>();
    
    public static void initPlayerScoreboard(CorePlayer cp) {
        PersonalScoreboard ps = new PersonalScoreboard(true);
        scoreboards.put(cp.getUniqueId(), ps);
        ps.setScoreboardName("SpleefLeague, " + cp.getDisplayName());
        cp.getPlayer().setScoreboard(ps.getScoreboard());
        
        // Pull all online players and add them to their respective ranked teams
        Scoreboard scoreboard = ps.getScoreboard();
        for (CorePlayer cp2 : Core.getInstance().getPlayers().getOnline()) {
            scoreboard.getTeam(cp2.getRank().getIdentifierShort()).addEntry(cp2.getName());
        }
    }

    public static void closePlayerScoreboard(CorePlayer cp) {
        if (scoreboards.containsKey(cp)) {
            scoreboards.remove(cp).close();
        }
    }
    
    public static void updatePlayerRank(CorePlayer cp) {
        Team team;
        if ((team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(cp.getName())) != null) {
            team.removeEntry(cp.getName());
        }
        for (PersonalScoreboard ps : scoreboards.values()) {
            ps.getScoreboard().getTeam(cp.getRank().getIdentifierShort()).addEntry(cp.getName());
        }
    }
    
    public static PersonalScoreboard createScoreboard(UUID uuid, boolean showRanks) {
        CorePlayer cp = Core.getInstance().getPlayers().getOffline(uuid);
        PersonalScoreboard ps = new PersonalScoreboard(showRanks);
        scoreboards.put(uuid, ps);
        // Testing scoreboard display name for personal boards
        ps.setScoreboardName(cp.getDisplayName());
        return ps;
    }
    
    public static PersonalScoreboard getScoreboard(UUID uuid) {
        if (!scoreboards.containsKey(uuid)) {
            return createScoreboard(uuid, true);
        }
        return scoreboards.get(uuid);
    }
    
    protected Scoreboard scoreboard;
    protected Objective tabList, sideBar;
    protected boolean showRanks;
    
    public PersonalScoreboard(boolean showRanks) {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        scoreboard.registerNewTeam("Players");
        scoreboard.getTeam("Players").setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);

        resetObjective();
        
        this.showRanks = showRanks;
        if (showRanks) {
            Rank.initScoreboard(scoreboard);
        }
    }

    public void close() {
        if (tabList != null) tabList.unregister();
        if (sideBar != null) sideBar.unregister();
    }
    
    public void resetObjective() {
        if (tabList != null) tabList.unregister();
        tabList = scoreboard.registerNewObjective("Welcome to SL!", "dummy", "=-=");
        tabList.setDisplaySlot(DisplaySlot.PLAYER_LIST);

        if (sideBar != null) sideBar.unregister();
        sideBar = scoreboard.registerNewObjective("ServerName", "dummy", "=---=");
        sideBar.setDisplaySlot(DisplaySlot.SIDEBAR);
    }
    
    public void setScoreboardName(String name) {
        sideBar.setDisplayName(name);
    }
    
    public void createTeam(String teamId) {
        scoreboard.registerNewTeam(teamId);
        sideBar.getScore(teamId).setScore(0);
    }
    
    public void setTeamName(String teamId, String displayName) {
        scoreboard.getTeam(teamId);
    }
    
    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public Objective getTabList() {
        return tabList;
    }

    public Objective getSideBar() {
        return sideBar;
    }
    
}
