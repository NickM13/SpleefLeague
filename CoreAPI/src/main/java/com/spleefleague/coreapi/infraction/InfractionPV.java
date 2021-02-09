package com.spleefleague.coreapi.infraction;

import com.spleefleague.coreapi.utils.packet.shared.PacketVariable;

import java.util.UUID;

/**
 * @author NickM13
 */
public class InfractionPV extends PacketVariable {

    public InfractionType type;
    public UUID target;
    public String punisher;
    public String reason;
    public Long time;
    public Long duration;

    public InfractionPV() {

    }

    public InfractionPV(Infraction infraction) {
        this.type = infraction.getType();
        this.target = infraction.getTarget();
        this.punisher = infraction.getPunisher();
        this.reason = infraction.getReason();
        this.time = infraction.getTime();
        this.duration = infraction.getDuration();
    }

}
