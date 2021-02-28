package com.spleefleague.core.world.build;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import com.google.common.primitives.UnsignedBytes;
import com.spleefleague.core.util.variable.Position;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.annotation.DBLoad;
import com.spleefleague.coreapi.database.annotation.DBSave;
import com.spleefleague.coreapi.database.variable.DBEntity;
import com.spleefleague.coreapi.utils.BinaryUtils;
import net.minecraft.server.v1_15_R1.Block;
import net.minecraft.server.v1_15_R1.IBlockData;
import net.minecraft.server.v1_15_R1.RegistryBlockID;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_15_R1.block.data.CraftBlockData;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * BuildStructure is a collection of fake blocks that can be
 * saved to a database and added to a FakeWorld
 *
 * @author NickM13
 * @since 4/26/2020
 */
public class BuildStructure extends DBEntity {

    private static final int STRUCTURES_VERSION = 3;
    private static final RegistryBlockID<IBlockData> REGISTRY = Block.REGISTRY_ID;

    @DBField
    private String name;
    private final Map<BlockPosition, FakeBlock> fakeBlocks = new HashMap<>();
    @DBField
    private Integer version;
    private BuildWorld constructor = null;
    private BlockPosition low, high;

    public BuildStructure() {

    }

    public BuildStructure(String name, BlockPosition originPos) {
        this.name = name;
        this.low = new BlockPosition(0, 0, 0);
        this.high = new BlockPosition(0, 0, 0);
        this.version = STRUCTURES_VERSION;
    }

    @DBSave(fieldName = "blocks")
    protected Document saveFakeBlocks() {
        Map<BlockData, Short> blockToIndex = new HashMap<>();
        List<Integer> palette = new ArrayList<>();
        palette.add(REGISTRY.getId(((CraftBlockData) Material.AIR.createBlockData()).getState()));
        List<Long> blocks = new ArrayList<>();
        for (Map.Entry<BlockPosition, FakeBlock> entry : fakeBlocks.entrySet()) {
            BlockData blockData = entry.getValue().getBlockData();
            if (!blockToIndex.containsKey(blockData)) {
                IBlockData iBlockData = ((CraftBlockData) blockData).getState();
                if (iBlockData.isAir()) {
                    blockToIndex.put(blockData, (short) 0);
                } else {
                    blockToIndex.put(blockData, (short) palette.size());
                    palette.add(REGISTRY.getId(iBlockData));
                }
            }
            byte[] bData = Shorts.toByteArray(blockToIndex.get(blockData));
            byte[] bX = Shorts.toByteArray((short) entry.getKey().getX());
            byte[] bY = Shorts.toByteArray((short) entry.getKey().getY());
            byte[] bZ = Shorts.toByteArray((short) entry.getKey().getZ());
            blocks.add(Longs.fromByteArray(new byte[]{bData[0], bData[1], bX[0], bX[1], bY[0], bY[1], bZ[0], bZ[1]}));
        }

        Document doc = new Document();

        doc.append("palette", palette);
        doc.append("blocks", blocks);

        return doc;
    }

