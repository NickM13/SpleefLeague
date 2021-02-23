/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.splegg.game;

import com.google.common.collect.Sets;
import com.mongodb.client.MongoCollection;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.menu.hotbars.main.HeldItemMenu;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.CorePlayerCollectibles;
import com.spleefleague.core.player.collectible.Holdable;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.core.vendor.Vendorables;
import com.spleefleague.core.world.game.projectile.ProjectileStats;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.annotation.DBLoad;
import com.spleefleague.splegg.Splegg;
import com.spleefleague.splegg.game.classic.ClassicSpleggBattle;
import com.spleefleague.splegg.game.multi.MultiSpleggBattle;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * @author NickM13
 */
public class SpleggGun extends Holdable {

    private static final ItemStack RANDOM_GUN = InventoryMenuUtils.createCustomItem(Material.IRON_INGOT, 1);
    
    public static void init() {
        Vendorable.registerParentType(SpleggGun.class);

        if (loadCollectibles(SpleggGun.class) == 0) {
            create(SpleggGun.class, "henholster", "Hen Holster");
        }

        InventoryMenuAPI.createItemHotbar(0, "spleggGunHotbarItemS1")
                .setName(cp -> cp.getCollectibles().getSkinnedName(((ClassicSpleggBattle) cp.getBattle()).getBattler(cp).getGun1()))
                .setDescription(cp -> cp.isInBattle() ? ((ClassicSpleggBattle) cp.getBattle()).getBattler(cp).getGun1().getDescription() : "")
                .setDisplayItem(cp -> cp.getCollectibles().getSkinnedIcon(((ClassicSpleggBattle) cp.getBattle()).getBattler(cp).getGun1()))
                .setAvailability(cp -> cp.isInBattle()
                        && cp.getBattleState() == BattleState.BATTLER
                        && cp.getBattle() instanceof ClassicSpleggBattle);

        InventoryMenuAPI.createItemHotbar(1, "spleggGunHotbarItemS2")
                .setName(cp -> cp.getCollectibles().getSkinnedName(((ClassicSpleggBattle) cp.getBattle()).getBattler(cp).getGun2()))
                .setDescription(cp -> cp.isInBattle() ? ((ClassicSpleggBattle) cp.getBattle()).getBattler(cp).getGun2().getDescription() : "")
                .setDisplayItem(cp -> cp.getCollectibles().getSkinnedIcon(((ClassicSpleggBattle) cp.getBattle()).getBattler(cp).getGun2()))
                .setAvailability(cp -> cp.isInBattle()
                        && cp.getBattleState() == BattleState.BATTLER
                        && cp.getBattle() instanceof ClassicSpleggBattle);

        InventoryMenuAPI.createItemHotbar(0, "spleggGunHotbarItemM1")
                .setName(cp -> cp.getCollectibles().getSkinnedName(((MultiSpleggBattle) cp.getBattle()).getBattler(cp).getGun1()))
                .setDescription(cp -> cp.isInBattle() ? ((MultiSpleggBattle) cp.getBattle()).getBattler(cp).getGun1().getDescription() : "")
                .setDisplayItem(cp -> cp.getCollectibles().getSkinnedIcon(((MultiSpleggBattle) cp.getBattle()).getBattler(cp).getGun1()))
                .setAvailability(cp -> cp.isInBattle()
                        && cp.getBattleState() == BattleState.BATTLER
                        && cp.getBattle() instanceof MultiSpleggBattle);

        InventoryMenuAPI.createItemHotbar(1, "spleggGunHotbarItemM2")
                .setName(cp -> cp.getCollectibles().getSkinnedName(((MultiSpleggBattle) cp.getBattle()).getBattler(cp).getGun2()))
                .setDescription(cp -> cp.isInBattle() ? ((MultiSpleggBattle) cp.getBattle()).getBattler(cp).getGun2().getDescription() : "")
                .setDisplayItem(cp -> cp.getCollectibles().getSkinnedIcon(((MultiSpleggBattle) cp.getBattle()).getBattler(cp).getGun2()))
                .setAvailability(cp -> cp.isInBattle()
                        && cp.getBattleState() == BattleState.BATTLER
                        && cp.getBattle() instanceof MultiSpleggBattle);
    }

