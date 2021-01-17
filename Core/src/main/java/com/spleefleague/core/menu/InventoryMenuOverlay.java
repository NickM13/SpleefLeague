package com.spleefleague.core.menu;

import com.spleefleague.core.player.CorePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author NickM13
 */
public class InventoryMenuOverlay {

    private SortedMap<Integer, InventoryMenuItem> sortedItems = new TreeMap<>();

    protected int rowFirst;
    protected int rowLast;
    protected int colFirst;
    protected int colLast;

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

    public void addItem(InventoryMenuItem item, int x, int y) {
        addItem(item, (x) + (y * (colLast - colFirst + 1)));
    }

    public void addItem(InventoryMenuItem item, int slot) {
        sortedItems.put(slot, item);
    }

    public void openOverlay(Inventory inventory, CorePlayer cp, int selected) {
        ItemStack[] contents = inventory.getContents();
        for (Map.Entry<Integer, InventoryMenuItem> entry : sortedItems.entrySet()) {
            contents[entry.getKey()] = entry.getValue().createItem(cp, entry.getKey() == selected);
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
