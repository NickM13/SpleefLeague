package com.spleefleague.zone.fragments;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.NetworkMarker;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.collect.Sets;
import com.spleefleague.core.Core;
import com.spleefleague.coreapi.database.variable.DBVariable;
import net.minecraft.server.v1_15_R1.*;
import org.bson.Document;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author NickM13
 * @since 2/13/2021
 */
public class FragmentChunk extends DBVariable<Document> {

    private final Map<Short, Fragment> fragments = new HashMap<>();
    private final Set<Integer> fragmentIds = new HashSet<>();
    private PacketContainer destroyFragmentsPacket;

    private final Set<Player> viewers = new HashSet<>();

    final short chunkX, chunkZ;
    final long chunkShifted;

    final double offsetX, offsetZ;

    public FragmentChunk(Document doc) {
        this(doc.getInteger("chunkX").shortValue(), doc.getInteger("chunkZ").shortValue());
        load(doc);
    }

    public FragmentChunk(short chunkX, short chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.chunkShifted = ((long) chunkX << 48L) + ((long) chunkZ << 32L);

        this.offsetX = chunkX * 16 + 0.5;
        this.offsetZ = chunkZ * 16 + 0.5;
    }

    public void setItems(ItemStack uncollectedItem, ItemStack collectedItem) {
        this.uncollectedItem = uncollectedItem;
        this.collectedItem = collectedItem;
    }

    private void initIdArray() {
        int[] fragmentIdArray = new int[fragmentIds.size()];
        int i = 0;
        for (int id : fragmentIds) {
            fragmentIdArray[i] = id;
            i++;
        }
        destroyFragmentsPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY, new PacketPlayOutEntityDestroy(fragmentIdArray));
    }

    public boolean isEmpty() {
        return fragments.isEmpty();
    }

    @Override
    public void load(Document document) {
        for (int pos : document.getList("data", Integer.class)) {
            Fragment fragment = new Fragment(chunkShifted, (short) pos);
            fragments.put((short) pos, fragment);
            fragmentIds.add(fragment.entityId);
        }
        initIdArray();
    }

    @Override
    public Document save() {
        Document doc = new Document("chunkX", chunkX).append("chunkZ", chunkZ);
        doc.append("data", fragments.keySet());
        return doc;
    }

    public boolean add(short pos) {
        if (!fragments.containsKey(pos)) {
            Fragment fragment = new Fragment(chunkShifted, pos);
            fragments.put(pos, fragment);
            fragmentIds.add(fragment.entityId);
            initIdArray();

            for (Player p : viewers) {
                sendSpawnPacket(p, fragment);
            }
            return true;
        }
        return false;
    }

    public boolean remove(short pos) {
        Fragment fragment = fragments.remove(pos);
        if (fragment == null) return false;
        fragmentIds.remove(fragment.entityId);
        initIdArray();

        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        packet.getIntegerArrays().write(0, new int[]{fragment.entityId});

        for (Player p : viewers) {
            try {
                protocolManager.sendServerPacket(p, packet, null, false);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public Fragment checkContained(short pos) {
        return fragments.get(pos);
    }

    public Collection<Fragment> getFragments() {
        return fragments.values();
    }

    public void addViewer(Set<Long> collected, Player player) {
        viewers.add(player);
        sendSpawnPackets(collected, player);
    }

    public void removeViewer(Player player) {
        viewers.remove(player);
        sendDestroyPacket(player);
    }

    public void sendUpdatePackets(byte rotation) {
        for (Fragment fragment : fragments.values()) {
            PacketContainer packetLook = new PacketContainer(PacketType.Play.Server.ENTITY_LOOK);
            packetLook.getIntegers().write(0, fragment.entityId);
            packetLook.getBytes().write(0, rotation);
            packetLook.getBooleans().write(0, true);

            for (Player p : viewers) {
                try {
                    protocolManager.sendServerPacket(p, packetLook, null, false);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static final ProtocolManager protocolManager = Core.getProtocolManager();

    private ItemStack uncollectedItem;
    private ItemStack collectedItem;

    public void sendSpawnPackets(Set<Long> collected, Player player) {
        for (Fragment fragment : fragments.values()) {
            if (!collected.contains(fragment.fullId)) {
                sendSpawnPacket(player, fragment);
            }
        }
    }

    private void sendSpawnPacket(Player player, Fragment fragment) {
        FragmentUtils.sendSpawnPacket(player,
                fragment.entityId,
                fragment.x + offsetX,
                fragment.y,
                fragment.z + offsetZ,
                uncollectedItem);
    }

    public void sendDestroyPacket(Player player) {
        try {
            protocolManager.sendServerPacket(player, destroyFragmentsPacket, null, false);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
