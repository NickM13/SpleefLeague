package com.spleefleague.zone.gear;

import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Holdable;
import com.spleefleague.core.util.CoreUtils;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.zone.CoreZones;
import com.spleefleague.zone.gear.fortunescarabs.GearFortuneScarabs;
import com.spleefleague.zone.gear.hookshot.GearHookshot;
import com.spleefleague.zone.gear.mead.GearMead;
import com.spleefleague.zone.gear.steampack.GearSteamPack;
import com.spleefleague.zone.gear.wayfinder.GearWayfinder;
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
        FORTUNE_SCARABS(GearFortuneScarabs.class, 5000L, false),
        HOOKSHOT(GearHookshot.class, 30000L, false),
        MEAD(GearMead.class, 5000L, false),
        STEAM_PACK(GearSteamPack.class, 5000L, false),
        WAYFINDER(GearWayfinder.class, 5000L, false);

        public final Class<? extends Gear> gearClass;
        public final long cooldown;
        public final boolean offhand;

        GearType(Class<? extends Gear> gearClass, long cooldown, boolean offhand) {
            this.gearClass = gearClass;
            this.cooldown = cooldown;
            this.offhand = offhand;
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

    public static void init() {
        reload();

        InventoryMenuAPI.createItemHotbar(7, "Gear")
                .setName(cp -> cp.getCollectibles().getActiveName(Gear.class))
                .setDisplayItem(cp -> cp.getCollectibles().getActive(Gear.class).getGearItem(cp))
                .setDescription(cp -> cp.getCollectibles().getActive(Gear.class).getDescription())
                .setAvailability(cp -> !cp.isInBattle() && cp.getCollectibles().hasActive(Gear.class) && cp.getCollectibles().isEnabled(Gear.class))
                .setAction(cp -> cp.getCollectibles().getActive(Gear.class).onRightClick(cp));

        /*
        InventoryMenuAPI.createItemHotbar(40, "GearOffhand")
                .setName(cp -> cp.getCollectibles().getActiveName(Gear.class))
                .setDisplayItem(cp -> cp.getCollectibles().getActive(Gear.class).getGearOffhandItem(cp))
                .setDescription(cp -> cp.getCollectibles().getActive(Gear.class).getDescription())
                .setAvailability(cp -> !cp.isInBattle() &&
                        cp.getCollectibles().hasActive(Gear.class) &&
                        cp.getCollectibles().isEnabled(Gear.class) &&
                        cp.getCollectibles().getActive(Gear.class).hasOffhand(cp));
        */
    }

    public static void reload() {
        Vendorable.registerParentType(Gear.class);

        for (GearType gearType : GearType.values()) {
            Vendorable.registerExactType(gearType.gearClass);
        }

        loadCollectibles(Gear.class);

        GearHookshot.init();
    }

    public static void close() {

    }

    public static Set<String> getGearTypes() {
        return CoreUtils.enumToStrSet(GearType.class, false);
    }

    @DBField private String usageZone = null;

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
        this.material = Material.BLAZE_ROD;
        createGearItems();
    }

    @Override
    public void afterLoad() {
        super.afterLoad();
        createGearItems();
    }

    protected abstract void createGearItems();

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

    public void update() {

    }

    protected abstract boolean onActivate(CorePlayer corePlayer);

    public boolean isAvailable(CorePlayer corePlayer) {
        ZonePlayer zonePlayer = CoreZones.getInstance().getPlayers().get(corePlayer);
        return zonePlayer.isReady(gearType);
    }

    public abstract ItemStack getGearItem(CorePlayer corePlayer);

    public boolean hasOffhand(CorePlayer corePlayer) {
        return gearType.offhand;
    }

    public ItemStack getGearOffhandItem(CorePlayer corePlayer) {
        return null;
    }

}
