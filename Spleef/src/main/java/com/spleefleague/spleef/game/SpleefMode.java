/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game;

import com.spleefleague.core.game.BattleMode;
import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.spleef.game.battle.classic.affix.ClassicSpleefAffixes;
import com.spleefleague.spleef.game.battle.power.team.PowerSpleefTeamBattle;
import com.spleefleague.spleef.game.battle.power.training.PowerTrainingBattle;
import com.spleefleague.spleef.game.battle.power.versus.PowerSpleefVersusBattle;
import com.spleefleague.spleef.game.battle.team.*;
import com.spleefleague.spleef.game.battle.multi.*;
import com.spleefleague.spleef.game.battle.classic.*;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public enum SpleefMode {
    
    CLASSIC,
    TEAM,
    MULTI,
    POWER,
    POWER_TRAINING,
    POWER_TEAM;
    //WC,
    //BONANZA;
    
    private static final String prefix = "spleef:";
    
    public static void init() {
        /*
        BattleMode.createArenaMode(BONANZA.getName())
                .setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Bonanza Spleef")
                .setDescription("Drop in anytime to this fast paced, constant battleground of Spleef glory!" +
                        "  Compete for kill streaks against other players and become a champion." +
                        "\n\nYou may queue for other games while playing Bonanza Spleef.")
                .setDisplayItem(Material.DIAMOND_SHOVEL, 1561)
                .setTeamStyle(BattleMode.TeamStyle.BONANZA)
                .setBattleClass(BonanzaSpleefBattle.class)
                .setJoinOngoing(true)
                .setForceRandom(true);
        */

        BattleMode.createArenaMode(CLASSIC.getName())
                .setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Classic Spleef")
                .setDisplayItem(Material.DIAMOND_SHOVEL, 1)
                .setDescription("Take to the field in SpleefLeague's premier gamemode - Classic Spleef!" +
                        " Dig out blocks from under your opponent before they are able to do the same to you!" +
                        "\n\n&6Current Affixes: &7" + ClassicSpleefAffixes.getActiveDisplayNames())
                .setTeamStyle(BattleMode.TeamStyle.VERSUS)
                .setBattleClass(ClassicSpleefBattle.class)
                .setRewards(0, 10, 0.05, 0.02, 0.01, 0.002);

        BattleMode.createArenaMode(MULTI.getName())
                .setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Multispleef")
                .setDisplayItem(Material.SHEARS, 238)
                .setDescription("Fight your dominance in this free-for-all edition of Spleef.")
                .setTeamStyle(BattleMode.TeamStyle.DYNAMIC)
                .setBattleClass(MultiSpleefBattle.class)
                .setJoinOngoing(true)
                .setForceRandom(true)
                .setRewards(0, 20, 0.05, 0.02, 0.01, 0.002);

        BattleMode.createArenaMode(POWER.getName())
                .setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Power Spleef")
                .setDisplayItem(Material.GOLDEN_SHOVEL, 32)
                .setDescription("A twist on the original 1v1 Spleef Mode. Add unique powers to your Spleefing strategy!")
                .setTeamStyle(BattleMode.TeamStyle.VERSUS)
                .setBattleClass(PowerSpleefVersusBattle.class)
                .setRewards(0, 4, 0.025, 0.01, 0.005, 0.001);

        BattleMode.createArenaMode(POWER_TRAINING.getName())
                .setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Power Training")
                .setDisplayItem(Material.WOODEN_SHOVEL, 1)
                .setDescription("Hone your skills in this solo sandbox version of Power Spleef! Change your powers in game, control field regeneration and learn new combos to best your foes.")
                .setTeamStyle(BattleMode.TeamStyle.SOLO)
                .setBattleClass(PowerTrainingBattle.class)
                .setForceRandom(true);

        BattleMode.createArenaMode(POWER_TEAM.getName())
                .setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Team Power Spleef")
                .setDisplayItem(Material.LEATHER_HELMET, 56)
                .setDescription("United with a team of the same color, conquer your foes with your allies in this multiplayer gamemode.")
                .setTeamStyle(BattleMode.TeamStyle.TEAM)
                .setBattleClass(PowerSpleefTeamBattle.class)
                .setRewards(0, 15, 0.05, 0.02, 0.01, 0.002);

        BattleMode.createArenaMode(TEAM.getName())
                .setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Team Spleef")
                .setDisplayItem(Material.LEATHER_HELMET, 56)
                .setDescription("United with a team of the same color, conquer your foes with your allies in this multiplayer gamemode.")
                .setTeamStyle(BattleMode.TeamStyle.TEAM)
                .setBattleClass(TeamSpleefBattle.class)
                .setRewards(0, 15, 0.05, 0.02, 0.01, 0.002);
    }
    
    public String getName() {
        return prefix + name().toLowerCase();
    }
    
    public BattleMode getBattleMode() {
        return BattleMode.get(getName());
    }
    
}
