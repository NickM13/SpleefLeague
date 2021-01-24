package com.spleefleague.core.player;

import com.spleefleague.core.Core;
import com.spleefleague.core.menu.*;
import com.spleefleague.core.menu.overlays.SLMainOverlay;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NickM13
 */
public class CorePlayerMenu {

    // Current inventory menu page
    private final Map<String, Object> menuTags = new HashMap<>();
    private InventoryMenuOverlay overlay;
    // Current inventory menu
    private InventoryMenuContainer inventoryMenuContainer;
    private int overlaySelect = -1;

    private CorePlayer owner;

    public CorePlayerMenu(CorePlayer owner) {
        this.owner = owner;
        this.overlay = SLMainOverlay.getOverlay();
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
            this.inventoryMenuContainer = inventoryMenuChest;
            if (initialize) openInventory(inventoryMenuChest);
            else refreshInventory(inventoryMenuChest);
            owner.getPlayer().setItemOnCursor(item);
        } else if (invSwap <= 0) {
            this.inventoryMenuContainer = null;
            this.menuTags.clear();
        } else {
            invSwap--;
        }
    }

    private void openInventory(InventoryMenuContainerChest inventoryMenuContainerChest) {
        Inventory inventory = inventoryMenuContainerChest.open(owner);
        if (overlay != null) overlay.openOverlay(inventory, owner, inventoryMenuContainerChest);
        owner.getPlayer().openInventory(inventory);
    }

    private void refreshInventory(InventoryMenuContainerChest inventoryMenuContainerChest) {
        Inventory inventory = inventoryMenuContainerChest.refreshInventory(owner);
        if (overlay != null) overlay.openOverlay(inventory, owner, inventoryMenuContainerChest);
        owner.getPlayer().openInventory(inventory);
    }

    private void closeInventory() {
        owner.getPlayer().closeInventory();
        inventoryMenuContainer = null;
        overlaySelect = -1;
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
                inventoryMenuContainer = inventoryMenuItem.getLinkedChest();
                openInventory(inventoryMenuItem.getLinkedChest());
                owner.getPlayer().setItemOnCursor(item);
            } else {
                closeInventory();
            }
        } else {
            closeInventory();
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
            ItemStack itemStack = owner.getPlayer().getItemOnCursor();
            owner.getPlayer().setItemOnCursor(null);
            refreshInventory((InventoryMenuContainerChest) inventoryMenuContainer);
            owner.getPlayer().setItemOnCursor(itemStack);
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

    public void onInventoryInteract(InventoryClickEvent e) {
        if (e.getClickedInventory() == null
                || e.getClickedInventory().getType() == InventoryType.PLAYER) {
            e.setCancelled(true);
        }
        else if (e.getClickedInventory().getType() == InventoryType.CHEST) {
            InventoryMenuContainer screen = inventoryMenuContainer;
            if (screen instanceof InventoryMenuContainerChest) {
                InventoryMenuItem clicked = null;
                if (overlay != null) {
                    clicked = overlay.getMenuItem(owner, e.getSlot());
                }
                if (clicked == null) {
                    InventoryMenuContainerChest container = (InventoryMenuContainerChest) screen;
                    clicked = container.getMenuItem(owner, e.getSlot());
                }
                e.setCancelled(true);
                if (clicked != null &&
                        clicked.isAvailable(owner)) {
                    InventoryMenuItem finalClicked = clicked;
                    Bukkit.getScheduler().runTask(Core.getInstance(), () -> {
                        finalClicked.callAction(owner);
                        if (finalClicked.shouldCloseOnAction() && finalClicked.isVisible(owner)) {
                            owner.getMenu().setInventoryMenuItem(finalClicked);
                        } else {
                            owner.getMenu().refreshInventoryMenuContainer();
                        }
                    });
                }
            }
        }
    }

    public void onBackButton() {
        InventoryMenuContainer parent = ((InventoryMenuContainerChest) inventoryMenuContainer).getParent();
        if (parent != null) {
            owner.getMenu().setInventoryMenuContainer(parent);
        } else {
            closeInventory();
        }
    }

    public boolean hasPagePrevious() {
        return getMenuTag("page", Integer.class) > 0;
    }

    public void onPagePrevious() {
        if (hasPagePrevious()) {
            setMenuTag("page", getMenuTag("page", Integer.class) - 1);
            refreshInventoryMenuContainer();
        }
    }

    public boolean hasPageNext() {
        return getMenuTag("page", Integer.class) < ((InventoryMenuContainerChest) inventoryMenuContainer).getPageCount(owner) - 1;
    }

    public void onPageNext() {
        if (hasPageNext()) {
            setMenuTag("page", getMenuTag("page", Integer.class) + 1);
            refreshInventoryMenuContainer();
        }
    }

    public void setPage(int page) {
        setMenuTag("page", page);
    }

    public int getPage() {
        return getMenuTag("page", Integer.class);
    }

    public boolean hasPages() {
        return ((InventoryMenuContainerChest) inventoryMenuContainer).hasPages();
    }

}
