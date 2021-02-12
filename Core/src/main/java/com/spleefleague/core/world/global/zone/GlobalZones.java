package com.spleefleague.core.world.global.zone;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.Dimension;
import com.spleefleague.core.util.variable.Point;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.*;

/**
 * @author NickM13
 */
public class GlobalZones {

    private static GlobalZone WILDERNESS;
    private static final Map<String, GlobalZone> globalZoneMap = new HashMap<>();
    private static MongoCollection<Document> globalZoneCol;

    private static final Set<UUID> editingPlayers = new HashSet<>();
    private static final Set<UUID> globalLeafUuids = new HashSet<>();
    private static final Map<UUID, CorePlayer> unfrozenPlayers = new HashMap<>();

    public static void init() {
        Core.addProtocolPacketAdapter(new PacketAdapter(Core.getInstance(), PacketType.Play.Server.SPAWN_ENTITY) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer spawnEntityPacket = event.getPacket();
                UUID uuid = spawnEntityPacket.getUUIDs().read(0);
                net.minecraft.server.v1_15_R1.Entity entity = ((CraftWorld) (event.getPlayer().getWorld())).getHandle().getEntity(uuid);
                if (entity instanceof ZoneLeafEntity) {
                    CorePlayer cp = Core.getInstance().getPlayers().getOffline(event.getPlayer().getUniqueId());
                    ZoneLeafEntity zoneLeaf = (ZoneLeafEntity) entity;
                }
            }
        });

        globalZoneCol = Core.getInstance().getPluginDB().getCollection("Zones");
        for (Document doc : globalZoneCol.find()) {
            GlobalZone globalZone = new GlobalZone();
            globalZone.load(doc);
            globalZoneMap.put(doc.get("identifier", String.class), globalZone);
        }
        if (!globalZoneMap.containsKey("wild")) {
            GlobalZone globalZone = new GlobalZone("wild", "Wilderness");
            globalZoneMap.put("wild", globalZone);
        }
        WILDERNESS = globalZoneMap.get("wild");

        Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            for (UUID uuid : editingPlayers) {
                CorePlayer cp = Core.getInstance().getPlayers().get(uuid);
                if (cp == null || cp.getOnlineState() != DBPlayer.OnlineState.HERE) {
                    editingPlayers.remove(uuid);
                    continue;
                }
                for (GlobalZone zone : globalZoneMap.values()) {
                    for (ZoneLeaf leaf : zone.getLeaves()) {
                        cp.getPlayer().spawnParticle(Particle.SWEEP_ATTACK, leaf.getPos().x + 0.5, leaf.getPos().y + 0.5, leaf.getPos().z + 0.5, 1, 0, 0, 0);
                    }
                }
            }
            for (CorePlayer cp : unfrozenPlayers.values()) {
                cp.setGlobalZone(GlobalZones.getZone(new Point(cp.getLocation())));
            }
            for (CorePlayer cp : Core.getInstance().getPlayers().getAllHereExtended()) {
                cp.showGlobalZone();
            }
        }, 20L, 20L);

        Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            for (CorePlayer cp : Core.getInstance().getPlayers().getAllHereExtended()) {
                if (cp.getGlobalZone().isOnDrain(cp)) {
                    System.out.println("DRain!");
                }
            }
        }, 5L, 5L);

        InventoryMenuAPI.createItemHotbar(4, "ZoneScanner")
                .setName("Zone Scanner")
                .setDisplayItem(Material.HONEYCOMB)
                .setDescription(cp -> {
                    GlobalZone zone = getZone(new Point(cp.getLocation()));
                    return Chat.DEFAULT + "Id: " + zone.getIdentifier() + "\n" +
                            Chat.DEFAULT + "Name: " + zone.getName() + "\n" +
                            Chat.DEFAULT + "Leaves: " + zone.getLeaves().size();
                })
                .setAvailability(cp -> editingPlayers.contains(cp.getUniqueId()))
                .setAction(cp -> {
                    if (cp.getPlayer().isSneaking()) {
                        removeLeaves(new Point(cp.getLocation()), 1);
                    } else {
                        cp.getGlobalZone().addLeaf(new Point(cp.getLocation()));
                    }
                    cp.refreshHotbar();
                });
    }

    public static void close() {
        for (UUID uuid : globalLeafUuids) {
            Objects.requireNonNull(Bukkit.getEntity(uuid)).remove();
        }
    }

    public static GlobalZone getWilderness() {
        return WILDERNESS;
    }

    public static boolean toggleScanner(CorePlayer cp) {
        if (editingPlayers.contains(cp.getUniqueId())) {
            editingPlayers.remove(cp.getUniqueId());
            return false;
        } else {
            editingPlayers.add(cp.getUniqueId());
            return true;
        }
    }

    public static void onPlayerJoin(CorePlayer cp) {
        unfrozenPlayers.put(cp.getUniqueId(), cp);
    }

    public static void onPlayerLeave(CorePlayer cp) {
        unfrozenPlayers.remove(cp.getUniqueId());
    }

    public static Map<String, GlobalZone> getAll() {
        return globalZoneMap;
    }

    public static boolean createZone(String identifier, String name) {
        if (!globalZoneMap.containsKey(identifier)) {
            GlobalZone globalZone = new GlobalZone(identifier, name);
            globalZoneMap.put(identifier, globalZone);
            return true;
        }
        return false;
    }

    public static void destroyZone(String identifier) {
        GlobalZone zone;
        if ((zone = globalZoneMap.remove(identifier)) != null) {
            zone.unsave(globalZoneCol);
        }
    }

    public static List<GlobalZone> getZones(Point point) {
        List<GlobalZone> zones = new ArrayList<>();
        for (GlobalZone zone : globalZoneMap.values()) {
            if (!zone.getIdentifier().equals("wild")) {
                for (Dimension border : zone.getBorders()) {
                    if (border.isContained(point)) {
                        zones.add(zone);
                        break;
                    }
                }
            } else {
                zones.add(zone);
            }
        }
        return zones;
    }

    public static GlobalZone getZone(Point point) {
        for (GlobalZone zone : globalZoneMap.values()) {
            if (!zone.getName().equals("wild")) {
                for (Dimension border : zone.getBorders()) {
                    if (border.isContained(point)) {
                        return zone;
                    }
                }
            }
        }
        return WILDERNESS;
    }

    public static boolean isNotWild(Point point) {
        for (GlobalZone zone : globalZoneMap.values()) {
            if (!zone.getName().equals("wild")) {
                for (Dimension border : zone.getBorders()) {
                    if (border.isContained(point)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void freezePlayer(CorePlayer cp, GlobalZone zone) {
        unfrozenPlayers.remove(cp.getUniqueId());
        cp.setGlobalZone(zone);
    }

    public static void unfreezePlayer(CorePlayer cp) {
        unfrozenPlayers.put(cp.getUniqueId(), cp);
    }

    public static int removeLeaves(Point pos, double distance) {
        int removed = 0;
        for (GlobalZone zone : globalZoneMap.values()) {
            removed += zone.removeLeaf(pos, distance);
        }
        for (Entity entity : Core.DEFAULT_WORLD.getEntities()) {
            if (((CraftEntity) entity).getHandle() instanceof ZoneLeafEntity &&
                    entity.getLocation().distance(pos.toVector().toLocation(Core.DEFAULT_WORLD)) < distance - 0.1) {
                entity.remove();
            }
        }
        return removed;
    }

    public static GlobalZone getZone(String identifier) {
        return globalZoneMap.getOrDefault(identifier, WILDERNESS);
    }

    public static void saveZone(GlobalZone zone) {
        zone.save(globalZoneCol);
    }

    public static UUID dropLeaf(String zoneName, ZoneLeaf leaf) {
        ZoneLeafEntity leafEntity = new ZoneLeafEntity(((CraftWorld) Core.DEFAULT_WORLD).getHandle(), leaf, zoneName);
        ((CraftWorld) Core.DEFAULT_WORLD).getHandle().addEntity(
                leafEntity,
                CreatureSpawnEvent.SpawnReason.CUSTOM);
        globalLeafUuids.add(leafEntity.getUniqueID());
        return leafEntity.getUniqueID();
    }

}
