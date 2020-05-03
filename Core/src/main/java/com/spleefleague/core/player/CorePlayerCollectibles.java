package com.spleefleague.core.player;

import com.spleefleague.core.database.annotation.DBField;
import com.spleefleague.core.database.variable.DBEntity;
import com.spleefleague.core.database.variable.DBVariable;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainerChest;
import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.player.collectible.Holdable;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.core.vendor.Vendorables;
import org.bson.Document;
import org.bukkit.Material;

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
    private Holdable heldItem;
    
    public CorePlayerCollectibles() {
        this.collectibles = new TreeMap<>();
        this.activeCollectibles = new TreeMap<>();
        this.owner = null;
        this.heldItem = null;
    }
    
    public void setOwner(CorePlayer owner) {
        this.owner = owner;
    }
    
    /**
     * Collectibles are saved to documents within a document
     * example:
     * active: {
     *     "Pet": "Monkey",
     *     "Shovel": "15"
     * }
     * held: {
     *     "Shovel": "15"
     * }
     * collection: {
     *     {
     *         "type": "Pet"
     *         "collection": {
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
     *         "type": "Shovel"
     *         "collection": {
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
        if (collectible == null) return;
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
        if (collectible == null) return;
        if (collectibles.containsKey(collectible.getType())) {
            collectibles.get(collectible.getType()).remove(collectible.getIdentifier());
        }
        deactivate(collectible);
    }
    
    /**
     * Returns the current active collectibles item of a type, or null if there is none
     *
     * @param <T> ? extends Collectible
     * @param clazz Collectible Class
     * @return Nullable Collectible
     */
    public <T extends Collectible> T getActive(Class<T> clazz) {
        if (!activeCollectibles.containsKey(Vendorable.getTypeName(clazz))) return null;
        return Vendorables.get(clazz, activeCollectibles.get(Vendorable.getTypeName(clazz)));
    }
    
    public boolean hasActive(Class<? extends Collectible> clazz) {
        return activeCollectibles.containsKey(Vendorable.getTypeName(clazz));
    }
    
    /**
     * Returns the current active collectibles item of a type, or default if there is none or the active collectible
     * is no longer available
     *
     * @param <T> ? extends Collectible
     * @param clazz Collectible Class
     * @param defaultCollectible Default Collectible
     * @return NonNull Collectible
     */
    public <T extends Collectible> T getActiveOrDefault(Class<T> clazz, T defaultCollectible) {
        T collectible = getActive(clazz);
        if (collectible == null) return defaultCollectible;
        return collectible;
    }
    
    /**
     * Gets a list of all holdable items
     *
     * @return List of Holdables
     */
    @SuppressWarnings("unchecked")
    public List<Holdable> getAllHoldables() {
        List<Holdable> holdableList = new ArrayList<>();
        
        for (Map.Entry<String, Map<String, CollectibleInfo>> collectibleEntry : collectibles.entrySet()) {
            Class<? extends Vendorable> collectibleClass = Vendorable.getClassFromType(collectibleEntry.getKey());
            if (collectibleClass != null && Holdable.class.isAssignableFrom(collectibleClass)) {
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
    
    public <T extends Collectible> List<T> getAll(Class<T> clazz) {
        List<T> collectibleList = new ArrayList<>();
    
        Map<String, CollectibleInfo> collectibleMap = this.collectibles.get(Vendorable.getTypeName(clazz));
        if (collectibleMap != null) {
            for (Map.Entry<String, CollectibleInfo> entry : collectibleMap.entrySet()) {
                T collectible = Vendorables.get(clazz, entry.getKey());
                if (collectible != null) {
                    collectibleList.add(collectible);
                }
            }
        }
        
        return collectibleList;
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
    public void setActiveItem(Collectible collectible) {
        Collectible current = getActive(collectible.getClass());
        if (current != null) current.onDisable(owner);
        collectible.onEnable(owner);
        activeCollectibles.put(collectible.getType(), collectible.getIdentifier());
        owner.refreshHotbar();
    }
    
    public void removeActiveItem(Class<? extends Collectible> clazz) {
        String type = Vendorable.getTypeName(clazz);
        if (activeCollectibles.containsKey(type)) {
            Collectible collectible = Vendorables.get(clazz, activeCollectibles.get(type));
            if (collectible != null) {
                collectible.onDisable(owner);
            }
            activeCollectibles.remove(type);
            owner.refreshHotbar();
        }
    }
    
    /**
     * Deactivates a collectible
     *
     * @param collectible Collectible
     */
    protected void deactivate(Collectible collectible) {
        if (activeCollectibles.containsKey(collectible.getType())
                && activeCollectibles.get(collectible.getType()).equalsIgnoreCase(collectible.getIdentifier())) {
            collectible.onDisable(owner);
            activeCollectibles.remove(collectible.getType());
        }
    }
    
    /**
     * Creates an Inventory Menu Container with all available collectibles of a type, including
     * an item at the start that allows for disabling the cosmetic if possible
     *
     * @param clazz Class
     * @param container Menu Container
     * @param canHaveNone Disable Item Shown
     */
    public static void createCollectibleContainer(Class<? extends Collectible> clazz, InventoryMenuContainerChest container, boolean canHaveNone) {
        container.setOpenAction((container2, cp1) -> {
                container2.clearUnsorted();
                if (canHaveNone) {
                    container2.addMenuItem(InventoryMenuAPI.createItem()
                            .setName("None")
                            .setDescription("")
                            .setDisplayItem(Material.BAKED_POTATO)
                            .setAction(cp -> cp.getCollectibles().removeActiveItem(clazz))
                            .setCloseOnAction(false));
                }
            
                for (Collectible collectible : cp1.getCollectibles().getAll(clazz)) {
                    container2.addMenuItem(InventoryMenuAPI.createItem()
                            .setName(collectible.getName())
                            .setDescription(collectible.getDescription())
                            .setDisplayItem(collectible.getDisplayItem())
                            .setAction(cp2 -> cp2.getCollectibles().setActiveItem(collectible))
                            .setCloseOnAction(false));
                }
            });
    
        container.addStaticItem(InventoryMenuAPI.createItem()
                .setName("Selected Collectible")
                .setDescription(cp -> cp.getCollectibles().getActive(clazz).getDescription())
                .setDisplayItem(cp -> cp.getCollectibles().getActive(clazz).getDisplayItem())
                .setVisibility(cp -> cp.getCollectibles().hasActive(clazz)),
                4, 4);
    }
    
}
