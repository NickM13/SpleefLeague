/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu.menus.main;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.vendor.KeyItem;
import com.spleefleague.core.vendor.VendorItem;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class HeldItemMenu {
    
    protected static InventoryMenuItem menuItem = null;
    
    protected static final VendorItem DEFAULT_HELD_ITEM = new VendorItem("none", Material.BAKED_POTATO, 0, "None", "");
    
    public static VendorItem getDefault() {
        return DEFAULT_HELD_ITEM;
    }
    
    /**
     * Gets the menu item for this menu, if it doesn't exist
     * already then initialize it
     *
     * @return Inventory Menu Item
     */
    public static InventoryMenuItem getItem() {
        if (menuItem == null) {
            menuItem = InventoryMenuAPI.createItem()
                    .setName("Held Item")
                    .setDisplayItem(Material.BAKED_POTATO)
                    .setDescription("Change your held item")
                    .setAvailability(cp -> cp.getRank().hasPermission(Rank.DONOR_1))
                    .createLinkedContainer("Held Item");
            
            menuItem.getLinkedContainer()
                    .setOpenAction((container, cp1) -> {
                            container.clearUnsorted();
                            
                            menuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createItem()
                                    .setName(DEFAULT_HELD_ITEM.getDisplayName())
                                    .setDescription(DEFAULT_HELD_ITEM.getDescription())
                                    .setDisplayItem(DEFAULT_HELD_ITEM.getItem())
                                    .setAction(cp -> {
                                        cp.setHeldItem(null);
                                    })
                                    .setCloseOnAction(false));
                            
                            for (String type : VendorItem.getItemTypes()) {
                                if (type.equalsIgnoreCase("key")) continue;
                                if (cp1.getSelectedItems().containsKey(type)) {
                                    VendorItem item = VendorItem.getVendorItem(type, cp1.getSelectedItems().get(type));
                                    if (item != null) {
                                        container.addMenuItem(InventoryMenuAPI.createItem()
                                                .setName(item.getDisplayName())
                                                .setDescription(item.getDescription())
                                                .setDisplayItem(item.getItem())
                                                .setAction((cp2) -> {
                                                    cp2.setHeldItem(item);
                                                })
                                                .setCloseOnAction(false));
                                    }
                                }
                            }
                            
                            for (String key : cp1.getKeys()) {
                                KeyItem item = KeyItem.getKeyItem(key);
                                if (item != null) {
                                    container.addMenuItem(InventoryMenuAPI.createItem()
                                            .setName(item.getDisplayName())
                                            .setDescription(item.getDescription())
                                            .setDisplayItem(item.getItem())
                                            .setAction(cp2 -> {
                                                cp2.setHeldItem(item);
                                            })
                                            .setCloseOnAction(false));
                                }
                            }
                    });
            
            menuItem.getLinkedContainer()
                    .addStaticItem(InventoryMenuAPI.createItem()
                            .setName("Held Item")
                            .setDescription(cp -> { return cp.getHeldItem().getDescription(); })
                            .setDisplayItem(cp -> { return cp.getHeldItem().getItem(); }), 4, 4);
        }
        return menuItem;
    }

}
