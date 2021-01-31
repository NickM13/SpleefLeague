/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu;

import com.google.common.collect.Lists;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatUtils;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author NickM13
 */
public abstract class InventoryMenuItem {

    protected Rank minRank;

    protected boolean closeOnAction;
    protected Consumer<CorePlayer> action;
    protected InventoryMenuContainer linkedContainer;
    protected InventoryMenuContainerChest parentContainer;
    protected int descriptionBuffer = 1;
    
    public InventoryMenuItem() {
        minRank = Rank.DEFAULT;

        closeOnAction = true;
        action = null;
        linkedContainer = null;
        parentContainer = null;
    }

    public abstract InventoryMenuItem setName(String name);

    public InventoryMenuItem setDescriptionBuffer(int buffer) {
        descriptionBuffer = buffer;
        return this;
    }

    public abstract InventoryMenuItem setDescription(String description);
    public abstract InventoryMenuItem setDescription(List<String> lore);

    public abstract InventoryMenuItem setDisplayItem(Material material);
    public abstract InventoryMenuItem setDisplayItem(Material material, int customModelData);
    public abstract InventoryMenuItem setDisplayItem(ItemStack displayItem);

    public abstract InventoryMenuItem setSelectedItem(Material material, int customModelData);

    public abstract InventoryMenuItem setCloseOnAction(boolean closeOnAction);
    public abstract InventoryMenuItem setAction(Consumer<CorePlayer> action);
    
    public boolean hasLinkedContainer() {
        return linkedContainer != null;
    }

    /**
     * Returns currently linked container
     * @return Menu Container
     */
    public InventoryMenuContainerChest getLinkedChest() {
        return (InventoryMenuContainerChest) linkedContainer;
    }
    public InventoryMenuItem setLinkedContainer(InventoryMenuContainer container) {
        linkedContainer = container;
        if (parentContainer != null) {
            if (linkedContainer instanceof InventoryMenuContainerChest) {
                ((InventoryMenuContainerChest) linkedContainer).setParent(parentContainer);
            }
        }
        return this;
    }
    public InventoryMenuItem createLinkedContainer(String title) {
        setLinkedContainer(InventoryMenuAPI.createContainer()
                .setTitle(title));
        return this;
    }
    
    public InventoryMenuItem setParent(InventoryMenuContainerChest container) {
        parentContainer = container;
        if (hasLinkedContainer()) {
            if (linkedContainer instanceof InventoryMenuContainerChest) {
                ((InventoryMenuContainerChest) linkedContainer).setParent(parentContainer);
            }
        }
        return this;
    }

    public abstract boolean isVisible(CorePlayer cp);

    public abstract boolean isAvailable(CorePlayer cp);
    
    public InventoryMenuItem setMinRank(Rank minRank) {
        this.minRank = minRank;
        return this;
    }
    public Rank getMinRank() {
        return minRank;
    }

    public ItemStack createItem(CorePlayer cp) {
        return createItem(cp, false);
    }

    public abstract ItemStack createItem(CorePlayer cp, boolean selected);
    
    public boolean shouldCloseOnAction() {
        return closeOnAction;
    }
    public void callAction(CorePlayer cp) {
        if (action != null && isAvailable(cp)) action.accept(cp);
    }

    public InventoryMenuItem build() {
        return this;
    }

    public abstract String toString(CorePlayer cp);

}
