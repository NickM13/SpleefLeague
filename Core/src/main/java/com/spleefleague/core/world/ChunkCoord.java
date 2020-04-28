package com.spleefleague.core.world;


import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.ChunkCoordIntPair;

/**
 * Chunk Coordinates for finding which chunks have
 * fake blocks in them for quicker updating
 *
 * @author NickM13
 * @since 4/16/2020
 */
public class ChunkCoord {

    public static ChunkCoord fromBlockPos(BlockPosition pos) {
        return new ChunkCoord(pos.getX() >> 4, pos.getZ() >> 4);
    }

    public int x, z;

    public ChunkCoord(int x, int z) {
        this.x = x;
        this.z = z;
    }

    @Override
    public boolean equals(Object cc) {
        if (cc == this) return true;
        if (cc instanceof ChunkCoord) {
            return (((ChunkCoord)cc).x == x && ((ChunkCoord) cc).z == z);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.x;
        hash = 29 * hash + this.z;
        return hash;
    }
    
    @Override
    public String toString() {
        return x + "," + z;
    }
    
    public ChunkCoordIntPair toChunkCoordIntPair() {
        return new ChunkCoordIntPair(x, z);
    }
    
}
