package com.spleefleague.core.vendor;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.ChatUtils;
import com.spleefleague.core.database.annotation.DBField;
import com.spleefleague.core.database.variable.DBEntity;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.player.CorePlayer;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Vendorable items are items that can be sold by vendors
 *
 * @author NickM13
 * @since 4/18/2020
 */
public abstract class Vendorable extends DBEntity {
    
    private static final Map<String, Class<? extends Vendorable>> REGISTERED_TYPES = new HashMap<>();
    
    /**
     * Registers a vendorable type class, any vendorables created will
     * use the key assigned with each class based on which one they
     * are an instance of, or "Invalid" if none apply.
     *
     * If any vendorables use the type "Invalid", they have not been
     * set up correctly and their base class should call this function
     * in their static init
     */
    protected static void registerVendorableType(Class<? extends Vendorable> clazz) {
        REGISTERED_TYPES.put(clazz.getSimpleName(), clazz);
    }
    
    /**
     * Checks against the classes in REGISTERED_TYPES to see if the
     * class passed is a sub-class of any, returning the simple class
     * name of that class, or Invalid if none found
     */
    public static String getTypeName(Class<? extends Vendorable> clazz) {
        for (Map.Entry<String, Class<? extends Vendorable>> type : REGISTERED_TYPES.entrySet()) {
            if (type.getValue().isAssignableFrom(clazz)) {
                return type.getKey();
            }
        }
        return "Invalid";
    }
    
    public static Class<? extends Vendorable> getClassFromType(String type) {
        return REGISTERED_TYPES.get(type);
    }
    
    public static final String typeNbt = "ventype";
    public static final String identifierNbt = "vendintifier";
    
    protected final String type;
    @DBField protected String identifier;
    @DBField protected String name;
    @DBField protected String description;
    @DBField protected Material material;
    @DBField protected Document nbts;
    @DBField protected Integer coinCost;
    
    private final InventoryMenuItem vendorMenuItem;
    private ItemStack displayItem;
    
    /**
     * Constructor for Vendorable items
     */
    public Vendorable() {
        this.type = getTypeName(getClass());
        this.nbts = new Document();
        vendorMenuItem = InventoryMenuAPI.createItem()
                .setAction(this::attemptPurchase);
    }
    
    @Override
    public void afterLoad() {
        Vendorables.register(this);
        displayItem = createItem();
    }
    
    /**
     * Gets the type declared by the extending class, such
     * as Pet, Consumable, Shovel, E T C
     *
     * @return Type Name
     */
    public String getType() {
        return type;
    }
    
    /**
     * Get the String found on the vendorable NBT tag
     *
     * @return Identifier String
     */
    public String getIdentifier() {
        return identifier;
    }
    
    /**
     * Get the display name of this vendor item item
     *
     * @return Display Name
     */
    public String getName() {
        return name;
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
    public Material getMaterial() {
        return material;
    }
    
    protected void setDamageNbt(int damage) {
        nbts.append("Damage", damage);
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
     * Make sure to call this whenever a change has been made
     * to the vendorable that would change it's appearance
     */
    public void updateDisplayItem() {
        displayItem = createItem();
        vendorMenuItem
                .setName(name)
                .setDisplayItem(material)
                .setDescription(getDescriptionVendor());
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
        return (cp.getCoins() >= getCoinCost() && isAvailableToPurchase(cp));
    }
    
    /**
     * Whether an item is available for purchasing for things
     * such as requiring prerequisites, levels or achievements
     *
     * @return Availability
     */
    public abstract boolean isAvailableToPurchase(CorePlayer cp);
    
    /**
     * Called when a player successfully purchases this item from the vendor
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
                } else {
                    System.out.println("\"" + nbt.getKey() + "\" tag not set up yet, Vendorable.java:230");
                }
            }
            itemMeta.addEnchant(Enchantment.DIG_SPEED, 5, true);
            itemMeta.setUnbreakable(true);
            itemMeta.addItemFlags(ItemFlag.values());
            itemMeta.setDisplayName(name);
            itemMeta.setLore(ChatUtils.wrapDescription(description));
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
    
}
