package com.spleefleague.coreapi.utils.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

/**
 * @author NickM13
 * @since 9/19/2020
 */
public abstract class PacketVariable {

    public void fromInput(ByteArrayDataInput input) {
        PacketUtil.readToFields(this, input);
    }

    public void toOutput(ByteArrayDataOutput output) {
        PacketUtil.writeFromFields(this, output);
    }

}
