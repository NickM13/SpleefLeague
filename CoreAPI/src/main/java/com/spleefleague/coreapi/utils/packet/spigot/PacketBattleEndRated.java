package com.spleefleague.coreapi.utils.packet.spigot;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.spleefleague.coreapi.utils.packet.Packet;
import com.spleefleague.coreapi.utils.packet.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.RatedPlayerInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketBattleEndRated extends PacketSpigot {

    public String mode;
    public int season;
    public List<RatedPlayerInfo> players;

    public PacketBattleEndRated() { }

    public PacketBattleEndRated(String mode, int season, List<RatedPlayerInfo> players) {
        this.mode = mode;
        this.season = season;
        this.players = players;
    }

    @Override
    public int getTag() {
        return PacketType.Spigot.BATTLE_END_RATED.ordinal();
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
