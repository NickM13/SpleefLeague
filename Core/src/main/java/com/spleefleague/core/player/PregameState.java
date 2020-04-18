/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.player;

import com.google.common.collect.Sets;
import com.spleefleague.core.Core;
import com.spleefleague.core.database.variable.DBPlayer;
import java.util.Set;
import javax.annotation.Nullable;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class PregameState {
    
    public enum PSFlag {
        LOCATION,
        INVENTORY,
        GAMEMODE,
        ALL
    }
    
    private final CorePlayer cp;
    private Location location = null;
    private ItemStack[] inventory = null;
    private GameMode gameMode = null;
    
    public PregameState(CorePlayer cp) {
        this.cp = cp;
    }
    
    public void save(PSFlag ... include) {
        Set<PSFlag> flags = Sets.newHashSet(include);
        if (flags.contains(PSFlag.ALL)) {
            location = cp.getPlayer().getLocation();
            inventory = cp.getPlayer().getInventory().getContents();
            gameMode = cp.getPlayer().getGameMode();
        } else {
            if (flags.contains(PSFlag.LOCATION)) {
                location = cp.getPlayer().getLocation();
            }
            if (flags.contains(PSFlag.INVENTORY)) {
                inventory = cp.getPlayer().getInventory().getContents();
            }
            if (flags.contains(PSFlag.GAMEMODE)) {
                gameMode = cp.getPlayer().getGameMode();
            }
        }
    }
    
    public void clear() {
        location = null;
        inventory = null;
    }
    
    public void load(@Nullable Location arenaLoc) {
        if (location != null) {
            switch (cp.getOptions().getOption(CorePlayerOptions.CPOptions.POST_GAME_WARP)) {
                case 0:
                    cp.gotoSpawn();
                    break;
                case 1:
                    cp.teleport(location);
                    break;
                case 2:
                    if (arenaLoc != null)
                        cp.teleport(arenaLoc);
                    else
                        cp.teleport(location);
                    break;
            }
            location = null;
        }
        if (inventory != null) {
            cp.getPlayer().getInventory().setContents(inventory);
        }
        if (gameMode != null) {
            cp.setGameMode(gameMode);
        }
        clear();
    }
    
    public Location getLocation() {
        return location;
    }
    
    public ItemStack[] getInventory() {
        return inventory;
    }
    
}
