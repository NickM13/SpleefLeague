package com.spleefleague.core.world;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.MathUtils;
import com.spleefleague.core.util.variable.Position;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author NickM13
 * @since 4/26/2020
 */
public class FakeUtils {

    public static boolean isInstantBreak(CorePlayer cp, Material material) {
        return true;
    }

    public static boolean isOnGround(CorePlayer cp) {
        if (cp.getPlayer().isOnGround()) return true;
        if (cp.getPlayer().getVelocity().getY() > 0) return false;
        Set<BlockPosition> blockPositions = new HashSet<>();
        BoundingBox bb = cp.getPlayer().getBoundingBox();
        blockPositions.add(new BlockPosition(
                (int) Math.floor(bb.getMinX()),
                (int) Math.floor(bb.getMinY() - 0.85),
                (int) Math.floor(bb.getMinZ())));
        blockPositions.add(new BlockPosition(
                (int) Math.floor(bb.getMaxX()),
                (int) Math.floor(bb.getMinY() - 0.85),
                (int) Math.floor(bb.getMinZ())));
        blockPositions.add(new BlockPosition(
                (int) Math.floor(bb.getMaxX()),
                (int) Math.floor(bb.getMinY() - 0.85),
                (int) Math.floor(bb.getMaxZ())));
        blockPositions.add(new BlockPosition(
                (int) Math.floor(bb.getMinX()),
                (int) Math.floor(bb.getMinY() - 0.85),
                (int) Math.floor(bb.getMaxZ())));
        Iterator<FakeWorld<?>> fit = cp.getFakeWorlds();
        while (fit.hasNext()) {
            FakeWorld<?> fakeWorld = fit.next();
            Iterator<BlockPosition> posit = blockPositions.iterator();
            while (posit.hasNext()) {
                FakeBlock fb = fakeWorld.getFakeBlocks().get(posit.next());
                if (fb != null) {
                    if (fb.getBlockData().getMaterial().isBlock()) {
                        return true;
                    } else {
                        posit.remove();
                    }
                }
            }
        }
        return false;
    }

    public static BlockPosition getHighestFakeBlockBelow(CorePlayer cp) {
        BlockPosition pos = new BlockPosition(cp.getLocation().getBlockX(), cp.getLocation().getBlockY() - 1, cp.getLocation().getBlockZ());
        while (pos.getY() >= 0) {
            Iterator<FakeWorld<?>> fit = cp.getFakeWorlds();
            while (fit.hasNext()) {
                FakeWorld<?> fakeWorld = fit.next();
                FakeBlock fb = fakeWorld.getFakeBlocks().get(pos);
                if (fb != null && !fb.getBlockData().getMaterial().isAir()) {
                    return pos;
                }
            }
            pos = pos.subtract(new BlockPosition(0, 1, 0));
        }
        return pos;
    }

