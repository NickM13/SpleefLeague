/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu;

import com.google.common.collect.Lists;
import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;

import java.util.Map;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author NickM13
 */
public class InventoryMenuEditor extends InventoryMenuContainerChest {

    protected Consumer<Map<Integer, InventoryMenuItem>> saveFun;
    
    public InventoryMenuEditor() {
        super();
    }
    
    public InventoryMenuEditor setSaveFun(Consumer<Map<Integer, InventoryMenuItem>> saveFun) {
        this.saveFun = saveFun;
        return this;
    }
    
    public void onInventoryInteract(InventoryClickEvent e, CorePlayer cp) {
        if (e.getClickedInventory() != null && e.getClickedInventory().getType() == InventoryType.CHEST) {
            InventoryMenuContainerChest menu = (InventoryMenuContainerChest) cp.getMenu().getInventoryMenuContainer();
            InventoryMenuItem clicked = menu.getMenuItem(cp, e.getSlot());
            if (e.getSlot() < pageItemTotal) {
                InventoryMenuItem prevItem = this.getMenuItem(cp, e.getSlot());
                if (clicked != null) {
                    removeMenuItem(cp.getMenu().getMenuTag("page", Integer.class), e.getSlot());
                }
                // TODO: Is air a thing for cursors?
                if (e.getCursor() != null && !e.getCursor().getType().equals(Material.AIR)) {
                    ItemMeta meta = e.getCursor().getItemMeta();
                    menu.addMenuItem(InventoryMenuAPI.createItemDynamic()
                            .setName(meta != null ? meta.getDisplayName() : "")
                            .setDescription(meta != null ? meta.getLore() : Lists.newArrayList())
                            .setDisplayItem(e.getCursor()),
                            cp.getMenu().getMenuTag("page", Integer.class) * pageItemTotal + e.getSlot());
                }
                cp.getPlayer().setItemOnCursor(prevItem == null ? null : prevItem.createItem(cp));
                saveFun.accept(sortedItems);
                cp.getMenu().refreshInventoryMenuContainer();
            } else {
                e.setCancelled(true);
                if (clicked != null &&
                        clicked.isAvailable(cp)) {
                    Bukkit.getScheduler().runTask(Core.getInstance(), () -> {
                        clicked.callAction(cp);
                        if (clicked.shouldCloseOnAction() && clicked.isVisible(cp)) {
                            cp.getMenu().setInventoryMenuItem(clicked);
                        } else {
                            cp.getMenu().refreshInventoryMenuContainer();
                        }
                    });
                }
            }
        }
    }
    
}
