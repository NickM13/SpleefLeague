/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
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

    protected UUID uuid;

    public class PageBoundary {
        int rowFirst, rowLast, colFirst, colLast;
        int pageItemTotal;

        public PageBoundary(int rowFirst, int rowLast, int colFirst, int colLast, int pageItemTotal) {
            this.rowFirst = rowFirst;
            this.rowLast = rowLast;
            this.colFirst = colFirst;
            this.colLast = colLast;
            this.pageItemTotal = pageItemTotal;
        }

    }

    protected PageBoundary pageBoundary;

    protected int forcedPageCount = -1;
    protected int forcedPageStart = 0;

    protected int itemBuffer = 1;

    public InventoryMenuContainerChest() {
        this.openAction = null;
        this.refreshAction = null;
        this.titleFun = null;
        this.unsortedItems = new ArrayList<>();
        this.sortedItems = new TreeMap<>();
        this.controlItems = new ArrayList<>();
        this.deadSpaces = new HashSet<>();
        this.uuid = UUID.randomUUID();

        setPageBoundaries(1, 5, 0, 5);
    }

    public InventoryMenuContainerChest(InventoryMenuContainerChest container) {
        super(container);
        this.openAction = container.getOpenAction();
        this.refreshAction = container.getRefreshAction();
        this.titleFun = container.getTitleFun();
        this.unsortedItems = new ArrayList<>(container.getUnsortedItems());
        this.sortedItems = new TreeMap<>(container.getSortedItems());
        this.controlItems = new ArrayList<>(container.getControlItems());
        this.deadSpaces = new HashSet<>(container.getDeadSpaces());
        this.itemBuffer = container.getItemBuffer();
        this.parentContainer = container.getParent();
        this.uuid = container.getUuid();

        setPageBoundaries(container.getPageBoundary());
    }

    public BiConsumer<InventoryMenuContainerChest, CorePlayer> getOpenAction() {
        return openAction;
    }

    public BiConsumer<InventoryMenuContainerChest, CorePlayer> getRefreshAction() {
        return refreshAction;
    }

    public Function<CorePlayer, String> getTitleFun() {
        return titleFun;
    }

    public List<InventoryMenuItem> getUnsortedItems() {
        return unsortedItems;
    }

    public SortedMap<Integer, InventoryMenuItem> getSortedItems() {
        return sortedItems;
    }

    public List<InventoryMenuControl> getControlItems() {
        return controlItems;
    }

    public Set<Integer> getDeadSpaces() {
        return deadSpaces;
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
        this.pageBoundary = new PageBoundary(rowStart, rowStart + rowWidth - 1, colStart, colStart + colWidth - 1, rowWidth * colWidth);
        return this;
    }

    public InventoryMenuContainerChest setPageBoundaries(PageBoundary pageBoundary) {
        this.pageBoundary = new PageBoundary(pageBoundary.rowFirst, pageBoundary.rowLast, pageBoundary.colFirst, pageBoundary.colLast, pageBoundary.pageItemTotal);
        return this;
    }

    public PageBoundary getPageBoundary() {
        return pageBoundary;
    }

    public int getPageItemTotal() {
        return pageBoundary.pageItemTotal;
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

    public int getItemBuffer() {
        return itemBuffer;
    }

    public void clear() {
        sortedItems.clear();
        unsortedItems.clear();
    }

    public void clearSorted() {
        sortedItems.clear();
    }

    public void clearUnsorted() {
        unsortedItems.clear();
    }

    public void clearStatic() {
        controlItems.clear();
    }

    public void clearDeadSpace() {
        deadSpaces.clear();
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
        addMenuItem(menuItem, (x) + (y * (pageBoundary.colLast - pageBoundary.colFirst + 1)));
        menuItem.setParent(this);
        return menuItem;
    }

    public InventoryMenuItem addMenuItem(InventoryMenuItem menuItem, int x, int y, int page) {
        addMenuItem(menuItem, (x) + (y * (pageBoundary.colLast - pageBoundary.colFirst + 1)) + (page * pageBoundary.pageItemTotal));
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
        deadSpaces.add(x + y * (pageBoundary.colLast - pageBoundary.colFirst + 1));
        return this;
    }

    public void removeMenuItem(int page, int slot) {
        sortedItems.remove((page * pageBoundary.pageItemTotal) + slot);
    }

    public Inventory refreshInventory(InventoryMenuOverlay overlay, CorePlayer cp) {
        ItemStack[] contents = new ItemStack[MENU_SIZE];

        if (refreshAction != null) refreshAction.accept(this, cp);

        int pageCount = this.getPageCount(cp);
        StringBuilder title = new StringBuilder(ChatColor.WHITE + "");
        if (overlay != null) {
            title.append(overlay.getTitlePrefix());
        }
        title.append(ChatColor.BOLD).append(" ").append(titleFun != null ? titleFun.apply(cp) : "");
        if (pageCount > 1) {
            title.append(" (").append(cp.getMenu().getMenuTag("page", Integer.class) + 1).append("/").append(pageCount).append(")");
        }

        int toSkip = pageBoundary.pageItemTotal * cp.getMenu().getMenuTag("page", Integer.class);

        for (Map.Entry<Integer, InventoryMenuItem> item : sortedItems.entrySet()) {
            if (item.getValue().isVisible(cp)) {
                int slotNum = item.getKey() - toSkip;
                if (slotNum < pageBoundary.pageItemTotal && slotNum >= 0) {
                    ItemStack itemStack = item.getValue().createItem(cp);
                    int leftSpacing = pageBoundary.colFirst;
                    int rightSpacing = (slotNum / (pageBoundary.colLast - pageBoundary.colFirst + 1)) * (9 - (pageBoundary.colLast - pageBoundary.colFirst + 1));
                    int topSpacing = pageBoundary.rowFirst * 9;
                    contents[leftSpacing + rightSpacing + slotNum + topSpacing] = itemStack;
                }
            }
        }

        int i = 0;
        for (InventoryMenuItem item : unsortedItems) {
            if (!item.isVisible(cp)) continue;
            while ((sortedItems.containsKey(i) &&
                    sortedItems.get(i).isVisible(cp)) ||
                    deadSpaces.contains(i % pageBoundary.pageItemTotal)) {
                i += itemBuffer;
            }
            if (i >= toSkip + pageBoundary.pageItemTotal) break;
            if (i >= toSkip) {
                ItemStack itemStack = item.createItem(cp);
                int slotNum = (i - toSkip);
                int leftSpacing = pageBoundary.colFirst;
                int rightSpacing = (slotNum / (pageBoundary.colLast - pageBoundary.colFirst + 1)) * (9 - (pageBoundary.colLast - pageBoundary.colFirst + 1));
                int topSpacing = pageBoundary.rowFirst * 9;
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

        Inventory inv = Bukkit.createInventory(null, MENU_SIZE, title.toString());
        inv.setContents(contents);
        return inv;
    }

    public Inventory open(InventoryMenuOverlay overlay, CorePlayer cp) {
        if (openAction != null) openAction.accept(this, cp);
        return refreshInventory(overlay, cp);
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
                pageCount = Math.max(pageCount, item.getKey() / pageBoundary.pageItemTotal + 1);
            }
        }

        int i = forcedPageStart;
        for (InventoryMenuItem item : unsortedItems) {
            if (!item.isVisible(cp)) continue;
            while (sortedItems.containsKey(i)
                    && sortedItems.get(i).isVisible(cp)) {
                i += itemBuffer;
            }
            pageCount = Math.max(pageCount, i / pageBoundary.pageItemTotal + 1);
            i += itemBuffer;
        }

        return pageCount;
    }

    public InventoryMenuItem getMenuItem(CorePlayer cp, int slot) {
        int toSkip = pageBoundary.pageItemTotal * cp.getMenu().getPage();

        for (Map.Entry<Integer, InventoryMenuItem> item : sortedItems.entrySet()) {
            int slotNum = (item.getKey() - toSkip);
            if (slotNum < 0 || slotNum >= pageBoundary.pageItemTotal) continue;
            if (item.getValue().isVisible(cp)) {
                int leftSpacing = pageBoundary.colFirst;
                int rightSpacing = (slotNum / (pageBoundary.colLast - pageBoundary.colFirst + 1)) * (9 - (pageBoundary.colLast - pageBoundary.colFirst + 1));
                int topSpacing = pageBoundary.rowFirst * 9;
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
                    deadSpaces.contains(i % pageBoundary.pageItemTotal)) {
                i += itemBuffer;
            }
            if (i >= toSkip + pageBoundary.pageItemTotal) break;
            if (i >= toSkip) {
                int slotNum = (i - toSkip);
                int leftSpacing = pageBoundary.colFirst;
                int rightSpacing = (slotNum / (pageBoundary.colLast - pageBoundary.colFirst + 1)) * (9 - (pageBoundary.colLast - pageBoundary.colFirst + 1));
                int topSpacing = pageBoundary.rowFirst * 9;
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
        } else if (e.getClickedInventory().getType() == InventoryType.CHEST) {
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

    public InventoryMenuContainerChest clone() {
        return new InventoryMenuContainerChest(this);
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean equals(InventoryMenuContainerChest container) {
        return container != null && uuid.equals(container.getUuid());
    }
}
