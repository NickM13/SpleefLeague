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
public class InventoryMenuItem {
    
    protected Rank minRank;
    protected Function<CorePlayer, Boolean> visibilityFun;
    protected Function<CorePlayer, Boolean> availableFun;
    
    protected Function<CorePlayer, String> nameFun;
    protected Function<CorePlayer, String> descriptionFun;
    protected Function<CorePlayer, ItemStack> displayItemFun;
    
    protected boolean closeOnAction;
    protected Consumer<CorePlayer> action;
    protected InventoryMenuContainer linkedContainer;
    protected InventoryMenuContainer parentContainer;
    
    public InventoryMenuItem() {
        minRank = Rank.DEFAULT;
        visibilityFun = null;
        availableFun = null;
        
        nameFun = null;
        descriptionFun = null;
        displayItemFun = null;
        
        closeOnAction = true;
        action = null;
        linkedContainer = null;
    }
    
    public InventoryMenuItem setName(String name) {
        this.nameFun = (cp) -> name;
        return this;
    }
    public InventoryMenuItem setName(Function<CorePlayer, String> nameFun) {
        this.nameFun = nameFun;
        return this;
    }
    
    public InventoryMenuItem setDescription(String description) {
        this.descriptionFun = (cp) -> description;
        return this;
    }
    public InventoryMenuItem setDescription(Function<CorePlayer, String> descriptionFun) {
        this.descriptionFun = descriptionFun;
        return this;
    }
    public InventoryMenuItem setDescription(List<String> lore) {
        this.descriptionFun = cp -> {
            String description = "";
            for (String line : lore) {
                description += line;
            }
            return description;
        };
        return this;
    }
    
    public InventoryMenuItem setDisplayItem(Material material) {
        this.displayItemFun = (cp) -> new ItemStack(material);
        return this;
    }
    public InventoryMenuItem setDisplayItem(Material material, int damage) {
        this.displayItemFun = (cp) -> InventoryMenuUtils.createCustomItem(material, damage);
        return this;
    }
    public InventoryMenuItem setDisplayItem(ItemStack displayItem) {
        this.displayItemFun = (cp) -> displayItem;
        return this;
    }
    public InventoryMenuItem setDisplayItem(Function<CorePlayer, ItemStack> displayItemFun) {
        this.displayItemFun = displayItemFun;
        return this;
    }
    
    public InventoryMenuItem setCloseOnAction(boolean closeOnAction) {
        this.closeOnAction = closeOnAction;
        return this;
    }
    public InventoryMenuItem setAction(Consumer<CorePlayer> action) {
        this.action = action;
        return this;
    }
    
    public boolean hasLinkedContainer() {
        return linkedContainer != null;
    }
    /**
     * Returns linked container, creates one if doesn't exist
     * @return 
     */
    public InventoryMenuContainer getLinkedContainer() {
        if (linkedContainer == null) createLinkedContainer("");
        return linkedContainer;
    }
    public InventoryMenuContainer setLinkedContainer(InventoryMenuContainer container) {
        linkedContainer = container;
        if (parentContainer != null)
            linkedContainer.setParentContainer(parentContainer);
        return linkedContainer;
    }
    public InventoryMenuItem createLinkedContainer(String title) {
        InventoryMenuContainer container = InventoryMenuAPI.createContainer();
        container.setTitle(title);
        setLinkedContainer(container);
        return this;
    }
    
    public void setParentContainer(InventoryMenuContainer container) {
        parentContainer = container;
        if (hasLinkedContainer()) {
            linkedContainer.setParentContainer(parentContainer);
        }
    }
    
    public InventoryMenuItem setVisibility(Function<CorePlayer, Boolean> visibilityFun) {
        this.visibilityFun =  visibilityFun;
        return this;
    }
    public boolean isVisible(CorePlayer cp) {
        if (!cp.getRank().hasPermission(getMinRank())) {
            return false;
        }
        return visibilityFun == null || visibilityFun.apply(cp);
    }
    
    public InventoryMenuItem setAvailability(Function<CorePlayer, Boolean> availableFun) {
        this.availableFun = availableFun;
        return this;
    }
    public boolean isAvailable(CorePlayer cp) {
        return availableFun == null || availableFun.apply(cp);
    }
    
    public InventoryMenuItem setMinRank(Rank minRank) {
        this.minRank = minRank;
        return this;
    }
    public Rank getMinRank() {
        return minRank;
    }
    
    protected List<String> getWrappedDescription(CorePlayer cp) {
        if (descriptionFun != null) {
            return ChatUtils.wrapDescription("\n" + descriptionFun.apply(cp));
        } else {
            return Lists.newArrayList();
        }
    }
    
    public ItemStack createItem(CorePlayer cp) {
        ItemStack item = displayItemFun.apply(cp).clone();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(Chat.MENU_NAME + nameFun.apply(cp));
            meta.setLore(getWrappedDescription(cp));
            meta.addItemFlags(ItemFlag.values());
            item.setItemMeta(meta);
        }
        return item;
    }
    
    public boolean shouldCloseOnAction() {
        return closeOnAction;
    }
    public void callAction(CorePlayer cp) {
        if (action != null) action.accept(cp);
    }
    
}
