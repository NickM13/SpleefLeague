package com.spleefleague.zone.gear;

import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Holdable;
import com.spleefleague.core.util.CoreUtils;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.zone.CoreZones;
import com.spleefleague.zone.gear.fortunescarabs.GearFortuneScarabs;
import com.spleefleague.zone.gear.hookshot.GearHookshot;
import com.spleefleague.zone.gear.mead.GearMead;
import com.spleefleague.zone.player.ZonePlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

/**
 * @author NickM13
 */
public abstract class Gear extends Holdable {

    public enum GearType {
        FORTUNE_SCARABS(GearFortuneScarabs.class, 5000L, Material.LAPIS_LAZULI),
        HOOKSHOT(GearHookshot.class, 30000L, Material.BLAZE_ROD),
        MEAD(GearMead.class, 5000L, Material.HONEY_BOTTLE);

        public Class<? extends Gear> gearClass;
        public long cooldown;
        public Material material;

        GearType(Class<? extends Gear> gearClass, long cooldown, Material material) {
            this.gearClass = gearClass;
            this.cooldown = cooldown;
            this.material = material;
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
        GearMead.init();

        loadCollectibles(Gear.class);

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

    @DBField private String usageZone = null;

    protected ItemStack available;
    protected ItemStack unavailable;
    protected final GearType gearType;

    public Gear(GearType gearType) {
        super();
        this.gearType = gearType;
    }

    public Gear(GearType gearType, String identifier, String name) {
        super();
        this.gearType = gearType;
        this.identifier = identifier;
        this.name = name;
        this.material = gearType.material;
        this.available = applyPersistents(InventoryMenuUtils.createCustomItem(material, 1));
        this.unavailable = applyPersistents(InventoryMenuUtils.createCustomItem(material, 2));
    }

    @Override
    public void afterLoad() {
        super.afterLoad();
        this.available = applyPersistents(InventoryMenuUtils.createCustomItem(material, 1));
        this.unavailable = applyPersistents(InventoryMenuUtils.createCustomItem(material, 2));
    }

    public void setUsageZone(String usageZone) {
        this.usageZone = usageZone;
        saveChanges();
    }

    @Override
    public boolean isAvailableToPurchase(CorePlayer cp) {
        return true;
    }

    @Override
    public final void onRightClick(CorePlayer corePlayer) {
        ZonePlayer zonePlayer = CoreZones.getInstance().getPlayers().get(corePlayer);
        if (usageZone == null || zonePlayer.hasZonePermissions(usageZone)) {
            if (onActivate(corePlayer)) {
                zonePlayer.startCooldown(gearType);
            }
        }
    }

    protected abstract boolean onActivate(CorePlayer corePlayer);

    public boolean isAvailable(CorePlayer corePlayer) {
        ZonePlayer zonePlayer = CoreZones.getInstance().getPlayers().get(corePlayer);
        return zonePlayer.isReady(gearType);
    }

    public ItemStack getGearItem(CorePlayer corePlayer) {
        if (isAvailable(corePlayer)) {
            return available;
        } else {
            return unavailable;
        }
    }

}
