package com.spleefleague.core.util.variable;

import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 * @since 5/16/2020
 */
public class BlockRaycastResult extends RaycastResult {

    private BlockPosition blockPos;
    private BlockPosition relative;
    private Integer axis;
    private BlockFace face;

    BlockRaycastResult(BlockPosition blockPos, Double distance, Vector intersection, Integer axis, BlockFace face) {
        super(distance, intersection);
        this.blockPos = blockPos;
        this.axis = axis;
        this.face = face;
        switch (face) {
            case UP:
                this.relative = blockPos.add(new BlockPosition(0, 1, 0));
                break;
            case DOWN:
                this.relative = blockPos.add(new BlockPosition(0, -1, 0));
                break;
            case EAST:
                this.relative = blockPos.add(new BlockPosition(1, 0, 0));
                break;
            case WEST:
                this.relative = blockPos.add(new BlockPosition(-1, 0, 0));
                break;
            case NORTH:
                this.relative = blockPos.add(new BlockPosition(0, 0, 1));
                break;
            case SOUTH:
                this.relative = blockPos.add(new BlockPosition(0, 0, -1));
                break;
        }
    }

    public BlockPosition getBlockPos() {
        return blockPos;
    }

    public BlockPosition getRelative() {
        return relative;
    }

    public Integer getAxis() {
        return axis;
    }

    public BlockFace getFace() {
        return face;
    }

}
