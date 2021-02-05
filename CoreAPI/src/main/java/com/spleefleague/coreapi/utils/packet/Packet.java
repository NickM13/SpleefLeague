package com.spleefleague.coreapi.utils.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.lang.reflect.Field;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public abstract class Packet {

    public Packet() {

    }

    public abstract int getTag();

    public final void fromByteArray(ByteArrayDataInput input) {
        PacketUtil.readToFields(this, input);
    }

    public final byte[] toByteArray() {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeInt(getTag());
        PacketUtil.writeFromFields(this, output);
        return output.toByteArray();
    }

    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder(this.getClass().getSimpleName() + "{");

        boolean first = true;
        for (Field field : getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (!first) builder.append(", ");
            else first = false;
            try {
                builder.append(field.getName()).append("=").append(field.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        builder.append("}");
        return builder.toString();
    }

}
