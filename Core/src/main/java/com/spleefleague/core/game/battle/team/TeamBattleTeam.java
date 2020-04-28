package com.spleefleague.core.game.battle.team;

import org.bukkit.ChatColor;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author NickM13
 * @since 4/25/2020
 */
public class TeamBattleTeam<BP extends TeamBattlePlayer> {
    
    public enum TeamName {
        BLUE(ChatColor.BLUE + "Blue", org.bukkit.Color.fromRGB(0, 0, 255)),
        RED(ChatColor.RED + "Red", org.bukkit.Color.fromRGB(255, 0, 0)),
        YELLOW(ChatColor.YELLOW + "Yellow", org.bukkit.Color.fromRGB(255, 255, 0)),
        GREEN(ChatColor.GREEN + "Green", org.bukkit.Color.fromRGB(0, 255, 0));
    
        String name;
        org.bukkit.Color color;
    
        TeamName(String name, org.bukkit.Color color) {
            this.name = name;
            this.color = color;
        }
    
        public String getName() {
            return name;
        }
    
        public org.bukkit.Color getColor() {
            return color;
        }
    }
    
    protected final TeamName teamName;
    protected Set<BP> players = new LinkedHashSet<>();
    
    public TeamBattleTeam(TeamName teamName) {
        this.teamName = teamName;
    }
    
    public TeamName getTeamName() {
        return teamName;
    }
    
    public void addPlayer(BP player) {
        players.add(player);
    }
    
}
