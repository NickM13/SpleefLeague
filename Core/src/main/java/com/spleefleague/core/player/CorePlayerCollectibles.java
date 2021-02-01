package com.spleefleague.core.player;

import com.spleefleague.core.menu.*;
import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.player.collectible.CollectibleSkin;
import com.spleefleague.core.player.collectible.Holdable;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.core.vendor.Vendorables;
import com.spleefleague.core.world.global.zone.GlobalZones;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.annotation.DBLoad;
import com.spleefleague.coreapi.database.annotation.DBSave;
import com.spleefleague.coreapi.database.variable.DBEntity;
import com.spleefleague.coreapi.database.variable.DBVariable;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author NickM13
 * @since 4/19/2020
 */
public class CorePlayerCollectibles extends DBVariable<Document> {
    
    public static class CollectibleInfo extends DBEntity {
        
        @DBField Long collectDate;
        @DBField String selectedSkin = null;
        @DBField String name = null;
        Map<String, Long> ownedSkins = new HashMap<>();
    
        /**
         * For creating an empty object to load info from document
         */
        public CollectibleInfo() {

        }
    
        /**
         * For newly unlocked collectibles
         *
         * @param collectDate Time Collected (millis)
         */
        public CollectibleInfo(Long collectDate) {
            this.collectDate = collectDate;
        }

        public void setSelectedSkin(String skin) {
            this.selectedSkin = skin;
        }

        public String getSelectedSkin() {
            return selectedSkin;
        }

        public boolean addSkin(String skin, Long collectDate) {
            if (ownedSkins.containsKey(skin)) return false;
            ownedSkins.put(skin, collectDate);
            return true;
        }

        public boolean removeSkin(String skin) {
            return ownedSkins.remove(skin) != null;
        }

        public Map<String, Long> getOwnedSkins() {
            return ownedSkins;
        }

        @DBLoad(fieldName = "ownedSkins")
        private void loadOwnedSkins(Document doc) {
            for (Map.Entry<String, Object> entry : doc.entrySet()) {
                ownedSkins.put(entry.getKey(), (Long) entry.getValue());
            }
        }

        @DBSave(fieldName = "ownedSkins")
        private Document saveOwnedSkins() {
            Document doc = new Document();
            for (Map.Entry<String, Long> entry : ownedSkins.entrySet()) {
                doc.put(entry.getKey(), entry.getValue());
            }
            return doc;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
        
    }
    
    private final CorePlayer owner;
    private final SortedMap<String, Map<String, CollectibleInfo>> collectibles;
    private final SortedMap<String, String> activeCollectibles;
    private final SortedMap<String, Boolean> enabledMap;
    private final Set<String> collectedLeaves;
    private Holdable heldItem;

    public CorePlayerCollectibles(CorePlayer owner) {
        this.owner = owner;
        this.collectibles = new TreeMap<>();
        this.activeCollectibles = new TreeMap<>();
        this.enabledMap = new TreeMap<>();
        this.collectedLeaves = new HashSet<>();
        this.heldItem = null;
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
                collectionFullDoc.append(collectible.getKey(), collectible.getValue().toDocument());
            }
            fullCollectionDoc.append(collectibleType.getKey(), collectionFullDoc);
        }
        fullDoc.append("collection", fullCollectionDoc);

        Document activeDoc = new Document();
        for (Map.Entry<String, String> active : activeCollectibles.entrySet()) {
            activeDoc.append(active.getKey(), active.getValue());
        }
        fullDoc.append("active", activeDoc);

        Document enabledDoc = new Document();
        for (Map.Entry<String, Boolean> enabled : enabledMap.entrySet()) {
            enabledDoc.append(enabled.getKey(), enabled.getValue());
        }
        fullDoc.append("enabled", enabledDoc);
        
        if (heldItem != null) {
            Document heldDoc = new Document("type", heldItem.getParentType()).append("identifier", heldItem.getIdentifier());
            fullDoc.append("held", heldDoc);
        }

        fullDoc.append("leaves", collectedLeaves);
        
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
        if (fullCollectionDoc != null) {
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
        }

