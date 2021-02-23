package com.spleefleague.core.vendor;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import com.spleefleague.core.player.CoreOfflinePlayer;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.purse.CoreCurrency;
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
public class Artisans {

    private static MongoCollection<Document> artisanCollection;
    private static final Map<String, Artisan> artisans = new HashMap<>();
    private static final Map<UUID, Artisan> entityVendorMap = new HashMap<>();
    private static final Map<CoreOfflinePlayer, Artisan> playerSetVendorMap = new HashMap<>();

    /**
     * Load all vendors from database
     */
    public static void init() {
        artisanCollection = Core.getInstance().getPluginDB().getCollection("Artisans");
        artisanCollection.find().iterator().forEachRemaining(doc -> {
            Artisan vendor = new Artisan();
            vendor.load(doc);
            vendor.refreshEntity();
            artisans.put(vendor.getIdentifier(), vendor);
        });
    }

    /**
     * Clear the database and save all vendors
     */
    public static void close() {

    }

    /**
     * For command use
     * Get a vendor by its name
     *
     * @param name Name
     * @return Vendor
     */
    public static Artisan getVendor(String name) {
        return artisans.get(name);
    }

    public static void setDisplayName(String identifier, String name) {
        Artisan artisan = artisans.get(identifier);
        if (artisan == null) return;
        artisan.setDisplayName(name);
        save(artisan);
    }

    public static void setCurrency(String identifier, CoreCurrency currency) {
        Artisan artisan = artisans.get(identifier);
        if (artisan == null) return;
        artisan.setCurrency(currency);
        save(artisan);
    }

    public static void setCrate(String identifier, String chestName) {
        Artisan artisan = artisans.get(identifier);
        if (artisan == null) return;
        artisan.setCrate(chestName);
        save(artisan);
    }

    public static void setBackground(String identifier, String background) {
        Artisan artisan = artisans.get(identifier);
        if (artisan == null) return;
        artisan.setBackground(background);
        save(artisan);
    }

    public static void setBorder(String identifier, String border) {
        Artisan artisan = artisans.get(identifier);
        if (artisan == null) return;
        artisan.setBorder(border);
        save(artisan);
    }

    public static void setCoinCost(String identifier, int coin) {
        Artisan artisan = artisans.get(identifier);
        if (artisan == null) return;
        artisan.setCoinCost(coin);
        save(artisan);
    }

    /**
     * Get the map of all vendors
     *
     * @return Map of String, Vendors
     */
    public static Map<String, Artisan> getArtisans() {
        return artisans;
    }

    /**
     * Create a new vendor
     *
     * @param identifier  Name
     * @param displayName Display Name
     */
    public static boolean createArtisan(String identifier, String displayName) {
        if (artisans.containsKey(identifier)) return false;
        Artisan artisan = new Artisan(identifier, displayName);
        artisans.put(identifier, artisan);
        save(artisan);
        return true;
    }

    /**
     * Clear all entities from a vendor and delete it from the database
     *
     * @param artisan Vendor
     * @return Success
     */
    public static boolean deleteArtisan(Artisan artisan) {
        artisan.unsave(artisanCollection);
        if (artisan.getEntityUuid() != null) {
            Entity entity = Bukkit.getEntity(artisan.getEntityUuid());
            if (entity != null) {
                clearEntityVendor(entity);
            }
        }
        artisans.remove(artisan.getDisplayName());
        return true;
    }

    /**
     * Save a single vendor object to the db, overwrites other with same name
     *
     * @param artisan Vendor item
     */
    public static void save(Artisan artisan) {
        artisan.save(artisanCollection);
    }

    /**
     * The next entity the player punches will become this vendor
     *
     * @param player Player
     * @param name   Vendor Name
     * @return Success
     */
    public static boolean setPlayerVendor(CoreOfflinePlayer player, String name) {
        Artisan vendor = artisans.get(name);
        if (vendor != null) {
            playerSetVendorMap.put(player, artisans.get(name));
            return true;
        }
        return false;
    }

    /**
     * The next entity the player punches if not a vendor will
     * no long be a vendor
     *
     * @param player Player
     */
    public static void unsetPlayerVendor(CoreOfflinePlayer player) {
        playerSetVendorMap.put(player, null);
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
        CoreOfflinePlayer cp = Core.getInstance().getPlayers().get(player);
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
     * @param artisan Vendor
     * @param entity  Entity
     */
    public static void setupEntityVendor(Artisan artisan, Entity entity) {
        if (artisan == null) return;
        if (artisan.getEntityUuid() != null) {
            Entity entity1 = Bukkit.getEntity(artisan.getEntityUuid());
            if (entity1 != null) {
                clearEntityVendor(entity1);
            }
        }
        System.out.println(entity.getUniqueId());
        if (entityVendorMap.containsKey(entity.getUniqueId())
                && entityVendorMap.get(entity.getUniqueId()) == artisan) {
            if (artisan.getEntityUuid() != null) {
                Entity prevEntity = Bukkit.getEntity(artisan.getEntityUuid());
                if (prevEntity != null) {
                    clearEntityVendor(prevEntity);
                }
            }
            entityVendorMap.get(entity.getUniqueId()).setEntityUuid(null);
        }
        entityVendorMap.put(entity.getUniqueId(), artisan);
        artisan.setEntityUuid(entity.getUniqueId());
        entity.setCustomName(artisan.getDisplayName());
        entity.setCustomNameVisible(true);
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).setAI(false);
        }
        entity.setInvulnerable(true);
        save(artisan);
    }

    /**
     * Restores an entity to being a regular entity
     *
     * @param entity Entity
     */
    public static void clearEntityVendor(Entity entity) {
        if (entityVendorMap.containsKey(entity.getUniqueId())) {
            entityVendorMap.remove(entity.getUniqueId()).setEntityUuid(null);
            entity.setCustomName("");
            entity.setCustomNameVisible(false);
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).setAI(true);
            }
            entity.setInvulnerable(false);
        }
    }

}
