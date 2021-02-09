package com.spleefleague.coreapi.utils.packet.bungee.player;

import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.bungee.PacketBungee;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 * @since 1/31/2021
 */
public class PacketBungeePlayerResync extends PacketBungee {

    public enum Field {
        COLLECTIBLES("collectibles"),
        CRATES("crates"),
        FRIENDS("fields"),
        PURSE("purse"),
        RANK("");

        String fieldName;

        Field(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getFieldName() {
            return fieldName;
        }
    }

    public UUID uuid;
    public List<Field> fields = new ArrayList<>();

    public PacketBungeePlayerResync() {

    }

    public PacketBungeePlayerResync(UUID uuid, List<Field> fields) {
        this.uuid = uuid;
        this.fields = fields;
    }

    @Nonnull
    @Override
    public PacketType.Bungee getBungeeTag() {
        return PacketType.Bungee.PLAYER_RESYNC;
    }

}
