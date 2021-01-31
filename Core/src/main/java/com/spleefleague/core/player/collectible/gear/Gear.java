package com.spleefleague.core.player.collectible.gear;

import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Holdable;
import com.spleefleague.core.player.collectible.gear.hookshot.GearHookshot;
import com.spleefleague.core.util.CoreUtils;
import com.spleefleague.core.vendor.Vendorable;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

/**
 * @author NickM13
 */
public class Gear extends Holdable {

    public enum GearType {
        HOOKSHOT(GearHookshot.class);

        public Class<? extends Gear> gearClass;

        GearType(Class<? extends Gear> gearClass) {
            this.gearClass = gearClass;
        }

        public Gear create(String identifier, String displayName) {
            try {
                return gearClass.getConstructor(String.class, String.class).newInstance(identifier, displayName);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                CoreLogger.logError(exception);
                return null;
            }
        }

    }

    /**
     * Load hats from the SpleefLeague:Hats collection
     */
    public static void init() {
        Vendorable.registerParentType(Gear.class);

        GearHookshot.init();

        InventoryMenuAPI.createItemHotbar(7, "Gear")
                .setName(cp -> cp.getCollectibles().getActiveName(Gear.class))
                .setDisplayItem(cp -> cp.getCollectibles().getActive(Gear.class).getGearItem(cp))
                .setDescription(cp -> cp.getCollectibles().getActive(Gear.class).getDescription())
                .setAvailability(cp -> !cp.isBattler() && cp.getCollectibles().hasActive(Gear.class) && cp.getCollectibles().isEnabled(Gear.class))
                .setAction(cp -> cp.getCollectibles().getActive(Gear.class).onRightClick(cp));
    }

    public static void close() {

    }

    public static Set<String> getGearTypes() {
        return CoreUtils.enumToStrSet(GearType.class, false);
    }

    private GearType gearType;

    public Gear(GearType gearType) {
        super();
        this.gearType = gearType;
    }

    public Gear(GearType gearType, String identifier, String name) {
        super();
        this.gearType = gearType;
        this.identifier = identifier;
        this.name = name;
    }

    @Override
    public boolean isAvailableToPurchase(CorePlayer cp) {
        return true;
    }

    @Override
    public void onRightClick(CorePlayer cp) {

    }

    public ItemStack getGearItem(CorePlayer cp) {
        return getDisplayItem();
    }

}