    public static SpleggGun getRandom(SpleggGun blacklist) {
        Set<SpleggGun> collection = Sets.newHashSet(Vendorables.getAll(SpleggGun.class).values());
        if (blacklist != null) collection.remove(blacklist);
        int r = new Random().nextInt(collection.size());
        Iterator<SpleggGun> it = collection.iterator();
        for (int i = 0; i < r; i++) {
            it.next();
        }
        return it.next();
    }

    private static InventoryMenuItem createActiveSpleggGunMenuItem(String affix) {
        return InventoryMenuAPI.createItemDynamic()
                .setName(cp -> {
                    SpleggGun active = cp.getCollectibles().getActive(SpleggGun.class, affix);
                    if (active != null) {
                        return cp.getCollectibles().getActiveName(SpleggGun.class, affix);
                    } else {
                        return "Random Splegg Gun &6(Currently Selected)";
                    }
                })
                .setDescription(cp -> {
                    SpleggGun active = cp.getCollectibles().getActive(SpleggGun.class, affix);
                    if (active != null) {
                        return active.getDescription();
                    } else {
                        return "Select a random Splegg Gun for your next match!";
                    }
                })
                .setDisplayItem(cp -> {
                    SpleggGun active = cp.getCollectibles().getActive(SpleggGun.class, affix);
                    if (active != null) {
                        return cp.getCollectibles().getActiveIcon(SpleggGun.class, affix);
                    } else {
                        return RANDOM_GUN;
                    }
                })
                .setCloseOnAction(false);
    }

    public static void createMenu() {
        InventoryMenuItem gunMenu = CorePlayerCollectibles.createCollectibleContainer(SpleggGun.class,
                InventoryMenuAPI.createItemDynamic()
                        .setName("Splegg Guns")
                        .setDescription("Set your active Splegg Gun")
                        .setDisplayItem(InventoryMenuUtils.createCustomItem(Material.EGG, 10)));

        HeldItemMenu.getItem().getLinkedChest().addMenuItem(gunMenu, 1, 0);
        HeldItemMenu.getItem().getLinkedChest().addMenuItem(CorePlayerCollectibles.createActiveMenuItem(SpleggGun.class), 1, 1);
        HeldItemMenu.getItem().getLinkedChest().addMenuItem(CorePlayerCollectibles.createToggleMenuItem(SpleggGun.class), 1, 2);
    }

