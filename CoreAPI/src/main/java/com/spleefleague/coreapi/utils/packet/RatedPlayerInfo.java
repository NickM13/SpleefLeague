package com.spleefleague.coreapi.utils.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class RatedPlayerInfo {

    public UUID uuid;
    public int elo;

    public RatedPlayerInfo(UUID uuid, int elo) {
        this.uuid = uuid;
        this.elo = elo;
    }

    public RatedPlayerInfo(ByteArrayDataInput input) {
        this(UUID.fromString(input.readUTF()), input.readInt());
    }

    public void toOutput(ByteArrayDataOutput output) {
        output.writeUTF(uuid.toString());
        output.writeInt(elo);
    }

}