    @DBLoad(fieldName = "blocks")
    protected void loadFakeBlocks(Document doc) {
        if (version == null || version == 0) {
            List<String> paletteNames = doc.get("palette", List.class);
            if (paletteNames != null) {
                List<BlockData> palette = new ArrayList<>();
                for (String name : paletteNames) {
                    palette.add(Material.getMaterial(name).createBlockData());
                }
                List<List<Integer>> blocks = doc.get("blocks", List.class);
                if (blocks != null) {
                    BlockPosition pos;
                    BlockData blockData;
                    for (List<Integer> block : blocks) {
                        pos = new BlockPosition(block.get(0), block.get(1), block.get(2));
                        blockData = palette.get(block.get(3));
                        if (fakeBlocks.isEmpty()) {
                            low = new BlockPosition(pos.getX(), pos.getY(), pos.getZ());
                            high = new BlockPosition(pos.getX(), pos.getY(), pos.getZ());
                        } else {
                            low = new BlockPosition(
                                    Math.min(pos.getX(), low.getX()),
                                    Math.min(pos.getY(), low.getY()),
                                    Math.min(pos.getZ(), low.getZ()));
                            high = new BlockPosition(
                                    Math.max(pos.getX(), high.getX()),
                                    Math.max(pos.getY(), high.getY()),
                                    Math.max(pos.getZ(), high.getZ()));
                        }
                        fakeBlocks.put(pos, new FakeBlock(blockData));
                    }
                }
            }
        } else if (version == 1) {
            List<Integer> palette = doc.get("palette", List.class);
            if (palette != null) {
                Map<Byte, BlockData> idToBlock = new HashMap<>();
                for (byte i = 0; i < palette.size(); i++) {
                    idToBlock.put(i, CraftBlockData.fromData(REGISTRY.fromId(palette.get(i))));
                }
                List<Integer> blocks = doc.get("blocks", List.class);
                for (int block : blocks) {
                    byte[] bytes = Ints.toByteArray(block);
                    BlockData blockData = idToBlock.get(bytes[0]);
                    BlockPosition pos = new BlockPosition(bytes[1], bytes[2], bytes[3]);
                    if (fakeBlocks.isEmpty()) {
                        low = new BlockPosition(pos.getX(), pos.getY(), pos.getZ());
                        high = new BlockPosition(pos.getX(), pos.getY(), pos.getZ());
                    } else {
                        low = new BlockPosition(
                                Math.min(pos.getX(), low.getX()),
                                Math.min(pos.getY(), low.getY()),
                                Math.min(pos.getZ(), low.getZ()));
                        high = new BlockPosition(
                                Math.max(pos.getX(), high.getX()),
                                Math.max(pos.getY(), high.getY()),
                                Math.max(pos.getZ(), high.getZ()));
                    }
                    fakeBlocks.put(pos, new FakeBlock(blockData));
                }
            }
        } else if (version == 2) {
            List<Integer> palette = doc.get("palette", List.class);
            if (palette != null) {
                Map<Short, BlockData> idToBlock = new HashMap<>();
                for (short i = 0; i < palette.size(); i++) {
                    idToBlock.put(i, CraftBlockData.fromData(REGISTRY.fromId(palette.get(i))));
                }
                List<Long> blocks = doc.get("blocks", List.class);
                for (long block : blocks) {
                    byte[] bytes = Longs.toByteArray(block);
                    BlockData blockData = idToBlock.get(Shorts.fromBytes(bytes[0], bytes[1]));
                    BlockPosition pos = new BlockPosition(
                            Shorts.fromBytes(bytes[2], bytes[3]),
                            Shorts.fromBytes(bytes[4], bytes[5]),
                            Shorts.fromBytes(bytes[6], bytes[7]));
                    if (fakeBlocks.isEmpty()) {
                        low = new BlockPosition(pos.getX(), pos.getY(), pos.getZ());
                        high = new BlockPosition(pos.getX(), pos.getY(), pos.getZ());
                    } else {
                        low = new BlockPosition(
                                Math.min(pos.getX(), low.getX()),
                                Math.min(pos.getY(), low.getY()),
                                Math.min(pos.getZ(), low.getZ()));
                        high = new BlockPosition(
                                Math.max(pos.getX(), high.getX()),
                                Math.max(pos.getY(), high.getY()),
                                Math.max(pos.getZ(), high.getZ()));
                    }
                    fakeBlocks.put(pos, new FakeBlock(blockData));
                }
            }
        } else if (version == 3) {
            List<Integer> palette = doc.get("palette", List.class);
            if (palette != null) {
                Map<Short, BlockData> idToBlock = new HashMap<>();
                for (short i = 0; i < palette.size(); i++) {
                    idToBlock.put(i, CraftBlockData.fromData(REGISTRY.fromId(palette.get(i))));
                }
                List<Long> blocks = doc.get("blocks", List.class);
                for (long block : blocks) {
                    byte[] bytes = Longs.toByteArray(block);
                    BlockData blockData = idToBlock.get(Shorts.fromBytes(bytes[0], bytes[1]));
                    BlockPosition pos = new BlockPosition(
                            Shorts.fromBytes(bytes[2], bytes[3]),
                            Shorts.fromBytes(bytes[4], bytes[5]),
                            Shorts.fromBytes(bytes[6], bytes[7]));
                    if (fakeBlocks.isEmpty()) {
                        low = new BlockPosition(pos.getX(), pos.getY(), pos.getZ());
                        high = new BlockPosition(pos.getX(), pos.getY(), pos.getZ());
                    } else {
                        low = new BlockPosition(
                                Math.min(pos.getX(), low.getX()),
                                Math.min(pos.getY(), low.getY()),
                                Math.min(pos.getZ(), low.getZ()));
                        high = new BlockPosition(
                                Math.max(pos.getX(), high.getX()),
                                Math.max(pos.getY(), high.getY()),
                                Math.max(pos.getZ(), high.getZ()));
                    }
                    fakeBlocks.put(pos, new FakeBlock(blockData));
                }
            }
        }
        version = STRUCTURES_VERSION;
    }

    public String getName() {
        return name;
    }

    public boolean isUnderConstruction() {
        return constructor != null;
    }

    public void setUnderConstruction(BuildWorld buildWorld) {
        constructor = buildWorld;
    }

    public BuildWorld getConstructor() {
        return constructor;
    }

    public void shiftOrigin(BlockPosition shift) {
        Map<BlockPosition, FakeBlock> oldBlocks = Maps.newHashMap(fakeBlocks);
        fakeBlocks.clear();
        oldBlocks.forEach((pos, block) -> fakeBlocks.put(pos.add(shift), block));
    }

    public void setBlock(BlockPosition pos, FakeBlock fb) {
        fakeBlocks.put(pos, fb);
    }

    public Map<BlockPosition, FakeBlock> getFakeBlocks() {
        return fakeBlocks;
    }

    public BlockPosition getCenter() {
        return (high.subtract(low)).divide(2);
    }

}
