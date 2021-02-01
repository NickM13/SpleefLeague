/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.player.scoreboard;

import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRankManager;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

/**
 * @author NickM13
 */
public class PersonalScoreboard {
    
    protected static Map<UUID, PersonalScoreboard> scoreboards = new HashMap<>();

    public static void init() {
        Core.getInstance().addTask(Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            for (PersonalScoreboard ps : scoreboards.values()) {
                ps.update();
            }
        }, 20L, 20L));
    }

    public static void initPlayerScoreboard(CorePlayer cp) {
        PersonalScoreboard ps = new PersonalScoreboard(cp, true);
        scoreboards.put(cp.getUniqueId(), ps);
        ps.setScoreboardName("SpleefLeague, " + cp.getDisplayName());
        cp.getPlayer().setScoreboard(ps.getScoreboard());
        
        // Pull all online players and add them to their respective ranked teams
        Scoreboard scoreboard = ps.getScoreboard();
        for (CorePlayer cp2 : Core.getInstance().getPlayers().getAllHere()) {
            Objects.requireNonNull(scoreboard.getTeam(cp2.getRank().getIdentifierShort())).addEntry(cp2.getName());
        }

        /*
        List<CorePlayer> toScoreboardify = new ArrayList<>();
        for (CorePlayer cp2 : Core.getInstance().getPlayers().getAll()) {
            if (cp2.getOnlineState() != DBPlayer.OnlineState.OFFLINE) {
                toScoreboardify.add(cp2);
            }
        }
        if (!toScoreboardify.isEmpty()) {
            Core.sendPacket(cp, PacketUtils.createAddPlayerPacket(toScoreboardify));
        }
         */
    }

    public static void closePlayerScoreboard(CorePlayer cp) {
        if (scoreboards.containsKey(cp.getUniqueId())) {
            scoreboards.remove(cp.getUniqueId()).close();
        }
    }

    public static void refreshPlayers() {
        scoreboards.forEach((uuid, sb) -> {
            sb.getTabList().refreshPlayers();
        });
    }
    
    public static void updatePlayerRank(CorePlayer cp) {
        Team team;
        if ((team = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getEntryTeam(cp.getNickname())) != null) {
            team.removeEntry(cp.getNickname());
        }
        for (PersonalScoreboard ps : scoreboards.values()) {
            Objects.requireNonNull(ps.getScoreboard().getTeam(cp.getRank().getIdentifierShort())).addEntry(cp.getNickname());
        }
    }
    
    public static PersonalScoreboard createScoreboard(UUID uuid, boolean showRanks) {
        CorePlayer cp = Core.getInstance().getPlayers().getOffline(uuid);
        PersonalScoreboard ps = new PersonalScoreboard(cp, showRanks);
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

    public static void onPlayerJoin(CorePlayer corePlayer) {
        scoreboards.forEach((uuid2, sb) -> sb.getTabList().addPlayer(corePlayer));
    }

    public static void onPlayerQuit(UUID uuid) {
        scoreboards.remove(uuid);
        scoreboards.forEach((uuid2, sb) -> sb.getTabList().removePlayer(uuid));
    }
    
    protected Scoreboard scoreboard;
    protected PersonalTablist tabList;
    protected Objective sideBar;
    protected boolean showRanks;
    protected CorePlayer owner;
    
    public PersonalScoreboard(CorePlayer owner, boolean showRanks) {
        this.owner = owner;
        scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
        scoreboard.registerNewTeam("Players");
        Objects.requireNonNull(scoreboard.getTeam("Players")).setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        tabList = new PersonalTablist(owner);

        ((CraftServer) Bukkit.getServer()).getServer().getPlayerList();

        resetObjective();
        
        this.showRanks = showRanks;
        if (showRanks) {
            Core.getInstance().getRankManager().initScoreboard(scoreboard);
        }
    }

    public void close() {
        if (sideBar != null) sideBar.unregister();
    }
    
    public void resetObjective() {
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
        Objects.requireNonNull(scoreboard.getTeam(teamId)).setDisplayName(displayName);
    }
    
    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public PersonalTablist getTabList() {
        return tabList;
    }

    public Objective getSideBar() {
        return sideBar;
    }

    public void update() {
        tabList.updateHeaderFooter();
        tabList.updatePlayerList();
    }
    
}
