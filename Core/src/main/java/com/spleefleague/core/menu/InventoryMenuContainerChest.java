/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatUtils;
import com.spleefleague.core.player.CorePlayer;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class InventoryMenuContainerChest extends InventoryMenuContainer {
    
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
    
    protected BiConsumer<InventoryMenuContainerChest, CorePlayer> openAction;
    protected BiConsumer<InventoryMenuContainerChest, CorePlayer> refreshAction;
    
    protected Function<CorePlayer, String> titleFun;
    protected List<InventoryMenuItem> unsortedItems;
    protected SortedMap<Integer, InventoryMenuItem> sortedItems;
    protected List<InventoryMenuControl> controlItems;
    protected Set<Integer> deadSpaces;
    protected InventoryMenuContainer parentContainer = null;
    
    protected int rowFirst;
    protected int rowLast;
    protected int colFirst;
    protected int colLast;
    protected int pageItemTotal;

    protected int forcedPageCount = -1;
    protected int forcedPageStart = 0;

    protected int itemBuffer = 1;
    
    public InventoryMenuContainerChest() {
        openAction = null;
        refreshAction = null;
        titleFun = null;
        unsortedItems = new ArrayList<>();
        sortedItems = new TreeMap<>();
        controlItems = new ArrayList<>();
        deadSpaces = new HashSet<>();
        
        setPageBoundaries(1, 5, 0, 5);
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
    public InventoryMenuContainerChest setPageBoundaries(int rowStart, int rowWidth, int colStart, int colWidth) {
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
     * @param openAction Open Action (Consumer of Container(self) and Player)
     * @return Self
     */
    public InventoryMenuContainerChest setOpenAction(BiConsumer<InventoryMenuContainerChest, CorePlayer> openAction) {
        this.openAction = openAction;
        return this;
    }
    
    public InventoryMenuContainerChest setRefreshAction(BiConsumer<InventoryMenuContainerChest, CorePlayer> refreshAction) {
        this.refreshAction = refreshAction;
        return this;
    }

    public InventoryMenuContainerChest setItemBuffer(int buffer) {
        this.itemBuffer = buffer;
        return this;
    }
    
    public void clearSorted () {
        sortedItems.clear();
    }
    
    public void clearUnsorted() {
        unsortedItems.clear();
    }

    public InventoryMenuContainerChest setParent(InventoryMenuContainer parentContainer) {
        this.parentContainer = parentContainer;
        return this;
    }

    public InventoryMenuContainer getParent() {
        return parentContainer;
    }

    public InventoryMenuContainerChest setTitle(String title) {
        this.titleFun = (cp) -> Chat.colorize(title);
        return this;
    }
    public InventoryMenuContainerChest setTitle(Function<CorePlayer, String> titleFun) {
        this.titleFun = titleFun;
        return this;
    }
    
    public InventoryMenuItem addMenuItem(InventoryMenuItem menuItem, int slot) {
        sortedItems.put(slot, menuItem);
        menuItem.setParent(this);
        return menuItem;
    }
    public InventoryMenuItem addMenuItem(InventoryMenuItem menuItem, int x, int y) {
        addMenuItem(menuItem, (x) + (y * (colLast - colFirst + 1)));
        menuItem.setParent(this);
        return menuItem;
    }
    public InventoryMenuItem addMenuItem(InventoryMenuItem menuItem) {
        unsortedItems.add(menuItem);
        menuItem.setParent(this);
        return menuItem;
    }
    public InventoryMenuItem addStaticItem(InventoryMenuItem menuItem, int slot) {
        controlItems.add(new InventoryMenuControl(slot, menuItem));
        menuItem.setParent(this);
        return menuItem;
    }
    public InventoryMenuItem addStaticItem(InventoryMenuItem menuItem, int x, int y) {
        controlItems.add(new InventoryMenuControl(x + y * 9, menuItem));
        menuItem.setParent(this);
        return menuItem;
    }

    public InventoryMenuContainerChest addDeadSpace(int x, int y) {
        deadSpaces.add(x + y * (colLast - colFirst + 1));
        return this;
    }
    
    public void removeMenuItem(int page, int slot) {
        sortedItems.remove((page * pageItemTotal) + slot);
    }
    
    public Inventory refreshInventory(CorePlayer cp) {
        ItemStack[] contents = new ItemStack[MENU_SIZE];

        if (refreshAction != null) refreshAction.accept(this, cp);
        
        int pageCount = this.getPageCount(cp);
        String title = titleFun != null ? titleFun.apply(cp) : "";
        if (pageCount > 1) {
            title = title + " (" + (cp.getMenu().getMenuTag("page", Integer.class) + 1) + "/" + pageCount + ")";
        }
        String formattedTitle = ChatUtils.centerTitle(ChatColor.BLACK + "" + ChatColor.BOLD + title);

        int toSkip = pageItemTotal * cp.getMenu().getMenuTag("page", Integer.class);
    
        for (Map.Entry<Integer, InventoryMenuItem> item : sortedItems.entrySet()) {
            if (item.getValue().isVisible(cp)) {
                int slotNum = item.getKey() - toSkip;
                if (slotNum < pageItemTotal && slotNum >= 0) {
                    ItemStack itemStack = item.getValue().createItem(cp);
                    int leftSpacing = colFirst;
                    int rightSpacing = (slotNum / (colLast - colFirst + 1)) * (9 - (colLast - colFirst + 1));
                    int topSpacing = rowFirst * 9;
                    contents[leftSpacing + rightSpacing + slotNum + topSpacing] = itemStack;
                }
            }
        }
    
        int i = 0;
        for (InventoryMenuItem item : unsortedItems) {
            if (!item.isVisible(cp)) continue;
            while ((sortedItems.containsKey(i) &&
                    sortedItems.get(i).isVisible(cp)) ||
                    deadSpaces.contains(i % pageItemTotal)) {
                i += itemBuffer;
            }
            if (i >= toSkip + pageItemTotal) break;
            if (i >= toSkip) {
                ItemStack itemStack = item.createItem(cp);
                int slotNum = (i - toSkip);
                int leftSpacing = colFirst;
                int rightSpacing = (slotNum / (colLast - colFirst + 1)) * (9 - (colLast - colFirst + 1));
                int topSpacing = rowFirst * 9;
                contents[leftSpacing + rightSpacing + slotNum + topSpacing] = itemStack;
            }
            i += itemBuffer;
        }
    
        for (InventoryMenuControl control : controlItems) {
            if (control.menuItem.isVisible(cp) &&
                    contents[control.slot] == null) {
                ItemStack itemStack = control.menuItem.createItem(cp);
                contents[control.slot] = itemStack;
            }
        }

        Inventory inv = Bukkit.createInventory(null, MENU_SIZE, formattedTitle);
        inv.setContents(contents);
        return inv;
    }
    
    @Override
    public Inventory open(CorePlayer cp) {
        if (openAction != null) openAction.accept(this, cp);
        return refreshInventory(cp);
    }

    public void setForcedPageCount(int count) {
        forcedPageCount = count;
    }

    public void setForcedPageStart(int start) {
        forcedPageStart = start;
    }

    public boolean hasPages() {
        return unsortedItems.size() > 0 || sortedItems.size() > 0;
    }

    public int getPageCount(CorePlayer cp) {
        if (forcedPageCount >= 0) {
            return forcedPageCount;
        }
        int pageCount = 1;
        
        for (Map.Entry<Integer, InventoryMenuItem> item : sortedItems.entrySet()) {
            if (item.getValue().isVisible(cp)) {
                pageCount = Math.max(pageCount, item.getKey() / pageItemTotal + 1);
            }
        }
        
        int i = forcedPageStart;
        for (InventoryMenuItem item : unsortedItems) {
            if (!item.isVisible(cp)) continue;
            while (sortedItems.containsKey(i)
                    && sortedItems.get(i).isVisible(cp)) {
                i += itemBuffer;
            }
            pageCount = Math.max(pageCount, i / pageItemTotal + 1);
            i += itemBuffer;
        }
        
        return pageCount;
    }
    
    public InventoryMenuItem getMenuItem(CorePlayer cp, int slot) {
        int toSkip = pageItemTotal * cp.getMenu().getMenuTag("page", Integer.class);

        for (Map.Entry<Integer, InventoryMenuItem> item : sortedItems.entrySet()) {
            int slotNum = (item.getKey() - toSkip);
            if (slotNum < 0 || slotNum >= pageItemTotal) continue;
            if (item.getValue().isVisible(cp)) {
                int leftSpacing = colFirst;
                int rightSpacing = (slotNum / (colLast - colFirst + 1)) * (9 - (colLast - colFirst + 1));
                int topSpacing = rowFirst * 9;
                if (leftSpacing + rightSpacing + slotNum + topSpacing == slot) {
                    return item.getValue();
                }
            }
        }

        int i = 0;
        for (InventoryMenuItem item : unsortedItems) {
            if (!item.isVisible(cp)) continue;
            while ((sortedItems.containsKey(i) &&
                    sortedItems.get(i).isVisible(cp)) ||
                    (sortedItems.containsKey(i) &&
                    sortedItems.get(i).isVisible(cp)) ||
                    deadSpaces.contains(i % pageItemTotal) ||
                    i - toSkip < 0) {
                i += itemBuffer;
            }
            if (i >= toSkip) {
                int slotNum = (i - toSkip);
                int leftSpacing = colFirst;
                int rightSpacing = (slotNum / (colLast - colFirst + 1)) * (9 - (colLast - colFirst + 1));
                int topSpacing = rowFirst * 9;
                int selected = leftSpacing + rightSpacing + slotNum + topSpacing;
                if (selected == slot) {
                    return item;
                }
                if (selected > slot) {
                    break;
                }
            }
            i += itemBuffer;
        }
        
        for (InventoryMenuControl control : controlItems) {
            if (control.slot == slot &&
                    control.menuItem.isVisible(cp)) {
                return control.menuItem;
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
            InventoryMenuContainer screen = cp.getMenu().getInventoryMenuContainer();
            if (screen instanceof InventoryMenuContainerChest) {
                InventoryMenuContainerChest container = (InventoryMenuContainerChest) screen;
                InventoryMenuItem clicked = container.getMenuItem(cp, e.getSlot());
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
