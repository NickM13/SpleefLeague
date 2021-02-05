package com.spleefleague.core.menu;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatUtils;
import com.spleefleague.core.player.CorePlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author NickM13
 */
public class InventoryMenuItemEmpty extends InventoryMenuItem {

    private ItemStack DEFAULT_DISPLAY = new ItemStack(Material.SNOW_BLOCK);

    protected boolean visible;
    protected boolean available;

    protected String name;
    protected String description;
    protected ItemStack displayItem;
    protected ItemStack selectedItem;

    public InventoryMenuItemEmpty() {
        visible = true;
        available = true;

        name = "";
        description = "";
        displayItem = DEFAULT_DISPLAY;
        selectedItem = null;
    }

    @Override
    public String toString(CorePlayer cp) {
        return name;
    }

    @Override
    public InventoryMenuItemEmpty setDescriptionBuffer(int buffer) {
        this.descriptionBuffer = buffer;
        return this;
    }

    @Override
    public InventoryMenuItemEmpty setName(String name) {
        return this;
    }

    @Override
    public InventoryMenuItemEmpty setDescription(String description) {
        return this;
    }

    @Override
    public InventoryMenuItemEmpty setDescription(List<String> lore) {
        return this;
    }

    @Override
    public InventoryMenuItemEmpty setDisplayItem(Material material) {
        return this;
    }

    @Override
    public InventoryMenuItemEmpty setDisplayItem(Material material, int customModelData) {
        return this;
    }

    @Override
    public InventoryMenuItemEmpty setDisplayItem(ItemStack displayItem) {
        return this;
    }

    @Override
    public InventoryMenuItemEmpty setSelectedItem(Material material, int customModelData) {
        return this;
    }

    @Override
    public boolean isVisible(CorePlayer cp) {
        return visible;
    }

    public InventoryMenuItemEmpty setVisibility(boolean visible) {
        this.visible = visible;
        return this;
    }

    @Override
    public boolean isAvailable(CorePlayer cp) {
        return available;
    }

    @Override
    public InventoryMenuItemEmpty setCloseOnAction(boolean closeOnAction) {
        this.closeOnAction = closeOnAction;
        return this;
    }
    @Override
    public InventoryMenuItemEmpty setAction(Consumer<CorePlayer> action) {
        this.action = action;
        return this;
    }

    private static final ItemStack EMPTY = InventoryMenuUtils.createCustomItem(Material.AIR);

    public ItemStack createItem(CorePlayer cp, boolean selected) {
        return EMPTY;
    }

}
