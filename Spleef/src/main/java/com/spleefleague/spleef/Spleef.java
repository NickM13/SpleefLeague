/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainerChest;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.hotbars.main.GamemodeMenu;
import com.spleefleague.core.menu.hotbars.main.LeaderboardMenu;
import com.spleefleague.core.player.CoreDBPlayer;
import com.spleefleague.core.player.PlayerManager;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.spleef.commands.*;
import com.spleefleague.spleef.game.Shovel;
import com.spleefleague.spleef.game.battle.classic.affix.ClassicSpleefAffixes;
import com.spleefleague.spleef.game.battle.power.ability.Abilities;
import com.spleefleague.spleef.game.battle.power.ability.Ability;
import com.spleefleague.spleef.game.battle.power.ability.AbilityUtils;
import com.spleefleague.spleef.game.battle.power.training.PowerTrainingMenu;
import com.spleefleague.spleef.player.SpleefPlayer;
import com.spleefleague.spleef.game.SpleefMode;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class Spleef extends CorePlugin {
    
    private static Spleef instance;
    
    private InventoryMenuItem spleefMenuItem;

    private PlayerManager<SpleefPlayer, CoreDBPlayer> playerManager;
    
    @Override
    public void init() {
        instance = this;

        setPluginDB("Spleef");
        
        // Initialize commands
        initCommands();

        ClassicSpleefAffixes.init();
        Abilities.init();
        Shovel.init();
        
        // Initialize player manager
        playerManager = new PlayerManager<>(this, SpleefPlayer.class, CoreDBPlayer.class, getPluginDB().getCollection("Players"));
        
        // Load Spleef gamemodes
        SpleefMode.init();
        AbilityUtils.init();

        addBattleManager(SpleefMode.CLASSIC.getBattleMode());
        addBattleManager(SpleefMode.MULTI.getBattleMode());
        addBattleManager(SpleefMode.POWER.getBattleMode());
        addBattleManager(SpleefMode.POWER_TEAM.getBattleMode());
        addBattleManager(SpleefMode.POWER_TRAINING.getBattleMode());
        addBattleManager(SpleefMode.TEAM.getBattleMode());
        
        initMenu();
    }
    
    @Override
    public void close() {
        playerManager.close();
        
        Bukkit.getScheduler().cancelTasks(this);
    }
    
    public static Spleef getInstance() {
        return instance;
    }

    public PlayerManager<SpleefPlayer, CoreDBPlayer> getPlayers() {
        return playerManager;
    }
    
    public InventoryMenuItem getSpleefMenu() {
        return spleefMenuItem;
    }

    public void initMenu() {
        spleefMenuItem = InventoryMenuAPI.createItemDynamic()
                .setName(ChatColor.GOLD + "" + ChatColor.BOLD + "Spleef")
                .setDescription(cp -> "A competitive gamemode in which you must knock your opponent into the water while avoiding a similar fate." +
                        "\n\nThis is not with any ordinary weapon; the weapon of choice is a shovel, and you must destroy the blocks underneath your foe!"
                        /*"\n\n&7&lCurrently Playing: &6" + getCurrentlyPlaying()*/)
                .setDisplayItem(Material.DIAMOND_SHOVEL, 1)
                .createLinkedContainer("Spleef");

        InventoryMenuContainerChest mainContainer = spleefMenuItem.getLinkedChest();

        InventoryMenuItem classicMenu = Arenas.createMenu(getInstance(), SpleefMode.CLASSIC.getBattleMode());

        InventoryMenuItem powerMenu = Arenas.createMenu(getInstance(), SpleefMode.POWER.getBattleMode());

        InventoryMenuItem powerTrainingMenu = Arenas.createMenu(getInstance(), SpleefMode.POWER_TRAINING.getBattleMode());
        powerMenu.getLinkedChest().addStaticItem(powerTrainingMenu, 2, 5);

        powerMenu.getLinkedChest().addStaticItem(Abilities.createAbilityMenuItem(Ability.Type.OFFENSIVE, powerMenu.getLinkedChest()), 6, 2);
        powerMenu.getLinkedChest().addStaticItem(Abilities.createAbilityMenuItem(Ability.Type.UTILITY, powerMenu.getLinkedChest()), 6, 3);
        powerMenu.getLinkedChest().addStaticItem(Abilities.createAbilityMenuItem(Ability.Type.MOBILITY, powerMenu.getLinkedChest()), 6, 4);

        InventoryMenuItem powerTeamMenu = Arenas.createMenu(getInstance(), SpleefMode.POWER_TEAM.getBattleMode());

        powerTeamMenu.getLinkedChest().addStaticItem(Abilities.createAbilityMenuItem(Ability.Type.OFFENSIVE, powerMenu.getLinkedChest()), 6, 2);
        powerTeamMenu.getLinkedChest().addStaticItem(Abilities.createAbilityMenuItem(Ability.Type.UTILITY, powerMenu.getLinkedChest()), 6, 3);
        powerTeamMenu.getLinkedChest().addStaticItem(Abilities.createAbilityMenuItem(Ability.Type.MOBILITY, powerMenu.getLinkedChest()), 6, 4);

        InventoryMenuItem teamMenu = Arenas.createMenu(getInstance(), SpleefMode.TEAM.getBattleMode());

        InventoryMenuItem multiMenu = Arenas.createMenu(getInstance(), SpleefMode.MULTI.getBattleMode());

        mainContainer.addStaticItem(classicMenu, 6, 1);
        mainContainer.addStaticItem(powerMenu, 5, 1);
        mainContainer.addStaticItem(teamMenu, 4, 1);
        mainContainer.addStaticItem(multiMenu, 3, 1);
        mainContainer.addStaticItem(powerTeamMenu, 2, 1);

        Shovel.createMenu();
    
        GamemodeMenu.getItem().getLinkedChest().addStaticItem(spleefMenuItem, 6, 1);

        ClassicSpleefAffixes.createMenu();

        LeaderboardMenu.addLeaderboardMenu(SpleefMode.CLASSIC.getBattleMode());
        LeaderboardMenu.addLeaderboardMenu(SpleefMode.TEAM.getBattleMode());
        LeaderboardMenu.addLeaderboardMenu(SpleefMode.MULTI.getBattleMode());
        LeaderboardMenu.addLeaderboardMenu(SpleefMode.POWER.getBattleMode());

        PowerTrainingMenu.createMenu();
    }
    
    public void initCommands() {
        Core.getInstance().addCommand(new AffixCommand());
        Core.getInstance().addCommand(new ClassicSpleefCommand());
        Core.getInstance().addCommand(new MultiSpleefCommand());
        Core.getInstance().addCommand(new PowerSpleefCommand());
        Core.getInstance().addCommand(new ShovelCommand());
        Core.getInstance().addCommand(new SpleefCommand());
        Core.getInstance().addCommand(new TeamSpleefCommand());
    }
    
    /**
     * @return Chat Prefix
     */
    public TextComponent getChatPrefix() {
        return new TextComponent(Chat.TAG_BRACE + "[" + Chat.TAG + "Spleef" + Chat.TAG_BRACE + "] " + Chat.DEFAULT);
    }
    
}
