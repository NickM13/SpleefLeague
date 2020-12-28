package com.spleefleague.core.world.global.zone;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.Dimension;
import com.spleefleague.core.util.variable.Point;
import com.spleefleague.core.util.variable.Position;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.annotation.DBLoad;
import com.spleefleague.coreapi.database.annotation.DBSave;
import com.spleefleague.coreapi.database.variable.DBEntity;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * @author NickM13
 * @since 5/8/2020
 */
public class GlobalZone extends DBEntity {

    private static GlobalZone WILDERNESS;
    private static final Map<String, GlobalZone> globalZoneMap = new HashMap<>();
    private static final Set<String> globalLeafNames = new HashSet<>();
    private static MongoCollection<Document> globalZoneCol;

    private static final Set<UUID> editingPlayers = new HashSet<>();
    private static final Map<String, UUID> globalLeafUuids = new HashMap<>();

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
                    if (cp.getCollectibles().hasLeaf(zoneLeaf.getFullName())) {
                        event.setCancelled(true);
                    }
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
                }
                for (GlobalZone zone : globalZoneMap.values()) {
                    for (ZoneLeaf leaf : zone.getLeaves()) {
                        cp.getPlayer().spawnParticle(Particle.SWEEP_ATTACK, leaf.getPos().getX() + 0.5, leaf.getPos().getY() + 0.5, leaf.getPos().getZ() + 0.5, 1, 0, 0, 0);
                    }
                }
            }
            for (CorePlayer cp : Core.getInstance().getPlayers().getOnline()) {
                cp.setGlobalZone(GlobalZone.getZone(new Point(cp.getLocation())));
            }
        }, 20L, 20L);

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
                .setAction(CorePlayer::refreshHotbar);
    }

    public static void close() {
        for (UUID uuid : globalLeafUuids.values()) {
            Objects.requireNonNull(Bukkit.getEntity(uuid)).remove();
        }
    }

    public static Set<String> getLeafNames() {
        return globalLeafNames;
    }

    public static boolean removeLeafGlobal(String identifier) {
        if (globalLeafNames.contains(identifier)) {
            UUID uuid = globalLeafUuids.remove(identifier);
            if (uuid != null) {
                Entity entity = Bukkit.getEntity(uuid);
                if (entity != null) entity.remove();
            }
            String[] args = identifier.split(":", 2);
            globalZoneMap.get(args[0]).removeLeaf(args[1]);
            globalLeafNames.remove(identifier);
            return true;
        }
        return false;
    }
    
    public static ZoneLeaf getLeafGlobal(String identifier) {
        if (globalLeafNames.contains(identifier)) {
            String[] args = identifier.split(":", 2);
            return globalZoneMap.get(args[0]).getLeaf(args[1]);
        }
        return null;
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
        globalLeafUuids.put(leafEntity.getFullName(), leafEntity.getUniqueID());
        return leafEntity.getUniqueID();
    }

    @DBField private String name;
    @DBField private String description = "";
    private final Set<Dimension> borders = new HashSet<>();
    private final List<ZoneLeaf> leaves = new ArrayList<>();
    private final Map<Position, Integer> leafPosHash = new HashMap<>();
    private final Set<UUID> leafUuids = new HashSet<>();
    private final Set<CorePlayer> players = new HashSet<>();

    public GlobalZone() {

    }

    public GlobalZone(String identifier, String name) {
        this.identifier = identifier;
        this.name = Chat.colorize(name);
    }

    @Override
    public void afterLoad() {
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
            for (ZoneLeaf leaf : leaves) {
                leafUuids.add(dropLeaf(this.identifier, leaf));
                globalLeafNames.add(this.identifier + ":" + leaf.getName());
            }
        }, 20L);
    }

    @DBSave(fieldName="borders")
    private List<Document> saveBorders() {
        return borders
                .stream()
                .map(Dimension::save)
                .collect(Collectors.toList());
    }

    @DBLoad(fieldName ="borders")
    private void loadBorders(List<Document> docs) {
        docs.forEach(doc -> borders.add(new Dimension(doc)));
    }

    @DBSave(fieldName = "leaves")
    private List<Document> saveLeaves() {
        List<Document> list = new ArrayList<>();
        for (ZoneLeaf leaf : leaves) {
            list.add(leaf.save());
        }
        return list;
    }

    @DBLoad(fieldName = "leaves")
    private void loadLeaves(List<Document> list) {
        for (Document doc : list) {
            ZoneLeaf leaf = new ZoneLeaf(doc);
            leaves.add(leaf);
            leafPosHash.put(leaf.getPos(), leaves.size() - 1);
        }
    }

    public boolean isWild() {
        return identifier.equals("wild");
    }

    public void setName(String name) {
        this.name = Chat.colorize(name);
        saveZone(this);
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
        saveZone(this);
    }

    public String getDescription() {
        return description;
    }

    public boolean addBorder(Dimension border) {
        if (this.borders.add(border)) {
            saveZone(this);
            return true;
        }
        return false;
    }

    public boolean removeBorder(Dimension border) {
        if (this.borders.remove(border)) {
            saveZone(this);
            return true;
        }
        return false;
    }

    public Set<Dimension> getBorders() {
        return borders;
    }

    public boolean isLeaf(Position pos) {
        return (leafPosHash.containsKey(pos));
    }

    public boolean removeLeaf(Position pos) {
        if (leafPosHash.containsKey(pos)) {
            ZoneLeaf leaf = leaves.remove((int) leafPosHash.get(pos));
            for (int i = leafPosHash.get(pos); i < leaves.size(); i++) {
                leafPosHash.put(leaves.get(i).getPos(), i);
            }
            leafPosHash.remove(pos);
            globalLeafNames.remove(this.identifier + ":" + leaf.getName());
            saveZone(this);
            return true;
        }
        return false;
    }

    public boolean removeLeaf(String identifier) {
        for (int i = 0; i < leaves.size(); i++) {
            ZoneLeaf leaf = leaves.get(i);
            if (leaf.getName().equalsIgnoreCase(identifier)) {
                globalLeafNames.remove(this.identifier + ":" + leaf.getName());
                leafPosHash.remove(leaf.getPos());
                leaves.remove(i);
                saveZone(this);
                return true;
            }
        }
        return false;
    }
    
    public ZoneLeaf getLeaf(String identifier) {
        for (ZoneLeaf leaf : leaves) {
            if (leaf.getName().equalsIgnoreCase(identifier)) {
                return leaf;
            }
        }
        return null;
    }

    public boolean addLeaf(String identifier, Position pos) {
        if (!leafPosHash.containsKey(pos)) {
            ZoneLeaf leaf = new ZoneLeaf(identifier, pos);
            leaves.add(leaf);
            leafPosHash.put(pos, leaves.size() - 1);
            globalLeafNames.add(this.identifier + ":" + identifier);
            leafUuids.add(dropLeaf(this.identifier, leaf));
            saveZone(this);
            return true;
        }
        return false;
    }

    public void clearLeaves(CorePlayer cp) {
        Set<UUID> oldUuids = new HashSet<>(leafUuids);
        leafUuids.clear();
        for (UUID uuid : oldUuids) {
            Entity entity = Bukkit.getEntity(uuid);
            if (entity != null) {
                entity.remove();
            }
        }
        for (ZoneLeaf leaf : leaves) {
            leafUuids.add(dropLeaf(this.identifier, leaf));
        }
    }

    public List<ZoneLeaf> getLeaves() {
        return leaves;
    }

    public void addPlayer(CorePlayer cp) {
        players.add(cp);
    }

    public void removePlayer(CorePlayer cp) {
        players.remove(cp);
    }

    public Set<CorePlayer> getPlayers() {
        return players;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof GlobalZone) {
            return ((GlobalZone) o).getIdentifier().equalsIgnoreCase(getIdentifier());
        }
        return false;
    }

}
