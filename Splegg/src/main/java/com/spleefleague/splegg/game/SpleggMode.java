/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.splegg.game;

import com.spleefleague.core.game.BattleMode;
import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.splegg.game.classic.*;
import com.spleefleague.splegg.game.multi.MultiSpleggBattle;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public enum SpleggMode {

    VERSUS, MULTI;
    
    private static final String prefix = "splegg:";
    
    public static void init() {
        BattleMode.createArenaMode(VERSUS.getName())
                .setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Splegg Versus")
                .setDisplayItem(Material.EGG, 1)
                .setDescription("Test your might against another player in this 1v1 competition of precision and movement.")
                .setTeamStyle(BattleMode.TeamStyle.VERSUS)
                .setBattleClass(ClassicSpleggBattle.class);

        BattleMode.createArenaMode(MULTI.getName())
                .setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Multisplegg")
                .setDisplayItem(Material.EGG, 2)
                .setDescription("Take your skills to the next level in this free-for-all multiplayer edition of Splegg!")
                .setTeamStyle(BattleMode.TeamStyle.DYNAMIC)
                .setBattleClass(MultiSpleggBattle.class);
    }
    
    public String getName() {
        return prefix + name().toLowerCase();
    }
    
    public BattleMode getBattleMode() {
        return BattleMode.get(getName());
    }
    
}
