package com.spleefleague.core.util.variable;

import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.spleefleague.core.world.FakeBlock;
import org.bukkit.Material;

/**
 * @author NickM13
 * @since 2/21/2021
 */
public class MultiBlockChange {

    private static final FakeBlock AIR = new FakeBlock(Material.AIR.createBlockData());
    private static final WrappedBlockData WRAPPED_AIR = WrappedBlockData.createData(AIR.getBlockData());

    public final short pos;
    public final FakeBlock fakeBlock;
    public final WrappedBlockData blockData;
    public final boolean air;

    public MultiBlockChange(short pos, FakeBlock fakeBlock) {
        this.pos = pos;
        this.fakeBlock = fakeBlock;
        this.blockData = WrappedBlockData.createData(fakeBlock.getBlockData().getMaterial());
        this.air = false;
    }

    public MultiBlockChange(short pos) {
        this.pos = pos;
        this.fakeBlock = AIR;
        this.blockData = WRAPPED_AIR;
        this.air = true;
    }

}
