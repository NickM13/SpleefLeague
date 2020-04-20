package com.spleefleague.core.player;

import com.spleefleague.core.database.annotation.DBField;
import com.spleefleague.core.database.variable.DBEntity;
import com.spleefleague.core.database.variable.DBVariable;
import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.player.collectible.Holdable;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.core.vendor.Vendorables;
import org.bson.Document;

import java.util.*;

/**
 * @author NickM13
 * @since 4/19/2020
 */
public class CorePlayerCollectibles extends DBVariable<Document> {
    
    private static class CollectibleInfo extends DBEntity {
        
        @DBField Long collectDate;
    
        /**
         * For creating an empty object to load info from document
         */
        public CollectibleInfo() {}
    
        /**
         * For newly unlocked collectibles
         *
         * @param collectDate Time Collected (millis)
         */
        public CollectibleInfo(Long collectDate) {
            this.collectDate = collectDate;
        }
        
    }
    
    private CorePlayer owner;
    private final Map<String, Map<String, CollectibleInfo>> collectibles;
    private final Map<String, String> activeCollectibles;
    private Holdable heldItem = null;
    
    public CorePlayerCollectibles() {
        this.collectibles = new TreeMap<>();
        this.activeCollectibles = new TreeMap<>();
    }
    
    public void setOwner(CorePlayer owner) {
        this.owner = owner;
    }
    
    /**
     * Collectibles are saved to documents within a document
     * example:
     * collectibles: {
     *     {
     *         type: "Pet"
     *         collection: {
     *             {
     *                 identifier: "Monkey",
     *                 info: InfoDoc
     *             },
     *             {
     *                 identifier: "Owl",
     *                 info: InfoDoc
     *             }
     *         }
     *     },
     *     {
     *         type: "Shovel"
     *         collection: {
     *             {
     *                 identifier: "15",
     *                 info: InfoDoc
     *             }
     *         }
     *     }
     * }
     *
     * @return Document with collection and active docs
     */
    @Override
    public Document save() {
        Document fullDoc = new Document();
        
        Document fullCollectionDoc = new Document();
        for (Map.Entry<String, Map<String, CollectibleInfo>> collectibleType : collectibles.entrySet()) {
            Document collectionFullDoc = new Document();
            for (Map.Entry<String, CollectibleInfo> collectible : collectibleType.getValue().entrySet()) {
                collectionFullDoc.append(collectible.getKey(), collectible.getValue().save());
            }
            fullCollectionDoc.append(collectibleType.getKey(), collectionFullDoc);
        }
        fullDoc.append("collection", fullCollectionDoc);
        
        Document activeDoc = new Document();
        for (Map.Entry<String, String> active : activeCollectibles.entrySet()) {
            activeDoc.append(active.getKey(), active.getValue());
        }
        fullDoc.append("active", activeDoc);
        
        if (heldItem != null) {
            Document heldDoc = new Document("type", heldItem.getType()).append("identifier", heldItem.getIdentifier());
            fullDoc.append("held", heldDoc);
        }
        
        return fullDoc;
    }
    
    /**
     * Unwrap the saved documents and load them into collectibles
     *
     * @param collectiblesDoc Document with collection and active docs
     */
    @Override
    public void load(Document collectiblesDoc) {
        Document fullCollectionDoc = collectiblesDoc.get("collection", Document.class);
        collectibles.clear();
        for (Map.Entry<String, Object> typePair : fullCollectionDoc.entrySet()) {
            String type = typePair.getKey();
            Document collection = (Document) typePair.getValue();
            collectibles.put(type, new TreeMap<>());
            for (Map.Entry<String, Object> collectionPair : collection.entrySet()) {
                CollectibleInfo collectibleInfo = new CollectibleInfo();
                collectibleInfo.load((Document) collectionPair.getValue());
                collectibles.get(type).put(collectionPair.getKey(), collectibleInfo);
            }
        }
        
        Document activeDoc = collectiblesDoc.get("active", Document.class);
        activeCollectibles.clear();
        for (Map.Entry<String, Object> activePair : activeDoc.entrySet()) {
            activeCollectibles.put(activePair.getKey(), (String) activePair.getValue());
        }
        
        Document heldDoc = collectiblesDoc.get("held", Document.class);
        if (heldDoc != null) {
            heldItem = (Holdable) Vendorables.get(heldDoc.get("type", String.class), heldDoc.get("identifier", String.class));
        }
    }
    
    public void add(Collectible collectible) {
        if (!collectibles.containsKey(collectible.getType())) {
            collectibles.put(collectible.getType(), new TreeMap<>());
        }
        collectibles.get(collectible.getType()).put(collectible.getIdentifier(), new CollectibleInfo(System.currentTimeMillis()));
    }
    
