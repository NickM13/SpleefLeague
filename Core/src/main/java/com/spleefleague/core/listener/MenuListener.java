/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.listener;

import com.spleefleague.core.Core;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainerChest;
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
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());
        
        if ((event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
                && event.getItem() != null) {
            InventoryMenuItem menu = InventoryMenuAPI.getHotbarItem(event.getItem());
            if (menu != null) {
                menu.callAction(cp);
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onSwapHandItems(PlayerSwapHandItemsEvent event) {
        CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());
        if (!cp.canBuild()) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer().getName());
        if (cp != null) {
            cp.getMenu().setInventoryMenuChest(null, true);
        }
    }
    
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        CorePlayer cp = Core.getInstance().getPlayers().get(event.getWhoClicked().getName());
        
        if (cp.getMenu().getInventoryMenuContainer() != null) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || (event.getCurrentItem() == null && event.getCursor().getType().isAir())) return;
        CorePlayer cp = Core.getInstance().getPlayers().get(event.getWhoClicked().getName());
        
        if (cp.getMenu().getInventoryMenuContainer() instanceof InventoryMenuContainerChest
                && event.getClickedInventory().getType() == InventoryType.CHEST) {
            ((InventoryMenuContainerChest) cp.getMenu().getInventoryMenuContainer()).onInventoryInteract(event, cp);
        } else if (event.getCurrentItem() != null && !cp.canBuild()) {
            InventoryMenuItem menu = InventoryMenuAPI.getHotbarItem(event.getCurrentItem());
            if (menu != null) {
                menu.callAction(cp);
            }
            event.setCancelled(true);
        }
    }
    
}
