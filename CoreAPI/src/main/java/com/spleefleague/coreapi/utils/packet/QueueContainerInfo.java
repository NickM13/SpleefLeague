package com.spleefleague.coreapi.utils.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class QueueContainerInfo {

    public String name;
    public int queued, spectators, playing;

    public QueueContainerInfo(String name, int queued, int playing, int spectators) {
        this.name = name;
        this.queued = queued;
        this.playing = playing;
        this.spectators = spectators;
    }

    public QueueContainerInfo(ByteArrayDataInput input) {
        this(   input.readUTF(),
                input.readInt(),
                input.readInt(),
                input.readInt());
    }

    public void toOutput(ByteArrayDataOutput output) {
        output.writeUTF(name);
        output.writeInt(queued);
        output.writeInt(playing);
        output.writeInt(spectators);
    }

}
