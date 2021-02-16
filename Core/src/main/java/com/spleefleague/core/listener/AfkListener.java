/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.listener;

import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;

/**
 * @author NickM13
 */
public class AfkListener implements Listener {

    protected boolean setLastAction(String name) {
        return setLastAction(Core.getInstance().getPlayers().get(name));
    }

    protected boolean setLastAction(Player p) {
        return setLastAction(Core.getInstance().getPlayers().get(p));
    }

    protected boolean setLastAction(CorePlayer cp) {
        if (cp == null) return false;
        return cp.setLastAction();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {
        setLastAction(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommandSend(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().trim().equalsIgnoreCase("/afk")) {
            e.setCancelled(setLastAction(e.getPlayer()));
        } else {
            setLastAction(e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDropItem(PlayerDropItemEvent e) {
        e.setCancelled(setLastAction(e.getPlayer()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSlotChange(PlayerItemHeldEvent e) {
        e.setCancelled(setLastAction(e.getPlayer()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityInteractEntity(PlayerInteractEntityEvent e) {
        e.setCancelled(setLastAction(e.getPlayer()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent e) {
        e.setCancelled(setLastAction(e.getPlayer()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent e) {
        e.setCancelled(setLastAction(e.getPlayer()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent e) {
        setLastAction(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        e.setCancelled(setLastAction(e.getPlayer()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSwapHandItems(PlayerSwapHandItemsEvent e) {
        e.setCancelled(setLastAction(e.getPlayer()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClose(InventoryCloseEvent e) {
        setLastAction(e.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryDrag(InventoryDragEvent e) {
        e.setCancelled(setLastAction(e.getWhoClicked().getName()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryInteract(InventoryClickEvent e) {
        e.setCancelled(setLastAction(e.getWhoClicked().getName()));
    }

}
