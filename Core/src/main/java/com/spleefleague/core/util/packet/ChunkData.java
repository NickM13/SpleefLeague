package com.spleefleague.core.util.packet;

/**
 * @author Jonas
 */
public class ChunkData {

    private final ChunkSection[] sections;
    private final byte[] additionalData;

    public ChunkData(ChunkSection[] sections, byte[] additionalData) {
        this.sections = sections;
        this.additionalData = additionalData;
    }

    public ChunkSection[] getSections() {
        return sections;
    }

    public byte[] getAdditionalData() {
        return additionalData;
    }
}
