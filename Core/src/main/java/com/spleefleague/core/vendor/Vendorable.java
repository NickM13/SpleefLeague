package com.spleefleague.core.vendor;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatUtils;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Vendorable items are items that can be sold by vendors
 *
 * @author NickM13
 * @since 4/18/2020
 */
public abstract class Vendorable extends DBEntity implements Cloneable {
    
    private static final Map<String, Class<? extends Vendorable>> REGISTERED_TYPES = new HashMap<>();
    
    /**
     * Registers a vendorable type class, any vendorables created will
     * use the key assigned with each class based on which one they
     * are an instance of, or "Invalid" if none apply.
     *
     * If any vendorables use the type "Invalid", they have not been
     * set up correctly and their base class should call this function
     * in their static init
     * 
     * @param clazz Class of ? extends Vendorable
     */
    protected static void registerVendorableType(Class<? extends Vendorable> clazz) {
        REGISTERED_TYPES.put(clazz.getSimpleName(), clazz);
    }
    
    /**
     * Checks against the classes in REGISTERED_TYPES to see if the
     * class passed is a sub-class of any, returning the simple class
     * name of that class, or Invalid if none found
     *
     * @param clazz Class of Vendorable
     * @return Vendorable Type Name
     */
    public static String getTypeName(Class<? extends Vendorable> clazz) {
        for (Map.Entry<String, Class<? extends Vendorable>> type : REGISTERED_TYPES.entrySet()) {
            if (type.getValue().isAssignableFrom(clazz)) {
                return type.getKey();
            }
        }
        return "Invalid";
    }
    
    public static Set<String> getTypeNames() {
        return REGISTERED_TYPES.keySet();
    }
    
    /**
     * Get the Vendorable class that this type is instantiated from
     *
     * @param type Type
     * @return Class of ? extends Vendorable
     */
    public static Class<? extends Vendorable> getClassFromType(String type) {
        return REGISTERED_TYPES.get(type);
    }
    
    public static final String typeNbt = "ventype";
    public static final String identifierNbt = "vendintifier";
    
    @DBField protected String type;
    @DBField protected String name = "";
    @DBField protected String description = "";
    @DBField protected Material material;
    @DBField protected Document nbts;
    @DBField protected Integer coinCost = 0;
    
    private final InventoryMenuItem vendorMenuItem;
    private ItemStack displayItem;
    
    /**
     * Constructor for Vendorable items
     */
    public Vendorable() {
        this.type = getTypeName(getClass());
        this.nbts = new Document();
        this.coinCost = 0;
        vendorMenuItem = InventoryMenuAPI.createItem()
                .setAction(this::attemptPurchase);
    }
    
    @Override
    public void afterLoad() {
        Vendorables.register(this);
        updateDisplayItem();
    }
    
    /**
     * Gets the type declared by the extending class, such
     * as Pet, Consumable, Shovel, E T C
     *
     * @return Type Name
     */
    public final String getType() {
        return type;
    }
    
    /**
     * Get the String found on the vendorable NBT tag
     *
     * @return Identifier String
     */
    public final String getIdentifier() {
        return identifier;
    }
    
    /**
     * Sets the identifying String of this vendorable<br>
     * Should only be used when cloning
     *
     * @param identifier Identifier String
     */
    public void setIdentifier(String identifier) {
        super.setIdentifier(identifier);
        updateDisplayItem();
        saveChanges();
    }
    
    /**
     * Get the display name of this vendorable's item
     *
     * @return Display Name
     */
    public final String getName() {
        return name;
    }
    
    /**
     * Sets the display name of this vendorable's item
     *
     * @param name Display Name
     */
    public final void setName(String name) {
        this.name = Chat.colorize(name);
        updateDisplayItem();
        saveChanges();
    }
    
    /**
     * Get the Description string for this item
     *
     * @return Description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Set the Description string for this item
     *
     * @param description Description
     */
    public final void setDescription(String description) {
        this.description = Chat.colorize(description);
        updateDisplayItem();
        saveChanges();
    }
    
    /**
     * Gets the Vendor Description of this item, including
     * the cost and any additional attributes through override
     *
     * @return Vendor Description
     */
    public String getDescriptionVendor() {
        return getDescription() + "\n\n"
                + ChatColor.AQUA + "Cost: " + ChatColor.GOLD + getCoinCost();
    }
    
    /**
     * Gets the display material for this item
     *
     * @return Display Material
     */
    public final Material getMaterial() {
        return material;
    }

