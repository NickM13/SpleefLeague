package com.spleefleague.coreapi.utils.packet.bungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.PacketUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author NickM13
 * @since 1/31/2021
 */
public class PacketBungeeBundleIn {

    public List<PacketBungee> packets = new ArrayList<>();

    public PacketBungeeBundleIn() {

    }

    public final void fromByteArray(byte[] bundleData) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        ByteArrayDataInput bundleInput =  ByteStreams.newDataInput(bundleData);
        int packetCount = bundleInput.readInt();
        for (int i = 0; i < packetCount; i++) {
            int dataLen = bundleInput.readInt();
            byte[] data = new byte[dataLen];
            bundleInput.readFully(data);
            ByteArrayDataInput packetInput = ByteStreams.newDataInput(data);

            int tagId = packetInput.readInt();
            PacketType.Bungee tag = PacketType.Bungee.values()[tagId];
            PacketBungee packet = tag.getClazz().getDeclaredConstructor().newInstance();
            packet.fromByteArray(packetInput);

            packets.add(packet);
        }
    }

}
