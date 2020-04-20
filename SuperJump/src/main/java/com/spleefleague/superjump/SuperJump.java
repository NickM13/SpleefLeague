/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump;

import com.mongodb.client.MongoDatabase;
import com.spleefleague.core.Core;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainer;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.menu.hotbars.SLMainHotbar;
import com.spleefleague.core.player.PlayerManager;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.superjump.commands.*;
import com.spleefleague.superjump.game.SJArena;
import com.spleefleague.superjump.game.SJMode;
import com.spleefleague.superjump.game.conquest.ConquestSJArena;
import com.spleefleague.superjump.game.endless.EndlessSJArena;
import com.spleefleague.superjump.game.pro.ProSJArena;
import com.spleefleague.superjump.game.versus.classic.ClassicSJArena;
import com.spleefleague.superjump.game.versus.shuffle.ShuffleSJArena;
import com.spleefleague.superjump.player.SuperJumpPlayer;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class SuperJump extends CorePlugin<SuperJumpPlayer> {
    
    private static SuperJump instance;
    
    private InventoryMenuItem superJumpMenuItem;
    
    @Override
    public void init() {
        instance = this;

        setPluginDB("SuperJump");

        initCommands();
        
        playerManager = new PlayerManager<>(this, SuperJumpPlayer.class, getPluginDB().getCollection("Players"));
        
        SJMode.init();
        for (SJMode mode : SJMode.values()) {
            addBattleManager(mode.getArenaMode());
        }
        
        SJArena.init();
        initMenu();
        initLeaderboards();
    }
    
    public static SuperJump getInstance() {
        return instance;
    }
    
    @Override
    public void close() {
        playerManager.close();
    }
    
    private void initCommands() {
        Core.getInstance().addCommand(new SuperJumpCommand());
        
        Core.getInstance().flushCommands();
    }
    
    public InventoryMenuItem getSJMenuItem() {
        return superJumpMenuItem;
    }
    
    private void initMenu() {
        superJumpMenuItem = InventoryMenuAPI.createItem()
                .setName("SuperJump")
                .setDescription("Jump and run your way to the finish line as fast as you can. Whether you are racing a single opponent, a group of friends, or even the clock, the objective is the same!")
                .setDisplayItem(Material.LEATHER_BOOTS, 65);
        
        InventoryMenuContainer container = superJumpMenuItem.getLinkedContainer();
        
        container.addMenuItem(InventoryMenuUtils.createLockedMenuItem("Party"), 0, 2);
        container.addMenuItem(InventoryMenuUtils.createLockedMenuItem("Tetronimo"), 1, 3);
        ShuffleSJArena.createMenu(2, 2);
        ConquestSJArena.createMenu(3, 3);
        EndlessSJArena.createMenu(4, 2);
        ClassicSJArena.createMenu(5, 3);
        ProSJArena.createMenu(6, 2);
        container.addMenuItem(InventoryMenuUtils.createLockedMenuItem("Memory"), 7, 3);
        container.addMenuItem(InventoryMenuUtils.createLockedMenuItem("Practice"), 8, 2);
        //superJumpMenu.addMenuItem(PartySJArena.createMenu(), 5);
        //superJumpMenu.addMenuItem(PracticeSJArena.createMenu(), 6);
    
        SLMainHotbar.getItemHotbar().getLinkedContainer().addMenuItem(superJumpMenuItem, 3, 3);
    }
    
    private void initLeaderboards() {
        EndlessSJArena.initLeaderboard();
    }
    
}