    public final Integer getCustomModelDataNbt() {
        return nbts.get("CustomModelData", Integer.class);
    }

    public final void setCustomModelDataNbt(int customModelData) {
        nbts.put("CustomModelData", customModelData);
        updateDisplayItem();
        saveChanges();
    }

    public final String getSkullOwnerNbt() {
        return nbts.get("SkullOwner", String.class);
    }

    public final void setCustomModelDataNbt(String skullOwner) {
        nbts.put("SkullOwner", skullOwner);
        updateDisplayItem();
        saveChanges();
    }

    public final void setDamageNbt(int damage) {
        nbts.put("Damage", damage);
        updateDisplayItem();
        saveChanges();
    }
    
    public final Integer getDamageNbt() {
        return nbts.get("Damage", Integer.class);
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
     * Sets the coin cost for this vendorable item
     *
     * @param coinCost Coin Cost
     */
    public final void setCoinCost(int coinCost) {
        this.coinCost = coinCost;
        updateDisplayItem();
        saveChanges();
    }

    public abstract void saveChanges();

    public abstract void unsave();
    
    /**
     * Make sure to call this whenever a change has been made
     * to the vendorable that would change it's appearance
     */
    public void updateDisplayItem() {
        displayItem = createItem();
        vendorMenuItem
                .setName(name)
                .setDisplayItem(displayItem)
                .setDescription(getDescriptionVendor());
    }
    
    /**
     * Called when a player clicks on the vendorable item
     *
     * @param cp Core Player
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
     * @param cp Core Player
     * @return Can Purchase
     */
    public final boolean canPurchase(CorePlayer cp) {
        return (cp.getPurse().getCoins().getAmount() >= getCoinCost() && isAvailableToPurchase(cp));
    }
    
    /**
     * Whether an item is available for purchasing for things
     * such as requiring prerequisites, levels or achievements
     *
     * @param cp Core Player
     * @return Availability
     */
    public abstract boolean isAvailableToPurchase(CorePlayer cp);
    
    /**
     * Called when a player successfully purchases this item from the vendor
     *
     * @param cp Core Player
     */
    public abstract void purchase(CorePlayer cp);
    
    /**
     * Get the InventoryMenuItem vendor item
     *
     * @return Vendor Item Menu
     */
    public final InventoryMenuItem getVendorMenuItem() {
        return vendorMenuItem;
    }
    
    /**
     * Get the Display ItemStack for this item
     *
     * @return Display ItemStack
     */
    public final ItemStack getDisplayItem() { return displayItem; }
    
    protected final ItemStack createItem() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            for (Map.Entry<String, Object> nbt : nbts.entrySet()) {
                if (nbt.getKey().equalsIgnoreCase("damage")) {
                    if (itemMeta instanceof Damageable) {
                        ((Damageable) itemMeta).setDamage((Integer) nbt.getValue());
                    }
                } else if (nbt.getKey().equalsIgnoreCase("skullowner")) {
                    if (itemMeta instanceof SkullMeta) {
                        ((SkullMeta) itemMeta).setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString((String) nbt.getValue())));
                    }
                } else if (nbt.getKey().equalsIgnoreCase("custommodeldata")) {
                    itemMeta.setCustomModelData((Integer) nbt.getValue());
                } else {
                    CoreLogger.logError("\"" + nbt.getKey() + "\" tag not set up yet, Vendorable.java:324", null);
                }
            }
            itemMeta.setUnbreakable(true);
            itemMeta.addItemFlags(ItemFlag.values());
            itemMeta.setDisplayName(name);
            itemMeta.setLore(ChatUtils.wrapDescription(getDescription()));
            itemMeta.getPersistentDataContainer().set(
                    new NamespacedKey(Core.getInstance(), identifierNbt),
                    PersistentDataType.STRING,
                    identifier != null ? identifier : "");
            itemMeta.getPersistentDataContainer().set(
                    new NamespacedKey(Core.getInstance(), typeNbt),
                    PersistentDataType.STRING,
                    type != null ? type : "");
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }
    
    /**
     * Returns whether the passed Vendorable has the same type
     * and identifier as this
     *
     * @param vendorable Vendorable
     * @return Soft Equal
     */
    public boolean equalsSoft(Vendorable vendorable) {
        return vendorable.getType().equalsIgnoreCase(getType()) && vendorable.getIdentifier().equalsIgnoreCase(getIdentifier());
    }
    
}