/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef;

import com.spleefleague.core.Core;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.menu.hotbars.SLMainHotbar;
import com.spleefleague.core.player.PlayerManager;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.spleef.commands.*;
import com.spleefleague.spleef.game.SpleefArena;
import com.spleefleague.spleef.game.Shovel;
import com.spleefleague.spleef.game.SpleefField;
import com.spleefleague.spleef.player.SpleefPlayer;
import com.spleefleague.spleef.game.SpleefMode;
import com.spleefleague.spleef.game.spleef.banana.BananaSpleefArena;
import com.spleefleague.spleef.game.spleef.classic.ClassicSpleefArena;
import com.spleefleague.spleef.game.spleef.multi.MultiSpleefArena;
import com.spleefleague.spleef.game.spleef.power.Power;
import com.spleefleague.spleef.game.spleef.power.PowerSpleefArena;
import com.spleefleague.spleef.game.spleef.team.TeamSpleefArena;
import org.bukkit.Bukkit;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class Spleef extends CorePlugin<SpleefPlayer> {
    
    private static Spleef instance;
    
    private InventoryMenuItem spleefMenuItem;
    
    @Override
    public void init() {
        instance = this;

        setPluginDB("Spleef");
        
        // Initialize commands
        initCommands();
        
        Shovel.init();
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
    
    public void initMenu() {
        spleefMenuItem = InventoryMenuAPI.createItem()
                .setName("Spleef")
                .setDescription("A competitive gamemode in which you must knock your opponent into the water while avoiding a similar fate.\n\nThis is not with any ordinary weapon; the weapon of choice is a shovel, and you must destroy the blocks underneath your foe!")
                .setDisplayItem(Material.DIAMOND_SHOVEL, 1561)
                .createLinkedContainer("Spleef Menu");
        
        spleefMenuItem.getLinkedContainer().addMenuItem(InventoryMenuUtils.createLockedMenuItem("Other"), 0, 2);
        spleefMenuItem.getLinkedContainer().addMenuItem(InventoryMenuUtils.createLockedMenuItem("Bow Spleef"), 1, 3);
        BananaSpleefArena.createMenu(2, 2);
        TeamSpleefArena.createMenu(3, 3);
        ClassicSpleefArena.createMenu(4, 2);
        MultiSpleefArena.createMenu(5, 3);
        PowerSpleefArena.createMenu(6, 2);
        spleefMenuItem.getLinkedContainer().addMenuItem(InventoryMenuUtils.createLockedMenuItem("Other"), 7, 3);
        spleefMenuItem.getLinkedContainer().addMenuItem(InventoryMenuUtils.createLockedMenuItem("Other"), 8, 2);
        
        spleefMenuItem.getLinkedContainer().addStaticItem(Shovel.createMenu(), 4, 4);
    
        SLMainHotbar.getItemHotbar().getLinkedContainer().addMenuItem(spleefMenuItem, 4, 2);
    }
    
    public void initCommands() {
        Core.getInstance().addCommand(new ShovelCommand());
        Core.getInstance().addCommand(new SpleefCommand());
        Core.getInstance().addCommand(new SpleefFieldCommand());
        
        Core.getInstance().flushCommands();
    }
    
}
