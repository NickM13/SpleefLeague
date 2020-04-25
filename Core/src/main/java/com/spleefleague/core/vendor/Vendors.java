package com.spleefleague.core.vendor;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.*;

/**
 * Manages all active vendors
 *
 * @author NickM13
 * @since 4/18/2020
 */
public class Vendors {
    
    private static MongoCollection<Document> vendorCollection;
    private static final Map<String, Vendor> vendors = new HashMap<>();
    private static final Map<UUID, Vendor> entityVendorMap = new HashMap<>();
    private static final Map<CorePlayer, Vendor> playerSetVendorMap = new HashMap<>();
    
    /**
     * Load all vendors from database
     */
    public static void init() {
        vendorCollection = Core.getInstance().getPluginDB().getCollection("Vendors");
        vendorCollection.find().iterator().forEachRemaining(doc -> {
            Vendor vendor = new Vendor();
            vendor.load(doc);
            vendor.refreshEntities();
            vendors.put(vendor.getName().toLowerCase(), vendor);
        });
    }
    
    /**
     * Clear the database and save all vendors
     */
    public static void close() {
        vendorCollection.deleteMany(new Document());
        List<Document> docs = new ArrayList<>();
        vendors.values().forEach(v -> docs.add(v.save()));
        vendorCollection.deleteMany(new Document());
        if (!docs.isEmpty())
            vendorCollection.insertMany(docs);
    }
    
    /**
     * For command use
     * Get a vendor by its name
     *
     * @param name Name
     * @return Vendor
     */
    public static Vendor getVendor(String name) {
        return vendors.get(name.toLowerCase());
    }
    
    /**
     * Get the map of all vendors
     *
     * @return Map of String, Vendors
     */
    public static Map<String, Vendor> getVendors() {
        return vendors;
    }
    
    /**
     * Create a new vendor
     *
     * @param name Name
     * @param displayName Display Name
     */
    public static void createVendor(String name, String displayName) {
        vendors.put(name.toLowerCase(), new Vendor(name, displayName));
    }
    
    /**
     * Clear all entities from a vendor and delete it from the database
     *
     * @param vendor Vendor
     */
    public static boolean deleteVendor(Vendor vendor) {
        if (vendor == null) return false;
        vendorCollection.deleteMany(new Document("name", vendor.getName()));
        vendor.getEntities().forEach(entityUuid -> {
            Entity entity = Bukkit.getEntity(entityUuid);
            if (entity != null)
                clearEntityVendor(entity);
        });
        vendors.remove(vendor.getName());
        return true;
    }
    
    /**
     * Save a single vendor object to the db, overwrites other with same name
     *
     * @param vendor Vendor item
     */
    public static void save(Vendor vendor) {
        vendorCollection.deleteMany(new Document("name", vendor.getName()));
        vendorCollection.insertOne(vendor.save());
    }
    
    /**
     * The next entity the player punches will become this vendor
     *
     * @param player Player
     * @param name Vendor Name
     * @return Success
     */
    public static boolean setPlayerVendor(CorePlayer player, String name) {
        Vendor vendor = vendors.get(name.toLowerCase());
        if (vendor != null) {
            playerSetVendorMap.put(player, vendors.get(name.toLowerCase()));
            return true;
        }
        return false;
    }
    
    /**
     * The next entity the player punches if not a vendor will
     * no long be a vendor
     *
     * @param player Player
     * @return
     */
    public static boolean unsetPlayerVendor(CorePlayer player) {
        playerSetVendorMap.put(player, null);
        return true;
    }
    
    public static void interactEvent(PlayerInteractEntityEvent event) {
        Player p = event.getPlayer();
        CorePlayer cp = Core.getInstance().getPlayers().get(p);
        Entity entity = event.getRightClicked();
        if (entityVendorMap.containsKey(entity.getUniqueId())) {
            entityVendorMap.get(entity.getUniqueId()).openShop(cp);
        }
    }
    
    /**
     * Called when a player punches an entity
     * 
     * @param event Event
     */
    public static void punchEvent(EntityDamageByEntityEvent event) {
        Player player = (Player) event.getDamager();
        CorePlayer cp = Core.getInstance().getPlayers().get(player);
        if (playerSetVendorMap.containsKey(cp)) {
            if (playerSetVendorMap.get(cp) == null) {
                clearEntityVendor(event.getEntity());
            } else {
                setupEntityVendor(playerSetVendorMap.get(cp), event.getEntity());
            }
            playerSetVendorMap.remove(cp);
            event.setCancelled(true);
        } else if (entityVendorMap.containsKey(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }
    }
    
    /**
     * Sets an entity to being a vendor entity
     *
     * @param vendor Vendor
     * @param entity Entity
     */
    public static void setupEntityVendor(Vendor vendor, Entity entity) {
        if (entityVendorMap.containsKey(entity.getUniqueId())
                && entityVendorMap.get(entity.getUniqueId()) == vendor) {
            entityVendorMap.get(entity.getUniqueId()).removeEntity(entity);
        }
        entityVendorMap.put(entity.getUniqueId(), vendor);
        vendor.addEntity(entity);
        entity.setCustomName(vendor.getDisplayName());
        entity.setCustomNameVisible(true);
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).setAI(false);
        }
        entity.setInvulnerable(true);
    }
    
    /**
     * Restores an entity to being a regular entity
     *
     * @param entity Entity
     */
    public static void clearEntityVendor(Entity entity) {
        if (entityVendorMap.containsKey(entity.getUniqueId())) {
            entityVendorMap.get(entity.getUniqueId()).removeEntity(entity);
            entityVendorMap.remove(entity.getUniqueId());
            entity.setCustomName("");
            entity.setCustomNameVisible(false);
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).setAI(true);
            }
            entity.setInvulnerable(false);
        }
    }
    
}
