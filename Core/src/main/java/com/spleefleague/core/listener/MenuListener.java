/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.listener;

import com.spleefleague.core.Core;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.player.CorePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

/**
 * @author NickM13
 */
public class MenuListener implements Listener {
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e) {
        CorePlayer cp = Core.getInstance().getPlayers().get(e.getPlayer());
        
        if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
                && e.getItem() != null) {
            InventoryMenuItem menu = InventoryMenuAPI.getHotbarItem(e.getItem());
            if (menu != null) {
                menu.callAction(cp);
            }
        }
    }
    
    @EventHandler
    public void onSwapHandItems(PlayerSwapHandItemsEvent e) {
        CorePlayer cp = Core.getInstance().getPlayers().get(e.getPlayer());
        if (!cp.canBuild()) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        CorePlayer cp = Core.getInstance().getPlayers().get(e.getPlayer().getName());
        if (cp != null) {
            cp.setInventoryMenuContainer(null);
        }
    }
    
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        CorePlayer cp = Core.getInstance().getPlayers().get(e.getWhoClicked().getName());
        
        if (cp.getInventoryMenuContainer() != null) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onInventoryInteract(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        CorePlayer cp = Core.getInstance().getPlayers().get(e.getWhoClicked().getName());
        
        if (cp.getInventoryMenuContainer() != null
                && e.getClickedInventory().getType() != InventoryType.PLAYER) {
            cp.getInventoryMenuContainer().onInventoryInteract(e, cp);
        } else if (e.getCurrentItem() != null && !cp.canBuild()) {
            InventoryMenuItem menu = InventoryMenuAPI.getHotbarItem(e.getCurrentItem());
            if (menu != null) {
                menu.callAction(cp);
            }
            e.setCancelled(true);
        }
    }
    
}
