package com.spleefleague.core.vendor;

import com.spleefleague.core.Core;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author NickM13
 * @since 4/19/2020
 */
public class Vendorables {
    
    // <<Type, <Identifier, Vendorable>>
    private static final Map<String, Map<String, Vendorable>> VENDORABLE_MAP = new TreeMap<>();
    
    /**
     * Get the nbt tag under ventype
     *
     * @param itemStack ItemStack
     * @return String
     */
    public static String getTypeNbt(ItemStack itemStack) {
        if (itemStack != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                return (itemMeta.getPersistentDataContainer().getOrDefault(new NamespacedKey(Core.getInstance(), Vendorable.typeNbt), PersistentDataType.STRING, ""));
            }
        }
        return "";
    }
    
    /**
     * Get the nbt tag under vendentifier
     *
     * @param itemStack ItemStack
     * @return String
     */
    public static String getIdentifierNbt(ItemStack itemStack) {
        if (itemStack != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                return (itemMeta.getPersistentDataContainer().getOrDefault(new NamespacedKey(Core.getInstance(), Vendorable.identifierNbt), PersistentDataType.STRING, ""));
            }
        }
        return "";
    }
    
    public static void register(Vendorable vendorable) {
        if (!VENDORABLE_MAP.containsKey(vendorable.getType()))
            VENDORABLE_MAP.put(vendorable.getType(), new TreeMap<>());
        VENDORABLE_MAP.get(vendorable.getType()).put(vendorable.getIdentifier(), vendorable);
    }
    
    public static void unregister(String type, String identifier) {
        VENDORABLE_MAP.get(type).remove(identifier);
    }

    public static void unregister(Class<? extends Vendorable> clazz, String identifier) {
        unregister(Vendorable.getTypeName(clazz), identifier);
    }
    
    public static Map<String, Vendorable> getAll(String type) {
        return VENDORABLE_MAP.get(type);
    }
    
    public static <T extends Vendorable> Map<String, T> getAll(Class<T> clazz) {
        return (Map<String, T>) VENDORABLE_MAP.getOrDefault(Vendorable.getTypeName(clazz), new HashMap<>());
    }
    
    public static Vendorable get(ItemStack itemStack) {
        String type = getTypeNbt(itemStack);
        String identifier = getIdentifierNbt(itemStack);
        if (VENDORABLE_MAP.containsKey(type)) {
            return VENDORABLE_MAP.get(type).get(identifier);
        }
        return null;
    }
    
    public static Vendorable get(String type, String identifier) {
        if (VENDORABLE_MAP.containsKey(type)) {
            return VENDORABLE_MAP.get(type).get(identifier);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Vendorable> T get(Class<T> clazz, String identifier) {
        String typeName = Vendorable.getTypeName(clazz);
        if (VENDORABLE_MAP.containsKey(typeName)) {
            return (T) VENDORABLE_MAP.get(typeName).get(identifier);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Vendorable> T get(Class<T> clazz, ItemStack item) {
        String typeName = Vendorable.getTypeName(clazz);
        if (VENDORABLE_MAP.containsKey(typeName)) {
            return (T) VENDORABLE_MAP.get(typeName).get(Vendorables.getIdentifierNbt(item));
        }
        return null;
    }
    
    public static <T extends Vendorable> boolean contains(Class<T> clazz, String identifier) {
        return get(clazz, identifier) != null;
    }
    
    /**
     * Removes a vendorable from the global map and returns the vendorable
     * <p>Used for renaming identifier names</p>
     *
     * @param clazz Class extending Vendorable
     * @param identifier Identifier
     * @param <T> Object extending Vendorable
     * @return Vendorable
     */
    public static <T extends Vendorable> T pop(Class<T> clazz, String identifier) {
        T vendorable = get(clazz, identifier);
        if (vendorable != null) {
            unregister(vendorable.getType(), vendorable.getIdentifier());
        }
        return vendorable;
    }
    
}
