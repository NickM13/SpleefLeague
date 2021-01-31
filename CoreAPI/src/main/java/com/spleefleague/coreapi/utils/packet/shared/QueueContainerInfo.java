package com.spleefleague.coreapi.utils.packet.shared;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class QueueContainerInfo extends PacketVariable {

    public String name;
    public int queued, spectators, playing;

    public QueueContainerInfo() { }

    public QueueContainerInfo(String name, int queued, int playing, int spectators) {
        this.name = name;
        this.queued = queued;
        this.playing = playing;
        this.spectators = spectators;
    }

}
