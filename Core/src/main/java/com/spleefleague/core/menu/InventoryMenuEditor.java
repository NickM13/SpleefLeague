/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu;

import com.google.common.collect.Lists;
import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import java.util.HashMap;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author NickM13
 */
public class InventoryMenuEditor extends InventoryMenuContainer {
    
    protected HashMap<Integer, InventoryMenuItem> edittableItems;
    protected Consumer<HashMap<Integer, InventoryMenuItem>> saveFun;
    
    public InventoryMenuEditor() {
        super();
        upperBorder = false;
    }
    
    public InventoryMenuEditor setSaveFun(Consumer<HashMap<Integer, InventoryMenuItem>> saveFun) {
        this.saveFun = saveFun;
        return this;
    }
    
    @Override
    protected void initControls() {
        controlItems.add(0, new InventoryMenuControl(5 * 9 - 3, InventoryMenuAPI.createItem()
                .setName("Next Page")
                .setDescription("")
                .setDisplayItem(Material.DIAMOND_AXE, 8)
                .setCloseOnAction(false)
                .setAction(CorePlayer::nextPage)));
        
        controlItems.add(0, new InventoryMenuControl(5 * 9 - 7, InventoryMenuAPI.createItem()
                .setName("Prev Page")
                .setDescription("")
                .setDisplayItem(Material.DIAMOND_AXE, 9)
                .setCloseOnAction(false)
                .setVisibility(cp -> cp.getPage() > 0)
                .setAction(CorePlayer::prevPage)));
    }
    
    public void onInventoryInteract(InventoryClickEvent e, CorePlayer cp) {
        if (e.getClickedInventory() != null && e.getClickedInventory().getType() == InventoryType.CHEST) {
            InventoryMenuContainer menu = cp.getInventoryMenuContainer();
            InventoryMenuItem clicked = menu.getMenuItem(cp, e.getSlot());
            if (e.getSlot() < pageItemTotal) {
                InventoryMenuItem prevItem = this.getMenuItem(cp, e.getSlot());
                if (clicked != null) {
                    removeMenuItem(cp.getPage(), e.getSlot());
                }
                // TODO: Is air a thing for cursors?
                if (e.getCursor() != null && !e.getCursor().getType().equals(Material.AIR)) {
                    ItemMeta meta = e.getCursor().getItemMeta();
                    menu.addMenuItem(InventoryMenuAPI.createItem()
                            .setName(meta != null ? meta.getDisplayName() : "")
                            .setDescription(meta != null ? meta.getLore() : Lists.newArrayList())
                            .setDisplayItem(e.getCursor()),
                            cp.getPage() * pageItemTotal + e.getSlot());
                }
                cp.getPlayer().setItemOnCursor(prevItem == null ? null : prevItem.createItem(cp));
                saveFun.accept(sortedItems);
                cp.refreshInventoryMenuContainer();
            } else {
                e.setCancelled(true);
                if (clicked != null &&
                        clicked.isAvailable(cp)) {
                    Bukkit.getScheduler().runTask(Core.getInstance(), () -> {
                        clicked.callAction(cp);
                        if (clicked.shouldCloseOnAction() && clicked.isVisible(cp)) {
                            cp.setInventoryMenuItem(clicked);
                        } else {
                            cp.refreshInventoryMenuContainer();
                        }
                    });
                }
            }
        }
    }
    
}
