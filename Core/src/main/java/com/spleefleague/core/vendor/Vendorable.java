package com.spleefleague.core.vendor;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatUtils;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.purse.CoreCurrency;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
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

    public enum Rarity {

        COMMON(ChatColor.WHITE),
        RARE(ChatColor.BLUE),
        EPIC(ChatColor.DARK_PURPLE),
        LEGENDARY(ChatColor.GOLD),
        MYTHIC(ChatColor.LIGHT_PURPLE);

        ChatColor color;

        Rarity(ChatColor color) {
            this.color = color;
        }

        public ChatColor getColor() {
            return color;
        }

    }

    public enum UnlockType {

        DEFAULT(true, false),
        HIDDEN(false, false),
        EVENT(false, false),
        SHOP(true, true),
        EXPLORE(true, false);

        private final boolean showLocked;
        private final boolean rolled;

        UnlockType(boolean showLocked, boolean rolled) {
            this.showLocked = showLocked;
            this.rolled = rolled;
        }

        public boolean shouldShowLocked() {
            return showLocked;
        }

        public boolean isRolled() {
            return rolled;
        }

    }

    private static final Map<String, Class<? extends Vendorable>> REGISTERED_PARENT_TYPES = new HashMap<>();
    private static final Map<String, Class<? extends Vendorable>> REGISTERED_EXACT_TYPES = new HashMap<>();

    /**
     * Registers a vendorable type class, any vendorables created will
     * use the key assigned with each class based on which one they
     * are an instance of, or "Invalid" if none apply.
     * <p>
     * If any vendorables use the type "Invalid", they have not been
     * set up correctly and their base class should call this function
     * in their static init
     *
     * @param clazz Class of ? extends Vendorable
     */
    protected static void registerParentType(Class<? extends Vendorable> clazz) {
        REGISTERED_PARENT_TYPES.put(clazz.getSimpleName(), clazz);
        Vendorables.registerParent(clazz.getSimpleName());
    }

    protected static void registerExactType(Class<? extends Vendorable> clazz) {
        REGISTERED_EXACT_TYPES.put(clazz.getSimpleName(), clazz);
    }

    /**
     * Checks against the classes in REGISTERED_TYPES to see if the
     * class passed is a sub-class of any, returning the simple class
     * name of that class, or Invalid if none found
     *
     * @param clazz Class of Vendorable
     * @return Vendorable Type Name
     */
    public static String getParentTypeName(Class<? extends Vendorable> clazz) {
        for (Map.Entry<String, Class<? extends Vendorable>> type : REGISTERED_PARENT_TYPES.entrySet()) {
            if (type.getValue().isAssignableFrom(clazz)) {
                return type.getKey();
            }
        }
        return "Invalid";
    }

    public static Set<String> getParentTypeNames() {
        return REGISTERED_PARENT_TYPES.keySet();
    }

    public static String getExactTypeName(Class<? extends Vendorable> clazz) {
        for (Map.Entry<String, Class<? extends Vendorable>> type : REGISTERED_EXACT_TYPES.entrySet()) {
            if (type.getValue().isAssignableFrom(clazz)) {
                return type.getKey();
            }
        }
        return getParentTypeName(clazz);
    }

    /**
     * Get the Vendorable class that this type is instantiated from
     *
     * @param type Type
     * @return Class of ? extends Vendorable
     */
    public static Class<? extends Vendorable> getClassFromParentType(String type) {
        return REGISTERED_PARENT_TYPES.get(type);
    }

    /**
     * Get the Vendorable class that this type is instantiated from
     *
     * @param type Type
     * @return Class of ? extends Vendorable
     */
    public static Class<? extends Vendorable> getClassFromExactType(String type) {
        if (REGISTERED_EXACT_TYPES.containsKey(type)) {
            return REGISTERED_EXACT_TYPES.get(type);
        } else {
            return REGISTERED_PARENT_TYPES.get(type);
        }
    }

    public static final String typeNbt = "ventype";
    public static final String identifierNbt = "vendintifier";

    @DBField
    protected String type;
    @DBField
    protected String parentType;
    @DBField
    protected UnlockType unlockType = UnlockType.HIDDEN;
    @DBField
    protected Rarity rarity = Rarity.COMMON;
    @DBField
    protected String name = "";
    @DBField
    protected String description = "";
    @DBField
    protected Material material;
    @DBField
    protected Integer coinCost = 0;
    @DBField
    protected UUID skullOwner = null;
    @DBField
    protected Integer customModelData = 0;

    private final InventoryMenuItem vendorMenuItem;
    private ItemStack displayItem;

    /**
     * Constructor for Vendorable items
     */
    public Vendorable() {
        this.type = getExactTypeName(getClass());
        this.parentType = getParentTypeName(getClass());
        this.coinCost = 0;
        vendorMenuItem = InventoryMenuAPI.createItemDynamic()
                .setAction(this::attemptPurchase);
    }

    @Override
    public void afterLoad() {
        Vendorables.register(this);

        if (rarity == null) {
            for (ChatColor chatColor : ChatColor.getChatColors(name)) {
                for (Rarity rarity : Rarity.values()) {
                    if (chatColor.equals(rarity.getColor())) {
                        setRarity(rarity);
                        break;
                    }
                }
                if (rarity != null) break;
            }
            name = ChatColor.stripColor(name);
            if (rarity == null) {
                rarity = Rarity.COMMON;
            }
            saveChanges();
        }

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

    public final String getParentType() {
        return parentType;
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

    public final String getDisplayName() {
        return rarity.color + "" + ChatColor.BOLD + name;
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

    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
        saveChanges();
    }

    public Rarity getRarity() {
        return rarity;
    }

    public void setUnlockType(UnlockType type) {
        this.unlockType = type;
        saveChanges();
    }

    public UnlockType getUnlockType() {
        return unlockType;
    }

    /**
     * Gets the display material for this item
     *
     * @return Display Material
     */
    public final Material getMaterial() {
        return material;
    }

    public final int getCustomModelData() {
        return customModelData;
    }

    public final void setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
        updateDisplayItem();
        saveChanges();
    }

    public final UUID getSkullOwner() {
        return skullOwner;
    }

    public final void setSkullOwner(UUID skullOwner) {
        this.skullOwner = skullOwner;
        updateDisplayItem();
        saveChanges();
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

    public boolean isDefault(CorePlayer cp) {
        return cp.getRank().hasPermission(CoreRank.DEVELOPER);
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
                .setName(rarity.color + "" + ChatColor.BOLD + name)
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
        return cp.getPurse().getCurrency(CoreCurrency.COIN.name()) >= getCoinCost() && isAvailableToPurchase(cp);
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
    public final ItemStack getDisplayItem() {
        return displayItem;
    }

    protected final ItemStack createItem() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            if (skullOwner != null && itemMeta instanceof SkullMeta) {
                ((SkullMeta) itemMeta).setOwningPlayer(Bukkit.getOfflinePlayer(skullOwner));
            }
            itemMeta.setCustomModelData(customModelData);
            itemMeta.setUnbreakable(true);
            itemMeta.addItemFlags(ItemFlag.values());
            itemMeta.setDisplayName(rarity.color + "" + ChatColor.BOLD + name);
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
