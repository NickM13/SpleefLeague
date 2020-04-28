/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.ChatUtils;
import com.spleefleague.core.player.CorePlayer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class InventoryMenuContainer {
    
    // Entire menu size
    public static final int MENU_ROWS = 6, MENU_COLUMNS = 9, MENU_SIZE = MENU_ROWS * MENU_COLUMNS;
    
    protected static class InventoryMenuControl {
        int slot;
        InventoryMenuItem menuItem;
        
        InventoryMenuControl(int slot, InventoryMenuItem menuItem) {
            this.slot = slot;
            this.menuItem = menuItem;
        }
    }
    
    protected boolean upperBorder = true, lowerBorder = true;
    
    protected BiConsumer<InventoryMenuContainer, CorePlayer> openAction;
    
    protected Function<CorePlayer, String> titleFun;
    protected ArrayList<InventoryMenuItem> unsortedItems;
    protected HashMap<Integer, InventoryMenuItem> sortedItems;
    protected ArrayList<InventoryMenuControl> controlItems;
    protected InventoryMenuControl backButton;
    
    protected int rowFirst;
    protected int rowLast;
    protected int colFirst;
    protected int colLast;
    protected int pageItemTotal;
    
    public InventoryMenuContainer() {
        openAction = null;
        titleFun = null;
        unsortedItems = new ArrayList<>();
        sortedItems = new HashMap<>();
        controlItems = new ArrayList<>();
        initControls();
        
        setPageBoundaries(0, 4, 0, 9);
    }
    
    /**
     * Set the bounding area of pages
     *
     * @param rowStart Starting Row (0-5)
     * @param rowWidth Ending Row (0-5)
     * @param colStart Starting Column (1-9)
     * @param colWidth Ending Column (1-9)
     * @return Self
     */
    public InventoryMenuContainer setPageBoundaries(int rowStart, int rowWidth, int colStart, int colWidth) {
        rowFirst = rowStart;
        rowLast = rowStart + rowWidth - 1;
        colFirst = colStart;
        colLast = colStart + colWidth - 1;
        pageItemTotal = rowWidth * colWidth;
        return this;
    }
    
    public int getPageItemTotal() {
        return pageItemTotal;
    }
    
    /**
     * Set the action to occur on the opening of a container, to have a dynamic flow of options
     *
     * @param openAction Open Action (Consumer<Container(self), Player>)
     * @return Self
     */
    public InventoryMenuContainer setOpenAction(BiConsumer<InventoryMenuContainer, CorePlayer> openAction) {
        this.openAction = openAction;
        return this;
    }
    
    public void clearSorted () {
        sortedItems.clear();
    }
    
    public void clearUnsorted() {
        unsortedItems.clear();
    }
    
    protected void initControls() {
        backButton = new InventoryMenuControl((5 * 9 - 9), InventoryMenuAPI.createItem()
                .setName(ChatColor.RED + "" + ChatColor.BOLD + "Return")
                .setDisplayItem(Material.DIAMOND_AXE, 9)
                .setVisibility(cp -> backButton.menuItem.hasLinkedContainer()));
        controlItems.add(backButton);
        
        controlItems.add(0, new InventoryMenuControl(5 * 9 - 3, InventoryMenuAPI.createItem()
                .setName("Next Page")
                .setDescription("")
                .setDisplayItem(Material.DIAMOND_AXE, 8)
                .setCloseOnAction(false)
                .setVisibility(cp -> (cp.getPage() < this.getPageCount(cp) - 1/* || editting*/))
                .setAction(CorePlayer::nextPage)));
        
        controlItems.add(0, new InventoryMenuControl(5 * 9 - 7, InventoryMenuAPI.createItem()
                .setName("Prev Page")
                .setDescription("")
                .setDisplayItem(Material.DIAMOND_AXE, 9)
                .setCloseOnAction(false)
                .setVisibility(cp -> cp.getPage() > 0)
                .setAction(CorePlayer::prevPage)));
    }
    
    public InventoryMenuContainer setParentContainer(InventoryMenuContainer parentContainer) {
        backButton.menuItem.setLinkedContainer(parentContainer);
        return this;
    }
    
    public InventoryMenuContainer setTitle(String title) {
        this.titleFun = (cp) -> title;
        return this;
    }
    public InventoryMenuContainer setTitle(Function<CorePlayer, String> titleFun) {
        this.titleFun = titleFun;
        return this;
    }
    
    public InventoryMenuItem addMenuItem(InventoryMenuItem menuItem, int slot) {
        sortedItems.put(slot, menuItem);
        menuItem.setParentContainer(this);
        return menuItem;
    }
    public InventoryMenuItem addMenuItem(InventoryMenuItem menuItem, int x, int y) {
        addMenuItem(menuItem, (x) + (y * (colLast - colFirst + 1)));
        menuItem.setParentContainer(this);
        return menuItem;
    }
    public InventoryMenuItem addMenuItem(InventoryMenuItem menuItem) {
        unsortedItems.add(menuItem);
        menuItem.setParentContainer(this);
        return menuItem;
    }
    public InventoryMenuItem addStaticItem(InventoryMenuItem menuItem, int x, int y) {
        controlItems.add(new InventoryMenuControl(x + y * 9, menuItem));
        menuItem.setParentContainer(this);
        return menuItem;
    }
    
    public void removeMenuItem(int page, int slot) {
        sortedItems.remove((page * pageItemTotal) + slot);
    }
    
    public void openInventory(CorePlayer cp) {
        if (openAction != null) openAction.accept(this, cp);
        int pageCount = this.getPageCount(cp);
        String title = titleFun.apply(cp);
        if (pageCount > 1) {
            title = title + " (" + (cp.getPage() + 1) + "/" + pageCount + ")";
        }
        String formattedTitle = ChatUtils.centerTitle(ChatColor.BLACK + "" + ChatColor.BOLD + title);
        
        Inventory inv = Bukkit.createInventory(null, MENU_SIZE, formattedTitle);
        
        if (lowerBorder) {
            for (int i = 0; i < 9; i++) {
                inv.setItem(i + 9 * 5, new ItemStack(Material.SNOW_BLOCK));
            }
        }
        
        for (Map.Entry<Integer, InventoryMenuItem> item : sortedItems.entrySet()) {
            if (item.getKey() >= pageItemTotal * cp.getPage() &&
                    item.getKey() < pageItemTotal * (cp.getPage() + 1) &&
                    item.getValue().isVisible(cp)) {
                ItemStack itemStack = item.getValue().createItem(cp);
                inv.setItem(item.getKey() - pageItemTotal * cp.getPage(), itemStack);
            }
        }
        
        int i = 0;
        for (InventoryMenuItem item : unsortedItems) {
            if (!item.isVisible(cp)) continue;
            while (sortedItems.containsKey(i)
                    && sortedItems.get(i).isVisible(cp)) {
                i++;
            }
            if (i >= pageItemTotal * cp.getPage()) {
                ItemStack itemStack = item.createItem(cp);
                int slotNum = (i - pageItemTotal * cp.getPage());
                int leftSpacing = colFirst;
                int rightSpacing = (slotNum / (colLast - colFirst + 1)) * (9 - (colLast - colFirst + 1));
                int topSpacing = rowFirst * 9;
                inv.setItem(leftSpacing + rightSpacing + slotNum + topSpacing, itemStack);
            }
            i++;
            if (i >= pageItemTotal * (cp.getPage() + 1)) {
                break;
            }
        }
        
        for (InventoryMenuControl control : controlItems) {
            if (control.menuItem.isVisible(cp) &&
                    inv.getItem(control.slot) == null) {
                ItemStack itemStack = control.menuItem.createItem(cp);
                inv.setItem(control.slot, itemStack);
            }
        }
        
        if (upperBorder) {
            boolean hasTopRow = false;
            for (i = 0; i < 9; i++) {
                if (inv.getItem(i) != null) {
                    hasTopRow = true;
                    break;
                }
            }
            if (!hasTopRow) {
                for (i = 0; i < 9; i++) {
                    inv.setItem(i, new ItemStack(Material.SNOW_BLOCK));
                }
            }
        }
        
        cp.getPlayer().openInventory(inv);
    }
    
    public int getPageCount(CorePlayer cp) {
        int pageCount = 1;
        
        for (Map.Entry<Integer, InventoryMenuItem> item : sortedItems.entrySet()) {
            if (item.getValue().isVisible(cp)) {
                pageCount = Math.max(pageCount, item.getKey() / pageItemTotal + 1);
            }
        }
        
        int i = 0;
        for (InventoryMenuItem item : unsortedItems) {
            if (!item.isVisible(cp)) continue;
            while (sortedItems.containsKey(i)
                    && sortedItems.get(i).isVisible(cp)) {
                i++;
            }
            pageCount = Math.max(pageCount, i / pageItemTotal + 1);
            i++;
        }
        
        return pageCount;
    }
    
    public InventoryMenuItem getMenuItem(CorePlayer cp, int slot) {
        for (InventoryMenuControl control : controlItems) {
            if (control.slot == slot &&
                    control.menuItem.isVisible(cp)) {
                return control.menuItem;
            }
        }
        
        InventoryMenuItem menuItem;
        if (slot < pageItemTotal) {
            if ((menuItem = sortedItems.get(slot + (cp.getPage() * pageItemTotal))) != null
                    && menuItem.isVisible(cp)) {
                return menuItem;
            }
            int i = 0;
            for (InventoryMenuItem item : unsortedItems) {
                if (!item.isVisible(cp)) continue;
                while (sortedItems.containsKey(i)
                        && sortedItems.get(i).isVisible(cp)) {
                    i++;
                }
                if (i - pageItemTotal * cp.getPage() == slot) {
                    return item;
                }
                if (i - pageItemTotal * cp.getPage() > slot) {
                    return null;
                }
                i++;
            }
        }
        
        return null;
    }
    
    public void onInventoryInteract(InventoryClickEvent e, CorePlayer cp) {
        if (e.getClickedInventory() == null
                || e.getClickedInventory().getType() == InventoryType.PLAYER) {
            e.setCancelled(true);
        }
        else if (e.getClickedInventory().getType() == InventoryType.CHEST) {
            InventoryMenuContainer menu = cp.getInventoryMenuContainer();
            InventoryMenuItem clicked = menu.getMenuItem(cp, e.getSlot());
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
