/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.spleef.team;

import com.spleefleague.core.Core;
import com.spleefleague.core.database.annotation.DBField;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.player.party.Party;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefArena;
import com.spleefleague.spleef.game.SpleefMode;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class TeamSpleefArena extends SpleefArena {
    
    @DBField
    protected Integer teamSize;
    @DBField
    protected Integer teamCount;
    
    public TeamSpleefArena() {
        this.mode = SpleefMode.TEAM.getArenaMode();
    }
    
    public static void createMenu(int x, int y) {
        String mainColor = ChatColor.YELLOW + "" + ChatColor.BOLD;
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .createLinkedContainer("Team Spleef Menu")
                .setName(mainColor + "Team Spleef")
                .setDescription("United with a team of the same color, conquer your foes with your allies in this multiplayer gamemode.")
                .setAvailability(cp -> {
                    Party party = cp.getParty();
                    if (party == null) {
                        Core.getInstance().sendMessage(cp, "You have to be in a party for TeamSpleef!");
                        return false;
                    } else if (!SpleefMode.TEAM.getArenaMode().getRequiredTeamSizes().contains(party.getPlayers().size())) {
                        Core.getInstance().sendMessage(cp, "No TeamSpleef maps exist with a party size of " + party.getPlayers().size() + "!");
                        Core.getInstance().sendMessage(cp, "Valid sizes: " + SpleefMode.TEAM.getArenaMode().getRequiredTeamSizesString());
                        return false;
                    }
                    return true;
                })
                .setDisplayItem(Material.LEATHER_HELMET, 56);
        
        menuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createItem()
                .setName("Random Arena")
                .setDisplayItem(new ItemStack(Material.EMERALD))
                .setAction(cp -> Spleef.getInstance().queuePlayer(SpleefMode.TEAM.getArenaMode(), cp)));
        
        getArenas(SpleefMode.TEAM.getArenaMode()).forEach((String s, Arena arena) -> menuItem.getLinkedContainer()
                .addMenuItem(InventoryMenuAPI.createItem()
                        .setName(arena.getDisplayName())
                        .setVisibility(cp -> {
                            Party party = cp.getParty();
                            return party != null
                                    && (party.getPlayers().size() == arena.getTeamSize());
                        })
                        .setDescription(cp -> arena.getDescription())
                        .setDisplayItem(cp -> { return new ItemStack(Material.FILLED_MAP); })
                        .setAction(cp -> Spleef.getInstance().queuePlayer(SpleefMode.TEAM.getArenaMode(), cp, arena))));
        
        Spleef.getInstance().getSpleefMenu().getLinkedContainer().addMenuItem(menuItem, x, y);
    }
    
}
