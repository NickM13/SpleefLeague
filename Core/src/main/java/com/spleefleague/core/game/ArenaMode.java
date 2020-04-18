/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.game;

import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Arena Mode defines a gamemode, named ArenaMode for lack of
 * a better word since GameMode is already taken by Minecraft
 *
 * TODO: Better name
 *
 * @author NickM13
 */
public class ArenaMode {
    
    public enum TeamStyle {
        SOLO,
        TEAM,
        MULTI_STATIC,
        MULTI_DYNAMIC,
        MULTI_BANANA
    }
    
    protected static Map<String, ArenaMode> arenaModes = new HashMap<>();
    
    protected final String name;
    protected final String displayName;
    protected final int requiredTeams, maximumTeams;
    protected final TeamStyle teamStyle;
    protected final Set<Integer> requiredTeamSizes;
    protected final boolean joinOngoing;
    protected final Class<? extends Arena> arenaClass;
    protected final Class<? extends Battle<? extends Arena>> battleClass;

    // TODO: Rework this, constructor is enormongo
    protected ArenaMode(String name, String displayName, int requiredTeams, int maximumTeams, TeamStyle teamStyle, boolean joinOngoing, Class<? extends Arena> arenaClass, Class<? extends Battle<? extends Arena>> battleClass) {
        this.name = name;
        this.displayName = displayName;
        this.requiredTeams = requiredTeams;
        this.maximumTeams = maximumTeams;
        this.teamStyle = teamStyle;
        this.requiredTeamSizes = new HashSet<>();
        this.joinOngoing = joinOngoing;
        this.arenaClass = arenaClass;
        this.battleClass = battleClass;
    }
    
    public static void addArenaMode(String name, String displayName, int requiredTeams, int maximumTeams, TeamStyle teamStyle, boolean joinOngoing, Class<? extends Arena> arenaClass, Class<? extends Battle<? extends Arena>> battleClass) {
        arenaModes.put(name, new ArenaMode(name, displayName, requiredTeams, maximumTeams, teamStyle, joinOngoing, arenaClass, battleClass));
    }
    public static ArenaMode getArenaMode(String name) {
        return arenaModes.get(name);
    }
    
    public TeamStyle getTeamStyle() {
        return teamStyle;
    }
    
    public int getRequiredTeams() {
        return requiredTeams;
    }
    
    public int getMaximumTeams() {
        return maximumTeams;
    }
    
    public void addRequiredTeamSize(int requiredTeamSize) {
        if (requiredTeamSize != 0) {
            requiredTeamSizes.add(requiredTeamSize);
        }
    }
    public Set<Integer> getRequiredTeamSizes() {
        if (requiredTeamSizes.isEmpty()) return Sets.newHashSet(1);
        return requiredTeamSizes;
    }
    public String getRequiredTeamSizesString() {
        String formatted = "";
        for (Integer size : requiredTeamSizes) {
            formatted = formatted.concat(!formatted.isEmpty() ? ", " : "") + size;
        }
        return formatted;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public Class<? extends Arena> getArenaClass() {
        return arenaClass;
    }
    
    public Class<? extends Battle<? extends Arena>> getBattleClass() {
        return battleClass;
    }
    
}
