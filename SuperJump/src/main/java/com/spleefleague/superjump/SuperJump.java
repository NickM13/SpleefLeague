/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.menu.*;
import com.spleefleague.core.menu.hotbars.SLMainHotbar;
import com.spleefleague.core.menu.hotbars.main.GamemodeMenu;
import com.spleefleague.core.player.PlayerManager;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.superjump.commands.*;
import com.spleefleague.superjump.game.SJMode;
import com.spleefleague.superjump.game.conquest.ConquestSJArena;
import com.spleefleague.superjump.game.endless.EndlessSJArena;
import com.spleefleague.superjump.game.pro.ProSJArena;
import com.spleefleague.superjump.game.classic.ClassicSJArena;
import com.spleefleague.superjump.game.shuffle.ShuffleSJArena;
import com.spleefleague.superjump.player.SuperJumpPlayer;
import com.spleefleague.superjump.util.SJUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
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
        
        SJUtils.init();
        SJMode.init();
        for (SJMode mode : SJMode.values()) {
            addBattleManager(mode.getBattleMode());
        }
        
        //SJArena.init();
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
    
    @Override
    public TextComponent getChatPrefix() {
        return new TextComponent(Chat.TAG_BRACE + "[" + Chat.TAG + "SuperJump" + Chat.TAG_BRACE + "] ");
    }
    
    private void initCommands() {
        Core.getInstance().addCommand(new SuperJumpCommand());
    }
    
    public InventoryMenuItem getSJMenuItem() {
        return superJumpMenuItem;
    }

    private int getCurrentlyPlaying() {
        int playing = 0;
        for (SJMode mode : SJMode.values()) {
            for (Battle<?> battle : mode.getBattleMode().getOngoingBattles()) {
                playing += battle.getBattlers().size();
            }
        }
        return playing;
    }
    
    private void initMenu() {
        superJumpMenuItem = InventoryMenuAPI.createItemDynamic()
                .setName(ChatColor.GOLD + "" + ChatColor.BOLD + "SuperJump")
                .setDescription("Jump and run your way to the finish line as fast as you can. Whether you are racing a single opponent, a group of friends, or even the clock, the objective is the same!"
                        /*"\n\n&7&lCurrently Playing: &6" + getCurrentlyPlaying()*/)
                .setDisplayItem(Material.LEATHER_BOOTS, 1)
                .createLinkedContainer("SuperJump");
        
        InventoryMenuContainerChest container = superJumpMenuItem.getLinkedChest();

        InventoryMenuItem shuffleMenu = Arenas.createMenu(getInstance(), SJMode.SHUFFLE.getBattleMode());
        InventoryMenuItem conquestMenu = Arenas.createMenu(getInstance(), SJMode.CONQUEST.getBattleMode());
        InventoryMenuItem endlessMenu = Arenas.createMenu(getInstance(), SJMode.ENDLESS.getBattleMode());
        InventoryMenuItem classicMenu = Arenas.createMenu(getInstance(), SJMode.CLASSIC.getBattleMode());
        InventoryMenuItem proMenu = Arenas.createMenu(getInstance(), SJMode.PRO.getBattleMode());

        container.addStaticItem(shuffleMenu, 2, 1);
        container.addStaticItem(conquestMenu, 3, 1);
        container.addStaticItem(endlessMenu, 4, 1);
        container.addStaticItem(classicMenu, 5, 1);
        container.addStaticItem(proMenu, 6, 1);

        //container.addStaticItem(InventoryMenuUtils.createLockedMenuItem("Party"), 3, 2);
        //container.addStaticItem(InventoryMenuUtils.createLockedMenuItem("Tetronimo"), 4, 2);
        //container.addStaticItem(InventoryMenuUtils.createLockedMenuItem("Memory"), 6, 2);
        //container.addStaticItem(InventoryMenuUtils.createLockedMenuItem("Practice"), 5, 2);
    
        GamemodeMenu.getItem().getLinkedChest().addStaticItem(superJumpMenuItem, 5, 1);
    }
    
    private void initLeaderboards() {
        //EndlessSJArena.initLeaderboard();
    }
    
}
