package com.spleefleague.core.vendor;

import com.spleefleague.core.database.annotation.DBField;
import com.spleefleague.core.database.variable.DBEntity;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.player.CorePlayer;
import org.bukkit.Material;

/**
 * Vendorable items are items that can be sold by vendors
 *
 * @author NickM13
 * @since 4/18/2020
 */
public abstract class Vendorable extends DBEntity {
    
    // Type of vendorable, such as Shovel, Pet, **Consumable**
    private String type;
    // Identifier, stored in "collectible" tag
    private String identifier;
    @DBField private String displayName;
    @DBField private String description;
    @DBField private Integer coinCost;
    @DBField private Material material;
    
    private final InventoryMenuItem vendorMenuItem;
    
    /**
     * Constructor for Vendorable items
     *
     * @param type String
     */
    public Vendorable(String type) {
        vendorMenuItem = InventoryMenuAPI.createItem()
                .setAction(this::attemptPurchase);
    }
    
    /**
     * Make sure to call this whenever a change has been made
     * to the vendor item that would change it's appearance
     */
    public void updateVendorItem() {
        vendorMenuItem
                .setName(displayName)
                .setDisplayItem(material)
                .setDescription(description);
    }
    
    public String getType() {
        return type;
    }
    
    /**
     * Get the tag that is stored on a vendor item's NBT tag "collectible"
     *
     * @return Vendor Tag String
     */
    public String getIdentifier() {
        return identifier;
    }
    
    /**
     * Get the coin cost of an item, used for vendor inventories
     *
     * @return Coin Cost
     */
    public final int getCoinCost() {
        return coinCost;
    }
    
    /**
     * Called when a player clicks on the vendorable item
     */
    public final void attemptPurchase(CorePlayer cp) {
        if (canPurchase(cp)) {
            purchase(cp);
        }
    }
    
    /**
     * Whether an item can be purchased, does not
     * refer to the coin cost of an item, that always
     * overrides this
     *
     * @return Can Purchase
     */
    public final boolean canPurchase(CorePlayer cp) {
        return (cp.getCoins() >= getCoinCost() && isAvailable(cp));
    }
    
    /**
     * Whether an item is available for purchasing for things
     * such as requiring prerequisites, levels or achievements
     *
     * @return Availability
     */
    public abstract boolean isAvailable(CorePlayer cp);
    
    /**
     * Called when a player successfully purchases this item from the vendor
     */
    public abstract void purchase(CorePlayer cp);
    
    /**
     * Get the InventoryMenuItem vendor item
     *
     * @return Vendor Item Menu
     */
    public final InventoryMenuItem getVendorItem() {
        return vendorMenuItem;
    }
    
}
