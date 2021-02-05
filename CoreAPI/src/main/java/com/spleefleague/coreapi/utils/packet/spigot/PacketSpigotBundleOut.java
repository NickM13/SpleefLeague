package com.spleefleague.coreapi.utils.packet.spigot;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.util.ArrayList;
import java.util.List;

/**
 * @author NickM13
 * @since 1/31/2021
 */
public class PacketSpigotBundleOut {

    public List<byte[]> packetData = new ArrayList<>();

    public PacketSpigotBundleOut() {

    }

    public void addPacket(byte[] data) {
        packetData.add(data);
    }


    public final byte[] toByteArray() {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();

        output.writeInt(packetData.size());
        for (byte[] data : packetData) {
            output.writeInt(data.length);
            output.write(data);
        }

        return output.toByteArray();
    }

}
