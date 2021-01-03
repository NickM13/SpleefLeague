package com.spleefleague.core.player;

import com.spleefleague.core.menu.*;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NickM13
 */
public class CorePlayerMenu {

    // Current inventory menu page
    private final Map<String, Object> menuTags = new HashMap<>();
    // Current inventory menu
    private InventoryMenuContainer inventoryMenuContainer;

    private CorePlayer owner;

    public CorePlayerMenu(CorePlayer owner) {
        this.owner = owner;
    }

    public void addInvSwap() {
        invSwap++;
    }

    private int invSwap = 0;
    /**
     * Set player's current InventoryMenuContainer
     *
     * @param inventoryMenuChest InventoryMenuContainer
     * @param initialize Should Call OpenFunction
     */
    public void setInventoryMenuChest(InventoryMenuContainerChest inventoryMenuChest, boolean initialize) {
        if (inventoryMenuChest != null) {
            invSwap++;
            menuTags.put("page", 0);
            ItemStack item = owner.getPlayer().getItemOnCursor();
            owner.getPlayer().setItemOnCursor(null);
            if (initialize) inventoryMenuChest.open(owner);
            else            inventoryMenuChest.refreshInventory(owner);
            owner.getPlayer().setItemOnCursor(item);
            this.inventoryMenuContainer = inventoryMenuChest;
        } else if (invSwap <= 0) {
            this.inventoryMenuContainer = null;
            this.menuTags.clear();
        } else {
            invSwap--;
        }
    }

    public void setInventoryMenuAnvil(InventoryMenuContainerAnvil inventoryMenuAnvil) {
        invSwap++;
        ItemStack item = owner.getPlayer().getItemOnCursor();
        owner.getPlayer().getInventory().addItem(item);
        owner.getPlayer().setItemOnCursor(null);
        inventoryMenuAnvil.open(owner);
        this.inventoryMenuContainer = inventoryMenuAnvil;
    }

    public void setInventoryMenuContainer(InventoryMenuContainer inventoryMenuContainer) {
        if (inventoryMenuContainer instanceof InventoryMenuContainerAnvil) {
            setInventoryMenuAnvil((InventoryMenuContainerAnvil) inventoryMenuContainer);
        } else if (inventoryMenuContainer instanceof InventoryMenuContainerChest) {
            setInventoryMenuChest((InventoryMenuContainerChest) inventoryMenuContainer, true);
        }
    }

    private InventoryMenuDialog inventoryMenuDialog = null;
    private int nextDialog;

    public void setInventoryMenuDialog(InventoryMenuDialog inventoryMenuDialog) {
        this.inventoryMenuDialog = inventoryMenuDialog;
        nextDialog = 0;
        openNextDialog();
    }

    public void openNextDialog() {
        if (inventoryMenuDialog != null) {
            if (inventoryMenuDialog.openNextContainer(owner, nextDialog)) {
                nextDialog++;
            }
        }
    }

    /**
     * Set player's current InventoryMenuContainer based on linked container of item
     *
     * @param inventoryMenuItem InventoryMenuItem
     */
    public void setInventoryMenuItem(InventoryMenuItem inventoryMenuItem) {
        if (inventoryMenuItem != null) {
            invSwap++;
            menuTags.put("page", 0);
            if (inventoryMenuItem.hasLinkedContainer()) {
                ItemStack item = owner.getPlayer().getItemOnCursor();
                owner.getPlayer().setItemOnCursor(null);
                inventoryMenuItem.getLinkedChest().open(owner);
                owner.getPlayer().setItemOnCursor(item);
                inventoryMenuContainer = inventoryMenuItem.getLinkedChest();
            } else {
                owner.getPlayer().closeInventory();
                inventoryMenuContainer = null;
            }
        } else {
            owner.getPlayer().closeInventory();
            inventoryMenuContainer = null;
            menuTags.clear();
        }
    }

    /**
     * Updates the player's current inventoryMenuContainer
     * For refreshing/page change
     */
    public void refreshInventoryMenuContainer() {
        if (inventoryMenuContainer != null
                && inventoryMenuContainer instanceof InventoryMenuContainerChest) {
            invSwap++;
            InventoryMenuContainerChest container = (InventoryMenuContainerChest) inventoryMenuContainer;
            ItemStack itemStack = owner.getPlayer().getItemOnCursor();
            owner.getPlayer().setItemOnCursor(null);
            container.refreshInventory(owner);
            owner.getPlayer().setItemOnCursor(itemStack);
            inventoryMenuContainer = container;
        }
    }

    /**
     * @return Current InventoryMenuContainer
     */
    public InventoryMenuContainer getInventoryMenuContainer() {
        return inventoryMenuContainer;
    }

    /**
     * @param <T> ? extends Collectible
     * @param name Menu Tag Identifier
     * @param clazz Class of T
     * @return Current Menu Tags
     */
    public <T> T getMenuTag(String name, Class<T> clazz) {
        if (menuTags.containsKey(name)
                && clazz.isAssignableFrom(menuTags.get(name).getClass())) {
            return clazz.cast(menuTags.get(name));
        }
        return null;
    }

    public <T> void setMenuTag(String name, T obj) {
        menuTags.put(name, obj);
    }

    public boolean hasMenuTag(String name) {
        return menuTags.containsKey(name);
    }


}
