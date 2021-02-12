package com.spleefleague.core.player;

import com.spleefleague.core.Core;
import com.spleefleague.core.menu.*;
import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.player.collectible.CollectibleSkin;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.core.vendor.Vendorables;
import com.spleefleague.coreapi.player.collectibles.CollectibleInfo;
import com.spleefleague.coreapi.player.collectibles.PlayerCollectibles;
import com.spleefleague.coreapi.utils.packet.shared.CollectibleAction;
import com.spleefleague.coreapi.utils.packet.spigot.player.PacketSpigotPlayerCollectible;
import com.spleefleague.coreapi.utils.packet.spigot.player.PacketSpigotPlayerCollectibleSkin;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author NickM13
 * @since 4/19/2020
 */
public class CorePlayerCollectibles extends PlayerCollectibles {

    private final CorePlayer owner;

    public CorePlayerCollectibles(CorePlayer owner) {
        this.owner = owner;
    }

    @Override
    public void afterLoad() {
        super.afterLoad();

        /*
        for (String parent : Vendorable.getParentTypeNames()) {
            boolean first = true;
            for (Map.Entry<String, Vendorable> entry : Vendorables.getAll(parent).entrySet()) {
                if (first && !(entry.getValue() instanceof Collectible)) {
                    break;
                }
                first = false;
                Collectible collectible = (Collectible) entry.getValue();
                if (collectible.isDefault(owner) && (!collectibleMap.containsKey(parent) || !collectibleMap.get(parent).containsKey(entry.getKey()))) {
                    add(collectible);
                }
            }
        }
        */
    }

    public Map<Vendorable.Rarity, List<String>> getAvailableCollectibles() {
        Map<Vendorable.Rarity, List<String>> available = new HashMap<>();
        for (Vendorable.Rarity rarity : Vendorable.Rarity.values()) {
            available.put(rarity, new ArrayList<>());
        }
        /*
        // This finds all possible items that you haven't unlocked that arent locked behind a base item
        for (String parent : Vendorable.getParentTypeNames()) {
            boolean first = true;
            for (Map.Entry<String, Vendorable> entry : Vendorables.getAll(parent).entrySet()) {
                if (first && !(entry.getValue() instanceof Collectible)) {
                    break;
                }
                first = false;
                Vendorable.Rarity rarity = entry.getValue().getRarity();
                Collectible collectible = (Collectible) entry.getValue();
                if (!collectibleMap.containsKey(parent) || !collectibleMap.get(parent).containsKey(entry.getKey())) {
                    available.get(rarity).add(collectible.getParentType() + ":" + collectible.getIdentifier());
                } else {
                    CollectibleInfo collectibleInfo = collectibleMap.get(parent).get(entry.getKey());
                    for (String skin : collectible.getSkinIds()) {
                        if (!collectibleInfo.getOwnedSkins().containsKey(skin)) {
                            available.get(rarity).add(collectible.getParentType() + ":" + collectible.getIdentifier() + ":" + skin);
                        }
                    }
                }
            }
        }
        */
        for (String parent : Vendorable.getParentTypeNames()) {
            boolean first = true;
            for (Map.Entry<String, Vendorable> entry : Vendorables.getAll(parent).entrySet()) {
                if (first) {
                    if (!(entry.getValue() instanceof Collectible)) break;
                    first = false;
                }
                Vendorable.Rarity rarity = entry.getValue().getRarity();
                Collectible collectible = (Collectible) entry.getValue();
                if (collectible.getUnlockType().isRolled()) {
                    available.get(rarity).add(collectible.getParentType() + ":" + collectible.getIdentifier());
                    for (String skin : collectible.getSkinIds()) {
                        available.get(rarity).add(collectible.getParentType() + ":" + collectible.getIdentifier() + ":" + skin);
                    }
                }
            }
        }
        return available;
    }

    public boolean add(Collectible collectible) {
        if (collectible != null) {
            if (!collectibleMap.containsKey(collectible.getParentType())) {
                collectibleMap.put(collectible.getParentType(), new HashMap<>());
            }
            collectibleMap.get(collectible.getParentType()).put(collectible.getIdentifier(), new CollectibleInfo(System.currentTimeMillis()));
            Core.getInstance().sendPacket(new PacketSpigotPlayerCollectible(
                    owner.getUniqueId(),
                    collectible.getParentType(),
                    collectible.getIdentifier(),
                    CollectibleAction.UNLOCK,
                    ""));
            return true;
        }
        return false;
    }