    public static BlockPosition getHighestBlockBelow(CorePlayer cp) {
        BlockPosition pos = new BlockPosition(cp.getLocation().getBlockX(), cp.getLocation().getBlockY() - 1, cp.getLocation().getBlockZ());
        while (pos.getY() >= 0) {
            if (!cp.getPlayer().getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()).getType().isAir()) {
                return pos;
            }
            Iterator<FakeWorld<?>> fit = cp.getFakeWorlds();
            while (fit.hasNext()) {
                FakeWorld<?> fakeWorld = fit.next();
                FakeBlock fb = fakeWorld.getFakeBlocks().get(pos);
                if (fb != null && !fb.getBlockData().getMaterial().isAir()) {
                    return pos;
                }
            }
            pos = pos.subtract(new BlockPosition(0, 1, 0));
        }
        return pos;
    }

    public static Map<BlockPosition, FakeBlock> mergeBlocks(List<Map<BlockPosition, FakeBlock>> blockMaps) {
        Map<BlockPosition, FakeBlock> mergedBlocks = new HashMap<>();
        for (Map<BlockPosition, FakeBlock> blockMap : blockMaps) {
            for (Map.Entry<BlockPosition, FakeBlock> entry : blockMap.entrySet()) {
                mergedBlocks.put(entry.getKey(), entry.getValue());
            }
        }
        return mergedBlocks;
    }

    public static Set<BlockPosition> translateBlocks(Set<BlockPosition> blocks, BlockPosition translation) {
        Set<BlockPosition> transformedBlocks = new HashSet<>();
        for (BlockPosition pos : blocks) {
            transformedBlocks.add(pos.add(translation));
        }
        return transformedBlocks;
    }

    public static Map<BlockPosition, FakeBlock> translateBlocks(Map<BlockPosition, FakeBlock> blocks, BlockPosition translation) {
        Map<BlockPosition, FakeBlock> transformedBlocks = new HashMap<>();
        for (Map.Entry<BlockPosition, FakeBlock> entry : blocks.entrySet()) {
            transformedBlocks.put(entry.getKey().add(translation), entry.getValue());
        }
        return transformedBlocks;
    }

    public static Map<BlockPosition, FakeBlock> rotateBlocks(Map<BlockPosition, FakeBlock> blocks, int degrees) {
        double radians = Math.toRadians(degrees);
        Map<BlockPosition, FakeBlock> transformedBlocks = new HashMap<>();
        for (Map.Entry<BlockPosition, FakeBlock> entry : blocks.entrySet()) {
            BlockPosition newPos = new BlockPosition(
                    (int) (entry.getKey().getX() * MathUtils.cos(radians, 4) - entry.getKey().getZ() * MathUtils.sin(radians, 4)),
                    entry.getKey().getY(),
                    (int) (entry.getKey().getX() * MathUtils.sin(radians, 4) + entry.getKey().getZ() * MathUtils.cos(radians, 4)));
            transformedBlocks.put(newPos, entry.getValue());
        }
        return transformedBlocks;
    }

    public static Map<BlockPosition, FakeBlock> transformBlocks(Map<BlockPosition, FakeBlock> blocks, Position transform) {
        return FakeUtils.translateBlocks(FakeUtils.rotateBlocks(blocks, (int) transform.getYaw()), transform.toBlockPosition());
    }

    public static Set<BlockPosition> createCylinderShell(double radius, int height) {
        Set<BlockPosition> blocks = new HashSet<>();
        int lastMove1 = -1;
        int lastMove2 = -1;
        int prevX = -1;
        int prevZ = -1;
        for (double d = 0; d < Math.PI / 2D; d += Math.PI / 90) {
            int x = (int) (Math.cos(d) * radius + 0.25);
            int z = (int) (Math.sin(d) * radius + 0.25);
            if (x == prevX && z == prevZ) continue;
            if ((x != prevX && (lastMove1 != 0 || lastMove2 == 0)) || (z != prevZ && (lastMove1 != 1 || lastMove2 == 1))) {
                for (int i = 0; i < 4; i++) {
                    Vector vec = new Vector(Math.cos(d + i * Math.PI / 2D), 0, Math.sin(d + i * Math.PI / 2D)).multiply(radius).add(new Vector(0.5, 0, 0.5));
                    for (int h = 0; h < height; h++) {
                        blocks.add(new BlockPosition(vec.getBlockX(), h, vec.getBlockZ()));
                    }
                }
            }
            lastMove2 = lastMove1;
            if (prevX != x) {
                lastMove1 = 0;
            } else {
                lastMove1 = 1;
            }
            prevX = x;
            prevZ = z;
        }
        return blocks;
    }

    public static Set<BlockPosition> createSphere(double radius) {
        Set<BlockPosition> blocks = new HashSet<>();
        double dx, dy, dz;
        for (int x = -(int) Math.ceil(radius); x <= (int) Math.ceil(radius); x++) {
            dx = ((double) x) / radius;
            for (int y = -(int) Math.ceil(radius); y <= (int) Math.ceil(radius); y++) {
                dy = ((double) y) / radius;
                for (int z = -(int) Math.ceil(radius); z <= (int) Math.ceil(radius); z++) {
                    dz = ((double) z) / radius;
                    if (Math.sqrt(dx * dx + dy * dy + dz * dz) < 1) {
                        blocks.add(new BlockPosition(x, y, z));
                    }
                }
            }
        }
        return blocks;
    }

    private static BoundingBox computeDiscAABB(Vector center, Vector normal, double radius) {
        Vector e1 = (new Vector(1., 1., 1.).subtract(normal.clone().multiply(normal.clone())));
        Vector e = new Vector(
                e1.getX() > 0.001 ? Math.sqrt(e1.getX()) : 0,
                e1.getY() > 0.001 ? Math.sqrt(e1.getY()) : 0,
                e1.getZ() > 0.001 ? Math.sqrt(e1.getZ()) : 0).multiply(radius);
        return new BoundingBox(
                center.getX() - e.getX(),
                center.getY() - e.getY(),
                center.getZ() - e.getZ(),
                center.getX() + e.getX(),
                center.getY() + e.getY(),
                center.getZ() + e.getZ());
    }

    public static Set<BlockPosition> createCone(Vector dir, double distance, double radius) {
        dir = dir.clone().normalize();
        Set<BlockPosition> blocks = new HashSet<>();

        BoundingBox discBoundingBox = computeDiscAABB(dir.clone().multiply(distance), dir.clone(), radius);
        BoundingBox coneBoundingBox = discBoundingBox.union(new Vector(0, 0, 0));

        for (int x = (int) Math.floor(coneBoundingBox.getMinX()); x <= (int) Math.ceil(coneBoundingBox.getMaxX()); x++) {
            for (int y = (int) Math.floor(coneBoundingBox.getMinY()); y <= (int) Math.ceil(coneBoundingBox.getMaxY()); y++) {
                for (int z = (int) Math.floor(coneBoundingBox.getMinZ()); z <= (int) Math.ceil(coneBoundingBox.getMaxZ()); z++) {
                    Vector p = new Vector(x, y, z);
                    double coneDist = p.clone().dot(dir);
                    if (coneDist >= 0 && coneDist <= distance) {
                        double orthoDist = (p.clone().subtract(dir.clone().multiply(coneDist))).length();
                        if (orthoDist < (coneDist / distance) * radius) {
                            blocks.add(new BlockPosition(x, y, z));
                        }
                    }
                }
            }
        }
        return blocks;
    }

    public static Set<BlockPosition> createConeFurthest(Vector facing, double distance, double radius) {
        Set<BlockPosition> blocks = new HashSet<>();
        double dx, dy, dz;
        for (int x = -(int) Math.ceil(radius); x <= (int) Math.ceil(radius); x++) {
            dx = ((double) x) / radius;
            for (int y = -(int) Math.ceil(radius); y <= (int) Math.ceil(radius); y++) {
                dy = ((double) y) / radius;
                for (int z = -(int) Math.ceil(radius); z <= (int) Math.ceil(radius); z++) {
                    dz = ((double) z) / radius;
                    if (Math.sqrt(dx * dx + dy * dy + dz * dz) < 1) {
                        Vector p = new Vector(x, y, z).normalize();
                        double dist = p.dot(facing);
                        if (dist >= (dist / distance) * radius) {
                            blocks.add(new BlockPosition(x, y, z));
                        }
                    }
                }
            }
        }
        return blocks;
    }

    private static final AtomicInteger nextFakeId = new AtomicInteger(500000000);

    public static int getNextId() {
        return nextFakeId.getAndIncrement();
    }

    private static final ProtocolManager protocolManager = Core.getProtocolManager();
    private static final UUID unused = UUID.randomUUID();
    private static final EntityArmorStand armorStand = new EntityArmorStand(((CraftWorld) Core.DEFAULT_WORLD).getHandle(), 0, 0, 0);
    private static final DataWatcher dataWatcher;

    static {
        armorStand.setInvisible(true);
        armorStand.setSmall(true);

        dataWatcher = armorStand.getDataWatcher();
    }

    public static void sendArmorStandSpawn(Player player, int entityId, double x, double y, double z, ItemStack item) {
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

    public static void sendEntityMove(Player player, int entityId, short changeX, short changeY, short changeZ) {
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

    public static void sendEntityDestroy(Player player, int entityId) {
        try {
            protocolManager.sendServerPacket(player, new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY, new PacketPlayOutEntityDestroy(
                    entityId
            )), null, false);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
