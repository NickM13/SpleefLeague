package com.spleefleague.core.player.collectible;

import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class CollectibleSkin extends DBEntity {

    private final Collectible parent;
    @DBField private Integer cmd;
    @DBField private String displayName;

    private ItemStack displayItem;

    public CollectibleSkin(Collectible parent) {
        this.parent = parent;
    }

    public CollectibleSkin(Collectible parent, int cmd, String displayName) {
        this.parent = parent;
        this.cmd = cmd;
        this.displayName = displayName;

        this.displayItem = InventoryMenuUtils.createCustomItem(parent.getMaterial(), cmd);
    }

    public void updateDisplayItem() {
        this.displayItem = InventoryMenuUtils.createCustomItem(parent.getMaterial(), cmd);
    }

    public Integer getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
        this.displayItem = InventoryMenuUtils.createCustomItem(parent.getMaterial(), cmd);
        parent.saveChanges();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        parent.saveChanges();
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

}