    public boolean removeSkin(Collectible collectible, String skin) {
        if (collectible != null) {
            if (collectibleMap.containsKey(collectible.getParentType())) {
                if (collectibleMap.get(collectible.getParentType()).containsKey(collectible.getIdentifier())) {
                    if (collectibleMap.get(collectible.getParentType()).get(collectible.getIdentifier()).removeSkin(skin)) {
                        Core.getInstance().sendPacket(new PacketSpigotPlayerCollectibleSkin(
                                owner.getUniqueId(),
                                collectible.getParentType(),
                                collectible.getIdentifier(),
                                skin != null ? skin : "",
                                CollectibleAction.LOCK,
                                ""));
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @param collectible Collectible
     * @param skin        Skin ID
     * @return 1 for no collectible, 2 for already have, 3 for null
     */
    public int addSkin(Collectible collectible, String skin) {
        if (collectible != null) {
            if (!collectibleMap.containsKey(collectible.getParentType()) ||
                    !collectibleMap.get(collectible.getParentType()).containsKey(collectible.getIdentifier())) {
                if (collectible.isDefault(owner)) {
                    add(collectible);
                } else {
                    return 1;
                }
            }
            if (collectibleMap.get(collectible.getParentType()).get(collectible.getIdentifier()).addSkin(skin, System.currentTimeMillis())) {
                Core.getInstance().sendPacket(new PacketSpigotPlayerCollectibleSkin(
                        owner.getUniqueId(),
                        collectible.getParentType(),
                        collectible.getIdentifier(),
                        skin != null ? skin : "",
                        CollectibleAction.UNLOCK,
                        ""));
                return 0;
            } else {
                return 2;
            }
        }
        return 3;
    }

    public boolean contains(Collectible collectible) {
        if (collectibleMap.containsKey(collectible.getParentType())) {
            return collectibleMap.get(collectible.getParentType()).get(collectible.getIdentifier()) != null;
        }
        return false;
    }

    public CollectibleInfo getInfo(Collectible collectible) {
        if (!contains(collectible)) {
            add(collectible);
        }
        return collectibleMap.get(collectible.getParentType()).get(collectible.getIdentifier());
    }

    public boolean remove(Collectible collectible) {
        if (collectible != null && collectibleMap.containsKey(collectible.getParentType())) {
            collectibleMap.get(collectible.getParentType()).remove(collectible.getIdentifier());
            deactivate(collectible);
            Core.getInstance().sendPacket(new PacketSpigotPlayerCollectible(
                    owner.getUniqueId(),
                    collectible.getParentType(),
                    collectible.getIdentifier(),
                    CollectibleAction.LOCK,
                    ""));
            return true;
        }
        return false;
    }

    /**
     * Returns the current active collectibleMap item of a type, or null if there is none
     *
     * @param <T>   ? extends Collectible
     * @param clazz Collectible Class
     * @return Nullable Collectible
     */
    public <T extends Collectible> T getActive(Class<T> clazz) {
        if (!activeMap.containsKey(Vendorable.getParentTypeName(clazz))) return Vendorables.get(clazz, "default");
        return Vendorables.get(clazz, activeMap.get(Vendorable.getParentTypeName(clazz)));
    }

    /**
     * Returns the current active collectibleMap item of a type, or null if there is none
     *
     * @param <T>   ? extends Collectible
     * @param clazz Collectible Class
     * @return Nullable Collectible
     */
    public <T extends Collectible> T getActive(Class<T> clazz, String affix) {
        if (!activeMap.containsKey(Vendorable.getParentTypeName(clazz) + affix))
            return Vendorables.get(clazz, "default");
        return Vendorables.get(clazz, activeMap.get(Vendorable.getParentTypeName(clazz) + affix));
    }

    public boolean hasActive(Class<? extends Collectible> clazz) {
        String typeName = Vendorable.getParentTypeName(clazz);
        String activeName = activeMap.get(typeName);
        if (activeName != null) {
            if (Vendorables.get(clazz, activeName) == null) {
                activeMap.remove(typeName);
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
        return collectible.getDisplayItem(getInfo(collectible).getSelectedSkin());
    }

    public <T extends Collectible> ItemStack getActiveIcon(Class<T> clazz, String affix) {
        T collectible = getActive(clazz, affix);
        return collectible.getDisplayItem(getInfo(collectible).getSelectedSkin());
    }

    public <T extends Collectible> String getActiveName(Class<T> clazz) {
        T collectible = getActive(clazz);
        CollectibleInfo info = getInfo(collectible);
        if (info.getSelectedSkin() != null && info.getSelectedSkin().length() > 0) {
            return collectible.getDisplayName() + " (" + collectible.getSkin(info.getSelectedSkin()).getDisplayName() + ")";
        } else {
            return collectible.getDisplayName();
        }
    }

    public <T extends Collectible> String getActiveName(Class<T> clazz, String affix) {
        T collectible = getActive(clazz, affix);
        CollectibleInfo info = getInfo(collectible);
        if (info.getSelectedSkin() != null && info.getSelectedSkin().length() > 0) {
            return collectible.getDisplayName() + " (" + collectible.getSkin(info.getSelectedSkin()).getDisplayName() + ")";
        } else {
            return collectible.getDisplayName();
        }
    }

    public <T extends Collectible> ItemStack getSkinnedIcon(Collectible collectible) {
        if (!contains(collectible)) return collectible.getDisplayItem();
        return collectible.getDisplayItem(getInfo(collectible).getSelectedSkin());
    }

    public <T extends Collectible> String getSkinnedName(Collectible collectible) {
        if (!contains(collectible)) return collectible.getDisplayName();
        CollectibleInfo info = getInfo(collectible);
        if (info.getSelectedSkin() != null && info.getSelectedSkin().length() > 0) {
            return collectible.getDisplayName() + " (" + collectible.getSkin(info.getSelectedSkin()).getDisplayName() + ")";
        } else {
            return collectible.getDisplayName();
        }
    }

    public <T extends Collectible> boolean isEnabled(Class<T> clazz) {
        return owner.getOptions().getBoolean("Collectibles:" + Vendorable.getParentTypeName(clazz));
    }

    public <T extends Collectible> void setEnabled(Class<T> clazz, boolean state) {
        owner.getOptions().setBoolean("Collectibles:" + Vendorable.getParentTypeName(clazz), state);
        owner.refreshHotbar();
    }

    public <T extends Collectible> List<T> getAll(Class<T> clazz) {
        List<T> collectibleList = new ArrayList<>();

        Map<String, CollectibleInfo> collectibleMap = this.collectibleMap.get(Vendorable.getParentTypeName(clazz));
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
     * Sets a collectible as the active collectible
     *
     * @param collectible Collectible
     */
    public void setActiveItem(Collectible collectible) {
        Collectible current = getActive(collectible.getClass());
        if (current != null) current.onDisable(owner);
        collectible.onEnable(owner);
        activeMap.put(collectible.getParentType(), collectible.getIdentifier());
        owner.refreshHotbar();
        Core.getInstance().sendPacket(new PacketSpigotPlayerCollectible(
                owner.getUniqueId(),
                collectible.getParentType(),
                collectible.getIdentifier(),
                CollectibleAction.ACTIVE,
                ""));
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
        activeMap.put(collectible.getParentType() + affix, collectible.getIdentifier());
        owner.refreshHotbar();
        Core.getInstance().sendPacket(new PacketSpigotPlayerCollectible(
                owner.getUniqueId(),
                collectible.getParentType(),
                collectible.getIdentifier(),
                CollectibleAction.ACTIVE,
                affix));
    }

    public void setSkin(Collectible collectible, String skin) {
        owner.getCollectibles().getInfo(collectible).setSelectedSkin(skin);
        owner.refreshHotbar();
        Core.getInstance().sendPacket(new PacketSpigotPlayerCollectibleSkin(
                owner.getUniqueId(),
                collectible.getParentType(),
                collectible.getIdentifier(),
                skin != null ? skin : "",
                CollectibleAction.ACTIVE,
                ""));
    }

    public void removeActiveItem(Class<? extends Collectible> clazz) {
        String type = Vendorable.getParentTypeName(clazz);
        if (activeMap.containsKey(type)) {
            Collectible collectible = Vendorables.get(clazz, activeMap.get(type));
            if (collectible != null) {
                collectible.onDisable(owner);
            }
            activeMap.remove(type);
            owner.refreshHotbar();
            Core.getInstance().sendPacket(new PacketSpigotPlayerCollectible(
                    owner.getUniqueId(),
                    type,
                    "",
                    CollectibleAction.ACTIVE,
                    ""));
        }
    }

    public void removeActiveItem(Class<? extends Collectible> clazz, String affix) {
        String type = Vendorable.getParentTypeName(clazz) + affix;
        if (activeMap.containsKey(type)) {
            Collectible collectible = Vendorables.get(clazz, activeMap.get(type));
            if (collectible != null) {
                collectible.onDisable(owner);
            }
            activeMap.remove(type);
            owner.refreshHotbar();
            Core.getInstance().sendPacket(new PacketSpigotPlayerCollectible(
                    owner.getUniqueId(),
                    type,
                    "",
                    CollectibleAction.ACTIVE,
                    affix));
        }
    }

    /**
     * Deactivates a collectible
     *
     * @param collectible Collectible
     */
    protected void deactivate(Collectible collectible) {
        if (activeMap.containsKey(collectible.getParentType())
                && activeMap.get(collectible.getParentType()).equalsIgnoreCase(collectible.getIdentifier())) {
            collectible.onDisable(owner);
            activeMap.remove(collectible.getParentType());
            Core.getInstance().sendPacket(new PacketSpigotPlayerCollectible(
                    owner.getUniqueId(),
                    collectible.getParentType(),
                    "",
                    CollectibleAction.ACTIVE,
                    ""));
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
     * Creates an Inventory Menu Container with all available collectibleMap of a type
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
                                cp2.getCollectibles().setSkin(collectible, "");
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

}
