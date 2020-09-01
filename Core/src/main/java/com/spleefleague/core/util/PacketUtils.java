package com.spleefleague.core.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.MultiBlockChangeInfo;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.spleefleague.core.world.ChunkCoord;
import com.spleefleague.core.world.FakeBlock;
import net.minecraft.server.v1_16_R1.PacketPlayOutMultiBlockChange;
import net.minecraft.server.v1_16_R1.SectionPosition;

import java.util.Map;

/**
 * @author NickM13
 * @since 4/21/2020
 */
public class PacketUtils {
    
    public static PacketContainer createMultiBlockChangePacket(ChunkCoord chunkCoord, Map<Short, FakeBlock> fakeChunkBlocks) {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.MULTI_BLOCK_CHANGE);

        packetContainer.getChunkCoordIntPairs().write(0, chunkCoord.toChunkCoordIntPair());
        //packetContainer.getSectionPositions().writeSafely(0, new BlockPosition(chunkCoord.x, 0, chunkCoord.z));

        MultiBlockChangeInfo[] mbcia = new MultiBlockChangeInfo[fakeChunkBlocks.size()];
        int i = 0;
        for (Map.Entry<Short, FakeBlock> fakeBlock : fakeChunkBlocks.entrySet()) {
            mbcia[i] = new MultiBlockChangeInfo(fakeBlock.getKey(),
                    WrappedBlockData.createData(fakeBlock.getValue().getBlockData().getMaterial()),
                    chunkCoord.toChunkCoordIntPair());
            i++;
        }

        packetContainer.getMultiBlockChangeInfoArrays().write(0, mbcia);

        return packetContainer;
    }
    
}
