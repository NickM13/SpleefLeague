package com.spleefleague.zone.fragments;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.spleefleague.core.Core;
import com.spleefleague.zone.CoreZones;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.UUID;

/**
 * @author NickM13
 * @since 2/15/2021
 */
public class FragmentUtils {

    private static final ProtocolManager protocolManager = Core.getProtocolManager();

    private static final UUID unused = UUID.randomUUID();
    private ItemStack uncollectedItem;
    private ItemStack collectedItem;
    private static final EntityArmorStand armorStand = new EntityArmorStand(((CraftWorld) Core.OVERWORLD).getHandle(), 0, 0, 0);
    private static final DataWatcher dataWatcher;

    static {
        armorStand.setInvisible(true);
        armorStand.setSmall(true);

        dataWatcher = armorStand.getDataWatcher();
    }

    public static void sendSpawnPacket(Player player, int entityId, double x, double y, double z, ItemStack item) {
        try {
            PacketPlayOutSpawnEntity packetPlayOutSpawnEntity = new PacketPlayOutSpawnEntity(
                    entityId,
                    unused,
                    x,
                    y,
                    z,
                    0f, 0f,
                    EntityTypes.ARMOR_STAND,
                    0, Vec3D.a);
            protocolManager.sendServerPacket(player, new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY, packetPlayOutSpawnEntity), null, false);

            PacketPlayOutEntityMetadata packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(
                    entityId,
                    dataWatcher,
                    true);
            protocolManager.sendServerPacket(player, new PacketContainer(PacketType.Play.Server.ENTITY_METADATA, packetPlayOutEntityMetadata), null, false);

            PacketPlayOutEntityEquipment packetPlayOutEntityEquipment = new PacketPlayOutEntityEquipment(
                    entityId,
                    EnumItemSlot.HEAD,
                    item
            );
            protocolManager.sendServerPacket(player, new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT, packetPlayOutEntityEquipment), null, false);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void sendEntityMovePacket(Player player, int entityId, short changeX, short changeY, short changeZ) {
        try {
            protocolManager.sendServerPacket(player, new PacketContainer(PacketType.Play.Server.REL_ENTITY_MOVE, new PacketPlayOutEntity.PacketPlayOutRelEntityMove(
                    entityId,
                    changeX,
                    changeY,
                    changeZ,
                    true
            )), null, false);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void sendDestroyPacket(Player player, int entityId) {
        try {
            protocolManager.sendServerPacket(player, new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY, new PacketPlayOutEntityDestroy(
                    entityId
            )), null, false);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
