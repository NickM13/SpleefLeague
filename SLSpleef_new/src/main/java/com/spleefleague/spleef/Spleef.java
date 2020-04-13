/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef;

import com.mongodb.client.MongoDatabase;
import com.spleefleague.core.Core;
import com.spleefleague.core.game.Battle;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.player.PlayerManager;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.spleef.commands.*;
import com.spleefleague.spleef.game.SpleefArena;
import com.spleefleague.spleef.game.Shovel;
import com.spleefleague.spleef.game.SpleefField;
import com.spleefleague.spleef.player.SpleefPlayer;
import com.spleefleague.spleef.game.SpleefMode;
import com.spleefleague.spleef.game.SpleggMode;
import com.spleefleague.spleef.game.spleef.banana.BananaSpleefArena;
import com.spleefleague.spleef.game.splegg.classic.SpleggGun;
import com.spleefleague.spleef.game.spleef.classic.ClassicSpleefArena;
import com.spleefleague.spleef.game.spleef.multi.MultiSpleefArena;
import com.spleefleague.spleef.game.spleef.power.Power;
import com.spleefleague.spleef.game.spleef.power.PowerSpleefArena;
import com.spleefleague.spleef.game.splegg.classic.ClassicSpleggArena;
import com.spleefleague.spleef.game.splegg.multi.MultiSpleggArena;
import com.spleefleague.spleef.game.spleef.team.TeamSpleefArena;
import org.bukkit.Bukkit;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class Spleef extends CorePlugin<SpleefPlayer> {
    
    private static Spleef instance;
    
    private InventoryMenuItem spleefMenuItem;
    private InventoryMenuItem spleggMenuItem;
    
    @Override
    public void init() {
        instance = this;
        
        // Initialize commands
        initCommands();
        
        Shovel.init();
        SpleggGun.init();
        Power.init();
        
        // Initialize player manager
        playerManager = new PlayerManager<>(this, SpleefPlayer.class, getPluginDB().getCollection("Players"));
        
        // Load Spleef gamemodes
        SpleefMode.init();
        addBattleManager(SpleefMode.BONANZA.getArenaMode());
        addBattleManager(SpleefMode.CLASSIC.getArenaMode());
        addBattleManager(SpleefMode.MULTI.getArenaMode());
        addBattleManager(SpleefMode.POWER.getArenaMode());
        addBattleManager(SpleefMode.TEAM.getArenaMode());
        
        // Load database related static lists
        SpleefField.init();
        SpleefArena.init();
        
        // Load Splegg gamemodes
        SpleggMode.init();
        addBattleManager(SpleggMode.CLASSIC.getArenaMode());
        addBattleManager(SpleggMode.MULTI.getArenaMode());
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
    
    public InventoryMenuItem getSpleefMenu() {
        return spleefMenuItem;
    }
    public InventoryMenuItem getSpleggMenu() {
        return spleggMenuItem;
    }
    
    public void initMenu() {
        spleefMenuItem = InventoryMenuAPI.createItem()
                .setName("Spleef")
                .setDescription("A competitive gamemode in which you must knock your opponent into the water while avoiding a similar fate.\n\nThis is not with any ordinary weapon; the weapon of choice is a shovel, and you must destroy the blocks underneath your foe!")
                .setDisplayItem(Material.DIAMOND_SHOVEL, 1561)
                .createLinkedContainer("Spleef Menu");
        
        spleefMenuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createLockedMenuItem("Other"), 0, 2);
        spleefMenuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createLockedMenuItem("Bow Spleef"), 1, 3);
        BananaSpleefArena.createMenu(2, 2);
        TeamSpleefArena.createMenu(3, 3);
        ClassicSpleefArena.createMenu(4, 2);
        MultiSpleefArena.createMenu(5, 3);
        PowerSpleefArena.createMenu(6, 2);
        spleefMenuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createLockedMenuItem("Other"), 7, 3);
        spleefMenuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createLockedMenuItem("Other"), 8, 2);
        spleefMenuItem.getLinkedContainer().addStaticItem(Shovel.createMenu(), 4, 4);
        
        InventoryMenuAPI.getHotbarItem(InventoryMenuAPI.InvMenuType.SLMENU).getLinkedContainer().addMenuItem(spleefMenuItem, 4, 2);
        
        spleggMenuItem = InventoryMenuAPI.createItem()
                .setName("Splegg")
                .setDescription("Imagine the following description included the word egg in it somewhere.\n\nA competitive gamemode in which you must knock your opponent into the water while avoiding a similar fate.\n\nThis is not with any ordinary weapon; the weapon of choice is a shovel, and you must destroy the blocks underneath your foe!")
                .setDisplayItem(Material.EGG)
                .createLinkedContainer("Splegg Menu");
        spleggMenuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createLockedMenuItem("Other"), 0, 2);
        spleggMenuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createLockedMenuItem("Other"), 1, 3);
        spleggMenuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createLockedMenuItem("Other"), 2, 2);
        spleggMenuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createLockedMenuItem("TeamSplegg"), 3, 3);
        ClassicSpleggArena.createMenu(4, 2);
        MultiSpleggArena.createMenu(5, 3);
        spleggMenuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createLockedMenuItem("Other"), 6, 2);
        spleggMenuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createLockedMenuItem("Other"), 7, 3);
        spleggMenuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createLockedMenuItem("Other"), 8, 2);
        spleggMenuItem.getLinkedContainer().addStaticItem(SpleggGun.createMenu(), 4, 4);
        
        InventoryMenuAPI.getHotbarItem(InventoryMenuAPI.InvMenuType.SLMENU).getLinkedContainer().addMenuItem(spleggMenuItem, 5, 3);
    }
    
    public void initCommands() {
        Core.getInstance().addCommand(new ShovelCommand());
        Core.getInstance().addCommand(new SpleefCommand());
        Core.getInstance().addCommand(new SpleggCommand());
        
        Core.getInstance().flushCommands();
    }
    
    @Override
    public MongoDatabase getPluginDB() {
        return Core.getInstance().getMongoClient().getDatabase("SuperSpleef");
    }
    
    public Battle getPlayerBattle(SpleefPlayer dbp) {
        return dbp.getBattle();
    }
    public void spectatePlayer(SpleefPlayer spectator, SpleefPlayer target) {
        target.getBattle().addSpectator(spectator, target);
    }
    
}
