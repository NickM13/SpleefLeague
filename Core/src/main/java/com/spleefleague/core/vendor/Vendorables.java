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
    
    public static String getTypeNbt(ItemStack itemStack) {
        if (itemStack != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                return (itemMeta.getPersistentDataContainer().getOrDefault(new NamespacedKey(Core.getInstance(), Vendorable.typeNbt), PersistentDataType.STRING, ""));
            }
        }
        return "";
    }
    
    public static String getIdentifierNbt(ItemStack itemStack) {
        if (itemStack != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                return (itemMeta.getPersistentDataContainer().getOrDefault(new NamespacedKey(Core.getInstance(), Vendorable.identifierNbt), PersistentDataType.STRING, ""));
            }
        }
        return "";
    }
    
    // <<Type, <Identifier, Vendorable>>
    private static final Map<String, Map<String, Vendorable>> VENDORABLE_MAP = new TreeMap<>();
    
    public static void register(Vendorable vendorable) {
        if (!VENDORABLE_MAP.containsKey(vendorable.getType()))
            VENDORABLE_MAP.put(vendorable.getType(), new TreeMap<>());
        VENDORABLE_MAP.get(vendorable.getType()).put(vendorable.getIdentifier(), vendorable);
    }
    
    public static void unregister(String type, String identifier) {
        VENDORABLE_MAP.get(type).remove(identifier);
    }
    
    public static Map<String, Vendorable> getAll(String type) {
        return VENDORABLE_MAP.get(type);
    }
    
    public static Map<String, Vendorable> getAll(Class<? extends Vendorable> clazz) {
        return VENDORABLE_MAP.getOrDefault(clazz.getSimpleName(), new HashMap<>());
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
    
    public static Vendorable get(Class<? extends Vendorable> clazz, String identifier) {
        if (VENDORABLE_MAP.containsKey(clazz.getSimpleName())) {
            return VENDORABLE_MAP.get(clazz.getSimpleName()).get(identifier);
        }
        return null;
    }
    
}
