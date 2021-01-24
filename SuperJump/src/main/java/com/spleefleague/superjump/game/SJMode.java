/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game;

import com.spleefleague.core.game.BattleMode;
import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.superjump.game.conquest.*;
import com.spleefleague.superjump.game.endless.*;
import com.spleefleague.superjump.game.party.*;
import com.spleefleague.superjump.game.practice.*;
import com.spleefleague.superjump.game.pro.*;
import com.spleefleague.superjump.game.classic.*;
import com.spleefleague.superjump.game.shuffle.ShuffleSJBattle;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public enum SJMode {
    
    CLASSIC,
    SHUFFLE,
    CONQUEST,
    ENDLESS,
    PARTY,
    PRACTICE,
    PRO;
    
    private static final String prefix = "sj:";
    
    public static void init() {
        BattleMode.createArenaMode(CLASSIC.getName())
                .setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "SuperJump: Classic")
                .setDescription("A Very Classy GameMode.")
                .setDisplayItem(Material.DIAMOND_AXE, 22)
                .setTeamStyle(BattleMode.TeamStyle.VERSUS)
                .setBattleClass(ClassicSJBattle.class);

        BattleMode.createArenaMode(SHUFFLE.getName())
                .setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "SuperJump: Shuffle")
                .setDescription("A Very Classy GameMode.")
                .setDisplayItem(Material.DIAMOND_AXE, 24)
                .setTeamStyle(BattleMode.TeamStyle.VERSUS)
                .setBattleClass(ShuffleSJBattle.class);

        BattleMode.createArenaMode(CONQUEST.getName())
                .setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "SuperJump: Conquest")
                .setDescription("A Very Classy GameMode.")
                .setDisplayItem(Material.DIAMOND_AXE, 21)
                .setTeamStyle(BattleMode.TeamStyle.SOLO)
                .setBattleClass(ConquestSJBattle.class);

        BattleMode.createArenaMode(ENDLESS.getName())
                .setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "SuperJump: Endless")
                .setDescription("A Very Classy GameMode.")
                .setDisplayItem(Material.DIAMOND_AXE, 20)
                .setTeamStyle(BattleMode.TeamStyle.SOLO)
                .setBattleClass(EndlessSJBattle.class)
                .setForceRandom(true);

        BattleMode.createArenaMode(PARTY.getName())
                .setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "SuperJump: Party")
                .setDescription("A Very Classy GameMode.")
                .setDisplayItem(Material.DIAMOND_AXE, 20)
                .setTeamStyle(BattleMode.TeamStyle.TEAM)
                .setBattleClass(PartySJBattle.class);

        BattleMode.createArenaMode(PRACTICE.getName())
                .setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "SuperJump: Practice")
                .setDescription("A Very Classy GameMode.")
                .setDisplayItem(Material.DIAMOND_AXE, 20)
                .setTeamStyle(BattleMode.TeamStyle.SOLO)
                .setBattleClass(PracticeSJBattle.class);

        BattleMode.createArenaMode(PRO.getName())
                .setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "SuperJump: Pro")
                .setDescription("A Very Classy GameMode.")
                .setDisplayItem(Material.DIAMOND_AXE, 23)
                .setTeamStyle(BattleMode.TeamStyle.SOLO)
                .setBattleClass(ProSJBattle.class);
    }
    
    public String getName() {
        return prefix + name().toLowerCase();
    }

    public BattleMode getBattleMode() {
        return BattleMode.get(getName());
    }
    
}
