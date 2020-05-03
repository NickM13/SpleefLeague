/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.game;

import com.google.common.collect.Sets;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.arena.ArenaBuilder;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.game.leaderboard.Leaderboards;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainerChest;
import org.bukkit.Material;

import java.util.*;

/**
 * Arena Mode defines a gamemode
 *
 * @author NickM13
 */
public class BattleMode {
    
    public enum TeamStyle {
        SOLO,
        TEAM,
        VERSUS,
        DYNAMIC,
        BONANZA
    }
    
    protected static Map<String, BattleMode> battleGameModes = new HashMap<>();
    
    protected final String name;
    protected final String displayName;
    protected final int requiredTeams, maximumTeams;
    protected final TeamStyle teamStyle;
    protected final Set<Integer> requiredTeamSizes;
    protected final boolean joinOngoing;
    protected final Class<? extends Battle<?>> battleClass;
    protected final Set<Battle<?>> ongoingBattles = new HashSet<>();

    private BattleMode(String name,
            String displayName,
            int requiredTeams,
            int maximumTeams,
            TeamStyle teamStyle,
            boolean joinOngoing,
            Class<? extends Battle<?>> battleClass) {
        this.name = name;
        this.displayName = displayName;
        this.requiredTeams = requiredTeams;
        this.maximumTeams = maximumTeams;
        this.teamStyle = teamStyle;
        this.requiredTeamSizes = new HashSet<>();
        this.joinOngoing = joinOngoing;
        this.battleClass = battleClass;
    }
    
    public static void addArenaMode(String name,
            String displayName,
            int requiredTeams,
            int maximumTeams,
            TeamStyle teamStyle,
            boolean joinOngoing,
            Class<? extends Battle<?>> battleClass) {
        battleGameModes.put(name, new BattleMode(name, displayName, requiredTeams, maximumTeams, teamStyle, joinOngoing, battleClass));
    }
    
    public static BattleMode get(String name) {
        return battleGameModes.get(name);
    }
    
    public static Set<String> getAllNames() {
        return battleGameModes.keySet();
    }
    
    public static Collection<BattleMode> getAllModes() {
        return battleGameModes.values();
    }
    
    public Set<Battle<?>> getOngoingBattles() {
        return ongoingBattles;
    }
    
    public void addBattle(Battle<?> battle) {
        this.ongoingBattles.add(battle);
    }
    
    public void removeBattle(Battle<?> battle) {
        this.ongoingBattles.remove(battle);
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
    
    public String getChatTag() {
        return Chat.TAG_BRACE + "[" + displayName + "]";
    }
    
    public Class<? extends Battle<?>> getBattleClass() {
        return battleClass;
    }
    
    public int getSeason() {
        return Leaderboards.get(name).getActive().getSeason();
    }
    
    public InventoryMenuContainerChest createEditMenu() {
        return InventoryMenuAPI.createContainer()
                .setTitle(getDisplayName())
                .setOpenAction((container, cp) -> {
                    cp.setMenuTag("arenamode", this);
                })
                .setRefreshAction((container, cp) -> {
                    container.clearUnsorted();
                    container.addMenuItem(ArenaBuilder.createNewItem(getName()));
                    container.addMenuItem(ArenaBuilder.createExistingItem(getName()));
                    for (Arena arena : Arenas.getAll(this).values()) {
                        container.addMenuItem(ArenaBuilder.createEditItem(arena));
                    }
                });
    }
    
}
