package com.spleefleague.coreapi.utils.packet.shared;

import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class RatedPlayerInfo extends PacketVariable {

    public UUID uuid;
    public int elo;

    public RatedPlayerInfo() { }

    public RatedPlayerInfo(UUID uuid, int elo) {
        this.uuid = uuid;
        this.elo = elo;
    }

}
