package com.spleefleague.core.vendor;

import com.spleefleague.core.Core;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainerChest;
import com.spleefleague.core.menu.InventoryMenuOverlay;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.player.CorePlayer;

import java.util.UUID;

import com.spleefleague.core.player.purse.CoreCurrency;
import com.spleefleague.coreapi.chat.Chat;
import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;

/**
 * Vendor is a type that can be applied to all living entities
 *
 * @author NickM13
 */
public class Artisan extends DBEntity {

    @DBField private String displayName = "";
    @DBField(fieldName = "entity")
    private UUID entityUuid = null;
    @DBField private CoreCurrency currency = CoreCurrency.COIN;
    @DBField private String crate = "common";
    @DBField private String background = "噔";
    @DBField private String border = "嗰";
    @DBField private Integer coinCost = 1;

    private final InventoryMenuOverlay overlay = InventoryMenuAPI.createOverlay();

    public Artisan() {
        super();
    }

    public Artisan(String identifier, String name) {
        this.identifier = identifier;
        this.displayName = name;
        updateMenu();
    }

    @Override
    public void afterLoad() {
        updateMenu();
    }

    /**
     * Opens the shop of this artisan for a player
     *
     * @param cp Core Player
     */
    public void openShop(CorePlayer cp) {
        cp.getMenu().setInventoryMenuChest(overlay, getShopContainer(), true);
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

    private void playCraftAnimation(CorePlayer cp) {
        cp.getPlayer().playSound(cp.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 0.75f);
        Core.getInstance().sendMessage(cp, "The Artisan takes your ores and crafts a " + Chat.MENU_NAME + Core.getInstance().getCrateManager().get(crate).getDisplayName() + ChatColor.GRAY + "!");
    }

    private void attemptCraft(CorePlayer cp) {
        if (cp.getPurse().getCurrency(currency) >= 3) {
            cp.getCrates().changeCrateCount(crate, 1);
            cp.getPurse().addCurrency(currency, -3);
            cp.getPurse().addCurrency(CoreCurrency.COIN, -coinCost);
            playCraftAnimation(cp);
        }
    }

    private InventoryMenuContainerChest shopContainer;

    public void updateMenu() {
        overlay.clear();
        overlay.setTitlePrefix(border, background);

        overlay.addItem(InventoryMenuUtils.getBackButton(), 0, 5);

        shopContainer = InventoryMenuAPI.createContainer()
                .setTitle(getDisplayName());

        shopContainer.addStaticItem(InventoryMenuAPI.createItemDynamic()
                        .setName(currency.displayName)
                        .setDisplayItem(currency.displayItem)
                        .setCloseOnAction(false)
                        .setVisibility(cp -> cp.getPurse().getCurrency(currency) >= 1),
                2, 1);

        shopContainer.addStaticItem(InventoryMenuAPI.createItemDynamic()
                        .setName(currency.displayName)
                        .setDisplayItem(currency.displayItem)
                        .setCloseOnAction(false)
                        .setVisibility(cp -> cp.getPurse().getCurrency(currency) >= 2),
                4, 1);

        shopContainer.addStaticItem(InventoryMenuAPI.createItemDynamic()
                        .setName(currency.displayName)
                        .setDisplayItem(currency.displayItem)
                        .setCloseOnAction(false)
                        .setVisibility(cp -> cp.getPurse().getCurrency(currency) >= 3),
                6, 1);

        if (Core.getInstance().getCrateManager().get(crate) != null) {
            shopContainer.addStaticItem(InventoryMenuAPI.createItemStatic()
                            .setName(Core.getInstance().getCrateManager().get(crate).getDisplayName())
                            .setDisplayItem(Core.getInstance().getCrateManager().get(crate).getArtisan())
                            .setAction(this::attemptCraft),
                    2, 3);
        }

        shopContainer.addStaticItem(InventoryMenuAPI.createItemDynamic()
                .setName(cp -> "Gold Coins [" + coinCost + "]")
                .setDisplayItem(CoreCurrency.COIN.displayItem)
                .setDescription(CoreCurrency.COIN.description)
                .setCloseOnAction(false), 5, 5);

        InventoryMenuUtils.createDigitMenu(shopContainer, 6, 5, 3, cp -> coinCost, true);

        /*
        shopContainer.addStaticItem(InventoryMenuAPI.createItemDynamic()
                .setName(cp -> {
                    if (cp.getPurse().getCurrency(currency) >= 3) {
                        return "Craft";
                    } else {
                        return "Missing materials!";
                    }
                })
                .setDisplayItem(cp -> cp.getPurse().getCurrency(currency) >= 3 ?
                        InventoryMenuUtils.MenuIcon.CRAFT.getIconItem() : InventoryMenuUtils.MenuIcon.CRAFT_GRAY.getIconItem())
                .setAction(this::attemptCraft)
                .setCloseOnAction(false), 4, 5);
        */

        /*
        shopContainer.addStaticItem(InventoryMenuAPI.createItemEmpty()
                .setAction(this::attemptCraft)
                .setCloseOnAction(false), 3, 5);
        shopContainer.addStaticItem(InventoryMenuAPI.createItemEmpty()
                .setAction(this::attemptCraft)
                .setCloseOnAction(false), 5, 5);
        */
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
        updateMenu();
    }

    public void setCurrency(CoreCurrency currency) {
        this.currency = currency;
        updateMenu();
    }

    public void setCrate(String crate) {
        this.crate = crate;
        updateMenu();
    }

    public void setBackground(String background) {
        this.background = background;
        updateMenu();
    }

    public void setBorder(String border) {
        this.border = border;
        updateMenu();
    }

    public void setCoinCost(int coinCost) {
        this.coinCost = coinCost;
        updateMenu();
    }

    /**
     * Get the display name of Vendor
     *
     * @return Display Name
     */
    public String getDisplayName() {
        return displayName;
    }

}
