package com.spleefleague.core.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.MultiBlockChangeInfo;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.mojang.authlib.GameProfile;
import com.spleefleague.core.Core;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.packet.BlockPalette;
import com.spleefleague.core.util.packet.ByteBufferReader;
import com.spleefleague.core.util.packet.ChunkData;
import com.spleefleague.core.util.packet.ChunkSection;
import com.spleefleague.core.util.packet.ProtocolLongArrayBitReader;
import com.spleefleague.core.util.packet.ProtocolLongArrayBitWriter;
import com.spleefleague.core.world.ChunkCoord;
import com.spleefleague.core.world.FakeBlock;
import gnu.trove.list.array.TByteArrayList;
import net.minecraft.server.v1_16_R1.EnumGamemode;
import net.minecraft.server.v1_16_R1.IBlockData;
import net.minecraft.server.v1_16_R1.IChatBaseComponent;
import net.minecraft.server.v1_16_R1.PacketPlayOutPlayerInfo;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_16_R1.block.data.CraftBlockData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author NickM13 and Jonas
 * @since 4/21/2020
 */
public class PacketUtils {

    public static PacketContainer createAddPlayerPacket(List<CorePlayer> corePlayers) {
        try {
            PacketPlayOutPlayerInfo nmsPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
            Field playerListField = PacketPlayOutPlayerInfo.class.getDeclaredField("b");
            playerListField.setAccessible(true);
            List playerList = (List) playerListField.get(nmsPacket);
            for (CorePlayer cp : corePlayers) {
                playerList.add(PacketPlayOutPlayerInfo.class.getDeclaredClasses()[0].getDeclaredConstructor(PacketPlayOutPlayerInfo.class, GameProfile.class, int.class, EnumGamemode.class, IChatBaseComponent.class)
                        .newInstance(nmsPacket, new GameProfile(cp.getUniqueId(), cp.getName()), 1, EnumGamemode.ADVENTURE, IChatBaseComponent.ChatSerializer.a(WrappedChatComponent.fromText(cp.getDisplayName()).getJson())));
            }
            return new PacketContainer(PacketType.Play.Server.PLAYER_INFO, nmsPacket);
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException exception) {
            CoreLogger.logError(exception);
        }
        return null;
    }