    public static InventoryMenuItem createMenu(String affix, String secondary) {
        InventoryMenuItem menuItem = InventoryMenuAPI.createItemDynamic()
                .setName(cp -> {
                    SpleggGun active = cp.getCollectibles().getActive(SpleggGun.class, affix);
                    if (active != null) {
                        return cp.getCollectibles().getActiveName(SpleggGun.class, affix) + " &6&i(Click to Change)";
                    } else {
                        return "Random Splegg Gun &6&i(Click to Change)";
                    }
                })
                .setDescription(cp -> {
                    SpleggGun active = cp.getCollectibles().getActive(SpleggGun.class, affix);
                    if (active != null) {
                        return active.getDescription();
                    } else {
                        return "Select a random Splegg Gun for your next match!";
                    }
                })
                .setDisplayItem(cp -> {
                    SpleggGun active = cp.getCollectibles().getActive(SpleggGun.class, affix);
                    if (active != null) {
                        return cp.getCollectibles().getActiveIcon(SpleggGun.class, affix);
                    } else {
                        return RANDOM_GUN;
                    }
                })
                .createLinkedContainer("Active Splegg Gun");

        menuItem.getLinkedChest()
                .setItemBuffer(2)
                .addDeadSpace(0, 0)
                .addDeadSpace(2, 2)
                .addDeadSpace(0, 4)
                .addStaticItem(createActiveSpleggGunMenuItem(affix), 6, 2);

        menuItem.getLinkedChest().addStaticItem(InventoryMenuAPI.createItemDynamic()
                .setName("Random Splegg Gun")
                .setDescription("Select a random Splegg Gun for your next match!")
                .setDisplayItem(RANDOM_GUN)
                .setAction(cp -> cp.getCollectibles().removeActiveItem(SpleggGun.class, affix))
                .setCloseOnAction(false), 2, 3);

        for (SpleggGun spleggGun : Vendorables.getAll(SpleggGun.class).values()) {
            InventoryMenuItem smi = InventoryMenuAPI.createItemDynamic()
                    .setName(cp -> spleggGun.isAvailable(cp) ? cp.getCollectibles().getSkinnedName(spleggGun) : "Locked")
                    .setDisplayItem(cp -> spleggGun.isAvailable(cp) ? cp.getCollectibles().getSkinnedIcon(spleggGun) : InventoryMenuUtils.MenuIcon.LOCKED.getIconItem())
                    .setDescription(cp -> spleggGun.isAvailable(cp) ? spleggGun.getDescription() : "")
                    .setAction(cp -> {
                        if (spleggGun.isAvailable(cp)) {
                            SpleggGun gunCurrent = cp.getCollectibles().getActive(SpleggGun.class, affix);
                            SpleggGun gunOther = cp.getCollectibles().getActive(SpleggGun.class, secondary);
                            if (gunOther != null && gunOther.equals(spleggGun)) {
                                cp.getCollectibles().setActiveItem(gunCurrent, secondary);
                            }
                            cp.getCollectibles().setActiveItem(spleggGun, affix);
                        }
                    })
                    .setCloseOnAction(false);
            menuItem.getLinkedChest().addMenuItem(smi);
        }

        return menuItem;
    }

    @DBField
    private ProjectileStats projectileStats;
    
    public SpleggGun() {
        super();
    }

    public SpleggGun(String identifier, String displayName) {
        super();
        this.material = Material.DIAMOND_SHOVEL;
        this.identifier = identifier;
        this.name = displayName;
        this.projectileStats = new ProjectileStats();
    }

    public void resetStats() {
        projectileStats = new ProjectileStats();
    }
    
    @Override
    public boolean isAvailableToPurchase(CorePlayer corePlayer) {
        return false;
    }
    
    @Override
    public void onRightClick(CorePlayer corePlayer) {
        corePlayer.getPlayer().getWorld().spawn(corePlayer.getPlayer().getEyeLocation(), Snowball.class, entity -> {
            entity.setVelocity(corePlayer.getPlayer().getLocation().getDirection().normalize().multiply(projectileStats.fireRange * 0.25f));
        });
    }

    @Override
    public String getDescription() {
        /*
        StringBuilder debugDesc = new StringBuilder("");
        for (Field field : ProjectileStats.class.getFields()) {
            try {
                if (field.isAnnotationPresent(Deprecated.class)) {
                    debugDesc.append("(disabled) " + field.getName() + ": " + field.get(getProjectileStats()) + "\n");
                } else {
                    debugDesc.append(field.getName() + ": " + field.get(getProjectileStats()) + "\n");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return debugDesc.toString();
        */
        return description;
    }
    
    @DBLoad(fieldName="projectile")
    private void loadProjectile(Document doc) {
        projectileStats = new ProjectileStats();
        projectileStats.load(doc);
    }
    
    public boolean isDefault() {
        return true;
    }
    
    public ProjectileStats getProjectileStats() {
        return projectileStats;
    }
    
    public boolean isAvailable(CorePlayer cp) {
        return isDefault() || cp.getCollectibles().contains(this);
    }

    @Override
    public void updateDisplayItem() {
        super.updateDisplayItem();
        projectileStats.updateProjectileItem();
    }
    
}
