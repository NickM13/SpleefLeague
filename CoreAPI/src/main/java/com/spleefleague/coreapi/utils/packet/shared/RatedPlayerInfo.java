package com.spleefleague.coreapi.utils.packet.shared;

import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class RatedPlayerInfo extends PacketVariable {

    public NumAction action;
    public UUID uuid;
    public int elo;

    public RatedPlayerInfo() { }

    public RatedPlayerInfo(NumAction action, UUID uuid, int elo) {
        this.action = action;
        this.uuid = uuid;
        this.elo = elo;
    }

}
