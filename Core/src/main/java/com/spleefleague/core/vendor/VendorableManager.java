package com.spleefleague.core.vendor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NickM13
 * @since 4/18/2020
 */
public class VendorableManager {
    
    private static final Map<String, Vendorable> vendorableMap = new HashMap<>();
    
    public static void init() {
    
    }
    
    public static void close() {
    
    }
    
    public static void addVendorable(Vendorable vendorable) {
        vendorableMap.put(vendorable.getType(), vendorable);
    }
    
}
