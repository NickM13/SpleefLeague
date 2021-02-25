/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.game;

import com.google.common.collect.Sets;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.arena.ArenaBuilder;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainerChest;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.settings.Settings;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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

    private static final ItemStack DEFAULT_ITEM = new ItemStack(Material.BARRIER);

    protected final String name;
    protected String displayName = "";
    protected String description = "";
    protected ItemStack displayItem = DEFAULT_ITEM;
    protected int requiredTeams = -1, maximumTeams = -1;
    protected TeamStyle teamStyle;
    protected Set<Integer> requiredTeamSizes = new HashSet<>();
    protected boolean joinOngoing = false;
    protected boolean forceRandom = false;
    protected Class<? extends Battle<?>> battleClass = null;
    protected final Map<UUID, Battle<?>> ongoingBattles = new HashMap<>();

    protected boolean hasRewards = false;
    protected int minCoins = 0, maxCoins = 0;
    protected double commonWeight = 0, rareWeight = 0, epicWeight = 0, legendaryWeight = 0;

    private BattleMode(String name) {
        this.name = name;
    }

    public static BattleMode createArenaMode(String name) {
        BattleMode battleMode = new BattleMode(name);
        battleGameModes.put(name, battleMode);
        return battleMode;
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

    public BattleMode setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public BattleMode setDescription(String description) {
        this.description = description;
        return this;
    }

    public BattleMode setDisplayItem(Material material, int cmd) {
        this.displayItem = InventoryMenuUtils.createCustomItem(material, cmd);
        return this;
    }

    public BattleMode setRequiredTeams(int requiredTeams) {
        this.requiredTeams = requiredTeams;
        return this;
    }

    public BattleMode setMaximumTeams(int maximumTeams) {
        this.maximumTeams = maximumTeams;
        return this;
    }

    public BattleMode setTeamStyle(TeamStyle teamStyle) {
        if (requiredTeams == -1) {
            switch (teamStyle) {
                case SOLO:
                    requiredTeams = 1;
                    maximumTeams = 1;
                    break;
                case VERSUS:
                    requiredTeams = 2;
                    maximumTeams = 2;
                    break;
                case DYNAMIC:
                    requiredTeams = 2;
                    maximumTeams = 32;
                    break;
                case TEAM:
                    requiredTeams = 2;
                    maximumTeams = 16;
                    break;
                case BONANZA:
                    requiredTeams = 0;
                    maximumTeams = 0;
                    break;
            }
        }
        this.teamStyle = teamStyle;
        return this;
    }

    public BattleMode setJoinOngoing(boolean joinOngoing) {
        this.joinOngoing = joinOngoing;
        return this;
    }

    public BattleMode setForceRandom(boolean forceRandom) {
        this.forceRandom = forceRandom;
        return this;
    }

    public BattleMode setBattleClass(Class<? extends Battle<?>> clazz) {
        this.battleClass = clazz;
        return this;
    }

    public BattleMode setRewards(int minCoins, int maxCoins, double commonWeight, double rareWeight, double epicWeight, double legendaryWeight) {
        this.hasRewards = true;
        this.minCoins = minCoins;
        this.maxCoins = maxCoins;
        this.commonWeight = commonWeight;
        this.rareWeight = rareWeight;
        this.epicWeight = epicWeight;
        this.legendaryWeight = legendaryWeight;
        return this;
    }

    public boolean hasRewards() {
        return hasRewards;
    }

    public int getMinCoins() {
        return minCoins;
    }

    public int getMaxCoins() {
        return maxCoins;
    }

    public double getCommonWeight() {
        return commonWeight;
    }

    public double getRareWeight() {
        return rareWeight;
    }

    public double getEpicWeight() {
        return epicWeight;
    }

    public double getLegendaryWeight() {
        return legendaryWeight;
    }

    public Map<UUID, Battle<?>> getOngoingBattles() {
        return ongoingBattles;
    }

    public void addBattle(Battle<?> battle) {
        this.ongoingBattles.put(battle.getBattleId(), battle);
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

    public String getDescription() {
        return description;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public boolean isForceRandom() {
        return forceRandom;
    }

    public String getChatTag() {
        return Chat.TAG_BRACE + "[" + displayName + "]";
    }

    public Class<? extends Battle<?>> getBattleClass() {
        return battleClass;
    }

    @Deprecated
    public String getSeason() {
        return Settings.getCurrentSeason();
    }

    public InventoryMenuContainerChest createEditMenu() {
        return InventoryMenuAPI.createContainer()
                .setTitle(getDisplayName())
                .setOpenAction((container, cp) -> cp.getMenu().setMenuTag("arenamode", this))
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
