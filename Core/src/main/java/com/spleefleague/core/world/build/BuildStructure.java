package com.spleefleague.core.world.build;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.google.common.collect.Lists;
import com.spleefleague.core.util.variable.Position;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.annotation.DBLoad;
import com.spleefleague.coreapi.database.annotation.DBSave;
import com.spleefleague.coreapi.database.variable.DBEntity;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.util.*;

/**
 * BuildStructure is a collection of fake blocks that can be
 * saved to a database and added to a FakeWorld
 *
 * @author NickM13
 * @since 4/26/2020
 */
public class BuildStructure extends DBEntity {
    
    @DBField private String name;
    private final Map<BlockPosition, FakeBlock> fakeBlocks = new HashMap<>();
    @DBField private Position origin;
    private BuildWorld constructor = null;
    private BlockPosition low, high;

    public BuildStructure() {

    }

    public BuildStructure(String name, BlockPosition originPos) {
        this.name = name;
        this.origin = new Position(originPos.getX(), originPos.getY(), originPos.getZ());
        this.low = new BlockPosition(0, 0, 0);
        this.high = new BlockPosition(0, 0, 0);
    }

    @DBSave(fieldName = "blocks")
    protected Document saveFakeBlocks() {
        LinkedHashMap<String, Integer> palette = new LinkedHashMap<>();
        List<List<Integer>> blocks = new ArrayList<>();
        int i = 0;
        for (Map.Entry<BlockPosition, FakeBlock> entry : fakeBlocks.entrySet()) {
            Material mat = entry.getValue().getBlockData().getMaterial();
            if (!mat.isAir()) {
                if (!palette.containsKey(mat.toString())) {
                    palette.put(mat.toString(), i);
                    i++;
                }
                blocks.add(Lists.newArrayList(
                        entry.getKey().getX(),
                        entry.getKey().getY(),
                        entry.getKey().getZ(),
                        palette.get(mat.toString())));
            }
        }

        Document doc = new Document();

        doc.append("palette", palette.keySet());
        doc.append("blocks", blocks);

        return doc;
    }

    @DBLoad(fieldName = "blocks")
    protected void loadFakeBlocks(Document doc) {
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

    public void setBlock(BlockPosition pos, FakeBlock fb) {
        fakeBlocks.put(pos.subtract(getOriginPos()), fb);
    }

    public Map<BlockPosition, FakeBlock> getFakeBlocks() {
        return fakeBlocks;
    }
    
    public BlockPosition getOriginPos() {
        return origin.toBlockPosition();
    }
    
    public void setOriginPos(BlockPosition originPos) {
        this.origin = new Position(originPos.getX(), originPos.getY(), originPos.getZ());
    }

    public BlockPosition getCenter() {
        return (high.subtract(low)).divide(2);
    }

}
