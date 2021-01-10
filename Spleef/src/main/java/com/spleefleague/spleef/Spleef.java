/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.menu.hotbars.SLMainHotbar;
import com.spleefleague.core.player.PlayerManager;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.spleef.commands.*;
import com.spleefleague.spleef.game.Shovel;
import com.spleefleague.spleef.game.battle.classic.affix.ClassicSpleefAffixes;
import com.spleefleague.spleef.game.battle.power.ability.Abilities;
import com.spleefleague.spleef.game.battle.power.ability.AbilityUtils;
import com.spleefleague.spleef.player.SpleefPlayer;
import com.spleefleague.spleef.game.SpleefMode;
import com.spleefleague.spleef.game.battle.bonanza.BonanzaSpleefArena;
import com.spleefleague.spleef.game.battle.classic.ClassicSpleefArena;
import com.spleefleague.spleef.game.battle.multi.MultiSpleefArena;
import com.spleefleague.spleef.game.battle.power.PowerSpleefArena;
import com.spleefleague.spleef.game.battle.team.TeamSpleefArena;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

        ClassicSpleefAffixes.init();
        Abilities.init();
        Shovel.init();
        //Power.init();
        
        // Initialize player manager
        playerManager = new PlayerManager<>(this, SpleefPlayer.class, getPluginDB().getCollection("Players"));
        
        // Load Spleef gamemodes
        SpleefMode.init();
        AbilityUtils.init();

        addBattleManager(SpleefMode.BONANZA.getBattleMode());
        addBattleManager(SpleefMode.CLASSIC.getBattleMode());
        addBattleManager(SpleefMode.MULTI.getBattleMode());
        addBattleManager(SpleefMode.POWER.getBattleMode());
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
    
    public InventoryMenuItem getSpleefMenu() {
        return spleefMenuItem;
    }

    private int getCurrentlyPlaying() {
        int playing = 0;
        for (SpleefMode mode : SpleefMode.values()) {
            for (Battle<?> battle : mode.getBattleMode().getOngoingBattles()) {
                playing += battle.getBattlers().size();
            }
        }
        return playing;
    }

    public void initMenu() {
        spleefMenuItem = InventoryMenuAPI.createItem()
                .setName(ChatColor.GOLD + "" + ChatColor.BOLD + "Spleef")
                .setDescription(cp -> "A competitive gamemode in which you must knock your opponent into the water while avoiding a similar fate." +
                        "\n\nThis is not with any ordinary weapon; the weapon of choice is a shovel, and you must destroy the blocks underneath your foe!" +
                        "\n\n&7&lCurrently Playing: &6" + getCurrentlyPlaying())
                .setDisplayItem(Material.DIAMOND_SHOVEL, 1)
                .createLinkedContainer("Spleef Menu");
        
        spleefMenuItem.getLinkedChest().addMenuItem(InventoryMenuUtils.createLockedMenuItem("Coming Soon!"), 0, 2);
        spleefMenuItem.getLinkedChest().addMenuItem(InventoryMenuUtils.createLockedMenuItem("Coming Soon!"), 1, 3);
        spleefMenuItem.getLinkedChest().addMenuItem(InventoryMenuUtils.createLockedMenuItem("Coming Soon!"), 2, 2);
        ClassicSpleefArena.createMenu(3, 3);
        spleefMenuItem.getLinkedChest().addMenuItem(InventoryMenuUtils.createLockedMenuItem("Coming Soon!"), 4, 2);
        PowerSpleefArena.createMenu(5, 3);
        spleefMenuItem.getLinkedChest().addMenuItem(InventoryMenuUtils.createLockedMenuItem("Coming Soon!"), 6, 2);
        spleefMenuItem.getLinkedChest().addMenuItem(InventoryMenuUtils.createLockedMenuItem("Coming Soon!"), 7, 3);
        spleefMenuItem.getLinkedChest().addMenuItem(InventoryMenuUtils.createLockedMenuItem("Coming Soon!"), 8, 2);

        Shovel.createMenu();
    
        SLMainHotbar.getItemHotbar().getLinkedChest().addMenuItem(spleefMenuItem, 4, 2);

        ClassicSpleefAffixes.createMenu();
    
        ClassicSpleefArena.initLeaderboard(0, 1);
        TeamSpleefArena.initLeaderboard(2, 1);
        MultiSpleefArena.initLeaderboard(1, 2);
        PowerSpleefArena.initLeaderboard(3, 2);
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
    public String getChatPrefix() {
        return Chat.TAG_BRACE + "[" + Chat.TAG + "Spleef" + Chat.TAG_BRACE + "] " + Chat.DEFAULT;
    }
    
}
