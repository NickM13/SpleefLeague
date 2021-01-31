package com.spleefleague.core.menu;

import com.spleefleague.core.player.CorePlayer;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author NickM13
 */
public class InventoryMenuOverlay {

    private final int BG_SLOT = 6 * 9 - 3;

    private SortedMap<Integer, InventoryMenuItem> sortedItems = new TreeMap<>();

    protected int rowFirst;
    protected int rowLast;
    protected int colFirst;
    protected int colLast;

    protected int background = -1;
    protected InventoryMenuItem BG_ITEM = InventoryMenuAPI.createItemDynamic()
            .setName("")
            .setDisplayItem(cp -> InventoryMenuUtils.createCustomItem(Material.IRON_NUGGET, background))
            .setVisibility(cp -> background != -1);

    public InventoryMenuOverlay() {
        setPageBoundaries(0, 6, 0, 9);
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
    public InventoryMenuOverlay setPageBoundaries(int rowStart, int rowWidth, int colStart, int colWidth) {
        rowFirst = rowStart;
        rowLast = rowStart + rowWidth - 1;
        colFirst = colStart;
        colLast = colStart + colWidth - 1;
        return this;
    }

    public InventoryMenuOverlay setBackground(int cmd) {
        background = cmd;
        return this;
    }

    public void addItem(InventoryMenuItem item, int x, int y) {
        addItem(item, (x) + (y * (colLast - colFirst + 1)));
    }

    public void addItem(InventoryMenuItem item, int slot) {
        sortedItems.put(slot, item);
    }

    public void openOverlay(Inventory inventory, CorePlayer cp, InventoryMenuContainerChest currentScreen) {
        ItemStack[] contents = inventory.getContents();
        for (Map.Entry<Integer, InventoryMenuItem> entry : sortedItems.entrySet()) {
            InventoryMenuItem menuItem = entry.getValue();
            if (menuItem.isVisible(cp)) {
                if (menuItem.getLinkedChest() != null && menuItem.getLinkedChest().equals(currentScreen)) {
                    contents[entry.getKey()] = menuItem.createItem(cp, true);
                } else {
                    contents[entry.getKey()] = menuItem.createItem(cp, false);
                }
            }
        }
        if (background != -1) {
            contents[BG_SLOT] = BG_ITEM.createItem(cp);
        }
        inventory.setContents(contents);
    }

    public InventoryMenuItem getMenuItem(CorePlayer cp, int slot) {
        InventoryMenuItem item = sortedItems.get(slot);
        if (item != null && item.isVisible(cp)) {
            return item;
        }
        return null;
    }

}
