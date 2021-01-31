package com.spleefleague.coreapi.infraction;

import com.spleefleague.coreapi.utils.packet.shared.PacketVariable;

import java.util.UUID;

/**
 * @author NickM13
 */
public class InfractionPV extends PacketVariable {

    private InfractionType type;
    private UUID target;
    private UUID punisher;
    private String reason;
    private Long time;
    private Long duration;

    public InfractionPV() {

    }

}