    public boolean contains(Collectible collectible) {
        if (collectibles.containsKey(collectible.getType())) {
            return collectibles.get(collectible.getType()).get(collectible.getIdentifier()) != null;
        }
        return false;
    }
    
    public CollectibleInfo getInfo(Collectible collectible) {
        if (contains(collectible)) {
            return collectibles.get(collectible.getType()).get(collectible.getIdentifier());
        }
        return null;
    }
    
    public void remove(Collectible collectible) {
        if (collectibles.containsKey(collectible.getType())) {
            collectibles.get(collectible.getType()).remove(collectible.getIdentifier());
        }
        deactivate(collectible);
    }
    
    /**
     * Returns the current active collectibles item of a type, or
     * null if there is none
     *
     * @param clazz Collectible Class
     * @return Nullable Collectible
     */
    public Collectible getActive(Class<? extends Collectible> clazz) {
        if (!activeCollectibles.containsKey(Vendorable.getTypeName(clazz))) return null;
        return (Collectible) Vendorables.get(Vendorable.getTypeName(clazz), activeCollectibles.get(Vendorable.getTypeName(clazz)));
    }
    
    /**
     * Returns the current active collectibles item of a type, or
     * default if there is none or the active collectible is no longer
     * available
     *
     * @param clazz Collectible Class
     * @param defaultCollectible Default Collectible
     * @return NonNull Collectible
     */
    public Collectible getActiveOrDefault(Class<? extends Collectible> clazz, Collectible defaultCollectible) {
        Collectible collectible = getActive(clazz);
        if (collectible == null) return defaultCollectible;
        return collectible;
    }
    
    /**
     * Gets a list of all holdable items
     *
     * @return List of Holdables
     */
    public List<Holdable> getAllHoldables() {
        List<Holdable> holdableList = new ArrayList<>();
        
        for (Map.Entry<String, Map<String, CollectibleInfo>> collectibleEntry : collectibles.entrySet()) {
            Class<? extends Vendorable> collectibleClass = Vendorable.getClassFromType(collectibleEntry.getKey());
            if (Holdable.class.isAssignableFrom(collectibleClass)) {
                // TODO: Probably safer way to do this than finding the first and checking for showAll
                Holdable firstHoldable = (Holdable) Vendorables.get(collectibleEntry.getKey(), collectibleEntry.getValue().keySet().iterator().next());
                Holdable holdCollectible;
                if (firstHoldable != null) {
                    if (firstHoldable.isShowAll()) {
                        for (Map.Entry<String, CollectibleInfo> holdableEntry : collectibleEntry.getValue().entrySet()) {
                            holdCollectible = (Holdable) Vendorables.get(collectibleEntry.getKey(), holdableEntry.getKey());
                            if (holdCollectible != null) {
                                holdableList.add(holdCollectible);
                            }
                        }
                    } else {
                        holdCollectible = (Holdable) getActive((Class<? extends Holdable>) collectibleClass);
                        if (holdCollectible != null) {
                            holdableList.add(holdCollectible);
                        }
                    }
                }
            }
        }
        for (Map.Entry<String, String> activeEntry : activeCollectibles.entrySet()) {
            Vendorable vendorable = Vendorables.get(activeEntry.getKey(), activeEntry.getValue());
            if (vendorable instanceof Holdable) {
                holdableList.add((Holdable) vendorable);
            }
        }
        
        return holdableList;
    }
    
    
    
    /**
     * Sets the player's currently held item
     *
     * @param holdable Holdable
     */
    public void setHeldItem(Holdable holdable) {
        heldItem = holdable;
        owner.refreshHotbar();
    }
    
    /**
     * Gets the player's currently held item
     *
     * @return Held Item
     */
    public Holdable getHeldItem() {
        return heldItem;
    }
    
    /**
     * Whether player currently has a selected held item
     *
     * @return Held Item?
     */
    public boolean hasHeldItem() {
        return heldItem != null;
    }
    
    /**
     * Sets a collectible as the active collectible
     *
     * @param collectible Collectible
     */
    public void activate(Collectible collectible) {
        Collectible current = getActive(collectible.getClass());
        if (current != null) current.onDisable(owner);
        collectible.onEnable(owner);
        activeCollectibles.put(collectible.getType(), collectible.getIdentifier());
    }
    
    /**
     * Deactivates a collectible
     *
     * @param collectible Collectible
     */
    public void deactivate(Collectible collectible) {
        collectible.onDisable(owner);
        activeCollectibles.put(collectible.getType(), "");
    }
    
}
