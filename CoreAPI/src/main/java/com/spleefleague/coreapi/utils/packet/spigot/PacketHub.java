package com.spleefleague.coreapi.utils.packet.spigot;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.coreapi.utils.packet.Packet;
import com.spleefleague.coreapi.utils.packet.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketHub extends PacketSpigot {

    public List<UUID> players;

    public PacketHub() { }

    public PacketHub(List<? extends DBPlayer> players) {
        this.players = new ArrayList<>();
        for (DBPlayer dbp : players) {
            this.players.add(dbp.getUniqueId());
        }
    }

    @Override
    public int getTag() {
        return PacketType.Spigot.HUB.ordinal();
    }

    @Override
    public void fromByteArray(ByteArrayDataInput input) {
        int playerCount = input.readInt();
        players = new ArrayList<>();
        for (int i = 0; i < playerCount; i++) {
            players.add(UUID.fromString(input.readUTF()));
        }
    }

    @Override
    protected void toByteArray(ByteArrayDataOutput output) {
        output.writeInt(players.size());
        for (UUID uuid : players) {
            output.writeUTF(uuid.toString());
        }
    }
}