    public static PacketContainer createRemovePlayerPacket(List<UUID> uuids) {
        try {
            PacketPlayOutPlayerInfo nmsPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
            Field playerListField = PacketPlayOutPlayerInfo.class.getDeclaredField("b");
            playerListField.setAccessible(true);
            List playerList = (List) playerListField.get(nmsPacket);
            for (UUID uuid : uuids) {
                playerList.add(PacketPlayOutPlayerInfo.class.getDeclaredClasses()[0].getDeclaredConstructor(PacketPlayOutPlayerInfo.class, GameProfile.class, int.class, EnumGamemode.class, IChatBaseComponent.class)
                        .newInstance(nmsPacket, new GameProfile(uuid, null), 1, EnumGamemode.ADVENTURE, IChatBaseComponent.ChatSerializer.a(WrappedChatComponent.fromText("").getJson())));
            }
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO, nmsPacket);
            Core.sendPacketAll(packet);
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException exception) {
            CoreLogger.logError(exception);
        }
        return null;
    }
    
    public static PacketContainer createMultiBlockChangePacket(ChunkCoord chunkCoord, Map<Short, FakeBlock> fakeChunkBlocks) {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.MULTI_BLOCK_CHANGE);

        packetContainer.getChunkCoordIntPairs().write(0, chunkCoord.toChunkCoordIntPair());

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

    public static void writeFakeChunkPacket(PacketContainer mapChunkPacket, Map<BlockPosition, FakeBlock> fakeBlocks) {
        Map<Integer, Map<BlockPosition, FakeBlock>> sectionMap = toSectionMap(fakeBlocks);
        if (sectionMap.isEmpty()) return;
        try {
            byte[] bytes = mapChunkPacket.getByteArrays().read(0);
            int bitmask = mapChunkPacket.getIntegers().read(2);
            int originalMask = bitmask;
            for (int i : sectionMap.keySet()) {
                bitmask |= 1 << i;
            }
            ChunkData chunkData = splitToChunkSections(bitmask, originalMask, bytes, true);
            insertFakeBlocks(chunkData.getSections(), sectionMap);

            byte[] data = toByteArray(chunkData);
            mapChunkPacket.getByteArrays().write(0, data);
            mapChunkPacket.getIntegers().write(2, bitmask);
        } catch (NullPointerException | IOException exception) {
            CoreLogger.logError(exception);
        }
    }

    private static byte[] toByteArray(ChunkData data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (ChunkSection section : data.getSections()) {
            if (section != null) {
                writeChunkSectionData(baos, section);
            }
        }
        baos.write(data.getAdditionalData());
        return baos.toByteArray();
    }

    private static void writeChunkSectionData(ByteArrayOutputStream baos, ChunkSection section) throws IOException {
        BlockData[] used = section.getContainedBlocks();
        BlockPalette palette;
        if (used == null) {
            palette = BlockPalette.GLOBAL;
        } else {
            palette = BlockPalette.createPalette(used);
        }
        short nonAirCount = section.getNonAirCount();
        byte bpb = (byte) palette.getBitsPerBlock();
        int paletteLength = palette.getLength();
        int[] paletteInfo;
        if (paletteLength == 0) {
            paletteInfo = new int[0];
        } else {
            paletteInfo = palette.getPaletteData();
        }
        baos.write((nonAirCount >> 8) & 0xFF);
        baos.write(nonAirCount & 0xFF);
        baos.write(bpb);
        if(palette.includePaletteLength()) {
            ByteBufferReader.writeVarIntToByteArrayOutputStream(paletteLength, baos);
        }
        for (int p : paletteInfo) {
            ByteBufferReader.writeVarIntToByteArrayOutputStream(p, baos);
        }
        byte[] blockdata = palette.encode(section.getBlockData());
        ByteBufferReader.writeVarIntToByteArrayOutputStream(blockdata.length / 8/*it's represented as a long array*/, baos);
        baos.write(blockdata);
    }

    private static void insertFakeBlocks(ChunkSection[] sections, Map<Integer, Map<BlockPosition, FakeBlock>> blocks) {
        for (Map.Entry<Integer, Map<BlockPosition, FakeBlock>> e : blocks.entrySet()) {
            int id = e.getKey();
            ChunkSection section = sections[id];
            for (Map.Entry<BlockPosition, FakeBlock> block : e.getValue().entrySet()) {
                int relX = block.getKey().getX() & 15; //Actual positive modulo, in java % means remainder. Only works as replacement for mod of powers of two
                int relZ = block.getKey().getZ() & 15; //Can be replaced with ((block.getZ() % 16) + 16) % 16
                boolean previouslyAir = section.getBlockRelative(relX, block.getKey().getY() % 16, relZ).getMaterial() == Material.AIR;
                section.setBlockRelative(block.getValue().getBlockData(), relX, block.getKey().getY() % 16, relZ);
                if(previouslyAir) {
                    if(block.getValue().getBlockData().getMaterial() != Material.AIR) {
                        section.setNonAirCount((short) (section.getNonAirCount() + 1));
                    }
                }
                else {
                    if(block.getValue().getBlockData().getMaterial() == Material.AIR) {
                        section.setNonAirCount((short) (section.getNonAirCount() - 1));
                    }
                }
            }
        }
    }

    private static ChunkData splitToChunkSections(int bitmask, int originalMask, byte[] data, boolean isOverworld) {
        ChunkSection[] sections = new ChunkSection[16];
        ByteBuffer buffer = ByteBuffer.wrap(data);
        ByteBufferReader bbr = new ByteBufferReader(buffer);
        for (int i = 0; i < 16; i++) {
            if ((bitmask & 0x8000 >> (15 - i)) != 0) {
                if ((originalMask & 0x8000 >> (15 - i)) != 0) {
                    short nonAirCount = buffer.getShort();
                    short bpb = (short) Byte.toUnsignedInt(buffer.get());
                    BlockPalette palette;
                    if(bpb <= 8) {
                        int paletteLength = bbr.readVarInt();
                        int[] paletteData = new int[paletteLength];
                        for (int j = 0; j < paletteLength; j++) {
                            paletteData[j] =
                                    bbr.readVarInt();
                        }
                        palette = BlockPalette.createPalette(paletteData, bpb);
                    } else {
                        palette = BlockPalette.GLOBAL;
                    }
                    int dataLength = bbr.readVarInt();
                    byte[] blockData = new byte[dataLength * 8];
                    buffer.get(blockData);
                    sections[i] = new ChunkSection(blockData, nonAirCount, palette);
                }
                else {
                    sections[i] = new ChunkSection(isOverworld);
                }
            }
        }

        byte[] additional = new byte[data.length - buffer.position()];
        buffer.get(additional);
        return new ChunkData(sections, additional);
    }

    private static Map<Integer, Map<BlockPosition, FakeBlock>> toSectionMap(Map<BlockPosition, FakeBlock> fakeBlocks) {
        Map<Integer, Map<BlockPosition, FakeBlock>> sectionMap = new HashMap<>();
        for (Map.Entry<BlockPosition, FakeBlock> fb : fakeBlocks.entrySet()) {
            int section = fb.getKey().getY() / 16;
            if (!sectionMap.containsKey(section)) {
                sectionMap.put(section, new HashMap<>());
            }
            sectionMap.get(section).put(fb.getKey(), fb.getValue());
        }
        return sectionMap;
    }

}
