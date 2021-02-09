package com.spleefleague.core.vendor;

import com.spleefleague.core.Core;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainerChest;
import com.spleefleague.core.player.CorePlayer;

import java.util.UUID;

import com.spleefleague.core.player.purse.CoreCurrency;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

/**
 * Vendor is a type that can be applied to all living entities
 *
 * @author NickM13
 */
public class Artisan extends DBEntity {

    @DBField private String displayName = "";
    @DBField(fieldName = "entity") private UUID entityUuid = null;
    @DBField private CoreCurrency currency = CoreCurrency.COIN;
    @DBField private String crate = "COMMON";

    public Artisan() {
        super();
        updateMenu();
    }

    public Artisan(String identifier, String name) {
        this.identifier = identifier;
        this.displayName = name;
        updateMenu();
    }

    /**
     * Opens the shop of this artisan for a player
     *
     * @param cp Core Player
     */
    public void openShop(CorePlayer cp) {
        cp.getMenu().setOverlay(ArtisanOverlay.getOverlay());
        cp.getMenu().setInventoryMenuChest(getShopContainer(), true);
    }

    /**
     * Saves the current Vendor to the database
     */
    public void quicksave() {
        Artisans.save(this);
    }

    /**
     * Sets an entity to this artisan
     *
     * @param entityUuid Entity
     */
    public void setEntityUuid(UUID entityUuid) {
        this.entityUuid = entityUuid;
    }

    /**
     * Refreshes all entities to have the current display name of the artisan
     */
    public void refreshEntity() {
        if (entityUuid != null) {
            Entity entity = Bukkit.getEntity(entityUuid);
            if (entity != null) {
                Artisans.setupEntityVendor(this, entity);
            } else {
                entityUuid = null;
            }
        }
    }

    /**
     * Get the set of controlled entities by their Entity::getUniqueId
     *
     * @return Set of Entity UUIDs
     */
    public UUID getEntityUuid() {
        return entityUuid;
    }

    private void playCraftAnimation() {

    }

    private InventoryMenuContainerChest shopContainer;

    public void updateMenu() {
        shopContainer = InventoryMenuAPI.createContainer()
                .setTitle(getDisplayName());

        shopContainer.addStaticItem(InventoryMenuAPI.createItemDynamic()
                .setName("")
                .setDisplayItem(cp -> cp.getPurse().getCurrency(currency) > 0 ? currency.displayItem : new ItemStack(Material.BARRIER))
                .setCloseOnAction(false), 5, 3);

        shopContainer.addStaticItem(InventoryMenuAPI.createItemDynamic()
                .setName("")
                .setDisplayItem(cp -> cp.getPurse().getCurrency(currency) > 1 ? currency.displayItem : new ItemStack(Material.BARRIER))
                .setCloseOnAction(false), 4, 2);

        shopContainer.addStaticItem(InventoryMenuAPI.createItemDynamic()
                .setName("")
                .setDisplayItem(cp -> cp.getPurse().getCurrency(currency) > 2 ? currency.displayItem : new ItemStack(Material.BARRIER))
                .setCloseOnAction(false), 3, 3);

        shopContainer.addStaticItem(InventoryMenuAPI.createItemDynamic()
                .setName("")
                .setDisplayItem(cp -> cp.getPurse().getCurrency(currency) > 2 ?
                        Core.getInstance().getCrateManager().get(crate).getClosed() :
                        new ItemStack(Material.BARRIER))
                .setAction(cp -> {

                }), 4, 4);
    }

    /**
     * Create a player-specific artisan menu
     *
     * @return Shop Container
     */
    protected InventoryMenuContainerChest getShopContainer() {
        return shopContainer;
    }

    /**
     * Set the display name for Vendor
     *
     * @param displayName Display Name
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        refreshEntity();
    }

    public void setCurrency(CoreCurrency currency) {
        this.currency = currency;
    }

    public void setCrate(String crate) {
        this.crate = crate;
    }
    
    /**
     * Get the display name of Vendor
     * @return Display Name
     */
    public String getDisplayName() {
        return displayName;
    }
    
}
