/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.player.scoreboard;

import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
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
        if (cp.getPlayer() == null) return;
        cp.getPlayer().setScoreboard(ps.getScoreboard());

        // Pull all online players and add them to their respective ranked teams
        Scoreboard scoreboard = ps.getScoreboard();

        for (CorePlayer cp2 : Core.getInstance().getPlayers().getAllLocal()) {
            Team team = scoreboard.getTeam(cp2.getRank().getIdentifierShort());
            if (team != null) {
                team.addEntry(cp2.getName());
            }
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

    public static void updatePlayerRank(CorePlayer cp) {
        Team team;
        if ((team = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getEntryTeam(cp.getName())) != null) {
            team.removeEntry(cp.getName());
        }
        for (PersonalScoreboard ps : scoreboards.values()) {
            if (cp.getRank() == null) {
                Thread.dumpStack();
                break;
            }
            Team containedTeam = ps.getScoreboard().getTeam(cp.getRank().getIdentifierShort());
            if (containedTeam != null) {
                containedTeam.addEntry(cp.getName());
            }
        }
    }

    public static PersonalScoreboard getScoreboard(UUID uuid) {
        return scoreboards.get(uuid);
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
    }

}
