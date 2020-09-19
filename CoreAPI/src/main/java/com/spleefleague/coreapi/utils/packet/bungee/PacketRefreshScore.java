package com.spleefleague.coreapi.utils.packet.bungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.spleefleague.coreapi.utils.packet.Packet;
import com.spleefleague.coreapi.utils.packet.PacketBungee;
import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.RatedPlayerInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketRefreshScore extends PacketBungee {

    public String mode;
    public int season;
    public List<RatedPlayerInfo> players;

    public PacketRefreshScore() { }

    public PacketRefreshScore(String mode, int season, List<RatedPlayerInfo> players) {
        this.mode = mode;
        this.season = season;
        this.players = players;
    }

    @Override
    public int getTag() {
        return PacketType.Bungee.REFRESH_SCORE.ordinal();
    }

    @Override
    public void fromByteArray(ByteArrayDataInput input) {
        mode = input.readUTF();
        season = input.readInt();
        int playerCount = input.readInt();
        players = new ArrayList<>();
        for (int i = 0; i < playerCount; i++) {
            players.add(new RatedPlayerInfo(input));
        }
    }

    @Override
    protected void toByteArray(ByteArrayDataOutput output) {
        output.writeUTF(mode);
        output.writeInt(season);
        output.writeInt(players.size());
        for (RatedPlayerInfo rpi : players) {
            rpi.toOutput(output);
        }
    }

}