        Document activeDoc = collectiblesDoc.get("active", Document.class);
        if (activeDoc != null) {
            activeCollectibles.clear();
            for (Map.Entry<String, Object> activePair : activeDoc.entrySet()) {
                activeCollectibles.put(activePair.getKey(), (String) activePair.getValue());
            }
        }

        Document enabledDoc = collectiblesDoc.get("enabled", Document.class);
        if (enabledDoc != null) {
            enabledMap.clear();
            for (Map.Entry<String, Object> enabledPair : enabledDoc.entrySet()) {
                enabledMap.put(enabledPair.getKey(), (Boolean) enabledPair.getValue());
            }
        }
        
        Document heldDoc = collectiblesDoc.get("held", Document.class);
        if (heldDoc != null) {
            heldItem = (Holdable) Vendorables.get(heldDoc.get("type", String.class), heldDoc.get("identifier", String.class));
        }

        if (collectiblesDoc.containsKey("leaves")) {
            collectedLeaves.addAll(collectiblesDoc.get("leaves", List.class));
        }
    }
    
    public boolean add(Collectible collectible) {
        if (collectible != null) {
            if (!collectibles.containsKey(collectible.getParentType())) {
                collectibles.put(collectible.getParentType(), new HashMap<>());
            }
            collectibles.get(collectible.getParentType()).put(collectible.getIdentifier(), new CollectibleInfo(System.currentTimeMillis()));
            return true;
        }
        return false;
    }

    public boolean removeSkin(Collectible collectible, String skin) {
        if (collectible != null) {
            if (collectibles.containsKey(collectible.getParentType())) {
                if (collectibles.get(collectible.getParentType()).containsKey(collectible.getIdentifier())) {
                    return (collectibles.get(collectible.getParentType()).get(collectible.getIdentifier()).removeSkin(skin));
                }
            }
        }
        return false;
    }

    /**
     *
     * @param collectible Collectible
     * @param skin Skin ID
     * @return 1 for no collectible, 2 for already have, 3 for null
     */
    public int addSkin(Collectible collectible, String skin) {
        if (collectible != null) {
            if (!collectibles.containsKey(collectible.getParentType()) ||
                    !collectibles.get(collectible.getParentType()).containsKey(collectible.getIdentifier())) {
                if (collectible.isDefault(owner)) {
                    add(collectible);
                } else {
                    return 1;
                }
            }
            return collectibles.get(collectible.getParentType()).get(collectible.getIdentifier()).addSkin(skin, System.currentTimeMillis()) ? 0 : 2;
        }
        return 3;
    }
    
    public boolean contains(Collectible collectible) {
        if (collectibles.containsKey(collectible.getParentType())) {
            return collectibles.get(collectible.getParentType()).get(collectible.getIdentifier()) != null;
        }
        return false;
    }
    
    public CollectibleInfo getInfo(Collectible collectible) {
        if (!contains(collectible)) {
            add(collectible);
        }
        return collectibles.get(collectible.getParentType()).get(collectible.getIdentifier());
    }
    
    public boolean remove(Collectible collectible) {
        if (collectible != null && collectibles.containsKey(collectible.getParentType())) {
            collectibles.get(collectible.getParentType()).remove(collectible.getIdentifier());
            deactivate(collectible);
            return true;
        }
        return false;
    }

    /**
     * Returns the current active collectibles item of a type, or null if there is none
     *
     * @param <T> ? extends Collectible
     * @param clazz Collectible Class
     * @return Nullable Collectible
     */
    public <T extends Collectible> T getActive(Class<T> clazz) {
        if (!activeCollectibles.containsKey(Vendorable.getParentTypeName(clazz))) return Vendorables.get(clazz, "default");
        return Vendorables.get(clazz, activeCollectibles.get(Vendorable.getParentTypeName(clazz)));
    }

    /**
     * Returns the current active collectibles item of a type, or null if there is none
     *
     * @param <T> ? extends Collectible
     * @param clazz Collectible Class
     * @return Nullable Collectible
     */
    public <T extends Collectible> T getActive(Class<T> clazz, String affix) {
        if (!activeCollectibles.containsKey(Vendorable.getParentTypeName(clazz) + affix)) return Vendorables.get(clazz, "default");
        return Vendorables.get(clazz, activeCollectibles.get(Vendorable.getParentTypeName(clazz) + affix));
    }
    
    public boolean hasActive(Class<? extends Collectible> clazz) {
        String typeName = Vendorable.getParentTypeName(clazz);
        String activeName = activeCollectibles.get(typeName);
        if (activeName != null) {
            if (Vendorables.get(clazz, activeName) == null) {
                activeCollectibles.remove(typeName);
                return false;
            }
            return true;
        }
        return false;
    }

    public void toggleEnabled(Class<? extends Collectible> clazz) {
        setEnabled(clazz, !isEnabled(clazz));
    }

    public <T extends Collectible> ItemStack getActiveIcon(Class<T> clazz) {
        T collectible = getActive(clazz);
        return collectible.getDisplayItem(getInfo(collectible).selectedSkin);
    }

    public <T extends Collectible> String getActiveName(Class<T> clazz) {
        T collectible = getActive(clazz);
        CollectibleInfo info = getInfo(collectible);
        if (info.getSelectedSkin() != null) {
            return collectible.getDisplayName() + " (" + collectible.getSkin(info.getSelectedSkin()).getDisplayName() + ")";
        } else {
            return collectible.getDisplayName();
        }
    }

    public <T extends Collectible> boolean isEnabled(Class<T> clazz) {
        String typeName = Vendorable.getParentTypeName(clazz);
        if (!enabledMap.containsKey(typeName)) {
            enabledMap.put(typeName, true);
        }
        return enabledMap.get(typeName);
    }

    public <T extends Collectible> void setEnabled(Class<T> clazz, boolean state) {
        enabledMap.put(Vendorable.getParentTypeName(clazz), state);
        owner.refreshHotbar();
    }
    
    public <T extends Collectible> List<T> getAll(Class<T> clazz) {
        List<T> collectibleList = new ArrayList<>();
    
        Map<String, CollectibleInfo> collectibleMap = this.collectibles.get(Vendorable.getParentTypeName(clazz));
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
        activeCollectibles.put(collectible.getParentType(), collectible.getIdentifier());
        owner.refreshHotbar();
    }

    /**
     * Sets a collectible as the active collectible
     *
     * @param collectible Collectible
     */
    public void setActiveItem(Collectible collectible, String affix) {
        Collectible current = getActive(collectible.getClass(), affix);
        if (current != null) current.onDisable(owner);
        collectible.onEnable(owner);
        activeCollectibles.put(collectible.getParentType() + affix, collectible.getIdentifier());
        owner.refreshHotbar();
    }

    public void setSkin(Collectible collectible, String skin) {
        owner.getCollectibles().getInfo(collectible).setSelectedSkin(skin);
        owner.refreshHotbar();
    }

    public void removeActiveItem(Class<? extends Collectible> clazz) {
        String type = Vendorable.getParentTypeName(clazz);
        if (activeCollectibles.containsKey(type)) {
            Collectible collectible = Vendorables.get(clazz, activeCollectibles.get(type));
            if (collectible != null) {
                collectible.onDisable(owner);
            }
            activeCollectibles.remove(type);
            owner.refreshHotbar();
        }
    }

    public void removeActiveItem(Class<? extends Collectible> clazz, String affix) {
        String type = Vendorable.getParentTypeName(clazz) + affix;
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
        if (activeCollectibles.containsKey(collectible.getParentType())
                && activeCollectibles.get(collectible.getParentType()).equalsIgnoreCase(collectible.getIdentifier())) {
            collectible.onDisable(owner);
            activeCollectibles.remove(collectible.getParentType());
        }
    }

    public static InventoryMenuItem createActiveMenuItem(Class<? extends Collectible> clazz) {
        return InventoryMenuAPI.createItemDynamic()
                .setName(cp -> cp.getCollectibles().getActiveName(clazz))
                .setDescription(cp -> cp.getCollectibles().getActive(clazz).getDescription())
                .setDisplayItem(cp -> cp.getCollectibles().getActiveIcon(clazz))
                .setVisibility(cp -> cp.getCollectibles().hasActive(clazz))
                .setCloseOnAction(false);
    }

    public static InventoryMenuItemToggle createToggleMenuItem(Class<? extends Collectible> clazz) {
        return InventoryMenuAPI.createItemToggle()
                .setVisibility(cp -> cp.getCollectibles().hasActive(clazz))
                .setAction(cp -> cp.getCollectibles().toggleEnabled(clazz))
                .setEnabledFun(cp -> cp.getCollectibles().isEnabled(clazz));
    }

    private static final String COLLECTIBLE_SKIN = "collskin";
    private static final String COLLECTIBLE_SEARCH = "collsearch";

    /**
     * Creates an Inventory Menu Container with all available collectibles of a type
     *
     * @param clazz Class
     */
    public static InventoryMenuItem createCollectibleContainer(Class<? extends Collectible> clazz, InventoryMenuItem menuItem) {
        if (!menuItem.hasLinkedContainer())
            menuItem.createLinkedContainer(clazz.getSimpleName());
        InventoryMenuContainerChest container = menuItem.getLinkedChest();

        InventoryMenuItem activeMenuItem = container.addStaticItem(createActiveMenuItem(clazz), 6, 2);

        InventoryMenuItem skinMenuItem = container.addStaticItem(
                createSkinMenu(clazz).setAction(cp -> cp.getMenu().setMenuTag(COLLECTIBLE_SKIN, cp.getCollectibles().getActive(clazz).getIdentifier())),
                3, 0);

        InventoryMenuItem searchMenuItem = container.addStaticItem(createSearchMenu(clazz), 2, 0);

        container.setOpenAction((container2, cp) -> {
            container2.clearUnsorted();
            if (!cp.getMenu().hasMenuTag(COLLECTIBLE_SEARCH)) {
                int index = -1;
                int i = 0;
                Collectible active = cp.getCollectibles().getActive(clazz);
                for (Collectible collectible : Vendorables.getAllSorted(clazz, Vendorables.SortType.CUSTOM_MODEL_DATA)) {
                    if (index == -1 && collectible.equals(active)) {
                        index = i;
                    }
                    container2.addMenuItem(InventoryMenuAPI.createItemDynamic()
                            .setName(cp2 -> collectible.isUnlocked(cp2) ? collectible.getDisplayName() : "Locked")
                            .setDisplayItem(cp2 -> collectible.isUnlocked(cp2) ? collectible.getDisplayItem() : InventoryMenuUtils.MenuIcon.LOCKED.getIconItem())
                            .setDescription(cp2 -> collectible.isUnlocked(cp2) ? collectible.getDescription() : "")
                            .setAction(cp2 -> {
                                if (collectible.isUnlocked(cp2)) {
                                    if (collectible.hasSkins()) {
                                        cp2.getMenu().setMenuTag(COLLECTIBLE_SKIN, collectible.getIdentifier());
                                        cp2.getMenu().setInventoryMenuItem(skinMenuItem);
                                    } else {
                                        cp2.getCollectibles().setActiveItem(collectible);
                                    }
                                }
                            })
                            .setCloseOnAction(false));
                    i++;
                }
                cp.getMenu().setPage(index / container.getPageItemTotal());
            } else {
                String search = cp.getMenu().getMenuTag(COLLECTIBLE_SEARCH, String.class);
                for (Collectible collectible : Vendorables.getAllSorted(clazz, Vendorables.SortType.CUSTOM_MODEL_DATA)) {
                    if (!collectible.getName().toLowerCase().contains(search.toLowerCase())) continue;
                    container2.addMenuItem(InventoryMenuAPI.createItemDynamic()
                            .setName(cp2 -> collectible.isUnlocked(cp2) ? collectible.getDisplayName() : "Locked")
                            .setDisplayItem(cp2 -> collectible.isUnlocked(cp2) ? collectible.getDisplayItem() : InventoryMenuUtils.MenuIcon.LOCKED.getIconItem())
                            .setDescription(cp2 -> collectible.isUnlocked(cp2) ? collectible.getDescription() : "")
                            .setAction(cp2 -> {
                                if (collectible.isUnlocked(cp2)) {
                                    if (collectible.hasSkins()) {
                                        cp2.getMenu().setMenuTag(COLLECTIBLE_SKIN, collectible.getIdentifier());
                                        cp2.getMenu().setInventoryMenuItem(skinMenuItem);
                                    } else {
                                        cp2.getCollectibles().setActiveItem(collectible);
                                    }
                                }
                            })
                            .setCloseOnAction(false));
                    cp.getMenu().removeMenuTag(COLLECTIBLE_SEARCH);
                }
            }
        });

        return menuItem;
    }

    public static InventoryMenuItem createSearchMenu(Class<? extends Collectible> clazz) {
        return InventoryMenuAPI.createItemSearch()
                .setName("Search for " + clazz.getSimpleName())
                .setSearchTag(COLLECTIBLE_SEARCH)
                .setFailText("No " + clazz.getSimpleName() + "s found!")
                .build();
    }

    public static InventoryMenuItem createSkinMenu(Class<? extends Collectible> clazz) {
        InventoryMenuItem menuItem = InventoryMenuAPI.createItemDynamic()
                .setName("Skins")
                .setDescription(cp -> "Change the skin of your " + cp.getCollectibles().getActive(clazz).getDisplayName())
                .setDisplayItem(Material.LAVA_BUCKET, 1)
                .setVisibility(cp -> cp.getCollectibles().hasActive(clazz) && cp.getCollectibles().getActive(clazz).hasSkins())
                .createLinkedContainer("Skins");

        menuItem.getLinkedChest().addStaticItem(createActiveMenuItem(clazz), 6, 2);

        menuItem.getLinkedChest()
                .setOpenAction((container, cp) -> {
                    container.clearUnsorted();
                    Collectible collectible = Vendorables.get(clazz, cp.getMenu().getMenuTag(COLLECTIBLE_SKIN, String.class));
                    if (collectible == null) return;
                    container.addMenuItem(InventoryMenuAPI.createItemStatic()
                            .setName(collectible.getDisplayName())
                            .setDisplayItem(collectible.getDisplayItem())
                            .setDescription(collectible.getDescription())
                            .setAction(cp2 -> {
                                cp2.getCollectibles().setActiveItem(collectible);
                                cp2.getCollectibles().setSkin(collectible, null);
                            })
                            .setCloseOnAction(false));
                    for (String id : collectible.getSkinIds()) {
                        Set<String> skins = cp.getCollectibles().getInfo(collectible).getOwnedSkins().keySet();
                        InventoryMenuItem skinItem;
                        if (skins.contains(id)) {
                            CollectibleSkin skin = collectible.getSkin(id);
                            skinItem = InventoryMenuAPI.createItemStatic()
                                    .setName(skin.getDisplayName())
                                    .setDisplayItem(collectible.getMaterial(), skin.getCmd())
                                    .setDescription(collectible.getDescription())
                                    .setAction(cp2 -> {
                                        cp2.getCollectibles().setActiveItem(collectible);
                                        cp2.getCollectibles().setSkin(collectible, id);
                                    })
                                    .setCloseOnAction(false);
                        } else {
                            skinItem = InventoryMenuAPI.createItemStatic()
                                    .setName("Locked")
                                    .setDisplayItem(InventoryMenuUtils.MenuIcon.LOCKED.getIconItem())
                                    .setDescription("")
                                    .setCloseOnAction(false);
                        }
                        container.addMenuItem(skinItem);
                    }
                });

        return menuItem;
    }

    public boolean addLeaf(String leafName) {
        if (!collectedLeaves.contains(leafName)) {
            collectedLeaves.add(leafName);
            owner.updateLeaves();
            return true;
        }
        return false;
    }

    // TODO: This is temporary, redo this
    public int getLeafCount(String zoneName) {
        int count = 0;
        for (String leaf : collectedLeaves) {
            if (leaf.startsWith(zoneName)) {
                count++;
            }
        }
        return count;
    }

    public boolean hasLeaf(String leafName) {
        return collectedLeaves.contains(leafName);
    }

    public void clearLeaves(String zoneName) {
        collectedLeaves.removeIf(next -> next.startsWith(zoneName));
        owner.updateLeaves();
        GlobalZones.getZone(zoneName).clearLeaves(owner);
    }
    
}
