package com.spleefleague.coreapi.utils.packet.bungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.spleefleague.coreapi.utils.packet.PacketType;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author NickM13
 * @since 1/31/2021
 */
public class PacketBungeeBundleOut {

    public List<byte[]> packetData = new ArrayList<>();

    public PacketBungeeBundleOut() {

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
