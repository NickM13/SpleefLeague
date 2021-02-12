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
import javassist.util.proxy.ProxyObjectInputStream;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
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

    @DBField
    private String name;
    @DBField
    private String description = "";
    @DBField
    private Integer priority = 0;

    @DBField
    private Point drainPos = new Point();
    @DBField
    private String monumentPrefix = "";
    @DBField
    private Position monumentPos = new Position();

    private final Set<Dimension> borders = new HashSet<>();
    private final Map<String, ZoneLeaf> leafIds = new HashMap<>();
    private final List<ZoneLeaf> leaves = new ArrayList<>();
    private final Map<Point, Integer> leafPosHash = new HashMap<>();
    private final Set<UUID> leafUuids = new HashSet<>();
    private final Set<CorePlayer> players = new HashSet<>();
    private int nextLeafId = -1;

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
                leafUuids.add(GlobalZones.dropLeaf(this.identifier, leaf));
                nextLeafId = Math.max(leaf.getId(), nextLeafId);
            }
        }, 20L);
    }

    @DBSave(fieldName = "borders")
    private List<Document> saveBorders() {
        return borders
                .stream()
                .map(Dimension::save)
                .collect(Collectors.toList());
    }

    @DBLoad(fieldName = "borders")
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
            leafIds.put(String.valueOf(leaf.getId()), leaf);
            leafPosHash.put(leaf.getPos(), leaves.size() - 1);
        }
    }

    public Point getDrainPos() {
        return drainPos;
    }

    public void setDrainPos(Point drainPos) {
        this.drainPos = drainPos;
        GlobalZones.saveZone(this);
    }

    public boolean isOnDrain(CorePlayer cp) {
        return drainPos != null && drainPos.distance(new Point(cp.getLocation())) < 1;
    }

    public Position getMonumentPos() {
        return monumentPos;
    }

    public void setMonumentPos(Position monumentPos) {
        this.monumentPos = monumentPos;
        GlobalZones.saveZone(this);
    }

    public String getMonumentPrefix() {
        return monumentPrefix;
    }

    public void setMonumentPrefix(String monumentPrefix) {
        this.monumentPrefix = monumentPrefix;
        GlobalZones.saveZone(this);
    }

    public boolean isWild() {
        return identifier.equals("wild");
    }

    public void setName(String name) {
        this.name = Chat.colorize(name);
        GlobalZones.saveZone(this);
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
        GlobalZones.saveZone(this);
    }

    public String getDescription() {
        return description;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public boolean addBorder(Dimension border) {
        if (this.borders.add(border)) {
            GlobalZones.saveZone(this);
            return true;
        }
        return false;
    }

    public boolean removeBorder(Dimension border) {
        if (this.borders.remove(border)) {
            GlobalZones.saveZone(this);
            return true;
        }
        return false;
    }

    public Set<Dimension> getBorders() {
        return borders;
    }

    public boolean hasLeaf(Point pos) {
        return (leafPosHash.containsKey(pos));
    }

    public int removeLeaf(Point pos, double distance) {
        int removed = 0;
        for (int i = leaves.size() - 1; i >= 0; i--) {
            ZoneLeaf leaf = leaves.get(i);
            if (leaf.getPos().distance(pos) < distance) {
                leafPosHash.remove(leaf.getPos());
                leaves.remove(i);
                leafIds.remove(String.valueOf(i));
                removed++;
            }
        }
        if (removed > 0) {
            GlobalZones.saveZone(this);
        }
        return removed;
    }

    public ZoneLeaf addLeaf(Point pos) {
        pos = pos.rounded(2);
        if (hasLeaf(pos)) return null;

        ZoneLeaf leaf = new ZoneLeaf(++nextLeafId, pos);
        leaves.add(leaf);
        leafIds.put(String.valueOf(leaf.getId()), leaf);
        leafPosHash.put(pos, leaves.size() - 1);
        leafUuids.add(GlobalZones.dropLeaf(this.identifier, leaf));
        GlobalZones.saveZone(this);
        return leaf;
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
            leafUuids.add(GlobalZones.dropLeaf(this.identifier, leaf));
        }
    }

    public List<ZoneLeaf> getLeaves() {
        return leaves;
    }

    public Set<String> getLeafIds() {
        return leafIds.keySet();
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

    public ZoneLeaf getLeaf(String leafId) {
        return leafIds.get(leafId);
    }

}
