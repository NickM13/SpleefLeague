package com.spleefleague.coreapi.utils.packet.shared;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PartyContainerInfo extends PacketVariable {

    public List<UUID> players;

    public PartyContainerInfo() { }

    public PartyContainerInfo(List<UUID> players) {
        this.players = Lists.newArrayList(players);
    }

}
