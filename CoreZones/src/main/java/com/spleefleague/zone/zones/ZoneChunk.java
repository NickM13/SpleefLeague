package com.spleefleague.zone.zones;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.spleefleague.coreapi.database.variable.DBVariable;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * @author NickM13
 * @since 2/12/2021
 */

public class ZoneChunk extends DBVariable<Document> {

    final long[] data;
    final int chunkX, chunkZ;
    boolean modified;

    public ZoneChunk(Document doc) {
        this(doc.getInteger("chunkX"), doc.getInteger("chunkZ"));
        load(doc);
    }

    public ZoneChunk(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.data = new long[128];
    }

    @Override
    public void load(Document doc) {
        int lower = doc.getInteger("lower");
        List<Long> dataList = doc.getList("data", Long.class);
        int i = lower;
        for (Long d : dataList) {
            data[i] = d;
            i++;
        }
    }

    @Override
    public Document save() {
        int lower, upper;
        for (lower = 0; lower < 128; lower++) {
            if (data[lower] != 0) break;
        }
        for (upper = 127; upper >= 0; upper--) {
            if (data[upper] != 0) break;
        }
        Document doc = new Document("chunkX", chunkX).append("chunkZ", chunkZ).append("lower", lower);
        List<Long> dataList = new ArrayList<>();
        for (int i = lower; i < upper + 1; i++) {
            dataList.add(data[i]);
        }
        doc.append("data", dataList);
        return doc;
    }

    public byte[] toPacket() {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();

        output.writeInt(chunkX);
        output.writeInt(chunkZ);
        for (long d : data) {
            output.writeLong(d);
        }

        return output.toByteArray();
    }

    public boolean isEmpty() {
        for (int i = 0; i < 128; i++) {
            if (data[i] != 0) return false;
        }
        return true;
    }

    /**
     * Checks if y is within vertical bounds, and if bit h is on
     *
     * @param h horizontal bit ((x / 2) << 3 + (z / 2))
     * @param y vertical layer
     * @return Flag state
     */
    public boolean isContained(int h, int y) {
        return (data[y] & (1L << h)) != 0;
    }

    /**
     * bit 1 for maintain presence, bit 0 for remove bit
     *
     * @param y
     * @param data
     */
    public void unset(int y, long data) {
        this.data[y] &= data;
    }

    public void set(int y, long data) {
        this.data[y] |= data;
    }

    public void setModified() {
        modified = true;
    }

    public boolean onModified() {
        if (modified) {
            modified = false;
            return true;
        }
        return false;
    }

}
